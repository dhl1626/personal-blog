package com.example.demo.repository;

import com.example.demo.entity.Article;
import com.example.demo.entity.Category;
import com.example.demo.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// JpaRepository<实体类，主键类型> → 自动获得 CRUD 方法！
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 无需写任何代码！Spring Data JPA 自动生成实现
    // 后续可扩展：List<Article> findByAuthor(String author);
     // 根据分类查找文章
    List<Article> findByCategoryOrderByCreateTimeDesc(Category category);

    // 根据标签查找文章 (因为是多对多，JPA 通常能自动推导，或者手动写 JPQL)
    List<Article> findByTagsContainingOrderByCreateTimeDesc(Tag tag);

    // 【新增】根据作者名查询文章，并按创建时间倒序排列（最新的在前面）
    // Spring Data JPA 会自动解析这个方法名并生成 SQL:
    // SELECT * FROM article WHERE author = ? ORDER BY create_time DESC
    List<Article> findByAuthorOrderByCreateTimeDesc(String author);

    // ==================== 搜索功能 ====================

    /**
     * 综合搜索：支持按标题、作者、分类、标签搜索
     * 使用 JPQL 进行模糊查询
     */
    @Query("SELECT DISTINCT a FROM Article a " +
           "LEFT JOIN a.category c " +
           "LEFT JOIN a.tags t " +
           "WHERE (:keyword IS NULL OR :keyword = '' " +
           "       OR a.title LIKE %:keyword% " +
           "       OR a.author LIKE %:keyword% " +
           "       OR a.content LIKE %:keyword%) " +
           "  AND (:categoryId IS NULL OR c.id = :categoryId) " +
           "  AND (:tagName IS NULL OR t.name = :tagName) " +
           "ORDER BY a.createTime DESC")
    List<Article> searchArticles(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("tagName") String tagName
    );

    /**
     * 按标题搜索
     */
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% ORDER BY a.createTime DESC")
    List<Article> findByTitleContaining(@Param("keyword") String keyword);

    /**
     * 按作者搜索
     */
    @Query("SELECT a FROM Article a WHERE a.author LIKE %:keyword% ORDER BY a.createTime DESC")
    List<Article> findByAuthorContaining(@Param("keyword") String keyword);

    /**
     * 按分类名称搜索
     */
    @Query("SELECT a FROM Article a JOIN a.category c WHERE c.name = :categoryName ORDER BY a.createTime DESC")
    List<Article> findByCategoryName(@Param("categoryName") String categoryName);

    /**
     * 按标签名称搜索
     */
    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.name = :tagName ORDER BY a.createTime DESC")
    List<Article> findByTagName(@Param("tagName") String tagName);
    
    // ==================== 统计功能 ====================
    
    /**
     * 统计文章总数
     */
    @Query("SELECT COUNT(a) FROM Article a")
    Long countTotalArticles();
    
    /**
     * 统计总访问量（需要先在 Article 实体中添加 views 字段）
     */
    @Query("SELECT COALESCE(SUM(a.views), 0) FROM Article a")
    Long countTotalViews();
    
    /**
     * 查询最近 N 篇文章
     */
    @Query("SELECT a FROM Article a ORDER BY a.createTime DESC")
    List<Article> findRecentArticles(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 查询最热门的 N 篇文章（按访问量）
     */
    @Query("SELECT a FROM Article a ORDER BY a.views DESC")
    List<Article> findPopularArticles(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 统计每个分类的文章数量
     */
    @Query("SELECT new com.example.demo.dto.AdminDashboardDTO$CategoryStat(c.id, c.name, COUNT(a.id)) " +
           "FROM Category c LEFT JOIN Article a ON c.id = a.category.id GROUP BY c.id, c.name")
    List<com.example.demo.dto.AdminDashboardDTO.CategoryStat> countArticlesByCategory();
    
    /**
     * 统计每个标签的文章数量
     */
    @Query("SELECT new com.example.demo.dto.AdminDashboardDTO$TagStat(t.id, t.name, CAST(SIZE(t.articles) AS long)) " +
           "FROM Tag t ORDER BY SIZE(t.articles) DESC")
    List<com.example.demo.dto.AdminDashboardDTO.TagStat> countArticlesByTag();
}
