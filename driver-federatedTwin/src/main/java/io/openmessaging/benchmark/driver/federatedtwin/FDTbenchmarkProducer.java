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
package io.openmessaging.benchmark.driver.federatedtwin;


import io.openmessaging.benchmark.driver.BenchmarkProducer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class FDTbenchmarkProducer implements BenchmarkProducer {

    private final Producer<String, String> producer;
    private final String topic;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public FDTbenchmarkProducer(Producer<String, String> producer, String topic) {
        this.producer = producer;
        this.topic = "UnalliedDigitalObjects";
    }

    //"KR-1-TEST-00001" ~ "KR-1-TEST-04000" ID 생성
    private String generateRandomDigitalObjectId() {
        Random random = new Random();
        int randomValue = random.nextInt(4000) + 1;
        return String.format("KR-1-TEST-%05d", randomValue);
    }

    @Override
    public CompletableFuture<Void> sendAsync(Optional<String> key, byte[] payload) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String digitalObjectId = generateRandomDigitalObjectId();

        String payloadJson = String.format("{\"digital_twin_id\": \"KR-02-K10000-20240001\",\"digital_object_id\": \"%s\",\"data\": \"{\\\"payload\\\":\\\"fbb0cb87a24408039b4f07c89929b5f619a682ecf61a1d97d495e38f876729ddc5a74c04d2fe9382b5eb85379d0f3027fece\\\"}\",\"rowtime\": \"%s\",\"location\": \"POINT EMPTY\"}",
                    digitalObjectId, dateFormat.format(timestamp));

        String keyString = String.format("{\"digital_object_id\": \"%s\"}", digitalObjectId);
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
