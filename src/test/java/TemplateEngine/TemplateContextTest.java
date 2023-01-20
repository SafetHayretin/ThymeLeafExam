package TemplateEngine;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintStream;

class TemplateContextTest {
    @Test
    public void testTemplate() throws IOException, IllegalAccessException {
        TemplateContext ctx = new TemplateContext();
        WelcomeMessage welcome = new WelcomeMessage("hello world");
        ctx.put("welcome.message", welcome);

        Student[] students = {
                new Student(1, "Ivan"),
                new Student(2, "Maria"),
                new Student(3, "Nikola")
        };

        ctx.put("students", students);

        Template t = new Template("template.tm");
        PrintStream out = System.out;
        t.render(ctx, out);
    }
}