package net.drapuria.framework.repository.object;

import java.util.List;

public interface CachedObject {

    boolean isReady();

    List<Runnable> getReadyExecutors();

    void addReadyExecutor(Runnable runnable);

    void setReady(boolean ready);


}
