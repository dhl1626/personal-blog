package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.repository.ArticleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BlogController {
    
    // 【核心】通过依赖注入获取Repository（Spring自动创建实例）
    private final ArticleRepository articleRepository;
    
    // 构造器注入（Spring Boot 3推荐方式）
    public BlogController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // 列表页：从数据库查所有文章
    @GetMapping("/articles")
    public String articleList(Model model) {
        List<Article> articles = articleRepository.findAll(); // JPA自带方法
        model.addAttribute("articleList", articles);
        return "article-list";
    }

    // 详情页
    @GetMapping("/article/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) return "redirect:/articles";
        model.addAttribute("article", article);
        return "article-detail";
    }

    // 显示表单
    @GetMapping("/article/form")
    public String showForm(Model model) {
        model.addAttribute("article", new Article());
        return "article-form";
    }

    // 保存文章（关键：不手动设ID！）
    @PostMapping("/article/save")
    public String saveArticle(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "匿名") String author) {
        
        // 直接new对象，ID由数据库自增生成
        Article article = new Article(title, content, author);
        articleRepository.save(article); // 保存到数据库
        
        return "redirect:/articles";
    }
}