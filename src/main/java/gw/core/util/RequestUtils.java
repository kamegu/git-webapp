package gw.core.util;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

  public static String getHost(HttpServletRequest request) {
    String protocol = request.getProtocol();
    StringBuilder builder = new StringBuilder();
    builder.append(protocol).append("://");
    builder.append(request.getServerName());
    int port = request.getServerPort();
    if (protocol.equals("http") && port != 80
        || protocol.equals("https") && port != 443) {
      builder.append(port).append("");
    }
    
    return builder.toString();
  }
}
