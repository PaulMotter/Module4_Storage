package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Buffer {
    BlockingQueue<String> queue;
    public Buffer() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public synchronized void add(String m) {
        queue.add(m);
    }

    public synchronized List<String> clean() {
        List<String> list = new ArrayList<>();
        queue.drainTo(list);
        return list;
    }

}
