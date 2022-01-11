package net.drapuria.framework.database;

import java.util.ArrayList;
import java.util.List;

public class CompletionStateImpl implements CompletionState {

    private boolean ready = false;

    private final List<Runnable> readyExecutors = new ArrayList<>();

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
        if (this.ready) {
            this.readyExecutors.forEach(Runnable::run);
            this.readyExecutors.clear();
        }
    }


    @Override
    public List<Runnable> getReadyExecutors() {
        return this.readyExecutors;
    }

    @Override
    public void addReadyExecutor(Runnable runnable) {
        if (this.isReady())
            runnable.run();
        else
            this.readyExecutors.add(runnable);
    }
}
