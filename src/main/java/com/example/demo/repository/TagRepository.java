package com.example.demo.repository;

import com.example.demo.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <--- 新增导入
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    
    // 1. 根据标签名查找
    Optional<Tag> findByName(String name);
    
    // 2. 获取热门文章数最多的前 10 个标签
    // ❌ 旧代码 (报错): List<Tag> findTop10ByOrderByArticlesSizeDesc();
    
    // ✅ 新代码: 使用 @Query 和 JPQL 的 SIZE() 函数
    // SIZE(t.articles) 会计算关联集合的大小
    @Query("SELECT t FROM Tag t ORDER BY SIZE(t.articles) DESC")
    List<Tag> findTop10ByOrderByArticlesCountDesc();
}