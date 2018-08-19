package uk.co.stevegal.tensorweb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.tensorflow.Graph;
import uk.co.stevegal.tensorweb.controller.ImageEvaluator;
import uk.co.stevegal.tensorweb.controller.TensorflowImageEvaluator;

import java.io.IOException;
import java.nio.file.Files;

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
  public ImageEvaluator machineEvaluator(Graph graph) {
    return new TensorflowImageEvaluator(graph);
  }

}
