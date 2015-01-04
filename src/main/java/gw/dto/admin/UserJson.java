package gw.dto.admin;

import gw.model.Account;
import gw.model.UserAccount;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class UserJson {
  @Valid
  private AccountJson account = new AccountJson();
  private String fullName;
  private String password;
  @NotEmpty
  private String email;
  @NotNull
  private boolean admin;

  public UserJson(Account accountEntity) {
    this.account = new AccountJson(accountEntity.getName(), accountEntity.getUrl(), accountEntity.isDeleted());
    this.fullName = accountEntity.getUserAccount().getFullName();
    this.email = accountEntity.getUserAccount().getMailAddress();
    this.admin = accountEntity.getUserAccount().isAdmin();
  }

  public void applyUser(Account account, UserAccount userAccount) {
    account.setName(this.account.getName());
    account.setDeleted(this.account.isDelete());
    account.setUrl(this.account.getUrl());
    account.setGroup(false);
    userAccount.setName(this.account.getName());
    userAccount.setFullName(this.fullName);
    userAccount.setMailAddress(this.email);
    userAccount.setAdmin(this.admin);
  }
}
