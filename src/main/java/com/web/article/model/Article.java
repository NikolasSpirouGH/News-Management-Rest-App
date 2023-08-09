package com.web.article.model;

import com.web.topic.model.Topic;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@ToString
@Entity
@NoArgsConstructor
@Table(name="articles")
public class Article {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @NotBlank(message="Name should not be null")
    @Column(name="name")
    private String name;

    @Column(name="content")
    private String content;

    @CreationTimestamp
    @Column(name="create_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ArticleStatus status;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Topic> topics = new ArrayList<>();

}
