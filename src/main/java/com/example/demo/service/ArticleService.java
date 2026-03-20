package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.entity.Category;
import com.example.demo.entity.Tag;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.Optional;
import java.text.SimpleDateFormat;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.demo.dto.AdminDashboardDTO;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Transactional
    public Article createArticle(String title, String markdownContent,
                                 Long categoryId, String tagNamesStr, String authorName) {

        Article article = new Article();
        article.setTitle(title);
        article.setMarkdownContent(markdownContent);
        article.setAuthor(authorName);
        article.setCreateTime(new Date());

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在，ID: " + categoryId));
            article.setCategory(category);
        } else {
             throw new RuntimeException("请选择文章分类");
        }

        List<Tag> finalTags = new ArrayList<>();

        if (tagNamesStr != null && !tagNamesStr.trim().isEmpty()) {
            String[] names = tagNamesStr.split("[,，]");

            for (String name : names) {
                String trimmedName = name.trim();
                if (trimmedName.isEmpty()) continue;

                Tag tag = tagRepository.findByName(trimmedName).orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(trimmedName);
                    return newTag;
                });

                finalTags.add(tag);
            }
        }
        article.setTags(finalTags);

        return articleRepository.save(article);
    }

    @Transactional
    public Article updateArticle(Long id, String title, String markdownContent,
                                 @Nullable Long categoryId, String tagNamesStr) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("文章不存在"));

        article.setTitle(title);
        article.setMarkdownContent(markdownContent);

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        }

        List<Tag> finalTags = new ArrayList<>();
        if (tagNamesStr != null && !tagNamesStr.trim().isEmpty()) {
            String[] names = tagNamesStr.split("[,，]");
            for (String name : names) {
                String trimmedName = name.trim();
                if (trimmedName.isEmpty()) continue;

                Tag tag = tagRepository.findByName(trimmedName).orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(trimmedName);
                    return newTag;
                });
                finalTags.add(tag);
            }
        }
        article.setTags(finalTags);

        return articleRepository.save(article);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Tag> getHotTags(int limit) {
        List<Tag> all = tagRepository.findTop10ByOrderByArticlesCountDesc();
        return limit > 0 ? all.stream().limit(limit).collect(Collectors.toList()) : all;
    }

    public List<Article> getArticlesByCategory(@Nullable Long categoryId) {
        Category cat = categoryRepository.findById(categoryId).orElse(null);
        if (cat == null) return new ArrayList<>();
        return articleRepository.findByCategoryOrderByCreateTimeDesc(cat);
    }

    public List<Article> getArticlesByTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName).orElse(null);
        if (tag == null) return new ArrayList<>();
        return articleRepository.findByTagsContainingOrderByCreateTimeDesc(tag);
    }

    public List<Article> getArticlesByAuthor(String username) {
        if (username == null || username.isEmpty()) {
            return List.of();
        }
        return articleRepository.findByAuthorOrderByCreateTimeDesc(username);
    }

    public boolean deleteArticle(@Nullable Long id, String currentUsername) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        if (articleOpt.isEmpty()) {
            throw new RuntimeException("文章不存在");
        }

        Article article = articleOpt.get();

        if (!article.getAuthor().equals(currentUsername)) {
            throw new RuntimeException("无权删除他人的文章！");
        }
        articleRepository.delete(article);
        return true;
    }

    public Article findById(@Nullable Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    // ==================== 搜索功能 ====================

    /**
     * 综合搜索文章
     * @param keyword 搜索关键词（标题、作者、内容）
     * @param categoryId 分类 ID（可选）
     * @param tagName 标签名称（可选）
     * @return 搜索结果的文章列表
     */
    public List<Article> searchArticles(
            @Nullable String keyword,
            @Nullable Long categoryId,
            @Nullable String tagName) {
        
        // 处理空字符串为 null
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        if (tagName != null && tagName.trim().isEmpty()) {
            tagName = null;
        }
        
        return articleRepository.searchArticles(keyword, categoryId, tagName);
    }

    /**
     * 按标题搜索
     */
    public List<Article> searchByTitle(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllArticles();
        }
        return articleRepository.findByTitleContaining(keyword);
    }

    /**
     * 按作者搜索
     */
    public List<Article> searchByAuthor(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllArticles();
        }
        return articleRepository.findByAuthorContaining(keyword);
    }

    /**
     * 按分类名称搜索
     */
    public List<Article> searchByCategoryName(String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            return getAllArticles();
        }
        return articleRepository.findByCategoryName(categoryName);
    }

    /**
     * 按标签名称搜索
     */
    public List<Article> searchByTagName(String tagName) {
        if (!StringUtils.hasText(tagName)) {
            return getAllArticles();
        }
        return articleRepository.findByTagName(tagName);
    }
    
    // ==================== 后台管理功能 ====================
    
    /**
     * 获取管理看板数据
     */
    public AdminDashboardDTO getDashboardData() {
        AdminDashboardDTO dto = new AdminDashboardDTO();
        
        // 统计总数
        dto.setTotalArticles(articleRepository.countTotalArticles());
        dto.setTotalViews(articleRepository.countTotalViews());
        dto.setTotalComments(commentRepository.count());
        dto.setTotalUsers(userRepository.count());
        
        // 最近文章
        Pageable pageable = PageRequest.of(0, 10);
        List<Article> recentArticles = articleRepository.findRecentArticles(pageable);
        dto.setRecentArticles(convertToArticleInfo(recentArticles));
        
        // 热门文章
        List<Article> popularArticles = articleRepository.findPopularArticles(pageable);
        dto.setPopularArticles(convertToArticleInfo(popularArticles));
        
        // 分类统计
        dto.setCategoryStats(articleRepository.countArticlesByCategory());
        
        // 标签统计
        dto.setTagStats(articleRepository.countArticlesByTag());
        
        // 访问趋势（模拟数据，实际项目可记录访问日志）
        dto.setViewTrend(generateViewTrend());
        
        return dto;
    }
    
    /**
     * 转换文章列表为 ArticleInfo
     */
    private List<AdminDashboardDTO.ArticleInfo> convertToArticleInfo(List<Article> articles) {
        List<AdminDashboardDTO.ArticleInfo> result = new ArrayList<>();
        for (Article article : articles) {
            AdminDashboardDTO.ArticleInfo info = new AdminDashboardDTO.ArticleInfo();
            info.setId(article.getId());
            info.setTitle(article.getTitle());
            info.setAuthor(article.getAuthor());
            info.setViews(article.getViews() != null ? article.getViews() : 0);
            info.setComments(article.getComments() != null ? article.getComments().size() : 0);
            info.setCreateTime(dateFormat.format(article.getCreateTime()));
            info.setStatus(article.getStatus() != null ? article.getStatus() : "published");
            result.add(info);
        }
        return result;
    }
    
    /**
     * 生成访问趋势数据（最近 7 天）
     */
    private List<java.util.Map<String, Object>> generateViewTrend() {
        List<java.util.Map<String, Object>> trend = new ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Random random = new java.util.Random();
        
        for (int i = 6; i >= 0; i--) {
            cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("date", new SimpleDateFormat("MM-dd").format(cal.getTime()));
            // 模拟数据：基础值 + 随机波动
            data.put("views", 50 + random.nextInt(100));
            trend.add(data);
        }
        return trend;
    }
    
    /**
     * 批量删除文章
     */
    @Transactional
    public void batchDeleteArticles(List<Long> ids) {
        articleRepository.deleteAllById(ids);
    }
    
    /**
     * 批量发布文章
     */
    @Transactional
    public void batchPublish(List<Long> ids) {
        for (Long id : ids) {
            Article article = articleRepository.findById(id).orElse(null);
            if (article != null) {
                article.setStatus("published");
                articleRepository.save(article);
            }
        }
    }
    
    /**
     * 批量下架文章
     */
    @Transactional
    public void batchArchive(List<Long> ids) {
        for (Long id : ids) {
            Article article = articleRepository.findById(id).orElse(null);
            if (article != null) {
                article.setStatus("archived");
                articleRepository.save(article);
            }
        }
    }
    
    /**
     * 更新文章排序
     */
    @Transactional
    public void updateSortOrder(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            Article article = articleRepository.findById(ids.get(i)).orElse(null);
            if (article != null) {
                article.setSortOrder(i);
                articleRepository.save(article);
            }
        }
    }
    
    /**
     * 获取所有文章（含分页和排序）
     */
    public List<Article> getAllArticlesWithSort() {
        List<Article> articles = articleRepository.findAll();
        articles.sort((a1, a2) -> {
            int sortCompare = Integer.compare(a1.getSortOrder() != null ? a1.getSortOrder() : 0, 
                                               a2.getSortOrder() != null ? a2.getSortOrder() : 0);
            if (sortCompare != 0) return sortCompare;
            return a2.getCreateTime().compareTo(a1.getCreateTime());
        });
        return articles;
    }
}
