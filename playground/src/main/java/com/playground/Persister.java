package com.playground;

import com.google.gson.Gson;
import com.core.utils.FileUtils;

import java.io.File;

class Persister {
    private final File file;
    private static Persister persister;

    private Persister(File file) {
        this.file = file;
    }

    static Persister instance(File file) {
        if(persister == null) persister = new Persister(file);
        return persister;
    }

    <T> void persist(T t) {
        String json = new Gson().toJson(t);
        try {
            file.delete();
            FileUtils.save(file, json);
        } catch(Exception e) {
            System.err.println("Unable to save: "+file.getAbsolutePath());
            throw new RuntimeException(e);
        }
    }

    <T> T retrieve(Class<T> clazz) {
        if(file.exists()) {
            try {
                return new Gson().fromJson(FileUtils.read(file.getAbsolutePath()), clazz);
            } catch (Exception ignored) {}
        }
        return null;
    }
}
