package gw.dto.admin;

import gw.model.Account;
import gw.validator.ReservedName;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class AccountJson {
  @NotEmpty
  @Pattern(regexp = "[a-z-._]+")
  @ReservedName
  private String name;
  private String url;
  private boolean delete;

  public void apply(Account account) {
    account.setName(name);
    account.setDeleted(delete == true);
    account.setUrl(url);
  }

  public AccountJson(Account accountEntity) {
    this.name = accountEntity.getName();
    this.url = accountEntity.getUrl();
    this.delete = accountEntity.isDeleted();
  }
}
