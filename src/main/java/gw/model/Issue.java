package gw.model;

import gw.model.embed.UpdateInfo;
import gw.model.pk.IssuePK;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ISSUE")
@Getter
@Setter
@ToString(of = { "pk" })
public class Issue implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private IssuePK pk;

  @NotNull
  @Column(name = "OPENED_ACCOUNT_NAME")
  @Size(max = 100)
  private String openedAccountName;

  @Column(name = "ASSIGNED_ACCOUNT_NAME")
  @Size(max = 100)
  private String assignedAccountName;

  @NotNull
  @Column(name = "TITLE")
  @Size(max = 200)
  private String title;

  @Column(name = "CLOSED")
  private boolean closed;

  @Embedded
  private UpdateInfo updateInfo;

  @OneToOne(mappedBy = "issue", fetch = FetchType.LAZY, optional = true)
  private PullRequest pullRequest;

  @OneToMany(mappedBy = "issue", fetch = FetchType.LAZY)
  @OrderBy("id")
  private List<IssueComment> issueComments;

  @OneToMany(mappedBy = "issue", fetch = FetchType.LAZY)
  @OrderBy("pk.labelName")
  private List<IssueLabel> issueLabels;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "OPENED_ACCOUNT_NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  private Account openedAccount;
}
