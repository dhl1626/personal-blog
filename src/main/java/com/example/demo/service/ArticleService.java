package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.entity.Category;
import com.example.demo.entity.Tag;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.util.MarkdownUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    public List<Article> getAllArticles() {
        // 建议加上排序，否则每次刷新顺序可能不同
        return articleRepository.findAll(); 
    }
    
    @Transactional
    public Article createArticle(String title, String markdownContent, 
                                 Long categoryId, String tagNamesStr, String authorName) {
        
        Article article = new Article();
        article.setTitle(title);
        article.setContent(markdownContent);
        article.setHtmlContent(MarkdownUtils.markdownToHtml(markdownContent));
        article.setAuthor(authorName); 
        article.setCreateTime(new Date()); 

        // --- 1. 处理分类 ---
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在，ID: " + categoryId));
            article.setCategory(category);
        } else {
             throw new RuntimeException("请选择文章分类");
        }

        // --- 2. 处理标签 ---
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
                                 Long categoryId, String tagNamesStr) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("文章不存在"));

        article.setTitle(title);
        article.setContent(markdownContent);
        article.setHtmlContent(MarkdownUtils.markdownToHtml(markdownContent));

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
        // return categoryRepository.findAll();
        List<Category> list = categoryRepository.findAll();
        return list;
    }

    // --- 👇 这里修改了调用的方法名 👇 ---
    public List<Tag> getHotTags(int limit) {
        // 调用 Repository 中新的 @Query 方法
        List<Tag> all = tagRepository.findTop10ByOrderByArticlesCountDesc();
        return limit > 0 ? all.stream().limit(limit).collect(Collectors.toList()) : all;
    }
    // --- 👆 修改结束 👆 ---
    
    public List<Article> getArticlesByCategory(Long categoryId) {
        Category cat = categoryRepository.findById(categoryId).orElse(null);
        if (cat == null) return new ArrayList<>();
        // 确保 ArticleRepository 中有这个方法，如果没有请改用 findAll 后过滤或添加该方法
        return articleRepository.findByCategoryOrderByCreateTimeDesc(cat);
    }

    public List<Article> getArticlesByTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName).orElse(null);
        if (tag == null) return new ArrayList<>();
        // 确保 ArticleRepository 中有这个方法
        return articleRepository.findByTagsContainingOrderByCreateTimeDesc(tag);
    }
}