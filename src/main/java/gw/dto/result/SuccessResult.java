package gw.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SuccessResult<T> implements JsonResult {

  private T object;

  @Override
  public String getStatus() {
    return "success";
  }

}
