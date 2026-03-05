package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; 
import java.util.Collection;
import java.util.Collections; // 用于简化列表操作

@Entity
@Table(name = "user")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username; // 登录用
    
    @Column(nullable = false)
    private String password; // 存储加密后的密码
    
    @Column(nullable = false, length = 50)
    private String nickname; // 显示用

    // 一对多：一个用户有多篇文章
    @OneToMany(mappedBy = "authorUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles = new ArrayList<>();

    // 无参构造（JPA必需）
    public User() {}
    
    // 有参构造（注册时用）
    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    // Getter/Setter（IDEA Generate）
    // ...（此处省略，务必生成完整）
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }

     @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 这里返回用户的角色/权限
        // 假设您有一个 role 字段，或者暂时返回一个默认角色 "ROLE_USER"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 账户未过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 账户未锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 凭证未过期
    }

    @Override
    public boolean isEnabled() {
        return true; // 账户可用
    }
}

