package com.example.demo.util;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
//import com.vladsch.flexmark.ext.anchorlink.AnchorlinkExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.Arrays;

public class MarkdownUtils {

    // 配置扩展选项
    private static final MutableDataSet OPTIONS = new MutableDataSet();
    static {
        OPTIONS.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),          // 表格支持
                TaskListExtension.create(),        // 任务列表 [-]
                AutolinkExtension.create()        // 自动链接
                //AnchorlinkExtension.create()       // 标题自动添加锚点
                // 可以添加更多扩展
        ));
    }

    private static final Parser PARSER = Parser.builder(OPTIONS).build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    /**
     * 将 Markdown 转换为 HTML
     */
    public static String markdownToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        // 解析并渲染
        return RENDERER.render(PARSER.parse(markdown));
    }
    
    /**
     * 提取纯文本用于摘要 (可选功能)
     */
    public static String markdownToText(String markdown) {
        // 简单实现：先转HTML，再用Jsoup去除标签，或者直接返回前200字符
        // 这里为了简单，暂不引入Jsoup，你可以后续优化
        return markdown.length() > 200 ? markdown.substring(0, 200) + "..." : markdown;
    }
}