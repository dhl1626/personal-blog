package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.entity.Category;
import com.example.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // 1. 显示“写文章”或“编辑文章”页面
    @GetMapping("/form") // 新建文章的路径
    public String showEditForm(Model model) {
        // 准备分类列表给下拉框
        List<Category> categories = articleService.getAllCategories();
        model.addAttribute("categories", categories);

        // 如果是编辑，需要根据 ID 加载 article (此处省略加载逻辑，假设是新建)
        model.addAttribute("article", new Article());

        return "article-form"; // 对应 article-form.html
    }

    // 如果是编辑，路径可能是 /article/edit/{id}
    @GetMapping("/form/{id}")
    public String showEditFormWithId(@PathVariable Long id, Model model) {
        List<Category> categories = articleService.getAllCategories();
        model.addAttribute("categories", categories);

        // 这里需要从 Service 获取文章详情，假设 service 有 findById
        // Article article = articleService.findById(id); 
        // model.addAttribute("article", article);

        return "article-form";
    }

    // 2. 处理表单提交 (保存/更新)
    @PostMapping("/save")
    public String saveArticle(
            @RequestParam String title,
            @RequestParam String content,       // Markdown 内容
            @RequestParam Long categoryId,      // 分类 ID
            @RequestParam(required = false) String tagNames, // 标签字符串，允许为空
            Authentication authentication,      // 获取当前登录用户
            @RequestParam(required = false) Long id         // 如果有 ID 则是更新，否则是新建
    ) {
        String username = authentication.getName();

        if (id != null && id > 0) {
            // 更新逻辑
            articleService.updateArticle(id, title, content, categoryId, tagNames);
        } else {
            // 新建逻辑
            articleService.createArticle(title, content, categoryId, tagNames, username);
        }

        return "redirect:/articles"; // 保存成功后跳转到列表页
    }

    // 3. 文章列表页 (支持按分类或标签筛选)
    @GetMapping("/list") // 或者映射到根路径 /
    public String listArticles(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            Model model) {

        List<Article> articles;

        if (categoryId != null) {
            articles = articleService.getArticlesByCategory(categoryId);
            model.addAttribute("activeCategory", categoryId);
        } else if (tag != null) {
            articles = articleService.getArticlesByTag(tag);
            model.addAttribute("activeTag", tag);
        } else {
            // 获取所有文章 (按时间倒序)，需要在 Service 加一个 findAllOrderByTimeDesc
            articles = articleService.getAllArticles(); // 需自行补充此方法
        }

        // 同时把分类列表和标签云数据也传给页面，方便侧边栏展示
        model.addAttribute("categories", articleService.getAllCategories());
        model.addAttribute("hotTags", articleService.getHotTags(10));
        model.addAttribute("articles", articles);

        return "article-list"; // 对应列表页模板
    }

    // 4. 标签云页面
    @GetMapping("/tags")
    public String showTagsCloud(Model model) {
        model.addAttribute("hotTags", articleService.getHotTags(50)); // 获取多一点的标签
        return "tags-cloud";
    }
}