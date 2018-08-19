# objectdetect_ws
web service: java object detection using tensor flow

# Note's
bring in frozen model from [model zoo](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/detection_model_zoo.md) -
a set of pretrained models. copy in the frozen_inference_graph from the model you want to try

Using object detection download from [tensorflow models](https://github.com/tensorflow/models)
make sure you copy in the labels from
```properties
object_detection/data/mscoco_label_map.pbtxt
```


use ```protoc``` to generate src code from models to read in the labels:

e.g
```bash
protoc object_detection/protos/*.proto --java_out=${yourprojectroot}/src/main/java 
```

### TODO
 - [ ] generate output image with bounding boxes
 - [ ] add results threshold
 - [ ] convert label if to display text 
