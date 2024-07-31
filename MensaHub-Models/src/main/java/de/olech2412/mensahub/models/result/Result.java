package de.olech2412.mensahub.models.result;

/**
 * This class represents a result of a function call
 *
 * @param <T> a success type
 * @param <X> an error type
 * @since 0.0.1
 */
public class Result<T, X> {

    private final boolean success;
    private final T data;
    private final X error;

    private Result(boolean success, T data, X error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T, X> Result<T, X> success(T data) {
        return new Result<>(true, data, null);
    }

    public static <T, X> Result<T, X> error(X error) {
        return new Result<>(false, null, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public X getError() {
        return error;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}