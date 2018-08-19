package uk.co.stevegal.tensorweb.controller;

import java.io.IOException;
import java.util.List;

/**
 * Created by stevegal on 19/08/2018.
 */
public interface ImageResultCreator {

  /**
   * produces a base64 encoded image for response
   * @param inputImage
   * @param resultsToDraw
   * @return
   * @throws IOException
   */
  String createResultImage(byte[] inputImage, List<PredictionResult> resultsToDraw) throws IOException;
}
