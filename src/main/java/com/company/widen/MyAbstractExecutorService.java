package com.company.widen;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class MyAbstractExecutorService extends ThreadPoolExecutor {
    MyAbstractExecutorService() {
        super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1), threadFactory -> { // changed the LinkedBlockingQueue arg to 1
            Thread thread = new Thread(threadFactory);
            thread.setName("JNativeHook Dispatch Thread");
            thread.setDaemon(true);
            return thread;
        });
    }
}
