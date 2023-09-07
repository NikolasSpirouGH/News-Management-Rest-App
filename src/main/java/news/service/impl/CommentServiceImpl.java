package news.service.impl;

import lombok.RequiredArgsConstructor;
import news.entity.Article;
import news.entity.ArticleStatus;
import news.entity.Comment;
import news.entity.CommentStatus;
import news.exception.ResourceNotFoundException;
import news.payload.CommentDTO;
import news.repository.ArticleRepository;
import news.repository.CommentRepository;
import news.service.CommentService;
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
