package uk.co.stevegal.tensorweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Created by stevegal on 19/08/2018.
 */
@Service
public class MailClient {

  private JavaMailSender mailSender;
  private TemplateEngine engine;

  @Autowired
  public MailClient(JavaMailSender mailSender, TemplateEngine engine) {
    this.mailSender = mailSender;
    this.engine = engine;
  }

  public void sendResultsTo(String mailTo, PredictionResults results) {
    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom(mailTo);
      messageHelper.setTo(mailTo);
      messageHelper.setSubject("Something detected");
      Context context = new Context();
      context.setVariable("image", results.getImage());
      context.setVariable("results", results.getResults());
      String content = engine.process("pretty",context);
      messageHelper.setText(content, true);
    };
    mailSender.send(messagePreparator);
  }
}
