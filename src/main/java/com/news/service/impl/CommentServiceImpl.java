package com.news.service.impl;

import com.news.entity.Article;
import com.news.entity.Comment;
import com.news.entity.CommentStatus;
import com.news.exception.ResourceNotFoundException;
import com.news.payload.CommentDTO;
import com.news.repository.ArticleRepository;
import com.news.repository.CommentRepository;
import com.news.service.CommentService;
import lombok.RequiredArgsConstructor;
import com.news.entity.ArticleStatus;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;



@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    @Override
    public CommentDTO createComment(long articleId, CommentDTO request) {

        Comment comment = mapToEntity(request);

        Article findArticle =  articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", articleId));

        //comment.setText(request.getText());
        //comment.setAuthorName(request.getAuthorName());
        comment.setStatus(CommentStatus.CREATED);
        comment.setArticle(findArticle);

        if(findArticle.getStatus() != ArticleStatus.PUBLISHED) {
            throw new RuntimeException("Not published article");
        }
        Comment newComment = commentRepository.save(comment);
        return mapToDTO(newComment);
    }

    private CommentDTO mapToDTO(Comment comment){
        CommentDTO commentDto = modelMapper.map(comment, CommentDTO.class);

//        CommentDto commentDto = new CommentDto();
//        commentDto.setId(comment.getId());
//        commentDto.setName(comment.getName());
//        commentDto.setEmail(comment.getEmail());
//        commentDto.setBody(comment.getBody());
        return  commentDto;
    }

    private Comment mapToEntity(CommentDTO commentDTO){
        Comment comment = modelMapper.map(commentDTO, Comment.class);
//        Comment comment = new Comment();
//        comment.setId(commentDto.getId());
//        comment.setName(commentDto.getName());
//        comment.setEmail(commentDto.getEmail());
//        comment.setBody(commentDto.getBody());
        return  comment;
    }

    @Override
    public List<Comment> getCommentsByArticleId(Long articleId) {
        return commentRepository.findByArticleArticleId(articleId);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findByCommentId(commentId);
    }
}
