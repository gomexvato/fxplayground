package com.playground.utils;

import com.core.utils.FileUtils;
import com.core.utils.ListUtils;
import com.playground.ComponentVariant;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class VariantLoader {

    // loads all java files in the given package
    // expects all files to be ComponentVariant implementations
    public static ComponentVariant[] loadVariants(String variantsDirPath) {
        try {
            String path = new File(variantsDirPath).getAbsolutePath();
            String pkg = path.substring(path.indexOf("\\java\\")+6).replaceAll("\\\\", ".");
            List<File> files = FileUtils.list(variantsDirPath, "java", 1, null);
            List<File> variants = ListUtils.filter(files, file -> file.getName().startsWith("Variant"));
            ComponentVariant[] all = new ComponentVariant[variants.size()];
            for (int i=0;i<variants.size();i++) {
                String fname = variants.get(i).getName();
                Class<ComponentVariant> dynamicClass = (Class<ComponentVariant>)
                        Class.forName(pkg + "." + fname.substring(0, fname.indexOf(".java")));
                all[i] = (dynamicClass.newInstance());
            }
            return all;
        } catch(Exception e) {
            e.printStackTrace();
            return new ComponentVariant[0];
        }
    }
}
