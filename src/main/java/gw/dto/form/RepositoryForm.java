package gw.dto.form;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RepositoryForm {
  @NotEmpty
  private String owner;
  @NotEmpty
  private String name;
  private String description;
  @NotNull
  private boolean privateRepo;

}
