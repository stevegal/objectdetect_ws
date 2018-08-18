package uk.co.stevegal.tensorweb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.tensorflow.Graph;

import javax.annotation.PreDestroy;
import java.io.File;
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
    graph.importGraphDef(Files.readAllBytes(new ClassPathResource(path).getFile().toPath()));

    return graph;
  }

}
