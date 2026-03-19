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

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

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
}
