package gw.application.rs.admin;

import gw.core.action.Result;
import gw.core.auth.Admin;
import gw.dto.admin.MailJson;
import gw.dto.result.JsonResult;
import gw.dto.result.PostJsonResult;
import gw.service.MailSender;
import gw.service.MailService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotEmpty;

@Admin
public class MailResource {
  @Inject private MailService mailService;
  @Inject private MailSender mailSender;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public MailJson getMailSetting() {
    return mailService.getMailSetting();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult registerMailSetting(@Valid MailJson mailJson) {
    return PostJsonResult.get(mailService.registerMailSettings(mailJson));
  }

  @POST @Path("test")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult sendTestMail(@Valid TestMailJson mailJson) {
    Result<String> result = mailSender.sendTestMail(mailJson.getTo(), mailJson.getMailSetting());
    return PostJsonResult.get(result);
  }

  @Getter @Setter
  public static class TestMailJson {
    @NotEmpty
    private String to;
    private MailJson mailSetting;
  }
}
