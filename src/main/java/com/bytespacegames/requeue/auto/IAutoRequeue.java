package com.bytespacegames.requeue.auto;

public interface IAutoRequeue {
    public void onTick();
    public boolean canRequeue();
    public void requeueCleanup();
}
