package news.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="comments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @NotBlank(message="Comments content cant be empty")
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
            referencedColumnName = "article_id"
    )
    @JsonManagedReference
    private Article article;


}
