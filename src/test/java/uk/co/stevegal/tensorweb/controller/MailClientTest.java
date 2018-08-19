package uk.co.stevegal.tensorweb.controller;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by stevegal on 19/08/2018.
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(properties= {"spring.mail.port=2525","spring.mail.host=localhost"})
public class MailClientTest {

  private MailClient mailClient;

  private GreenMail smtpServer;

  @Autowired
  private JavaMailSender sender;

  @Autowired
  private TemplateEngine engine;

  @Before
  public void setUp() throws Exception {
    mailClient = new MailClient(sender,engine);
    smtpServer = new GreenMail(new ServerSetup(2525, null, "smtp"));
    smtpServer.start();
  }

  @After
  public void tearDown() throws Exception {
    smtpServer.stop();
  }

  @Test
  public void shouldSendMail() throws Exception {
    //given
    String recipient = "name@dolszewski.com";
    PredictionResults message = PredictionResults.newBuilder().result(PredictionResult.newBuilder().label("label").build()).build();
    //when
    mailClient.sendResultsTo(recipient, message);
    //then
    MimeMessage[] receivedMessages = smtpServer.getReceivedMessages();
    assertThat(receivedMessages).hasSize(1);
    String content = (String) receivedMessages[0].getContent();
    assertThat(content).contains("<td>label</td>");
  }
}