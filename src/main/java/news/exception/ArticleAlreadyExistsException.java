package news.exception;

public class ArticleAlreadyExistsException extends RuntimeException {

    public ArticleAlreadyExistsException(String message) {
        super(message);
    }
}