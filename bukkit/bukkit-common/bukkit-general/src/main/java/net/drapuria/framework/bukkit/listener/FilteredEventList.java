/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.listener;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerEvent;
import org.imanity.framework.reflect.Reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
public class FilteredEventList {

    private static final Map<Class<?>, Function<Event, Player>> EVENT_PLAYER_METHODS = new ConcurrentHashMap<>();
    private static final Set<Class<?>> NO_METHODS = Sets.newConcurrentHashSet();

    static {

        EVENT_PLAYER_METHODS.put(BlockBreakEvent.class, event -> ((BlockBreakEvent) event).getPlayer());
        EVENT_PLAYER_METHODS.put(BlockPlaceEvent.class, event -> ((BlockPlaceEvent) event).getPlayer());
        EVENT_PLAYER_METHODS.put(FoodLevelChangeEvent.class, event -> (Player) ((FoodLevelChangeEvent) event).getEntity());

    }

    public static void putCustomPlayerMethod(Class<?> eventClass, Function<Event, Player> method) {
        EVENT_PLAYER_METHODS.put(eventClass, method);
    }

    private final Predicate<Event>[] filters;

    private FilteredEventList(Builder builder) {
        this.filters = builder.filters.toArray(new Predicate[0]);
    }

    public boolean check(Event event) {

        for (Predicate<Event> filter : this.filters) {
            if (!filter.test(event)) {
                return false;
            }
        }

        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Predicate<Event>> filters;

        public Builder() {
            this.filters = new ArrayList<>(1);
        }

        public Builder filter(Predicate<Event> filter) {
            this.filters.add(filter);
            return this;
        }

        public Builder filter(BiPredicate<Player, Event> filter) {
            this.filters.add(event -> {
                Player player = null;
                Class<?> type = event.getClass();
                if (event instanceof PlayerEvent) {
                    player = ((PlayerEvent) event).getPlayer();
                } else if (EVENT_PLAYER_METHODS.containsKey(type)) {
                    player = EVENT_PLAYER_METHODS.get(type).apply(event);
                } else {
                    MethodHandle methodHandle = null;

                    if (!NO_METHODS.contains(type)) {
                        for (Method method : Reflect.getDeclaredMethods(type)) {
                            if (method.getParameterCount() == 0) {
                                Class<?> returnType = method.getReturnType();
                                if (Player.class.isAssignableFrom(returnType) || HumanEntity.class.isAssignableFrom(returnType)) {
                                    try {
                                        methodHandle = Reflect.lookup().unreflect(method);

                                        MethodHandleFunction methodHandleFunction = new MethodHandleFunction(methodHandle);
                                        EVENT_PLAYER_METHODS.put(event.getClass(), methodHandleFunction);

                                        player = methodHandleFunction.apply(event);
                                        break;
                                    } catch (Throwable throwable) {
                                        throw new IllegalArgumentException("Something wrong while looking for player", throwable);
                                    }
                                }
                            }
                        }

                        if (methodHandle == null) {
                            NO_METHODS.add(type);
                        }
                    }
                }

                if (player != null) {
                    return filter.test(player, event);
                }
                return true;
            });

            return this;
        }

        public FilteredEventList build() {
            return new FilteredEventList(this);
        }

    }

    @RequiredArgsConstructor
    private static class MethodHandleFunction implements Function<Event, Player> {

        private final MethodHandle methodHandle;

        @Override
        public Player apply(Event event) {
            try {
                Object entity = methodHandle.invoke(event);
                if (entity instanceof Player) {
                    return (Player) entity;
                }
            } catch (Throwable throwable) {
                throw new IllegalArgumentException("Something wrong while looking for player", throwable);
            }
            return null;
        }
    }

}
