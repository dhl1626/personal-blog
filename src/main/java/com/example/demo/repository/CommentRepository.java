package com.example.demo.repository;

import com.example.demo.entity.Article;
import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 根据文章查询评论列表（按创建时间排序）
    List<Comment> findByArticleOrderByCreateTimeAsc(Article article);
}
