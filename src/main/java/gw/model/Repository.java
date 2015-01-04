package gw.model;

import gw.model.embed.UpdateInfo;
import gw.model.pk.RepositoryPK;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "REPOSITORY")
@Getter @Setter
@ToString(of = { "pk" })
@NamedQueries({
    @NamedQuery(name = "Repository.findByOwner",
            query = "SELECT r FROM Repository r WHERE r.pk.accountName=:owner")
})
public class Repository implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private RepositoryPK pk;

  @Column(name = "PRIVATE")
  private boolean privateRepo;

  @Column(name = "DESCRIPTION")
  @Size(max = 200)
  private String description;

  @Column(name = "DEFAULT_BRANCH")
  @Size(max = 100)
  private String defaultBranch;

  @Embedded
  private UpdateInfo updateInfo;

  public String getDefaultBranchName() {
    return getDefaultBranchName(this);
  }

  public static String getDefaultBranchName(Repository repository) {
    return StringUtils.defaultIfBlank(repository.getDefaultBranch(), "master");
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ACCOUNT_NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  private Account owner;

  public Set<String> getAllOwners() {
    Account owner = getOwner();

    Set<String> allOwners = new HashSet<>();
    if (owner.isGroup()) {
      Set<String> members = new HashSet<>(owner.getGroupMembers()).stream()
          .map(m -> m.getPk().getMemberName())
          .collect(Collectors.toSet());
      allOwners.addAll(members);
    } else {
      allOwners.add(owner.getName());
    }
    return allOwners;
  }
}
