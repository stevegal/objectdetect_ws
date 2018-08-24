package uk.co.stevegal.tensorweb.controller;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by stevegal on 18/08/2018.
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(properties= {"tensor.confidenceLimit=0.15","tensor.include=car"})
@Import(TensorWebControllerTest.CustomConfiguration.class)
public class TensorWebControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ImageEvaluator mockEvaluator;

  @Autowired
  private MailClient mockMailClient;

  @Autowired
  private ImageResultCreator mockImageResult;

  @Autowired

  @Before
  public void reset() {
    Mockito.reset(this.mockEvaluator);
    Mockito.reset(this.mockImageResult);
    Mockito.reset(this.mockMailClient);
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
    when(mockImageResult.createResultImage(any(byte[].class), any(List.class))).thenReturn("abcde");


    this.mockMvc.perform(multipart("/detect").file(multipartFile).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results").isArray())
        .andExpect(jsonPath("$.results[0].confidence").value(closeTo(10,0.01)))
        .andExpect(jsonPath("$.results[0].label").value(equalTo("car")))
        .andExpect(jsonPath("$.image").value(equalTo("abcde")));

    verify(mockEvaluator).evaluate("Spring Framework".getBytes());
  }

  @Test
  public void shouldPredictOnPostOfImageAndOutputHtml() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile("image", "test.image",
        "image/jpg", "Spring Framework".getBytes());

    PredictionResults fakeResults = PredictionResults.newBuilder()
        .result(
            PredictionResult.newBuilder().confidence(10.0f).label("car").build()
        ).build();
    when(mockEvaluator.evaluate(any(byte[].class))).thenReturn(fakeResults);
    when(mockImageResult.createResultImage(any(byte[].class), any(List.class))).thenReturn("abcde");


    this.mockMvc.perform(multipart("").file(multipartFile).accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andExpect(content().string(Matchers.containsString("<tr><td>car</td><td>10.0</td></tr>")));

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
            PredictionResult.newBuilder().confidence(0.2f).label("car").build()
        )
        .build();
    when(mockEvaluator.evaluate(any(byte[].class))).thenReturn(fakeResults);
    when(mockImageResult.createResultImage(any(byte[].class), any(List.class))).thenReturn("12345");


    this.mockMvc.perform(multipart("/detect").file(multipartFile).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results").isArray())
        .andExpect(jsonPath("$.results.length()").value(equalTo(1)))
        .andExpect(jsonPath("$.results[0].confidence").value(closeTo(0.2,0.01)))
        .andExpect(jsonPath("$.results[0].label").value(equalTo("car")))
        .andExpect(jsonPath("$.image").value(equalTo("12345")));

    verify(mockEvaluator).evaluate("Spring Framework".getBytes());
    ArgumentCaptor<List<PredictionResult>> resultArgumentCaptor = ArgumentCaptor.forClass(List.class);
    verify(mockImageResult).createResultImage(eq("Spring Framework".getBytes()),resultArgumentCaptor.capture());
    assertThat(resultArgumentCaptor.getValue()).hasSize(1);
    assertThat(resultArgumentCaptor.getValue().get(0).getLabel()).isEqualTo("car");
  }

  @Test
  public void sendsMailIfHasResult() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile("image", "test.image",
        "image/jpg", "Spring Framework".getBytes());

    PredictionResults fakeResults = PredictionResults.newBuilder()
        .result(
            PredictionResult.newBuilder().confidence(10.0f).label("car").build()
        ).build();
    when(mockEvaluator.evaluate(any(byte[].class))).thenReturn(fakeResults);
    when(mockImageResult.createResultImage(any(byte[].class), any(List.class))).thenReturn("abcde");


    this.mockMvc.perform(multipart("/detect/mailTo").file(multipartFile).param("mailTo","steve"))
        .andExpect(status().isOk());

    verify(mockEvaluator).evaluate("Spring Framework".getBytes());
    ArgumentCaptor<PredictionResults> resultArgumentCaptor = ArgumentCaptor.forClass(PredictionResults.class);
    verify(mockMailClient).sendResultsTo(eq("steve"),resultArgumentCaptor.capture());
    assertThat(resultArgumentCaptor.getValue().getResults()).hasSize(1);
    assertThat(resultArgumentCaptor.getValue().getResults().get(0).getLabel()).isEqualTo("car");

  }

  @Test
  public void doesNotSendMailIfNoResult() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile("image", "test.image",
        "image/jpg", "Spring Framework".getBytes());

    PredictionResults fakeResults = PredictionResults.newBuilder()
        .build();
    when(mockEvaluator.evaluate(any(byte[].class))).thenReturn(fakeResults);
    when(mockImageResult.createResultImage(any(byte[].class), any(List.class))).thenReturn("abcde");


    this.mockMvc.perform(multipart("/detect/mailTo").file(multipartFile).param("mailTo","steve"))
        .andExpect(status().isOk());

    verify(mockEvaluator).evaluate("Spring Framework".getBytes());
    ArgumentCaptor<PredictionResults> resultArgumentCaptor = ArgumentCaptor.forClass(PredictionResults.class);
    verifyNoMoreInteractions(mockMailClient);
  }

  @Test
  public void willOnlyIncludeObjectsInFilterOnMail() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile("image", "test.image",
        "image/jpg", "Spring Framework".getBytes());

    PredictionResults fakeResults = PredictionResults.newBuilder()
        .result(
            PredictionResult.newBuilder().confidence(0.8f).label("not_included").build()
        )
        .result(
            PredictionResult.newBuilder().confidence(0.9f).label("car").build()
        )
        .build();
    when(mockEvaluator.evaluate(any(byte[].class))).thenReturn(fakeResults);
    when(mockImageResult.createResultImage(any(byte[].class), any(List.class))).thenReturn("12345");


    this.mockMvc.perform(multipart("/detect/mailTo").file(multipartFile).param("mailTo","steve"))
        .andExpect(status().isOk());

    verify(mockEvaluator).evaluate("Spring Framework".getBytes());
    ArgumentCaptor<PredictionResults> resultArgumentCaptor = ArgumentCaptor.forClass(PredictionResults.class);
    verify(mockMailClient).sendResultsTo(eq("steve"),resultArgumentCaptor.capture());
    assertThat(resultArgumentCaptor.getValue().getResults()).hasSize(1);
    assertThat(resultArgumentCaptor.getValue().getResults().get(0).getLabel()).isEqualTo("car");
  }

  @TestConfiguration
  @TestPropertySource("tensor.confidenceLimit=0.15")
  public static class CustomConfiguration {

    @Bean
    public ImageEvaluator machineEvaluator() {
      return mock(ImageEvaluator.class);
    }

    @Bean
    public ImageResultCreator imageResultCreator(){
      return mock(ImageResultCreator.class);
    }

    @Bean
    public MailClient mailClient() {
      return mock(MailClient.class);
    }
  }

}