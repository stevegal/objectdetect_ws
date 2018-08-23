package uk.co.stevegal.tensorweb.controller;

import object_detection.protos.StringIntLabelMapOuterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.types.UInt8;
import uk.co.stevegal.tensorweb.controller.ImageEvaluator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by stevegal on 18/08/2018.
 */
public class TensorflowImageEvaluator implements ImageEvaluator {

  private Map<Integer, String> map;
  final long BATCH_SIZE = 1;
  final long CHANNELS = 3;

  Logger logger = LoggerFactory.getLogger(TensorflowImageEvaluator.class);
  private BlockingQueue<Session> sessions;

  public TensorflowImageEvaluator(BlockingQueue<Session> sessions, StringIntLabelMapOuterClass.StringIntLabelMap map) {
    this.sessions = sessions;
    this.map = map.getItemList().stream().collect(Collectors.toMap(StringIntLabelMapOuterClass.StringIntLabelMapItem::getId, item -> item.getDisplayName()));
  }

  @Override
  public PredictionResults evaluate(byte[] imageBytes) throws IOException {
    PredictionResults.PredictionResultsBuilder resultsBuilder = PredictionResults.newBuilder();

    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
    BufferedImage image = ImageIO.read(inputStream);
    long[] shape = new long[]{BATCH_SIZE, image.getHeight(), image.getWidth(), CHANNELS};
    long start = System.currentTimeMillis();
    Tensor tensor = Tensor.create(UInt8.class, shape, ByteBuffer.wrap(((DataBufferByte) image.getData().getDataBuffer()).getData()));
    start = logTimeDiff("create  time:", start);
    List<Tensor<?>> outputs = null;

    start = logTimeDiff("session  time:", start);

    try {
      Session session = this.sessions.take();
      Session.Runner runner = session
          .runner()
          .feed("image_tensor", tensor)
          .fetch("detection_scores")
          .fetch("detection_classes")
          .fetch("detection_boxes");
      start = logTimeDiff("runner setup  time:" ,start);
      outputs = runner.run();
      start = logTimeDiff("run time:" , start);

      try (Tensor<Float> scoresT = outputs.get(0).expect(Float.class);
           Tensor<Float> classesT = outputs.get(1).expect(Float.class);
           Tensor<Float> boxesT = outputs.get(2).expect(Float.class)) {
        // All these tensors have:
        // - 1 as the first dimension
        // - maxObjects as the second dimension
        // While boxesT will have 4 as the third dimension (2 sets of (x, y) coordinates).
        // This can be verified by looking at scoresT.shape() etc.
        int maxObjects = (int) scoresT.shape()[1];
        float[] scores = scoresT.copyTo(new float[1][maxObjects])[0];
        float[] classes = classesT.copyTo(new float[1][maxObjects])[0];
        float[][] boxes = boxesT.copyTo(new float[1][maxObjects][4])[0];

        for (int i = 0; i < maxObjects; i++) {
          String label = map.get(Integer.valueOf((int) classes[i]));
          resultsBuilder.result(PredictionResult.newBuilder().confidence(scores[i]).label(label).boundingBox(boxes[i]).build());
        }
      }
      start = logTimeDiff("results time:", start);
      this.sessions.put(session);
    } catch (InterruptedException e) {
      logger.error("interupt while gett/putting session into queue");
    }
    return resultsBuilder.build();
  }

  private long logTimeDiff(String type,long start) {
    logger.info(type + (System.currentTimeMillis() - start));
    return System.currentTimeMillis();
  }
}
