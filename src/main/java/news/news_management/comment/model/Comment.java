package news.news_management.comment.model;

import news.news_management.article.model.Article;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="comments")
public class Comment {

    @Id
    @SequenceGenerator(
            name = "comment_generator",
            sequenceName = "comment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "comment_generator"
    )
    private Long commentId;

    @Column(name="text")
    private String text;

    @Column(name="author_name")
    private String authorName;

    @CreationTimestamp
    @Column(name="create_at",nullable = false,updatable = false)
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "article_id",
            referencedColumnName = "articleId"
    )
    private Article article;

}
