package com.news.service.impl;

import com.news.entity.*;
import com.news.exception.NewsAPIException;
import com.news.exception.ResourceNotFoundException;
import com.news.payload.CommentDTO;
import com.news.repository.ArticleRepository;
import com.news.repository.CommentRepository;
import com.news.repository.UserRepository;
import com.news.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;



@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public CommentDTO createComment(long articleId, CommentDTO request) {
        User user = userRepository.findByUsername(request.getUsername());
        System.out.println(user.getUsername());

        Comment comment = mapToEntity(request);

        Article findArticle =  articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", articleId));

        //comment.setText(request.getText());
        //comment.setAuthorName(request.getAuthorName());
        comment.setStatus(CommentStatus.CREATED);
        comment.setArticle(findArticle);
        comment.setUser(user);

        if(findArticle.getStatus() != ArticleStatus.PUBLISHED) {
            throw new RuntimeException("Not published article");
        }
        commentRepository.save(comment);
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);

        commentDTO.setUsername(comment.getUser().getUsername());

        return commentDTO;
    }

    @Override
    public CommentDTO updateComment(Long articleId, long commentId, CommentDTO commentRequest) {
        // retrieve post entity by id
        Article article = articleRepository.findById(articleId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", articleId));

        // retrieve comment by id
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment", "id", commentId));

        if(!comment.getArticle().getArticleId().equals(article.getArticleId())){
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "Comment does not belongs to article");
        }

        comment.setAuthorName(commentRequest.getUsername());
        comment.setText(commentRequest.getText());

        Comment updatedComment = commentRepository.save(comment);
        String username = modelMapper.map(comment.getUser().getUsername(),String.class);

        CommentDTO resultComment = modelMapper.map(updatedComment, CommentDTO.class);

        resultComment.setUsername(username);
        return resultComment;
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
