import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerTracker {
    private final HashMap<String, Job> hashMap = new HashMap<>();
    private final BlockingQueue<String> availableWorkers = new LinkedBlockingQueue<>();;

    public String getNextAvailableWorker() throws InterruptedException {
        return availableWorkers.take();
    }

    public void addWorker(String newWorkerClientId) {
        if(!hashMap.containsKey(newWorkerClientId)) {
            hashMap.put(newWorkerClientId, null);
            availableWorkers.add(newWorkerClientId);
        }
    }

    public Job getWorkerTask(String clientID) {
        return hashMap.get(clientID);
    }
    public void addJob(String newWorkerClientId, Job job) {
        hashMap.put(newWorkerClientId,job);
        availableWorkers.remove(newWorkerClientId);
    }

    public void jobFinished(String workerClientId, String jobId) {
        Job workersJob = hashMap.get(workerClientId);
        if(workersJob != null ) {
            int id = Integer.parseInt(jobId);
            if(workersJob.getId() == id) {
                hashMap.put(workerClientId,null);
                availableWorkers.add(workerClientId);
            }
        }
    }

}
