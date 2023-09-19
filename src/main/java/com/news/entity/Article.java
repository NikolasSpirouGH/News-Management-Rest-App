package com.news.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="article_id")
    private Long articleId ;

    @Column(name="name",unique = true)
    private String name;

    @Column(name="content")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "published_at")
    private LocalDate publishedAt; // Add the publishedAt field

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ArticleStatus status;

    @Column(name = "rejection_reason")
    @ColumnDefault("'No rejection reason provided'")
    private String rejectionReason;

    @ManyToMany
    @JoinTable(
            name = "article_topic_map",
            joinColumns = @JoinColumn(
                    name = "article_id",
                    referencedColumnName = "article_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name="topic_id",
                    referencedColumnName = "topic_id"
            )
    )
    private List<Topic> topics;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
