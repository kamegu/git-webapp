package gw.service;

import gw.core.action.Result;
import gw.dto.repository.LabelJson;
import gw.model.IssueLabel;
import gw.model.Label;
import gw.model.pk.IssueLabelPK;
import gw.model.pk.IssuePK;
import gw.model.pk.LabelPK;
import gw.model.pk.RepositoryPK;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class LabelService {
  @Inject private Provider<EntityManager> emProvider;
//    @Inject private Provider<HttpServletRequest> reqProvider;

  public List<LabelJson> findLabels(RepositoryPK repositoryPK) {
    List<Label> labels = emProvider.get().createQuery("SELECT l FROM Label l WHERE l.pk.repositoryPK=:repositoryPK", Label.class)
        .setParameter("repositoryPK", repositoryPK)
        .getResultList();
    return labels.stream().map(LabelJson::new).collect(Collectors.toList());
  }

  @Transactional
  public Result<LabelJson> createLabel(RepositoryPK repositoryPK, LabelJson form) {
    Label label = emProvider.get().find(Label.class, new LabelPK(repositoryPK, form.getName()));
    if (label != null) {
      return Result.error("inputted name is already used");
    }

    Label inserted = insertNewLabel(repositoryPK, form);
    return Result.success(new LabelJson(inserted));
  }

  @Transactional
  public Result<LabelJson> updateLabel(RepositoryPK repositoryPK, String originalName, LabelJson form) {
    if (form.isDelete()) {
      return deleteLabel(repositoryPK, originalName);
    }

    LabelPK labelPK = new LabelPK(repositoryPK, originalName);
    Label label = emProvider.get().find(Label.class, labelPK);
    if (label == null) {
      throw new NotFoundException("label");
    }

    if (originalName.equals(form.getName())) {
      label.setColor(form.getColor());
      return Result.success(new LabelJson(label));
    } else {
      Label check = emProvider.get().find(Label.class, new LabelPK(repositoryPK, form.getName()));
      if (check != null) {
        return Result.error("new name is already used");
      }
      Label inserted = insertNewLabel(repositoryPK, form);

      List<IssueLabel> issueLabels = findUsedLabels(repositoryPK, originalName);
      issueLabels.forEach(iLabel -> {
        insertIssueLabel(iLabel.getPk().getIssuePK(), form.getName());
        emProvider.get().remove(iLabel);
      });
      emProvider.get().remove(label);
      return Result.success(new LabelJson(inserted));
    }
  }

  @Transactional
  public Result<LabelJson> deleteLabel(RepositoryPK repositoryPK, String name) {
    LabelPK labelPK = new LabelPK(repositoryPK, name);
    Label label = emProvider.get().find(Label.class, labelPK);
    if (label == null) {
      throw new NotFoundException("label");
    }

    List<IssueLabel> issueLabels = findUsedLabels(repositoryPK, name);
    issueLabels.forEach(iLabel -> {
      emProvider.get().remove(iLabel);
    });
    emProvider.get().remove(label);
    return Result.success(new LabelJson());
  }

  private Label insertNewLabel(RepositoryPK repositoryPK, LabelJson form) {
    Label label = new Label();
    label.setPk(new LabelPK(repositoryPK, form.getName()));
    label.setColor(form.getColor());
    emProvider.get().persist(label);
    return label;
  }

  private IssueLabel insertIssueLabel(IssuePK issuePK, String labelName) {
    IssueLabelPK pk = new IssueLabelPK(issuePK, labelName);
    IssueLabel iLabel = new IssueLabel();
    iLabel.setPk(pk);
    emProvider.get().persist(iLabel);
    return iLabel;
  }

  private List<IssueLabel> findUsedLabels(RepositoryPK repositoryPK, String originalName) {
    List<IssueLabel> issueLabels = emProvider.get().createNamedQuery("IssueLabel.byLabel", IssueLabel.class)
        .setParameter("accountName", repositoryPK.getAccountName())
        .setParameter("repoName", repositoryPK.getRepositoryName())
        .setParameter("name", originalName)
        .getResultList();

    return issueLabels;
  }
}
