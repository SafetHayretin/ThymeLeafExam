package TemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class TemplateContext {
    private Map<String, Object> elements = new HashMap<>();

    public void put(String tag, Object obj) {
        elements.put(tag, obj);
    }

    public Object getElement(String key) {
        return elements.get(key);
    }
}
