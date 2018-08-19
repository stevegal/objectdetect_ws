package uk.co.stevegal.tensorweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by stevegal on 18/08/2018.
 */
@RestController("/predict")
public class TensorWebController {

  private ImageEvaluator evaluator;

  @Autowired
  public TensorWebController(ImageEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public PredictionResults predictFileUpload(@RequestParam("image") MultipartFile file) throws IOException {
    return this.evaluator.evaluate(file.getBytes());
  }

}
