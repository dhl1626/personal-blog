package com.example.demo.entity; // 改成你的包名

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.util.MarkdownUtils;

@Entity
@Table(name = "articles") // 可选：指定表名，默认为类名小
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100) // 非空，长度100
    private String title;
    
    @Column(columnDefinition = "TEXT") // 大文本
    private String content;
    
    @Column(nullable = false, length = 50)
    private String author;

    // 【新增】原始 Markdown 内容
    private String M_content;

    // 【新增】渲染后的 HTML 内容 (可以是大文本类型 TEXT/LONGTEXT)
    private String htmlContent; 

    // ✅ 新增：无参构造函数（必须！）
    public Article() {}

    public Article(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // 构造方法（IDEA：右键 → Generate → Constructor）
    public Article(Long id, String title, String content, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // Getter（IDEA：右键 → Generate → Getter）
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }

    // ===== Setter（新增！必须添加）=====
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthor(String author) { this.author = author; }

    // 安全认证
    @ManyToOne
    @JoinColumn(name = "user_id") // 数据库外键字段名
    private User authorUser; // 关联的用户

    // 生成getter/setter
    public User getAuthorUser() { return authorUser; }
    public void setAuthorUser(User authorUser) { this.authorUser = authorUser; }

    private Date createTime;

    public Date getCreateTime() { // 方法名必须是 get + 首字母大写
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    // 生成 Markdown的Getter 和 Setter (或者用 @Data)
    public String getMContent() { return M_content; }
    public void setMContent(String M_content) { 
        this.M_content = M_content;
        // 保存时自动转换 HTML
        this.htmlContent = MarkdownUtils.markdownToHtml(M_content);
    }
    
    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }

    // 多对多关联标签
    // cascade = CascadeType.PERSIST: 保存文章时，如果标签是新的，自动保存标签
    // 注意：通常建议先手动管理标签的查找或创建，这里简化处理
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "article_tag",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    // Getter, Setter...
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getArticles().add(this);
    }
    
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getArticles().remove(this);
    }

    // ---  新增：分类字段   ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // 对应数据库的外键列名
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}