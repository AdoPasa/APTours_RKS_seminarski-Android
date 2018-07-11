package repositories;

public class ApiResult<T> {
    public String errorMessage;
    public Boolean isError;
    public int errorCode;
    public T value;
}
