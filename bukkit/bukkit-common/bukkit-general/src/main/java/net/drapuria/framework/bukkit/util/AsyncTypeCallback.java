package net.drapuria.framework.bukkit.util;

import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.util.TypeCallback;
import org.bukkit.Bukkit;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * @author Marinus created on 18.06.2021 inside the package - de.vantrex.hardcore.utils
 */

public class AsyncTypeCallback<T> {

    private final TypeCallback<T> callback;

    public AsyncTypeCallback(TypeCallback<T> callback) {
        this.callback = callback;
    }

    @SneakyThrows
    public void performSync(boolean ignoreNullValue, Callable<T> callable) {
        CompletableFuture.runAsync(() -> {
            try {
                T t = callable.call();
                if (ignoreNullValue && t == null)
                    return;
                Bukkit.getScheduler().runTask(Drapuria.PLUGIN, () -> callback.callback(t));
            } catch (Exception ignored) {
            }
        });
    }

    public void performAsync(boolean ignoreNullValue, Callable<T> callable) {
        CompletableFuture.runAsync(() -> {
            try {
                T t = callable.call();
                if (ignoreNullValue && t == null)
                    return;
                callback.callback(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void performSync(T t) {
        CompletableFuture.supplyAsync(() -> t, DrapuriaCommon.executorService)
                .thenRun(() -> Bukkit.getScheduler().runTask(Drapuria.PLUGIN, () -> callback.callback(t)));
    }

    public void performCompletelySync(T t) {
        if (Bukkit.isPrimaryThread()) {
            callback.callback(t);
            return;
        }
        Bukkit.getScheduler().runTask(Drapuria.PLUGIN, () -> callback.callback(t));
    }
}