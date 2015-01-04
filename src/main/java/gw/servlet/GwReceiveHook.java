package gw.servlet;

import gw.model.pk.RepositoryPK;
import gw.service.RepositoryUpdateService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.PostReceiveHook;
import org.eclipse.jgit.transport.PreReceiveHook;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceiveCommand.Type;
import org.eclipse.jgit.transport.ReceivePack;

@RequiredArgsConstructor
class GwReceiveHook implements PostReceiveHook, PreReceiveHook {
  private final RepositoryPK repositoryPK;
  @Inject
  private RepositoryUpdateService service;

  private List<Command> commandsForBranch;
  private List<Command> commandsForTag;

  @Override
  public void onPreReceive(ReceivePack rp, Collection<ReceiveCommand> commands) {
    commandsForBranch = commands.stream()
        .filter(this::isBranch)
        .map(Command::new)
        .collect(Collectors.toList());
    commandsForTag = commands.stream()
        .filter(this::isTag)
        .map(Command::new)
        .collect(Collectors.toList());
  }

  @Override
  public void onPostReceive(ReceivePack rp, Collection<ReceiveCommand> commands) {
    service.onPost(repositoryPK, Lists.transform(commandsForBranch, command -> command.getRefName().substring(Constants.R_HEADS.length())));

    commandsForTag.forEach(command -> {
      // String tagName = command.getRefName().substring(Constants.R_TAGS.length());

      });
  }

  private boolean isBranch(ReceiveCommand command) {
    return command.getRefName().startsWith(Constants.R_HEADS);
  }

  private boolean isTag(ReceiveCommand command) {
    return command.getRefName().startsWith(Constants.R_TAGS);
  }

  @Getter
  private static class Command {
    private String refName;
    private Type type;

    private Command(ReceiveCommand receiveCommand) {
      this.refName = receiveCommand.getRefName();
      this.type = receiveCommand.getType();
    }
  }
}
