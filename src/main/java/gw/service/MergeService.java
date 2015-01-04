package gw.service;

import gw.dto.repository.PullRequestEndpoint;
import gw.git.GitOperation;
import gw.model.MergeableHistory;
import gw.model.pk.MergeableHistoryPK;
import gw.types.MergeableType;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.eclipse.jgit.lib.ObjectId;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class MergeService {
  @Inject private Provider<EntityManager> emProvider;
  @Inject private GitOperation gitOperation;

  @Transactional
  public MergeableType getMeargeable(PullRequestEndpoint baseEndpoint, PullRequestEndpoint requestEndpoint) {
    ObjectId commitId = gitOperation.getCommitId(baseEndpoint.getRepositoryPK(), baseEndpoint.getBranchName());
    ObjectId requestCommitId = gitOperation.getCommitId(requestEndpoint.getRepositoryPK(), requestEndpoint.getBranchName());

    MergeableHistoryPK pk = new MergeableHistoryPK(baseEndpoint.getRepositoryPK(), commitId.getName(), requestCommitId.getName());
    MergeableHistory history = emProvider.get().find(MergeableHistory.class, pk);
    if (history != null) {
      return history.getMergeableType();
    }

    boolean canMerge = gitOperation.canMerge(baseEndpoint.getRepositoryPK(), baseEndpoint.getBranchName(), requestEndpoint.getRepositoryPK(), requestEndpoint.getBranchName());
    history = new MergeableHistory();
    history.setPk(pk);
    history.setMergeableType(MergeableType.of(canMerge));
    emProvider.get().persist(history);

    return history.getMergeableType();
  }
}
