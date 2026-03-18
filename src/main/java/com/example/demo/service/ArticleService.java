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
}
