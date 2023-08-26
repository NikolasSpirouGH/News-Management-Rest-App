package news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class ArticleRequest {

    @NotBlank(message = "Article name should not be blank")
    private String name;

    @NotBlank(message = "Article content should not be blank")
    private String content;

    @NotEmpty(message = "Article topics should not be empty")
    private List<TopicRequest> topics;
}
