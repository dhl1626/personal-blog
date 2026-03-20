package com.example.demo.controller;

import com.example.demo.dto.AdminDashboardDTO;
import com.example.demo.entity.Article;
import com.example.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理 REST API 控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired
    private ArticleService articleService;

    // ==================== 数据看板 API ====================

    /**
     * 获取看板统计数据
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> getDashboard() {
        AdminDashboardDTO dto = articleService.getDashboardData();
        return ResponseEntity.ok(dto);
    }

    // ==================== 文章管理 API ====================

    /**
     * 获取所有文章（含排序）
     */
    @GetMapping("/articles")
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleService.getAllArticlesWithSort();
        return ResponseEntity.ok(articles);
    }

    /**
     * 批量删除文章
     */
    @PostMapping("/articles/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDeleteArticles(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            articleService.batchDeleteArticles(ids);
            response.put("success", true);
            response.put("message", "成功删除 " + ids.size() + " 篇文章");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 批量发布文章
     */
    @PostMapping("/articles/batch-publish")
    public ResponseEntity<Map<String, Object>> batchPublish(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            articleService.batchPublish(ids);
            response.put("success", true);
            response.put("message", "成功发布 " + ids.size() + " 篇文章");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发布失败：" + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 批量下架文章
     */
    @PostMapping("/articles/batch-archive")
    public ResponseEntity<Map<String, Object>> batchArchive(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            articleService.batchArchive(ids);
            response.put("success", true);
            response.put("message", "成功下架 " + ids.size() + " 篇文章");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "下架失败：" + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 更新文章排序
     */
    @PostMapping("/articles/update-sort")
    public ResponseEntity<Map<String, Object>> updateSortOrder(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            articleService.updateSortOrder(ids);
            response.put("success", true);
            response.put("message", "排序已更新");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新排序失败：" + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 单篇文章状态切换
     */
    @PostMapping("/articles/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleArticleStatus(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Article article = articleService.findById(id);
            if (article == null) {
                response.put("success", false);
                response.put("message", "文章不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            if ("published".equals(article.getStatus())) {
                article.setStatus("archived");
                response.put("message", "文章已下架");
            } else {
                article.setStatus("published");
                response.put("message", "文章已发布");
            }
            
            // 保存更改
            articleService.updateArticle(
                id, 
                article.getTitle(), 
                article.getMarkdownContent(), 
                article.getCategory() != null ? article.getCategory().getId() : null,
                article.getTags().stream()
                    .map(t -> t.getName())
                    .collect(java.util.stream.Collectors.joining(","))
            );
            
            response.put("success", true);
            response.put("status", article.getStatus());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "操作失败：" + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
