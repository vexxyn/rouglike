package com.example.roguelike.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * OBSERVER pattern. Sistem-sistem game tidak saling memanggil langsung;
 * mereka cukup publish event ("musuh mati") dan siapa pun yang tertarik subscribe.
 */
public class EventBus {
    private final Map<Class<?>, List<Consumer<Object>>> handlers = new HashMap<>();

    public <T> void subscribe(Class<T> type, Consumer<? super T> handler) {
        handlers.computeIfAbsent(type, k -> new ArrayList<>())
                .add(o -> handler.accept(type.cast(o)));
    }

    public void publish(Object event) {
        List<Consumer<Object>> list = handlers.get(event.getClass());
        if (list == null) return;
        for (Consumer<Object> c : new ArrayList<>(list)) c.accept(event);
    }
}
