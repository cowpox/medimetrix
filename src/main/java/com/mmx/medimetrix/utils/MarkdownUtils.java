package com.mmx.medimetrix.utils;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownUtils {
    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public static String toHtml(String markdown) {
        if (markdown == null) return "";
        Node document = parser.parse(markdown);
        String html = renderer.render(document);

        // Qualquer ênfase (<em>...</em>) vira negrito (<strong>...</strong>)
        html = html.replace("<em>", "<strong>")
                .replace("</em>", "</strong>");

        return html;
    }
}
