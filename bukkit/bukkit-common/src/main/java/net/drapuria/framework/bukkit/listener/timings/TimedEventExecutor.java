package net.drapuria.framework.bukkit.listener.timings;

import java.lang.reflect.Method;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.timings.MCTiming;
import net.drapuria.framework.bukkit.timings.TimingService;
import net.drapuria.framework.services.Autowired;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;


public class TimedEventExecutor implements EventExecutor {

    @Autowired
    private static TimingService TIMING_SERVICE;

    private final EventExecutor executor;
    private final MCTiming timings;

    public TimedEventExecutor(EventExecutor eventExecutor, Plugin plugin, Method method, Class<? extends Event> eventClass) {
        this.executor = eventExecutor;
        if (method == null && eventExecutor.getClass().getEnclosingClass() != null) {
            method = eventExecutor.getClass().getEnclosingMethod();
        }

        String methodName;
        if (method != null) {
            methodName = method.getDeclaringClass().getName();
        } else {
            methodName = eventExecutor.getClass().getName();
        }

        String eventName = eventClass.getSimpleName();
        boolean special = "BlockPhysicsEvent".equals(eventName) || "Drain".equals(eventName) || "Fill".equals(eventName);
        this.timings = TIMING_SERVICE.of(plugin, (special ? "## " : "") + "Event: " + methodName + " (" + eventName + ")");
    }

    public void execute(Listener listener, Event event) throws EventException {
        if (!event.isAsynchronous() && Drapuria.IMPLEMENTATION.isServerThread()) {
            try (MCTiming ignored = this.timings.startTiming()) {
                this.executor.execute(listener, event);
            }
        } else {
            this.executor.execute(listener, event);
        }
    }
}
