package uk.co.stevegal.tensorweb.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by stevegal on 18/08/2018.
 */
@RestController("/predict")
public class TensorWebController {

  @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public PredictionResults predictFileUpload(MultipartFile file) {
    return new PredictionResults();
  }

}
