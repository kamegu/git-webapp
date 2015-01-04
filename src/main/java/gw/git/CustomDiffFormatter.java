package gw.git;

import gw.git.DiffBlock.DiffLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.Repository;

public class CustomDiffFormatter extends DiffFormatter {

  private DiffOutputStream os;

  public CustomDiffFormatter(Repository repository) {
    super(new DiffOutputStream());
    os = (DiffOutputStream) getOutputStream();
    super.setRepository(repository);
  }

  public List<DiffBlock> diffFormat(DiffEntry entry) throws IOException {
    os.clear();
    format(entry);
    List<DiffBlock> obj = os.getDiffBlocks();
    os.clear();
    return obj;
  }

  @Override
  public void format(EditList edits, RawText a, RawText b) throws IOException {
    super.format(edits, a, b);
  }

  @Override
  protected void writeHunkHeader(int aStartLine, int aEndLine, int bStartLine, int bEndLine) throws IOException {

    os.startHeader(aStartLine, aEndLine, bStartLine, bEndLine);
    super.writeHunkHeader(aStartLine, aEndLine, bStartLine, bEndLine);
    os.endHeader();
  }

  @Override
  protected void writeContextLine(RawText text, int line) throws IOException {
    os.startDiff(3);
    text.writeLine(os, line);
    os.endDiff();
  }

  @Override
  protected void writeRemovedLine(RawText text, int line) throws IOException {
    os.startDiff(1);
    text.writeLine(os, line);
    os.endDiff();
  }

  @Override
  protected void writeAddedLine(RawText text, int line) throws IOException {
    os.startDiff(2);
    text.writeLine(os, line);
    os.endDiff();
  }

  private static class DiffOutputStream extends ByteArrayOutputStream {
    @Getter
    private List<DiffBlock> diffBlocks = new ArrayList<>();

    private DiffBlock currentBlock;
    private int curA;
    private int curB;

    public DiffOutputStream() {
      currentBlock = new DiffBlock(0, 0);
      diffBlocks.add(currentBlock);
      curA = curB = 0;
    }

    public void startHeader(int aStartLine, int aEndLine, int bStartLine, int bEndLine) {
      if (currentBlock.getAStart() == aStartLine && currentBlock.getBStart() == bStartLine) {
        currentBlock.setEnd(false, aEndLine, bEndLine);
      } else {
        if (currentBlock.isSame()) {
          currentBlock.setEnd(true, aStartLine - 1, bStartLine - 1);
        }

        currentBlock = new DiffBlock(aStartLine, bStartLine);
        currentBlock.setEnd(false, aEndLine, bEndLine);
        curA = aStartLine;
        curB = bStartLine;
        diffBlocks.add(currentBlock);
      }
      clear();
    }

    public void endHeader() {
      String header = this.toString();
      currentBlock.setHeader(header);
      clear();
    }

    private int mode = 0;

    public void startDiff(int mode) {
      this.mode = mode;
      clear();
    }

    public void endDiff() {
      String text = new String(buf, Charset.forName("UTF-8"));
      DiffLine diffLine = new DiffLine(mode, text);
      if (mode == 1 || mode == 3) {
        curA++;
        diffLine.setALine(curA);
      }
      if (mode == 2 || mode == 3) {
        curB++;
        diffLine.setBLine(curB);
      }
      currentBlock.getDiffLines().add(diffLine);
      clear();
    }

    private void clear() {
      buf = new byte[0];
      count = 0;
    }
  }
}
