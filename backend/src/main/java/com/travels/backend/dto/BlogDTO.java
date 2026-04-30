package com.travels.backend.dto;

import com.travels.backend.model.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogDTO {

    private Long id;

    private String title;

    private String content;

    private String featuredImage;

    private UserDTO author;

    private ContentStatus status;

    private Long viewCount;
}
