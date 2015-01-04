package gw.service;

import gw.core.action.Result;
import gw.dto.admin.MailJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class MailSender {
  @Inject private MailService mailService;

  private static SmtpSenderConfig masterConfig;

  public Result<String> sendMail(String subject, String text, String... sendTo) {
    initProperties(false);
    if (masterConfig == null || masterConfig.properties == null) {
      return Result.success("no-setting");
    }
    Sender sender = new Sender(masterConfig);

    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        sender.send(subject, text, sendTo);
      }
    });
    thread.start();
/*
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
      sender.send(subject, text, sendTo);
    });
    future.exceptionally(new Function<Throwable, Void>() {
      @Override
      public Void apply(Throwable t) {
        t.printStackTrace();
        return null;
      }
    });
*/
    return Result.success("ok");
  }

  public Result<String> sendTestMail(String sendTo, MailJson mailJson) {
    SmtpSenderConfig testConfig = buildConfig(mailJson);
    if (testConfig == null || testConfig.properties == null) {
      return Result.error("invalid setting");
    }
    Sender sender = new Sender(testConfig);
    return sender.send("git webapp test mail", "this is test mail.", sendTo);
  }

  public void initProperties(boolean force) {
    if (!force && masterConfig != null) {
      return;
    }

    MailJson mailSetting = mailService.getMailSetting();
    masterConfig = buildConfig(mailSetting);
  }

  private static Session getSession(SmtpSenderConfig conf) {
    if (conf.properties == null) {
      return null;
    }

    return Session.getInstance(conf.properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return conf.authentication;
      }
    });
  }

  private static SmtpSenderConfig buildConfig(MailJson mailSetting) {
    if (StringUtils.isBlank(mailSetting.getHost())
        || mailSetting.getPort() == null
        || StringUtils.isBlank(mailSetting.getFrom())
        ) {
      return new SmtpSenderConfig(null, null, null);
    }
    if (mailSetting.isAuth() && (StringUtils.isBlank(mailSetting.getUsername()) || StringUtils.isBlank(mailSetting.getPassword()))) {
      return new SmtpSenderConfig(null, null, null);
    }
    Address from;
    try {
      from = new InternetAddress(mailSetting.getFrom());
    } catch (AddressException e) {
      return new SmtpSenderConfig(null, null, null);
    }

    Properties properties = new Properties();

    // SMTPサーバのホスト名 or IPアドレス
    properties.put("mail.smtp.host", mailSetting.getHost());
    // SMTPサーバのポート
    properties.put("mail.smtp.port", mailSetting.getPort());
    properties.put("mail.smtp.auth", BooleanUtils.toStringTrueFalse(mailSetting.isAuth()));
    properties.put("mail.smtp.starttls.enable", BooleanUtils.toStringTrueFalse(mailSetting.isTls()));

    // デバッグ情報を出力する場合
    properties.put("mail.debug", "true");

    if (mailSetting.isAuth()) {
      PasswordAuthentication authentication = new PasswordAuthentication(mailSetting.getUsername(), mailSetting.getPassword());
      return new SmtpSenderConfig(properties, from, authentication);
    } else {
      return new SmtpSenderConfig(properties, from, null);
    }
  }

  public static class Sender {
    private final MimeMessage message;
    private final Address from;
    public Sender(SmtpSenderConfig config) {
      if (config == null || config.properties == null) {
        throw new NullPointerException();
      }
      // 送信メッセージ
      this.message = new MimeMessage(getSession(config));
      this.from = config.from;
    }

    public Result<String> send(String subject, String text, String... sendTo) {
      if (sendTo == null || sendTo.length == 0) {
        return Result.success("no-to");
      }

      try {
        // From
        message.setFrom(this.from);
        // 宛先（TO）
        List<InternetAddress> tos = new ArrayList<>();
        for (String address : sendTo) {
          tos.add(new InternetAddress(address));
        }
        message.setRecipients(MimeMessage.RecipientType.TO, tos.toArray(new InternetAddress[0]));

        // 件名および本文
        message.setSubject(subject, "ISO-2022-JP");

        Multipart multiPart = new MimeMultipart();
        BodyPart html = new MimeBodyPart();
        html.setContent(text, "text/html; charset=utf-8");
        multiPart.addBodyPart(html);
        message.setContent(multiPart);
//        message.setText(text, "ISO-2022-JP");

        Transport.send(message);
      } catch (MessagingException e) {
        throw new RuntimeException(e);
      }

      return Result.success("ok");
    }
  }

  @RequiredArgsConstructor
  private static class SmtpSenderConfig {
    private final Properties properties;
    private final Address from;
    private final PasswordAuthentication authentication;
  }
}
