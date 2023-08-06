package com.web.restfulapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Setter
@Getter
@ToString
@Entity
@Table(name="Articles")
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

    @Column(name="topic")
    private String topic;

    @CreationTimestamp
    @Column(name="create_at",nullable = false,updatable = false)
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ArticleStatus status;

}
