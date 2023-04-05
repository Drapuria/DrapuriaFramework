package net.drapuria.framework.beans.configuration;

import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class GenericConfigurationProvider extends AbstractConfigurationProvider<Object> {
    @SneakyThrows
    @Override
    public void onEnable(Object config) {
        for (Method method : config.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            method.invoke(config);
        }
    }

    @SneakyThrows
    @Override
    public void onPostEnable(Object config) {
        for (Method method : config.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            method.invoke(config);
        }
    }
}