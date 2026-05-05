package com.travels.backend.controller;

import com.travels.backend.dto.BlogDTO;
import com.travels.backend.dto.BlogRequestDTO;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.service.BlogService;
import com.travels.backend.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Slf4j
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    @PreAuthorize("hasRole('ASESOR') or hasRole('ADMIN')")
    public ResponseEntity<BlogDTO> createBlog(@Valid @RequestBody BlogRequestDTO dto) {
        log.info("Creando nuevo blog");
        var author = SecurityUtil.getCurrentUser();
        var blog = blogService.createBlog(author, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(blogService.convertToDTO(blog));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogDTO> getBlogById(@PathVariable Long id) {
        log.info("Obteniendo blog con ID: {}", id);
        var blog = blogService.getBlogById(id);
        if (!blogService.isVisibleToViewer(blog)) {
            throw new ResourceNotFoundException("Blog no encontrado");
        }
        blogService.incrementViewCount(id);
        return ResponseEntity.ok(blogService.convertToDTO(blog));
    }

    @GetMapping
    public ResponseEntity<List<BlogDTO>> getAllBlogs() {
        log.info("Obteniendo todos los blogs");
        var blogs = blogService.getAllBlogs();
        return ResponseEntity.ok(blogService.convertToDTO(blogs));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<BlogDTO>> getApprovedBlogs() {
        log.info("Obteniendo blogs aprobados");
        var blogs = blogService.getApprovedBlogs();
        return ResponseEntity.ok(blogService.convertToDTO(blogs));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BlogDTO>> getPendingBlogs() {
        log.info("Obteniendo blogs pendientes");
        var blogs = blogService.getPendingBlogs();
        return ResponseEntity.ok(blogService.convertToDTO(blogs));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<BlogDTO>> getBlogsByAuthor(@PathVariable Long authorId) {
        log.info("Obteniendo blogs del autor: {}", authorId);
        var blogs = blogService.getBlogsByAuthor(authorId);
        return ResponseEntity.ok(blogService.convertToDTO(blogs));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ASESOR') or hasRole('ADMIN')")
    public ResponseEntity<BlogDTO> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody BlogRequestDTO dto) {
        log.info("Actualizando blog con ID: {}", id);
        var author = SecurityUtil.getCurrentUser();
        var blog = blogService.updateBlog(id, dto, author);
        return ResponseEntity.ok(blogService.convertToDTO(blog));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BlogDTO> approveBlog(@PathVariable Long id) {
        log.info("Aprobando blog con ID: {}", id);
        var blog = blogService.approveBlog(id);
        return ResponseEntity.ok(blogService.convertToDTO(blog));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BlogDTO> rejectBlog(@PathVariable Long id) {
        log.info("Rechazando blog con ID: {}", id);
        var blog = blogService.rejectBlog(id);
        return ResponseEntity.ok(blogService.convertToDTO(blog));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        log.info("Eliminando blog con ID: {}", id);
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }
}
