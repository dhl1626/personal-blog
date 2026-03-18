package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.entity.Comment;
import com.example.demo.entity.User;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 保存评论
     */
    @Transactional
    public Comment saveComment(Long articleId, String content, String username) {
        // 查询文章
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 创建评论
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateTime(new Date());
        comment.setArticle(article);
        comment.setUser(user);

        return commentRepository.save(comment);
    }

    /**
     * 根据文章 ID 获取评论列表
     */
    public List<Comment> getCommentsByArticle(Long articleId) {
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null) {
            return List.of();
        }
        return commentRepository.findByArticleOrderByCreateTimeAsc(article);
    }

    /**
     * 根据 ID 获取评论
     */
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }
}
