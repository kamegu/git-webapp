package gw.model;

import gw.model.embed.UpdateInfo;
import gw.model.pk.CollaboratorPK;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "COLLABORATOR")
@NamedQuery(name = "Collaborator.fetchGroupMembers", query = ""
        + "SELECT DISTINCT c FROM Collaborator c LEFT JOIN FETCH c.collaborator a LEFT JOIN FETCH a.groupMembers WHERE c.pk.repositoryPK=:pk")
@Getter @Setter
public class Collaborator implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private CollaboratorPK pk;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "COLLABORATOR_NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  private Account collaborator;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({ @JoinColumn(name = "ACCOUNT_NAME", referencedColumnName = "ACCOUNT_NAME", updatable = false, insertable = false),
      @JoinColumn(name = "REPOSITORY_NAME", referencedColumnName = "REPOSITORY_NAME", updatable = false, insertable = false)
      })
  private Repository repository;

  @Embedded
  private UpdateInfo updateInfo;
}
