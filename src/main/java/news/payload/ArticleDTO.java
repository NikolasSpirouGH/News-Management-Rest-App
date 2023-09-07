package news.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import news.entity.ArticleStatus;

import java.time.LocalDate;
import java.util.List;

@Data
public class ArticleDTO {

    private Long articleId;

    @NotBlank(message = "Article name should not be blank")
    private String name;

    @NotBlank(message = "Article content should not be blank")
    private String content;

    @NotEmpty(message = "Article topics should not be empty")
    private List<TopicDTO> topics;

    private List<CommentDTO> comments;

    private LocalDate createdAt;

    private LocalDate publishedAt;

    private String rejectionReason;

    private ArticleStatus articleStatus;
}
