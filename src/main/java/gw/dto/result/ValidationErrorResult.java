package gw.dto.result;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ValidationErrorResult implements JsonResult {
  @Override
  public String getStatus() {
    return "validation";
  }

  @Getter
  private final List<ValidationError> errors = new ArrayList<>();

  public void addError(String name, String message) {
    this.errors.add(new ValidationError(name, message));
  }

  @AllArgsConstructor
  public static class ValidationError {
    public String name;
    public String message;
  }
}
