package com.example.demo.dto;

import java.util.List;
import java.util.Map;

/**
 * 后台管理看板数据传输对象
 */
public class AdminDashboardDTO {
    
    // 统计信息
    private Long totalArticles;
    private Long totalViews;
    private Long totalComments;
    private Long totalUsers;
    
    // 最近文章
    private List<ArticleInfo> recentArticles;
    
    // 热门文章（按访问量）
    private List<ArticleInfo> popularArticles;
    
    // 分类统计
    private List<CategoryStat> categoryStats;
    
    // 标签统计
    private List<TagStat> tagStats;
    
    // 访问趋势（最近 7 天）
    private List<Map<String, Object>> viewTrend;
    
    public AdminDashboardDTO() {}
    
    public Long getTotalArticles() {
        return totalArticles;
    }
    
    public void setTotalArticles(Long totalArticles) {
        this.totalArticles = totalArticles;
    }
    
    public Long getTotalViews() {
        return totalViews;
    }
    
    public void setTotalViews(Long totalViews) {
        this.totalViews = totalViews;
    }
    
    public Long getTotalComments() {
        return totalComments;
    }
    
    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }
    
    public Long getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public List<ArticleInfo> getRecentArticles() {
        return recentArticles;
    }
    
    public void setRecentArticles(List<ArticleInfo> recentArticles) {
        this.recentArticles = recentArticles;
    }
    
    public List<ArticleInfo> getPopularArticles() {
        return popularArticles;
    }
    
    public void setPopularArticles(List<ArticleInfo> popularArticles) {
        this.popularArticles = popularArticles;
    }
    
    public List<CategoryStat> getCategoryStats() {
        return categoryStats;
    }
    
    public void setCategoryStats(List<CategoryStat> categoryStats) {
        this.categoryStats = categoryStats;
    }
    
    public List<TagStat> getTagStats() {
        return tagStats;
    }
    
    public void setTagStats(List<TagStat> tagStats) {
        this.tagStats = tagStats;
    }
    
    public List<Map<String, Object>> getViewTrend() {
        return viewTrend;
    }
    
    public void setViewTrend(List<Map<String, Object>> viewTrend) {
        this.viewTrend = viewTrend;
    }
    
    // 内部类：文章信息
    public static class ArticleInfo {
        private Long id;
        private String title;
        private String author;
        private Integer views;
        private Integer comments;
        private String createTime;
        private String status; // published, draft, archived
        
        public ArticleInfo() {}
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getAuthor() {
            return author;
        }
        
        public void setAuthor(String author) {
            this.author = author;
        }
        
        public Integer getViews() {
            return views;
        }
        
        public void setViews(Integer views) {
            this.views = views;
        }
        
        public Integer getComments() {
            return comments;
        }
        
        public void setComments(Integer comments) {
            this.comments = comments;
        }
        
        public String getCreateTime() {
            return createTime;
        }
        
        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
    
    // 内部类：分类统计
    public static class CategoryStat {
        private Long id;
        private String name;
        private Long articleCount;
        
        public CategoryStat() {}
        
        public CategoryStat(Long id, String name, Long articleCount) {
            this.id = id;
            this.name = name;
            this.articleCount = articleCount;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Long getArticleCount() {
            return articleCount;
        }
        
        public void setArticleCount(Long articleCount) {
            this.articleCount = articleCount;
        }
    }
    
    // 内部类：标签统计
    public static class TagStat {
        private Long id;
        private String name;
        private Long articleCount;
        
        public TagStat() {}
        
        public TagStat(Long id, String name, Long articleCount) {
            this.id = id;
            this.name = name;
            this.articleCount = articleCount;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Long getArticleCount() {
            return articleCount;
        }
        
        public void setArticleCount(Long articleCount) {
            this.articleCount = articleCount;
        }
    }
}
