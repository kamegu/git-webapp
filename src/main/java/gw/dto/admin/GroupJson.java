package gw.dto.admin;

import gw.model.Account;
import gw.model.GroupMember;
import gw.model.pk.GroupMemberPK;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class GroupJson {
  @Valid
  private AccountJson account = new AccountJson();
  private List<MemberJson> members = new ArrayList<>();

  public GroupJson(Account accountEntity) {
    this.account = new AccountJson(accountEntity.getName(), accountEntity.getUrl(), accountEntity.isDeleted());
    this.members = new ArrayList<>(accountEntity.getGroupMembers()).stream()
        .map(member -> new MemberJson(member.getPk().getMemberName(), member.isManager()))
        .collect(Collectors.toList());
  }

  public void applyGroup(Account accountEntity) {
    this.account.apply(accountEntity);
    accountEntity.setGroup(true);
  }

  public List<GroupMember> newGroupMembers(String groupName) {
    return members.stream().map(member -> member.createGroupMember(groupName)).collect(Collectors.toList());
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter @Setter
  public static class MemberJson {
    private String name;
    private boolean manager;

    private GroupMember createGroupMember(String groupName) {
      GroupMember member = new GroupMember();
      member.setPk(new GroupMemberPK(groupName, this.name));
      member.setManager(this.manager);
      return member;
    }
  }

}
