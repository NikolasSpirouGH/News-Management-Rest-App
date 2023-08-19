package com.web.article.model;

import com.web.topic.model.Topic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt; // Add the publishedAt field

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ArticleStatus status;

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

    public void setRejectionReason(String rejectionReason) {

    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
