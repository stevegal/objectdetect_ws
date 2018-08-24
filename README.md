# objectdetect_ws
web service: java object detection using tensor flow

Note the first N runs will be slow as it initializes each of the new sessions and runs through the model, but after that it will fly

It's a spring boot app and you can tweak the settings using the properties file. Normal control of mail server (example below id for google)
I've allowed up to 2MB in the file uploader in the example below. Change to suit.

I've also filtered out results based on confidence level and label and that controlled from the properties as well


```yaml
server:
  port: 9000
spring:
  mail:
    properties:
      mail:
        smtp:
          starttls:
            required: true
            enable: true
          auth: true

    host: smtp.gmail.com
    port: 587
    username: XXX
    password: XXX

  servlet:
    multipart:
      max-file-size: 2MB

tensor:
  labelPath: mscoco_label_map.pbtxt
  modelPath: frozen_inference_graph.pb
  confidenceLimit: 0.4
  maxPoolSize: 5
  include:
    - dog
    - cat
    - person
    - car
  ```
If you build it, you can run the server with
```bash
java -jar objdectect4j-0.1-SNAPSHOT.jar --spring.config.location=yourProperties.properties
```

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
 - [x] generate output image with bounding boxes
 - [x] add results threshold
 - [x] convert label if to display text
 - [ ] allow colours etc for bounding boxes
