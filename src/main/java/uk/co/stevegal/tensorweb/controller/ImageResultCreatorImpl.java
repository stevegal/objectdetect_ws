package uk.co.stevegal.tensorweb.controller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * Created by stevegal on 19/08/2018.
 */
public class ImageResultCreatorImpl implements ImageResultCreator {

  @Override
  public String createResultImage(byte[] imageBytes, List<PredictionResult> resultsToDraw) throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
    BufferedImage image = ImageIO.read(inputStream);
    int totalWidth = image.getWidth();
    int totalHeight = image.getHeight();
    Graphics2D graphics = (Graphics2D)image.getGraphics();
    graphics.setStroke(new BasicStroke(5));
    graphics.setColor(Color.BLUE);
    for (PredictionResult result: resultsToDraw) {
      float[] boundingBox = result.getBoundingBox();
      int startY = (int) (boundingBox[0]* totalHeight);
      int startX = (int) (boundingBox[1]* totalWidth);
      int endY = (int) (boundingBox[2]*totalHeight);
      int endX = (int) (boundingBox[3]* totalWidth);
      graphics.drawRect(startX,startY,(endX-startX), endY - startY);
      graphics.drawString(result.getLabel(),startX+5, startY+10);
    }
    graphics.dispose();
    ByteArrayOutputStream baos=new ByteArrayOutputStream();
    ImageIO.write(image,"jpg",baos);
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }
}
