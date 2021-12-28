package net.drapuria.framework.database;

import java.util.List;

public interface CompletionState {

    boolean isReady();

    void setReady(boolean ready);

    List<Runnable> getReadyExecutors();

    void addReadyExecutor(Runnable runnable);

}
