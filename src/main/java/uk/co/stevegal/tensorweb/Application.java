package uk.co.stevegal.tensorweb;

import com.google.protobuf.TextFormat;
import object_detection.protos.StringIntLabelMapOuterClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.FileCopyUtils;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.thymeleaf.TemplateEngine;
import uk.co.stevegal.tensorweb.controller.ImageEvaluator;
import uk.co.stevegal.tensorweb.controller.ImageResultCreator;
import uk.co.stevegal.tensorweb.controller.ImageResultCreatorImpl;
import uk.co.stevegal.tensorweb.controller.MailClient;
import uk.co.stevegal.tensorweb.controller.TensorProperties;
import uk.co.stevegal.tensorweb.controller.TensorflowImageEvaluator;
import uk.co.stevegal.tensorweb.controller.TerminationBean;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by stevegal on 18/08/2018.
 *
 * Spring boot hook
 */
@SpringBootApplication
public class Application {

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }

  // load in the pre-trained model here (.pb extension)
  @Bean
  public Graph objectGraph(TensorProperties properties) throws IOException {
    Graph graph = new Graph();
    ClassPathResource cpr = new ClassPathResource(properties.getModelPath());
    graph.importGraphDef(FileCopyUtils.copyToByteArray(cpr.getInputStream()));
    return graph;
  }

  @Bean
  public TerminationBean terminationBean(BlockingQueue<Session> sessions) {
    return new TerminationBean(sessions);
  }

  @Bean
  public BlockingQueue<Session> sessionQueue(TensorProperties properties, Graph graph ) {
    BlockingQueue<Session> sessions = new ArrayBlockingQueue<>(properties.getMaxPoolSize());
    for (int i=0;i<properties.getMaxPoolSize();i++) {
      sessions.add(new Session(graph));
    }
    return sessions;
  }

  @Bean
  public ImageEvaluator machineEvaluator(BlockingQueue<Session> sessions, StringIntLabelMapOuterClass.StringIntLabelMap map) {
    return new TensorflowImageEvaluator(sessions, map);
  }

  @Bean
  public StringIntLabelMapOuterClass.StringIntLabelMap mapFrom(TensorProperties properties) throws IOException {
    ClassPathResource cpr = new ClassPathResource(properties.getLabelPath());
    StringIntLabelMapOuterClass.StringIntLabelMap.Builder builder = StringIntLabelMapOuterClass.StringIntLabelMap.newBuilder();
    InputStreamReader reader = new InputStreamReader(cpr.getInputStream(),"ASCII");
    TextFormat.merge(reader,builder);
    return builder.build();
  }

  @Bean
  public ImageResultCreator imageResultCreator(){
    return new ImageResultCreatorImpl();
  }


  @Bean
  public MailClient mailClient(JavaMailSender sender, TemplateEngine engine) {
    return new MailClient(sender, engine);
  }

}
