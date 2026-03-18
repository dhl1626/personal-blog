package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.entity.Category;
import com.example.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // ================= 1. 显示表单页面 (新建 vs 编辑) =================

    /**
     * 【新建文章】页面
     * 路径: GET /article/form
     * 只有这一个方法映射到 /form
     */
    @GetMapping("/form")
    public String showCreateForm(Model model, Authentication authentication) {
        // 安全检查
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        List<Category> categories = articleService.getAllCategories();
        model.addAttribute("categories", categories);

        // 新建空对象
        Article newArticle = new Article();
        newArticle.setAuthor(authentication.getName());
        model.addAttribute("article", newArticle);
        model.addAttribute("isEdit", false); // 标记为新建模式

        return "article-form";
    }

    /**
     * 【编辑文章】页面
     * 路径: GET /article/edit/{id}  <-- 注意路径变了，不再是 /form
     * 使用 @PathVariable 获取 ID
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        // 安全检查
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String currentUsername = authentication.getName();

        // 1. 获取文章
        Article article = articleService.findById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在 (ID: " + id + ")");
        }

        // 2. 权限校验：只能编辑自己的文章
        if (!article.getAuthor().equals(currentUsername)) {
            throw new RuntimeException("无权编辑他人的文章！");
        }

        // 3. 准备数据
        List<Category> categories = articleService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("article", article); // 放入旧数据
        model.addAttribute("isEdit", true);     // 标记为编辑模式

        return "article-form"; // 返回同一个模板文件
    }

    // ================= 2. 处理表单提交 (保存/更新) =================

    @PostMapping("/save")
    public String saveArticle(
            @RequestParam(required = false) Long id, // 如果有 ID 则是更新
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long categoryId,
            @RequestParam(required = false) String tagNames,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();

        if (id != null && id > 0) {
            // === 更新逻辑 ===
            // 双重校验权限
            Article existingArticle = articleService.findById(id);
            if (existingArticle == null || !existingArticle.getAuthor().equals(username)) {
                throw new RuntimeException("无权更新该文章或文章不存在");
            }
            articleService.updateArticle(id, title, content, categoryId, tagNames);
        } else {
            // === 新建逻辑 ===
            articleService.createArticle(title, content, categoryId, tagNames, username);
        }

        return "redirect:/article/list?msg=saved";
    }

    // ================= 3. 文章列表页 =================

    @GetMapping("/list")
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
            articles = articleService.getAllArticles();
        }

        model.addAttribute("categories", articleService.getAllCategories());
        // model.addAttribute("hotTags", articleService.getHotTags(10)); // 确保 Service 有此方法
        model.addAttribute("articles", articles);

        return "article-list";
    }

    // 根路径跳转
    @GetMapping("")
    public String index() {
        return "redirect:/article/list";
    }

    // ================= 4. 删除文章 =================

    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String currentUsername = authentication.getName();
        try {
            articleService.deleteArticle(id, currentUsername);
            return "redirect:/article/list?msg=deleted";
        } catch (RuntimeException e) {
            System.out.println("删除失败: " + e.getMessage());
            return "redirect:/article/list?error=forbidden";
        }
    }
    
    // ================= 5. 标签云 (可选) =================
    @GetMapping("/tags")
    public String showTagsCloud(Model model) {
        model.addAttribute("hotTags", articleService.getHotTags(50));
        return "tags-cloud";
    }
}