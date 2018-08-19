package uk.co.stevegal.tensorweb.controller;

import java.io.IOException;

/**
 * Created by stevegal on 18/08/2018.
 */
public interface ImageEvaluator {

  PredictionResults evaluate(byte[] imageBytes) throws IOException;
}
