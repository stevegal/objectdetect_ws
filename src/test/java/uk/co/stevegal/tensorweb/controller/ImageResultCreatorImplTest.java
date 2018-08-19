package uk.co.stevegal.tensorweb.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by stevegal on 19/08/2018.
 */
public class ImageResultCreatorImplTest {

  @Test
  public void drawsBoxesInImage() throws Exception {
    ClassPathResource cpr = new ClassPathResource("testImage.jpg");
    BufferedImage testImage = ImageIO.read(cpr.getInputStream());
    ImageResultCreator creator = new ImageResultCreatorImpl();


    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write( testImage, "jpg", baos );
    baos.flush();
    byte[] imageInByte = baos.toByteArray();
    baos.close();

    List<PredictionResult> results = new ArrayList<>();
    // note result is ymin, xmin, ymax, xmax !!!
    results.add(PredictionResult.newBuilder().label("a").boundingBox(new float[]{0.0f,0.0f,0.5f, 0.5f}).build());

    String bufferedImage = creator.createResultImage(imageInByte, results);

    ClassPathResource expectedResult = new ClassPathResource("expectedResult.txt");
    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
    IOUtils.copy(expectedResult.getInputStream(),baos2);
    String expected = new String(baos2.toByteArray());
    assertThat(bufferedImage).isEqualTo(expected);


  }

}