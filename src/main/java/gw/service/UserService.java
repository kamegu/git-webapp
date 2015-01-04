package gw.service;

import gw.core.LoginContext;
import gw.core.action.Result;
import gw.dto.admin.GroupJson;
import gw.dto.admin.GroupJson.MemberJson;
import gw.dto.admin.UserJson;
import gw.model.Account;
import gw.model.GroupMember;
import gw.model.UserAccount;
import gw.types.PasswordType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class UserService {

  @Inject private Provider<EntityManager> emProvider;
  @Inject private Provider<HttpServletRequest> requestProvider;

  public List<Account> findAll(boolean includeDeleted) {
    EntityManager em = emProvider.get();

    String namedQuery = BooleanUtils.toString(includeDeleted, "Account.findAll", "Account.findNotDeleted");
    List<Account> accounts = em.createNamedQuery(namedQuery, Account.class).getResultList();

    return accounts;
  }

  public Account findAccount(String userName) {
    Account account = emProvider.get().find(Account.class, userName);
    if (account == null) {
      throw new NotFoundException();
    }
    return account;
  }

  public Account findDto(String userName) {
    Account account = emProvider.get().find(Account.class, userName);
    if (account != null) {
      return account;
    } else {
      throw new NotFoundException();
    }
  }

  public GroupJson newGroup() {
    LoginContext context = LoginContext.get(requestProvider.get());
    MemberJson member = new MemberJson(context.getName(), true);
    GroupJson groupJson = new GroupJson();
    groupJson.setMembers(Arrays.asList(member));
    return groupJson;
  }

  @Transactional
  public Result<Boolean> register(UserJson userJson, boolean update) {
    String accountName = userJson.getAccount().getName();
    if (update) {
      Account account = emProvider.get().find(Account.class, accountName);
      if (account == null) {
        throw new NotFoundException();
      }
      UserAccount userAccount = account.getUserAccount();
      userJson.applyUser(account, userAccount);
      if (StringUtils.isNotBlank(userJson.getPassword())) {
        userAccount.setPassword(userJson.getPassword());
      }
    } else {
      Account account = new Account();
      UserAccount userAccount = new UserAccount();
      userJson.applyUser(account, userAccount);
      userAccount.setPassword(userJson.getPassword());
      userAccount.setPasswordType(PasswordType.PLAIN);
      userAccount.setSalt(UUID.randomUUID().toString().replaceAll("-", ""));
      emProvider.get().persist(account);
      emProvider.get().persist(userAccount);
    }

    return Result.success(true);
  }

  @Transactional
  public Result<Boolean> registerGroup(GroupJson groupJson, boolean update) {
    if (update) {
      Account account = emProvider.get().find(Account.class, groupJson.getAccount().getName());
      if (account == null) {
        throw new NotFoundException();
      }

      groupJson.applyGroup(account);
      mergeMembers(account.getGroupMembers(), groupJson.newGroupMembers(account.getName()));
    } else {
      Account account = new Account();
      groupJson.applyGroup(account);
      emProvider.get().persist(account);
      mergeMembers(new ArrayList<>(), groupJson.newGroupMembers(account.getName()));
    }

    return Result.success(true);
  }

  private void mergeMembers(List<GroupMember> currentMembers, List<GroupMember> newMembers) {
    new ArrayList<>(currentMembers).forEach(member -> {
      Optional<GroupMember> newMember = newMembers.stream().filter(m -> m.getPk().equals(member.getPk())).findFirst();
      if (newMember.isPresent()) {
        member.setManager(newMember.get().isManager());
      } else {
        emProvider.get().remove(member);
      }
    });
    new ArrayList<>(newMembers).forEach(member -> {
      Optional<GroupMember> oldMember = new ArrayList<>(currentMembers).stream()
          .filter(m -> m.getPk().equals(member.getPk())).findFirst();
      if (!oldMember.isPresent()) {
        emProvider.get().persist(member);
      }
    });
  }
}
