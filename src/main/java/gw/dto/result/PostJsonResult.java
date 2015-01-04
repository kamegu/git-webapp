package gw.dto.result;

import gw.core.action.Result;

import java.util.function.Function;

public class PostJsonResult {

  public static <T> JsonResult get(Result<T> actionResult) {
    if (actionResult.isSuccess()) {
      return new SuccessResult<T>(actionResult.getObject());
    }
    return new ErrorResult(actionResult.getMessages());
  }

  public static <T, R> JsonResult get(Result<T> actionResult, Function<T, R> func) {
    if (actionResult.isSuccess()) {
      return new SuccessResult<R>(func.apply(actionResult.getObject()));
    }
    return new ErrorResult(actionResult.getMessages());
  }
}
