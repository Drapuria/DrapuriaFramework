package net.drapuria.framework.database;

public class HandlerGroupFactory {

    public static HandlerGroupFactory factory() {
        return new HandlerGroupFactory();
    }

    private String name = null;
    private Long executeDelay = null;
    private HandlerGroupType type = null;

    public HandlerGroupFactory name(String name) {
        this.name = name;
        return this;
    }

    public HandlerGroupFactory executeDelay(long executeDelay) {
        this.executeDelay = executeDelay;
        return this;
    }

    public HandlerGroupFactory groupType(HandlerGroupType type) {
        this.type = type;
        return this;
    }


    public HandlerGroup build() {
        if (type == HandlerGroupType.SINGLE_THREADED) {
            return new SingleThreadedHandlerGroup(name, executeDelay);
        } else {
            return new ThreadedHandlerGroup(name, executeDelay);
        }
    }
}
