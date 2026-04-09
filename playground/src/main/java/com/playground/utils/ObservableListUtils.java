package com.playground.utils;

import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class ObservableListUtils {
    public static <T> void addOrRemove(ObservableList<T> list, T item, boolean adding) {
        list.remove(item); // makes sure that is not already there when adding (javafx exception)
        if(adding) {
            list.add(item);
        }
    }

    public static <T> void replace(ObservableList<T> list, T item) {
        List<T> items = list.stream().collect(Collectors.toList());
        for(int i=0; i<list.size();i++) list.remove(items.get(i));
        list.add(item);
    }
}
