package com.example.demo.repository;

import com.example.demo.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Category;
import com.example.demo.entity.Tag;
import java.util.List;

// JpaRepository<实体类, 主键类型> → 自动获得CRUD方法！
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 无需写任何代码！Spring Data JPA自动生成实现
    // 后续可扩展：List<Article> findByAuthor(String author);
     // 根据分类查找文章
    List<Article> findByCategoryOrderByCreateTimeDesc(Category category);
    
    // 根据标签查找文章 (因为是多对多，JPA 通常能自动推导，或者手动写 JPQL)
    List<Article> findByTagsContainingOrderByCreateTimeDesc(Tag tag);
}