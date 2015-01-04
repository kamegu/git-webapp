package gw.dto.result;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ErrorResult implements JsonResult {
  @Getter
  private List<String> errors;

  @Override
  public String getStatus() {
    return "error";
  }
}
