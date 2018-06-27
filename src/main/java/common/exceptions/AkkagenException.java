package common.exceptions;

public class AkkagenException extends RuntimeException {

    private String message;
    private AkkagenExceptionType type;

    public AkkagenException(String message){
        this.message = message;
    }

    public AkkagenException(String message, AkkagenExceptionType type){
        this.message = message;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AkkagenExceptionType getType() {
        return type;
    }

    public void setType(AkkagenExceptionType type) {
        this.type = type;
    }
}
