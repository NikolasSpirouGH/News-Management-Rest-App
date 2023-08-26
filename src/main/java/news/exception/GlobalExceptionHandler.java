package news.exception;

import news.entity.ErrorResponse;
import news.exception.ArticleAlreadyExistsException;
import news.exception.TopicNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ArticleAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleArticleAlreadyExists(ArticleAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, "Article already exists");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TopicNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTopicNotFoundException(TopicNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, "Topic not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


}
