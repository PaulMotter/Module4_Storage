import org.eclipse.paho.client.mqttv3.MqttException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;

public class Outsourcer implements Runnable {
    private final Repository<Job> localJobsQueue;
    private static final String broker = "tcp://test.mosquitto.org:1883";
    private static final String topicBase = "/csc364/wliu40";
    private final SubscriberManager subscriberManager ;
    private final PublisherManager publisherManager;
    private final WorkerTracker workerTracker;
    public Outsourcer(Repository<Job> queue) {
        this.localJobsQueue = queue;
        WorkerTracker workerTracker = new WorkerTracker();
        this.workerTracker = workerTracker;
        subscriberManager = new SubscriberManager(broker, topicBase,workerTracker);
        publisherManager = new PublisherManager(broker,topicBase,workerTracker);
    }

    @Override
    public void run() {
        while(true) {
            try {
                Job job = localJobsQueue.consume();
                String clientID = workerTracker.getNextAvailableWorker();
                outsource(job,clientID);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void outsource(Job job,String clientID) throws InterruptedException, MqttException {
        subscriberManager.addWorkerSubscription(clientID);
        publisherManager.publishTask(job,clientID);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Job currTask = workerTracker.getWorkerTask(clientID);
                if(currTask != null && currTask.getId() == job.getId()) {
                    try {
                        subscriberManager.removeClient(clientID);
                        String new_clientID = workerTracker.getNextAvailableWorker();
                        outsource(job,new_clientID);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 5000);
    }
}
