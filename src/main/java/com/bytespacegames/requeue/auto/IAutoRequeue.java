package com.bytespacegames.requeue.auto;

public interface IAutoRequeue {
    void onTick();
    boolean canRequeue();
    void requeueCleanup();
}
