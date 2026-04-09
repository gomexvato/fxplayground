package com.playground;

import java.util.Map;

public interface ComponentsApi {
    String[] getCategories();
    <T> Map<String, Map<String, T>> getSubcategories(String cat);
}
