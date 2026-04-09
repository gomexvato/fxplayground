package com.playground.utils.codeview;

import com.core.utils.FileUtils;
import com.playground.components.PFxWebView;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CodeView {

    private final static String codeViewDir = "/com/playground/utils/codeview/";

    // returns a webview for the component's code (getComponent method)
    public static WebView get(
            String pgModuleJavaPath,
            String scModuleJavaPath,
            Class variantClass
    ) {
        String variant = pkgName(variantClass);
        String javaCode = readGetComponent(pgModuleJavaPath+"/"+variant);
        if(javaCode == null) {
            System.err.println("Unable to get source for "+pgModuleJavaPath+"/"+variant);
        }

        return (new PFxWebView(html(scModuleJavaPath, javaCode), "hljs.highlightAll();")).view;
    }

    private static String html(String scModuleJavaPath, String javaCode) {
        try {
            String dir = scModuleJavaPath + codeViewDir;
            String template    = FileUtils.read(dir + "template.html");
            String highlightJs = FileUtils.read(dir + "highlight.min.js");
            String javaJs      = FileUtils.read(dir + "java.min.js");
            String defaultCss  = FileUtils.read(dir + "default.min.css");
            String styleCss    = FileUtils.read(dir + "style.css");

            String html = javaCode != null
                    ? template.replace("{JAVA_SOURCE}", javaCode)
                    : "check your source code - the code viewer only handles simple code";

            // Inline all assets so loadContent() works without a base URL
            html = html.replace("<base href=\"{BASE_HREF}\">", "");
            html = html.replace("<link rel=\"stylesheet\" href=\"default.min.css\"/>", "<style>" + defaultCss + "</style>");
            html = html.replace("<link rel=\"stylesheet\" href=\"style.css\"/>", "<style>" + styleCss + "</style>");
            html = html.replace("<script src=\"highlight.min.js\"></script>", "<script>" + highlightJs + "</script>");
            html = html.replace("<script src=\"java.min.js\"></script>", "<script>" + javaJs + "</script>");
            return html;
        } catch (IOException e) {
            return "Unable to load template: " + e.getMessage();
        }
    }

    // returns package name for the given class
    private static String pkgName(Class clazz) {
        String name = clazz.getPackage() + "." + clazz.getSimpleName();
        name = name.replace(".", "/");
        name = name.replace("package ", "");
        return name+".java";
    }

    // Primitive, but collects lines after getComponent in ComponentVariant
    private static String readGetComponent(String filepath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            List<String> lines = reader.lines().collect(Collectors.toList());
            List<String> source = new ArrayList();
            boolean capture = false;
            int spaces = 0;
            //captures anything after get component
            for(int i=0;i<lines.size();i++) {
                String line = lines.get(i);
                if(line.indexOf("getComponent()") > 0) {
                    capture = true;
                    continue;
                }
                if(capture) {
                    if(source.isEmpty()) spaces = indexOfNonWhitespace(line);
                    source.add(line.length() > spaces ? line.substring(spaces) : line);
                }
            }
            int endLinesToRemove = 0;
            for(int j=source.size()-1;j>0;j--) {
                String line = source.get(j).trim();
                if(line.isEmpty() || line.equals("}")) {
                    endLinesToRemove += 1;
                } else {
                    break;
                }
            }
            source = source.subList(0, source.size()-endLinesToRemove);
            return String.join("\n", source).replace("return ", "");
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int indexOfNonWhitespace(String line) {
        if(line != null) {
            for(int i=0;i<line.length();i++) {
                if(!Character.isWhitespace(line.charAt(i))) return i;
            }
        }
        return 0;
    }
}