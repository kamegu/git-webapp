package gw.git;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;

@NoArgsConstructor(access = AccessLevel.NONE)
class JGitUtils {

  public static RevTree getRevTree(Repository repository, String refName) throws IOException {
    ObjectId objectId = repository.resolve(refName);
    RevWalk revWalk = new RevWalk(repository);
    RevTree tree = revWalk.parseTree(objectId);
    revWalk.release();
    return tree;
  }
}
