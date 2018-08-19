package uk.co.stevegal.tensorweb.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.tensorflow.Graph;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by stevegal on 18/08/2018.
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(properties = {"tensor.confidenceLimit=0.15"})
@Import(TensorWebControllerTest.CustomConfiguration.class)
public class TensorWebControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ImageEvaluator mockEvaluator;

  @Before
  public void reset() {
    Mockito.reset(this.mockEvaluator);
  }

  @Test
  public void shouldPredictOnPostOfImage() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile("image", "test.image",
        "image/jpg", "Spring Framework".getBytes());

    PredictionResults fakeResults = PredictionResults.newBuilder()
        .result(
            PredictionResult.newBuilder().confidence(10.0f).label("car").build()
        ).build();
    when(mockEvaluator.evaluate(any(byte[].class))).thenReturn(fakeResults);


    this.mockMvc.perform(multipart("/predict").file(multipartFile))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results").isArray())
        .andExpect(jsonPath("$.results[0].confidence").value(closeTo(10,0.01)))
        .andExpect(jsonPath("$.results[0].label").value(equalTo("car")));

    verify(mockEvaluator).evaluate("Spring Framework".getBytes());
  }

  @Test
  public void filtersOutResultsBelowThreshold() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile("image", "test.image",
        "image/jpg", "Spring Framework".getBytes());

    PredictionResults fakeResults = PredictionResults.newBuilder()
        .result(
            PredictionResult.newBuilder().confidence(0.1f).label("car").build()
        )
        .result(
            PredictionResult.newBuilder().confidence(0.2f).label("car2").build()
        )
        .build();
    when(mockEvaluator.evaluate(any(byte[].class))).thenReturn(fakeResults);


    this.mockMvc.perform(multipart("/predict").file(multipartFile))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results").isArray())
        .andExpect(jsonPath("$.results.length()").value(equalTo(1)))
        .andExpect(jsonPath("$.results[0].confidence").value(closeTo(0.2,0.01)))
        .andExpect(jsonPath("$.results[0].label").value(equalTo("car2")));

    verify(mockEvaluator).evaluate("Spring Framework".getBytes());
  }

  @TestConfiguration
  @TestPropertySource("tensor.confidenceLimit=0.15")
  public static class CustomConfiguration {

    @Bean
    public ImageEvaluator machineEvaluator() {
      return mock(ImageEvaluator.class);
    }
  }

}