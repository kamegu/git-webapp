package gw.dto.repository;

import gw.model.Label;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.validator.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class LabelJson {
  @NotEmpty
  private String name;
  @NotEmpty
  @Pattern(regexp = "#[0-9a-fA-F]{6}")
  private String color;

  private int issueCount;
  private boolean delete = false;

  public LabelJson(Label label) {
    this(label.getPk().getLabelName(), label.getColor(), 0, false);
  }
}
