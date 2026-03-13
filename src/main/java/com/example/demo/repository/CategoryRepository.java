package com.example.demo.repository;

import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 根据名称查找分类 (可选，如果前端是用 ID 选择的，这个可能用不到)
    Optional<Category> findByName(String name);
}