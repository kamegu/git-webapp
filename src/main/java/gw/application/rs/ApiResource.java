package gw.application.rs;

import gw.core.util.Markdown;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("api")
public class ApiResource {

  @POST
  @Path("markdown")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String getHtmlFromMarkdown(String markdown) {
    return Markdown.compile(markdown);
  }
}
