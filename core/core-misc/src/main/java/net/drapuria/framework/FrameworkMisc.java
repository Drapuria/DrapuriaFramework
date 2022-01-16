package net.drapuria.framework;

import net.drapuria.framework.events.IEventHandler;
import net.drapuria.framework.jackson.libraries.LibraryHandler;
import net.drapuria.framework.task.ITaskScheduler;

public class FrameworkMisc {

    public static DrapuriaPlatform PLATFORM;
    public static IEventHandler EVENT_HANDLER;
    public static ITaskScheduler TASK_SCHEDULER;
    public static LibraryHandler LIBRARY_HANDLER;

    public static void close() {

    }

}
