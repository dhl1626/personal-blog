package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.entity.User; // 1. 新增：需要 User 实体类
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.UserRepository; // 2. 新增：需要 UserRepository

import org.springframework.security.core.Authentication; // 3. 修正：Authentication 类
import org.springframework.security.core.context.SecurityContextHolder; // 4. 修正：SecurityContextHolder 路径
import org.springframework.stereotype.Controller; // 5. 修正：是 Controller 不是 Controler
import org.springframework.ui.Model; // 6. 修正：是 ui 不是 ui (原代码拼写看起来对，但确认一下包)
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // 7. 修正：servlet 不是 servlet (原代码拼写错误)

import java.util.List;

@Controller // 注意：这里必须是 @Controller，不是 @RestController (因为你要返回视图名称)
public class BlogController {

    // 注入 ArticleRepository
    private final ArticleRepository articleRepository;
    // 8. 新增：注入 UserRepository，否则下面代码找不到 userRepository
    private final UserRepository userRepository;

    // 构造器注入
    public BlogController(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    // 列表页
    @GetMapping("/articles")
    public String articleList(Model model) {
        List<Article> articles = articleRepository.findAll();
        model.addAttribute("articleList", articles);
        return "article-list";
    }

    // 详情页
    @GetMapping("/article/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {
        // findById 返回 Optional，建议处理不存在的情况
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        model.addAttribute("article", article);
        return "article-detail";
    }

    // 显示表单
    @GetMapping("/article/form")
    public String showForm(Model model) {
        model.addAttribute("article", new Article());
        return "article-form";
    }

    // 保存文章
    @PostMapping("/article/save")
    public String saveArticle(
            @RequestParam String title,
            @RequestParam String content,
            RedirectAttributes redirectAttrs) { // 类型名修正为 RedirectAttributes

        // 【核心】从 SecurityContext 获取当前登录用户
        // 修正：SecurityContextHolder 位置对了，Authentication 也导入了
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // 防御性编程：防止未登录用户访问（auth 可能为 null 或 anonymous）
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login"; // 没登录跳回登录页
        }

        String currentUsername = auth.getName();

        // 查询当前用户
        // 修正：现在 userRepository 已经注入，可以使用了
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
}