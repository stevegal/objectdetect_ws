package uk.co.stevegal.tensorweb.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by stevegal on 18/08/2018.
 */
public class PredictionResults {

  private PredictionResults(PredictionResultsBuilder builder) {
    this.results = Collections.unmodifiableList(builder.results);
  }

  private List<PredictionResult> results;

  public List<PredictionResult> getResults() {
    return results;
  }

  public static PredictionResultsBuilder newBuilder() {
    return new PredictionResultsBuilder();
  }

  public static class PredictionResultsBuilder {
    private List<PredictionResult> results= new ArrayList<>();
    private PredictionResultsBuilder(){
    }

    public PredictionResultsBuilder result(PredictionResult result){
      this.results.add(result);
      return this;
    }

    public PredictionResults build(){
      return new PredictionResults(this);
    }
  }
}
