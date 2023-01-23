package TemplateEngine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;

public class Template {
    private final String fileName;

    private TemplateContext ctx;

    public Template(String file) {
        this.fileName = file;
    }

    public void render(TemplateContext ctx, PrintStream out) throws IOException, IllegalAccessException {
        this.ctx = ctx;
        File file = new File("src/main/resources/" + fileName);
        Document doc = Jsoup.parse(file);
        Elements tEach = doc.getElementsByAttribute("t:each");

        for (Element element : tEach)
            insertForEach(element);

        Elements textElements = doc.getElementsByAttribute("t:text");
        for (Element element : textElements) {
            insertText(element, null);
        }

        out.println(doc);
    }

    private void insertForEach(Element element) throws IllegalAccessException {
        String forEach = element.attr("t:each");
        Element parent = element.parent();
        element.remove();

        if (forEach.isEmpty()) {
            throw new IllegalArgumentException("t:each attribute doesn't exist");
        }

        String[] split = forEach.split(": ");
        String key = split[1].substring(2, split[1].length() - 1);
        Object obj = ctx.getElement(key);
        Object[] objects = (Object[]) obj;
        Elements children = element.children();

        for (int i = 0; i < objects.length; i++) {
            Tag tag = element.tag();
            Element newElement = new Element(tag.toString());
            Elements newChildren = children.clone();
            for (Element child : newChildren) {
                insertText(child, objects[i]);
            }
            newElement.prependChildren(newChildren);
            assert parent != null;
            parent.insertChildren(i, newElement);
        }
    }

    private void insertText(Element element, Object obj) throws IllegalAccessException {
        String text = element.attr("t:text");
        if (!text.isEmpty()) {
            text = text.substring(2, text.length() - 1);
            element.attr(text);
            if (obj == null)
                obj = ctx.getElement(text);
            element.text(getParam(text, obj));
            element.removeAttr("t:text");
        }
    }

    private String getParam(String key, Object obj) throws IllegalAccessException {
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
