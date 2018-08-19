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
import org.thymeleaf.TemplateEngine;
import uk.co.stevegal.tensorweb.controller.ImageEvaluator;
import uk.co.stevegal.tensorweb.controller.ImageResultCreator;
import uk.co.stevegal.tensorweb.controller.ImageResultCreatorImpl;
import uk.co.stevegal.tensorweb.controller.MailClient;
import uk.co.stevegal.tensorweb.controller.TensorflowImageEvaluator;

import java.io.IOException;
import java.io.InputStreamReader;

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
  public Graph objectGraph(@Value("${tensor.modelpath}") String path) throws IOException {
    Graph graph = new Graph();
    ClassPathResource cpr = new ClassPathResource(path);
    graph.importGraphDef(FileCopyUtils.copyToByteArray(cpr.getInputStream()));
    return graph;
  }

  @Bean
  public ImageEvaluator machineEvaluator(Graph graph, StringIntLabelMapOuterClass.StringIntLabelMap map) {
    return new TensorflowImageEvaluator(graph, map);
  }

  @Bean
  public StringIntLabelMapOuterClass.StringIntLabelMap mapFrom(@Value("${tensor.labelPath}") String path) throws IOException {
    ClassPathResource cpr = new ClassPathResource(path);
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
