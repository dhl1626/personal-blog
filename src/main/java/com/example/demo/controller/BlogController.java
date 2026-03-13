package com.example.demo.controller;

import com.example.demo.entity.Article;
// import com.example.demo.entity.User; // 需要 User 实体类时取消注释
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.UserRepository; // 需要 UserRepository 时取消注释

// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller // 注意：必须使用 @Controller 以返回视图名称，而非 @RestController
public class BlogController {

    // 注入 ArticleRepository
    private final ArticleRepository articleRepository;
    
    // 注入 UserRepository (当前被注释，需配合下方 saveArticle 逻辑使用)
    // private final UserRepository userRepository;

    // 构造器注入
    public BlogController(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        // this.userRepository = userRepository;
    }

    // 1. 列表页
    @GetMapping("/articles")
    public String articleList(Model model) {
        List<Article> articles = articleRepository.findAll();
        model.addAttribute("articleList", articles);
        return "article-list";
    }

    // 2. 详情页
    @GetMapping("/article/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {
        // findById 返回 Optional，处理不存在的情况
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        model.addAttribute("article", article);
        return "article-detail";
    }

    // 3. 显示表单页
    @GetMapping("/article/blog-form")
    public String showForm(Model model) {
        model.addAttribute("article", new Article());
        return "article-form";
    }

    /* 
     * 以下为保存文章的逻辑（当前被注释）
     * 若需启用，请取消本块代码及顶部相关 import 和字段注入的注释
     */
    /*
    // 保存文章
    @PostMapping("/article/save")
    public String saveArticle(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String tagNames,
            RedirectAttributes redirectAttrs) {

        // 【核心】从 SecurityContext 获取当前登录用户
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 防御性编程：防止未登录用户访问
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login"; // 没登录跳回登录页
        }

        String currentUsername = auth.getName();

        // 查询当前用户
        User author = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + currentUsername));

        // 创建文章
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);

        // 假设你的 Article 实体里有 setAuthor(String nickname) 和 setAuthorUser(User user)
        // 请确保 Article.java 中确实有这两个 setter 方法
        article.setAuthor(author.getNickname());
        article.setAuthorUser(author);

        articleRepository.save(article);

        redirectAttrs.addFlashAttribute("successMsg", "文章发布成功！");
        return "redirect:/articles";
    }
    */
}