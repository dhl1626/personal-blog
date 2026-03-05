package com.example.demo.repository;

import com.example.demo.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<实体类, 主键类型> → 自动获得CRUD方法！
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 无需写任何代码！Spring Data JPA自动生成实现
    // 后续可扩展：List<Article> findByAuthor(String author);
}