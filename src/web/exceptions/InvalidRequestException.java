package web.exceptions;

public class InvalidRequestException extends Exception {
    public InvalidRequestException(String s) {
        super(s);
    }
}