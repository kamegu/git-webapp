package gw.dto;

import gw.model.Account;
import gw.model.UserAccount;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountDto {
  private Account account;
  private UserAccount userAccount;
}
