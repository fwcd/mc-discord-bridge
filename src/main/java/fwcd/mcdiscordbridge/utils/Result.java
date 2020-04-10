package fwcd.mcdiscordbridge.utils;

/**
 * Either a value or an exception. Useful for
 * handling asynchronous errors.
 */
public class Result<T, E extends Exception> {
    private final T value;
    private final E error;
    
    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }
    
    public static <T, E extends Exception> Result<T, E> success(T value) {
        return new Result<>(value, null);
    }
    
    public static <T, E extends Exception> Result<T, E> failure(E error) {
        return new Result<>(null, error);
    }
    
    public boolean isSuccess() {
        return value != null;
    }
    
    public boolean isFailure() {
        return error != null;
    }
    
    public T get() throws E {
        if (isSuccess()) {
            return value;
        } else {
            throw error;
        }
    }
}
