package uk.co.stevegal.tensorweb.controller;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by stevegal on 24/08/2018.
 */
@Configuration
@ConfigurationProperties("tensor")
public class TensorProperties {
  private List<String> include;
  private String labelPath;
  private String modelPath;
  private float confidenceLimit;
  private int maxPoolSize;

  public List<String> getInclude() {
    return include;
  }

  public void setInclude(List<String> include) {
    this.include = include;
  }

  public float getConfidenceLimit() {
    return confidenceLimit;
  }

  public void setConfidenceLimit(float confidenceLimit) {
    this.confidenceLimit = confidenceLimit;
  }

  public int getMaxPoolSize() {
    return maxPoolSize;
  }

  public void setMaxPoolSize(int maxPoolSize) {
    this.maxPoolSize = maxPoolSize;
  }

  public String getLabelPath() {
    return labelPath;
  }

  public void setLabelPath(String labelPath) {
    this.labelPath = labelPath;
  }

  public String getModelPath() {
    return modelPath;
  }

  public void setModelPath(String modelPath) {
    this.modelPath = modelPath;
  }
}
