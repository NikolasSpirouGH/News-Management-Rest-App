package com.news.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="topic_id")
    private Long topicId;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDate creationDate;

    @Column(name = "name",unique = true)
    private String name;

    @Column(name = "parent_name")
    private String parentName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopicStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "topics", cascade = CascadeType.ALL)
    private List<Article> articles;

}