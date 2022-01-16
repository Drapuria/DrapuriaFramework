package net.drapuria.framework.task;

public interface ITask {

    void shutdown() throws TaskNotStartedException;

    void start(long delay, long period, Runnable runnable) throws TaskAlreadyStartedException;

    boolean isAsync();

}
