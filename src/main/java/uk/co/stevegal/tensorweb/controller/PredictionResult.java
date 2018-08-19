package uk.co.stevegal.tensorweb.controller;

/**
 * Created by stevegal on 18/08/2018.
 */
public class PredictionResult {

  private float confidence;

  private String label;

  public PredictionResult(PredictionResultBuilder predictionResultBuilder) {
    this.confidence = predictionResultBuilder.confidence;
    this.label = predictionResultBuilder.label;
  }

  public float getConfidence() {
    return confidence;
  }

  public String getLabel() {
    return label;
  }

  public static PredictionResultBuilder newBuilder() {
    return new PredictionResultBuilder();
  }

  public static class PredictionResultBuilder {
    private float confidence;
    private String label;

    private PredictionResultBuilder(){
    }

    public PredictionResultBuilder confidence(float confidence){
      this.confidence = confidence;
      return this;
    }
    public PredictionResultBuilder label(String label){
      this.label = label;
      return this;
    }

    public PredictionResult build() {
      return new PredictionResult(this);
    }

  }
}
