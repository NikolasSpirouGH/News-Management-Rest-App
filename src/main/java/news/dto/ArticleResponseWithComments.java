package news.dto;

import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class ArticleResponseWithComments {

    private String articleName;

    private Long articleId;

    private String articleContent;

    private List<CommentResponse> comments;

}
