package gw.core.action;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public class Result<T> {
  private T object = null;
  private List<String> messages;

  private Result() {

  }

  public boolean isSuccess() {
    return object != null;
  }

  public boolean isError() {
    return object == null;
  }

  public static <T> Result<T> success(T object) {
    Result<T> result = new Result<>();
    result.object = object;
    return result;
  }

  public static <T> Result<T> error(String message) {
    Result<T> result = new Result<>();
    result.messages = Arrays.asList(message);
    return result;

  }
}
