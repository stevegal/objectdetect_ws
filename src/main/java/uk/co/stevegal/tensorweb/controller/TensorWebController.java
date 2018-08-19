package uk.co.stevegal.tensorweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by stevegal on 18/08/2018.
 */
@RestController("/predict")
public class TensorWebController {

  private ImageEvaluator evaluator;
  private float confidence;


  @Autowired
  public TensorWebController(ImageEvaluator evaluator, @Value("${tensor.confidenceLimit}") float confidence) {
    this.evaluator = evaluator;
    this.confidence = confidence;
  }

  @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public PredictionResults predictFileUpload(@RequestParam("image") MultipartFile file) throws IOException {
    PredictionResults unfilteredResults = this.evaluator.evaluate(file.getBytes());
    List<PredictionResult> predictionResults = unfilteredResults.asStream()
        .filter(item -> item.getConfidence() > confidence)
        .collect(Collectors.toList());
    return PredictionResults.newBuilder().results(predictionResults).build();
  }

}
