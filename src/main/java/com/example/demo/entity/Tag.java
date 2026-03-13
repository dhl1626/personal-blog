package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data; // 确保引入了这个

@Entity
@Table(name = "tag")
@Data
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // 多对多：通过中间表关联
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Article> articles;

    // Getter, Setter, Constructors...
    // 构造函数
    public Tag() {}
    
    public Tag(String name) {
        this.name = name;
    }

    // 必须显式提供 getArticles() 和 setArticles()
    // 如果你没用 Lombok，必须手动写下面这两个方法！
    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}