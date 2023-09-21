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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public CommentDTO createComment(long articleId, CommentDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("start of service");
        Article article =  articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        if(article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "This article is not in PUBLISHED yet!");
        }
        Comment comment = modelMapper.map(request, Comment.class);
        comment.setStatus(CommentStatus.CREATED);
        comment.setArticle(article);
        CommentDTO commentDTO;
        if(userDetails != null){
            comment.setUser(userRepository.findByUsername(userDetails.getUsername()));
            commentDTO = modelMapper.map(comment, CommentDTO.class);
            commentDTO.setUsername(userDetails.getUsername());

        }else {
            System.out.println("in else null");
            comment.setUser(null);
            commentDTO = modelMapper.map(comment, CommentDTO.class);
            commentDTO.setUsername("Anonymous");
        }

        commentDTO.setCreatedDate(LocalDate.from(LocalDateTime.now()));
        commentRepository.save(comment);

        //return commentDTO;
        return ResponseEntity.ok(commentDTO).getBody();
    }

    @Override
    public CommentDTO updateComment(long commentId, CommentDTO commentRequest) {

        // retrieve comment by id
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment", "id", commentId));

        Article article = articleRepository.findById(comment.getArticle().getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", comment.getArticle().getArticleId()));

        if(article.getStatus() != ArticleStatus.PUBLISHED){
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "Article is not PUBLISHED anymore!");
        }

        //We don't keep information for the EDITOR/ADMIN changed the comment content
        comment.setText(commentRequest.getText());
        String username = comment.getUser().getUsername();
        Comment updatedComment = commentRepository.save(comment);
        CommentDTO resultComment = modelMapper.map(updatedComment, CommentDTO.class);
        resultComment.setUsername(username);
        //return resultComment;
        return ResponseEntity.ok(resultComment).getBody();
    }

    @Override
    public CommentDTO approveComment(long commentId) {
        // retrieve comment by id
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment", "id", commentId));

        Article article = articleRepository.findById(comment.getArticle().getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", comment.getArticle().getArticleId()));

        if(article.getStatus() == ArticleStatus.CREATED){
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "Article is CREATED!");
        }
        comment.setStatus(CommentStatus.PUBLISHED);
        commentRepository.save(comment);
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        if(comment.getUser() == null) {
            commentDTO.setUsername("ANONYMOUS");
        }
        else{
            commentDTO.setUsername(comment.getUser().getUsername());
        }
        return commentDTO;
    }

    @Override
    public ResponseEntity<String> rejectComment(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment", "id", commentId));

        Article article = articleRepository.findById(comment.getArticle().getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", comment.getArticle().getArticleId()));

        if(article.getStatus() != ArticleStatus.PUBLISHED){
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "Article is not PUBLISHED anymore!");
        }
        if(comment.getStatus() == CommentStatus.CREATED)
            return new ResponseEntity<>("Comment is not SUBMITTED YET!", HttpStatus.BAD_REQUEST);
        commentRepository.delete(comment);
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }

    private CommentDTO mapToDTO(Comment comment){
        CommentDTO commentDto = modelMapper.map(comment, CommentDTO.class);
        return  commentDto;
    }

    private Comment mapToEntity(CommentDTO commentDTO){
        Comment comment = modelMapper.map(commentDTO, Comment.class);
        return  comment;
    }

    @Override
    public List<CommentDTO> getCommentsByArticleId(Long articleId, @AuthenticationPrincipal UserDetails userDetails) {
        /// Visitors(Unauthorized) will get only published comments
        List<Comment> commentsList;
        List<CommentDTO> commentsDTOList = new ArrayList<>();
        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername());

            if (userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("JOURNALIST"))) {
                //First we find all the published comments for the article
                //and then we will find the unpublished comments for this article, created by current user
                commentsList = commentRepository.findAllByArticleIdAndStatus(articleId, CommentStatus.PUBLISHED);
                List<Comment> commentsTempList = commentRepository.findAllByUserIdAndArticleId(user.getId(), articleId);
                for (Comment comment : commentsTempList) {
                    if (!commentsList.contains(comment)) {
                        commentsList.add(comment);
                    }
                }
                //Converting to List<CommentDTO>
                for (Comment comment : commentsList) {
                    commentsDTOList.add(modelMapper.map(comment, CommentDTO.class));
                }

            } else if (userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("EDITOR"))
                    || userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
                //Find all comments no matter CommentStatus
                commentsList = commentRepository.findByArticleArticleId(articleId);

                for (Comment comment : commentsList) {
                    commentsDTOList.add(modelMapper.map(comment, CommentDTO.class));
                }
            }
        } //For Visitors(Unauthorized - not Users)
        else {
            commentsList = commentRepository.findAllByArticleIdAndStatus(articleId, CommentStatus.PUBLISHED);

            for (int i = 0 ; i<commentsList.size() ; i++) {
                commentsDTOList.add(modelMapper.map(commentsList.get(i), CommentDTO.class));
                commentsDTOList.get(i).setUsername("ANONYMOUS");
            }

        }
        return commentsDTOList;
    }

    @Override
    public CommentDTO getCommentById(Long commentId) {
        return modelMapper.map(commentRepository.findByCommentId(commentId), CommentDTO.class);
    }


}
