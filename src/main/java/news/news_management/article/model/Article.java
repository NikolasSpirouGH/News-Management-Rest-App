package news.news_management.article.model;

import news.news_management.comment.model.Comment;
import news.news_management.topic.model.Topic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="articles")
public class Article {


    @Id
    @SequenceGenerator(
            name = "article_generator",
            sequenceName = "article_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "article_generator"
    )
    private Long articleId;

    @Column(name="name")
    private String name;

    @Column(name="content")
    private String content;

    @CreationTimestamp
    @Column(name="create_at",nullable = false,updatable = false)
    private LocalDate createdAt;

    @Column(name = "published_at")
    private LocalDate publishedAt; // Add the publishedAt field

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ArticleStatus status;

    private String rejectionReason;

@ManyToMany
@JoinTable(
        name = "article_topic_map",
        joinColumns = @JoinColumn(
                name = "article_id",
                referencedColumnName = "articleId"
        ),
        inverseJoinColumns = @JoinColumn(
                name="topic_id",
                referencedColumnName = "topicId"

        )
)
    private List<Topic> topics;

@OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setPublishedAt(LocalDate publishedAt) {
        this.publishedAt = publishedAt;
    }
}
