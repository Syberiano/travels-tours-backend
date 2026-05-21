package com.travels.backend.service;

import com.travels.backend.dto.BlogDTO;
import com.travels.backend.dto.BlogRequestDTO;
import com.travels.backend.exception.InvalidOperationException;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.model.*;
import com.travels.backend.repository.BlogRepository;
import com.travels.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserService userService;

    public Blog createBlog(User author, BlogRequestDTO dto) {
        log.info("Creando blog de autor: {}", author.getId());

        if (author.getRole() != UserRole.ASESOR && author.getRole() != UserRole.ADMIN) {
            throw new InvalidOperationException("Solo los asesores y admins pueden crear blogs");
        }

        Blog blog = Blog.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .featuredImage(dto.getFeaturedImage())
                .author(author)
                .status(ContentStatus.PENDING)
                .viewCount(0L)
                .build();

        return blogRepository.save(blog);
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog no encontrado con ID: " + id));
    }

    /**
     * Blogs no aprobados solo los ve el autor o un ADMIN; el público anónimo solo ve APPROVED.
     */
    public boolean isVisibleToViewer(Blog blog) {
        if (blog.getStatus().equals(ContentStatus.APPROVED)) {
            return true;
        }
        var viewer = SecurityUtil.getCurrentUser();
        if (viewer == null) {
            return false;
        }
        if (viewer.getRole().equals(UserRole.ADMIN) || viewer.getRole().equals(UserRole.ASESOR)) {
            return true;
        }
        return blog.getAuthor().getId().equals(viewer.getId());
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public List<Blog> getApprovedBlogs() {
        return blogRepository.findByStatus(ContentStatus.APPROVED);
    }

    public List<Blog> getPendingBlogs() {
        return blogRepository.findByStatus(ContentStatus.PENDING);
    }

    public List<Blog> getBlogsByAuthor(Long authorId) {
        return blogRepository.findByAuthorId(authorId);
    }

    public Blog updateBlog(Long id, BlogRequestDTO dto, User author) {
        log.info("Actualizando blog con ID: {}", id);

        Blog blog = getBlogById(id);

        if (!blog.getAuthor().getId().equals(author.getId())) {
            throw new InvalidOperationException("Solo el autor puede editar este blog");
        }

        ContentStatus status = blog.getStatus();
        if (!status.equals(ContentStatus.PENDING)
                && !status.equals(ContentStatus.REJECTED)
                && !status.equals(ContentStatus.APPROVED)) {
            throw new InvalidOperationException(
                    "No se puede editar este blog en su estado actual");
        }

        blog.setTitle(dto.getTitle());
        blog.setContent(dto.getContent());
        blog.setFeaturedImage(dto.getFeaturedImage());
        blog.setStatus(ContentStatus.PENDING);
        blog.setApprovedAt(null);

        return blogRepository.save(blog);
    }

    public Blog approveBlog(Long id) {
        log.info("Aprobando blog con ID: {}", id);

        Blog blog = getBlogById(id);

        if (!blog.getStatus().equals(ContentStatus.PENDING)) {
            throw new InvalidOperationException("Solo se pueden aprobar blogs en estado PENDING");
        }

        blog.setStatus(ContentStatus.APPROVED);
        blog.setApprovedAt(LocalDateTime.now());

        return blogRepository.save(blog);
    }

    public Blog rejectBlog(Long id) {
        log.info("Rechazando blog con ID: {}", id);

        Blog blog = getBlogById(id);

        if (!blog.getStatus().equals(ContentStatus.PENDING)) {
            throw new InvalidOperationException("Solo se pueden rechazar blogs en estado PENDING");
        }

        blog.setStatus(ContentStatus.REJECTED);

        return blogRepository.save(blog);
    }

    public void deleteBlog(Long id, User actor) {
        log.info("Eliminando blog con ID: {}", id);
        Blog blog = getBlogById(id);
        if (actor.getRole() == UserRole.ASESOR
                && !blog.getAuthor().getId().equals(actor.getId())) {
            throw new InvalidOperationException("Solo puedes eliminar tus propios blogs");
        }
        blogRepository.deleteById(id);
    }

    public Blog incrementViewCount(Long id) {
        Blog blog = getBlogById(id);
        blog.setViewCount(blog.getViewCount() + 1);
        return blogRepository.save(blog);
    }

    public BlogDTO convertToDTO(Blog blog) {
        return BlogDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .featuredImage(blog.getFeaturedImage())
                .author(userService.convertToDTO(blog.getAuthor()))
                .status(blog.getStatus())
                .viewCount(blog.getViewCount())
                .build();
    }

    public List<BlogDTO> convertToDTO(List<Blog> blogs) {
        return blogs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
