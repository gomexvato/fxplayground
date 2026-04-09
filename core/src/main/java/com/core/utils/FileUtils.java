package com.core.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static<T> T read(File file, Function<InputStream, T> fn) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(file.toPath());
            return fn.apply(inputStream);
        } finally {
            if(inputStream != null) inputStream.close();
        }
    }

    public static String read(String filepath) throws IOException {
        File file = new File(filepath);
        if (!file.exists()) throw new IOException(filepath + " not found!");
        return org.apache.commons.io.FileUtils.readFileToString(file, "UTF-8");
    }

    public static List<String> readLines(String filepath) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filepath));
            return reader.lines().collect(Collectors.toList());
        } finally {
            if(reader != null) {
                reader.close();
            }
        }
    }

    public static List<File> list(
            String path,
            String extension,
            int maxDepth,
            Function<String, Boolean> filter
    ) throws IOException {
        Path dir = Paths.get(path);
        if (!dir.toFile().isDirectory()) throw new IOException(path + " is not a directory!");
        try (Stream<Path> stream = Files.walk(dir, maxDepth)) {
            return stream.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> filter != null ? filter.apply(file.getAbsolutePath()) : true)
                    .filter(file -> extension == null || file.getAbsolutePath().endsWith("." + extension))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public static List<String> listFiles(
            String path,
            String extension,
            int maxDepth,
            Function<String, Boolean> filter
    ) throws IOException {
        Path dir = Paths.get(path);
        if (!dir.toFile().isDirectory()) throw new IOException(path + " is not a directory!");
        try (Stream<Path> stream = Files.walk(dir, maxDepth)) {
            return stream.filter(Files::isRegularFile)
                    .map(it -> it.toAbsolutePath().toString())
                    .filter(filepath -> filter != null ? filter.apply(filepath) : true) //remove any files that this tool generates
                    .filter(filepath -> extension != null ? filepath.endsWith("."+extension) : true)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public static List<String> listFiles(String path, String extension, int maxDepth) throws IOException {
        return listFiles(path, extension, maxDepth, null);
    }

    public static List<String> listFiles(String path, String extension) throws IOException {
        return listFiles(path, extension, 1, null);
    }

    public static List<String> listFiles(String path) throws IOException {
        return listFiles(path, null, 1, null);
    }

    public static void deleteDir(String dirpath) {
        deleteDir(new File(dirpath));
    }

    public static void deleteDir(File dir) {
        if(dir.exists()) {
            File[] contents = dir.listFiles();
            if (contents != null) for (File f : contents) deleteDir(f);
            dir.delete();
        }
    }

    public static File makeDir(String pathname) {
        File dir = new File(pathname);
        dir.mkdir();
        return dir;
    }

    public static File makeDir(String pathname, boolean overwrite) {
        File newDir = new File(pathname);
        if(overwrite && newDir.exists()) deleteDir(newDir);
        newDir.mkdir();
        return newDir;
    }

    public static void copyDir(String fromDir, String toDir) throws IOException {
        File dir = new File(fromDir);
        if(!dir.exists()) throw new IOException("Unable to copy - directory not found "+fromDir);
        File newDir = makeDir(toDir);
        org.apache.commons.io.FileUtils.copyDirectory(dir, newDir);
    }

    public static void save(File file, String contents) throws IOException {
        save(file, contents, true);
    }

    private static void save(File file, String contents, boolean append) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8));
        out.write(contents);
        out.flush();
        out.close();
    }

    public static synchronized boolean isDirWithFullAccess(Path path) {
        try {
            Path tmp = null;
            boolean deleted;
            try {
                tmp = Paths.get(path.toString(), "." + System.currentTimeMillis() + ".tmp");
                save(tmp.toFile(), "");
                deleted = Files.deleteIfExists(tmp);
            } finally {
                if(tmp!=null) Files.deleteIfExists(tmp);
            }
            return Files.isDirectory(path) &&
                   Files.isWritable(path) &&
                   Files.isReadable(path) &&
                   deleted;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Attempts to find files with the given wildcard pattern
     * @param startPath initial directory
     * @param wildcarPattern file pattern. ie: devicedb-*.db
     * @return list of paths
     */
    public static List<Path> findWithWildcard(Path startPath, String wildcarPattern) {
        try {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcarPattern);
            return Files.walk(startPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> matcher.matches(path.getFileName()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Looks for file in the given paths.
     * Throws and exception when the file is not found.
     * @param paths
     * @param filename
     * @return file path
     */
    public static java.nio.file.Path getFilePath(String[] paths, String filename) {
        for(String path: paths){
            java.nio.file.Path xpath = Paths.get(path + (path.endsWith("/") ? "" : "/") + filename);
            if(Files.exists(xpath)) return xpath;
        }
        throw new RuntimeException("Unable to find file:"+filename);
    }
}
