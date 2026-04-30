package com.travels.backend.repository;

import com.travels.backend.model.Blog;
import com.travels.backend.model.ContentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByStatus(ContentStatus status);
    List<Blog> findByAuthorId(Long authorId);
    List<Blog> findByStatusAndAuthorId(ContentStatus status, Long authorId);
}
