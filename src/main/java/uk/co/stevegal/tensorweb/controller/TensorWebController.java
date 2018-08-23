package uk.co.stevegal.tensorweb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by stevegal on 18/08/2018.
 */
@RestController("/detect")
public class TensorWebController {

  Logger logger = LoggerFactory.getLogger(TensorWebController.class);

  private ImageEvaluator evaluator;
  private ImageResultCreator imageResultCreator;
  private MailClient mailClient;
  private float confidence;


  @Autowired
  public TensorWebController(ImageEvaluator evaluator, ImageResultCreator imageResultCreator, MailClient mailClient,@Value("${tensor.confidenceLimit}") float confidence) {
    this.evaluator = evaluator;
    this.imageResultCreator = imageResultCreator;
    this.mailClient = mailClient;
    this.confidence = confidence;
  }

  @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public PredictionResults predictFileUpload(@RequestParam("image") MultipartFile file) throws IOException {
    logger.info("starting prediction ");
    PredictionResults unfilteredResults = this.evaluator.evaluate(file.getBytes());
    logger.info("prediction done - results"+unfilteredResults.getResults().size());
    List<PredictionResult> predictionResults = unfilteredResults.asStream()
        .filter(item -> item.getConfidence() > confidence)
        .collect(Collectors.toList());
    logger.info("filtered - results"+predictionResults.size());
    String encodedImage = imageResultCreator.createResultImage(file.getBytes(),predictionResults);
    return PredictionResults.newBuilder().results(predictionResults).image(encodedImage).build();
  }

  @PostMapping()
  public ModelAndView detectAsHtml(@RequestParam("image") MultipartFile file) throws IOException {

    ModelAndView model = new ModelAndView("pretty");
    PredictionResults result = this.predictFileUpload(file);
    model.addObject("results",result.getResults());
    model.addObject("image", result.getImage());

    return model;
  }

  @PostMapping("/detect/mailTo")
  public void mailIfResult(@RequestParam("image") MultipartFile file,@RequestParam("mailTo") String mailTo) throws IOException {
    PredictionResults results = this.predictFileUpload(file);
    if (!results.getResults().isEmpty()) {
      logger.info("sending email");
//      this.mailClient.sendResultsTo(mailTo, results);
    }
  }

}
