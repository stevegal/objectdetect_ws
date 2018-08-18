package uk.co.stevegal.tensorweb.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by stevegal on 18/08/2018.
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TensorWebControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldPredictOnPostOfImage() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile("image", "test.image",
        "image/jpg", "Spring Framework".getBytes());

    this.mockMvc.perform(multipart("/predict").file(multipartFile))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results").isArray());
  }

}