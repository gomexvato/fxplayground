package com.playground;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.playground.utils.VariantLoader.loadVariants;

public class FilesystemComponents implements ComponentsApi{

    public static Function<String, FilesystemComponents> api = (path) -> new FilesystemComponents(path);
    private final String[] cats;
    private final String rootPath;

    private FilesystemComponents(String path) {
        File file = new File(path);
        if(!file.exists()) {
            throw new RuntimeException("Unable to find "+file.getAbsolutePath());
        }
        this.rootPath = file.getAbsolutePath();
        this.cats = dirs(rootPath);
    }

    @Override
    public String[] getCategories() {
        return cats;
    }

    @Override
    public <T> Map<String, Map<String, T>> getSubcategories(String cat) {
        String catPath = rootPath+"/"+cat;
        String[] subs = dirs(catPath);
        Map<String, Map<String, T>> map = new HashMap<>();
        for(String sub: subs) {
            Map<String, T> submap = new HashMap<>();
            String subcatPath = catPath + "/" + sub;
            for(String x: dirs(subcatPath)) {
                String xPath = subcatPath + "/" + x;
                submap.put(x, (T) loadVariants(xPath));
            }
            map.put(sub, submap);
        }
        return map;
    }

    private String[] dirs(String path) {
        String[] all = (new File(path)).list((dir, name) -> new File(dir, name).isDirectory());
        return all != null
            ? all//ArrayUtils.map(all, StringUtils::convertToCamelCase, String[]::new)
            : new String[]{};
    }
}
