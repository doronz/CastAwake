package com.doronzehavi.castawake;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Helper class for background threads.
 */
public final class AsyncHandler {

    private static final HandlerThread sHandlerThread = new HandlerThread("AsyncHandler");
    private static final Handler sHandler;

    static {
        sHandlerThread.start();
        sHandler = new Handler(sHandlerThread.getLooper());
    }

    public static void post (Runnable r) { sHandler.post(r); }

    private AsyncHandler() {}
}
