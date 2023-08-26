package news.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import news.entity.CommentStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class CommentResponse{

    private Long commentId;

    private String commentText;

    private LocalDate createdDate;

    private String commentAuthorName;

    private CommentStatus commentStatus;
}
