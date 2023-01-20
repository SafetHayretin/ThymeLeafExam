package TemplateEngine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;

public class Template {
    private String fileName;

    private TemplateContext ctx;

    private PrintStream out;

    public Template(String file) {
        this.fileName = file;
    }

    public void render(TemplateContext ctx, PrintStream out) throws IOException, IllegalAccessException {
        this.ctx = ctx;
        this.out = out;
        File file = new File("src/main/resources/" + fileName);
        Document doc = Jsoup.parse(file);
        Element element = doc.getElementsByAttribute("t:text").get(0);
        insertText(element);
        Element tEach = doc.getElementsByAttribute("t:each").get(0);
    }

    private void insertForEach(Element element) throws IllegalAccessException {
        String forEach = element.attr("t:each");
        if (!forEach.isEmpty()) {
            String[] split = forEach.split(": ");
            String key = split[1].substring(2, split[1].length() - 1);
            Object obj = ctx.getElement(key);
            Student[] students = (Student[]) obj;
        }
    }

    private void insertText(Element element) throws IllegalAccessException {
        String text = element.attr("t:text");
        if (!text.isEmpty()) {
            text = text.substring(2, text.length() - 1);
            element.attr(text);
            element.text(getParam(text));
            element.removeAttr("t:text");
        }
    }

    private String getParam(String key) throws IllegalAccessException {
        Object obj = ctx.getElement(key);
        if (obj == null)
            return null;

        String[] split = key.split("\\.");
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field f : fields) {
            f.setAccessible(true);
            if (f.getName().equals(split[1]))
                return f.get(obj).toString();
        }

        return null;
    }
}
