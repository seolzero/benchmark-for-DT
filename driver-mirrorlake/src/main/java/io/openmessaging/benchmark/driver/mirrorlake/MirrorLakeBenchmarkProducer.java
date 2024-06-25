/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.openmessaging.benchmark.driver.mirrorlake;


import io.openmessaging.benchmark.driver.BenchmarkProducer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MirrorLakeBenchmarkProducer implements BenchmarkProducer {

    private final Producer<String, String> producer;
    private final String topic;
    private final List<String> sensorId =
            Arrays.asList(
                    "S45f67fdc10821e9189a21d126774df43",
                    "S44ca97b40a9b764d8f49964d41af1327",
                    "S48519140c24a30868a4e427780d54fdb",
                    "S49279d2a7d5a2bb9ab1566a92b12449d",
                    "S42815af3b6ed779ea3c9890bb1dcf5d1",
                    "S4fd282e96dff1378a09209cafa3e658f",
                    "S45c209829280768c8748a9e708b82793",
                    "S440625ad446b7202a3ac6a1d7518b125",
                    "S441ac3840d361031bafddb99a7dad934",
                    "S4a27e3d6211f34f1a63bbb41648f510a",
                    "S4b880a169d4c7ec8b1b9ed4503a32c55",
                    "S45082d9969bf1870a4b36cdd9d2102bd");
    private String digitalTwinId = "D4d0a7f6124c9f92b9ef0ddb4302dd3d5";
    private String location = "POINT EMPTY";
    private int i = 0;

    public MirrorLakeBenchmarkProducer(Producer<String, String> producer, String topic) {
        this.producer = producer;
        this.topic = "Sensors";
    }

    @Override
    public CompletableFuture<Void> sendAsync(Optional<String> key, byte[] payload) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        // LocalDateTime dateTime = LocalDateTime.now();
        // String nanos = String.valueOf(System.nanoTime() % 1000000000);

        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        // String date = (String) dateTime.format(formatter);
        String payloadString = new String(payload);
        Random rand = new Random();
        String pickedSensor = sensorId.get(rand.nextInt(12));
        String payloadJson =
                "{\"digital_twin_id\": "
                        + "\""
                        + digitalTwinId
                        + "\","
                        + "\"sensor_id\": "
                        + "\""
                        + pickedSensor
                        + "\","
                        + "\"data\": "
                        + "\"{\\\"payload\\\":\\\""
                        + i++
                        + "\\\"}\","
                        + "\"rowtime\": "
                        + "\""
                        // + dateTime.format(formatter)
                        + dateFormat.format(timestamp)
                        // + date + "." + nanos
                        + "\","
                        + "\"location\": \""
                        + location
                        + "\"}";
        String keyString = "{\"sensor_id\": " + "\"" + pickedSensor + "\"}";
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, keyString, payloadJson);

        CompletableFuture<Void> future = new CompletableFuture<>();

        producer.send(
                record,
                (metadata, exception) -> {
                    if (exception != null) {
                        future.completeExceptionally(exception);
                    } else {
                        future.complete(null);
                    }
                });

        return future;
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }
}
