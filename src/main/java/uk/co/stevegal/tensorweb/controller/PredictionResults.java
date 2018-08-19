package uk.co.stevegal.tensorweb.controller;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by stevegal on 18/08/2018.
 */
public class PredictionResults {

  private PredictionResults(PredictionResultsBuilder builder) {
    this.results = Collections.unmodifiableList(builder.results);
    this.image = builder.resultImage;
  }

  public Stream<PredictionResult> asStream(){
    return this.results.stream();
  }

  private List<PredictionResult> results;

  private String image;

  public String getImage() {
    return image;
  }

  public List<PredictionResult> getResults() {
    return results;
  }

  public static PredictionResultsBuilder newBuilder() {
    return new PredictionResultsBuilder();
  }

  public static class PredictionResultsBuilder {
    private List<PredictionResult> results= new ArrayList<>();
    private String resultImage;

    private PredictionResultsBuilder(){
    }

    public PredictionResultsBuilder result(PredictionResult result){
      this.results.add(result);
      return this;
    }

    public PredictionResults build(){
      return new PredictionResults(this);
    }

    public PredictionResultsBuilder results(List<PredictionResult> predictionResults) {
      this.results.addAll(predictionResults);
      return this;
    }

    public PredictionResultsBuilder image(String resultImage) {
      this.resultImage = resultImage;
      return this;
    }
  }
}
