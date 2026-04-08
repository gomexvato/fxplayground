package com.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FxListeners {

    private final Map<ObservableValue, List<InvalidationListener>> invalidationListeners;
    private final Map<ObservableValue, List<ChangeListener>> changeListeners;

    public FxListeners() {
        invalidationListeners = new ConcurrentHashMap<>();
        changeListeners = new ConcurrentHashMap<>();
    }

    public final <T> void add(ObservableValue<T> observable, InvalidationListener listener) {
        observable.addListener(listener);
        invalidationListeners.compute(observable, (invList, listenerList) ->
                listenerList == null ? new ArrayList<>() : listenerList).add(listener);
    }

    public final <T> void add(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        changeListeners.compute(observable, (invList, listenerList) ->
                listenerList == null ? new ArrayList<>() : listenerList).add(listener);
    }

    public final void remove() {
        invalidationListeners.forEach((observable, listenerList) -> {
            listenerList.forEach(listener -> {
                observable.removeListener(listener);
            });
            invalidationListeners.remove(observable);
        });

        changeListeners.forEach((observable, listenerList) -> {
            listenerList.forEach(listener -> {
                observable.removeListener(listener);
            });
            changeListeners.remove(observable);
        });
    }

    public boolean isEmpty() {
        return invalidationListeners.isEmpty() && changeListeners.isEmpty();
    }
}