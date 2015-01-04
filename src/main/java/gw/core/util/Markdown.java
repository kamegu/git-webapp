package gw.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Markdown {

  public static String compile(String src) {
    PegDownProcessor processor = new PegDownProcessor(Extensions.AUTOLINKS | Extensions.FENCED_CODE_BLOCKS);
    String replaced = expandSpaceForLi(src);
    String html = processor.markdownToHtml(replaced);
    String cleanedHtml = Jsoup.clean(html, Whitelist.relaxed());
    return cleanedHtml;
  }

  private static String expandSpaceForLi(String src) {
    String replaced = StringUtils.replacePattern(src, "\\n((  )+)([*+-] )", "\n$1$1$3");
    replaced = StringUtils.replacePattern(replaced, "^((  )+)([*+-] )", "$1$1$3");
    return replaced;
  }
}
