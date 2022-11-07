package gmbh.conteco.kafka.climatedatagenerator.service;

import com.google.common.collect.Lists;
import gmbh.conteco.kafka.climatedatagenerator.functional.TriFunction;
import gmbh.conteco.kafka.climatedatagenerator.model.Weather;
import gmbh.conteco.kafka.climatedatagenerator.serializer.WeatherSerializer;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Shell;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherService {
    public List<Weather> sendWeatherDataToKafka() throws IOException {
        final Properties properties = new Properties();
        try (final InputStream stream =
                     this.getClass().getResourceAsStream("/application.properties")) {
            properties.load(stream);
        }

        // Kafka properties for SerDes
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, WeatherSerializer.class.getName());

        // Admin client for create topics automatically
        try(Admin adminClient = Admin.create(properties);
            Producer<String, Weather> producer = new KafkaProducer<>(properties)) {

            // Customized properties
            final int number = Integer.parseInt(properties.getProperty("gmbh.conteco.kafka.climatedatagenerator.number.entries"));
            final String inputTopic = properties.getProperty("gmbh.conteco.kafka.climatedatagenerator.input.topic");
            final String outputTopic = properties.getProperty("gmbh.conteco.kafka.climatedatagenerator.output.topic");
            final int partitions = Integer.parseInt(properties.getProperty("gmbh.conteco.kafka.climatedatagenerator.partitions"));
            final short replicationFactor = Short.parseShort(properties.getProperty("gmbh.conteco.kafka.climatedatagenerator.replication.factor"));

            // Create topics
            var topics = List.of(new NewTopic(inputTopic, partitions, replicationFactor),
                    new NewTopic(outputTopic, partitions, replicationFactor));
            adminClient.createTopics(topics);

            Callback callback = (metadata, exception) -> {
                if(exception != null)
                    System.out.printf("Producing records encountered error %s %n", exception);
                else
                    System.out.printf("Record produced - offset - %d timestamp - %d %s %n", metadata.offset(),
                            metadata.timestamp(), LocalDateTime.now());
            };

            var weatherData = getWeatherData(number);

            var producerRecords = weatherData.stream()
                    .map(r -> new ProducerRecord<>(inputTopic,"weather-key", r))
                    .collect(Collectors.toList());
            producerRecords.forEach((record -> producer.send(record, callback)));

            return weatherData;
        }
    }

    private List<Weather> getWeatherData(int number) {
        var temperatures = getNumbers(16, 42, number);
        var wind = getNumbers(3, 120, number);
        var rain = getNumbers(35, 107, number);

        // Create the list of type Weather with a specified number of entries
        return zip(temperatures, wind, rain, Weather::new);
    }

    private <A, B, C, R> List<R> zip(List<A> a, List<B> b, List<C> c, TriFunction<A, B, C, R> r) {
        return Lists.newArrayList(zip(a.iterator(), b.iterator(), c.iterator(), r));
    }

    private <A, B, C, R> Iterator<R> zip(Iterator<A> a, Iterator<B> b, Iterator<C> c, TriFunction<A, B, C, R> r) {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return a.hasNext() && b.hasNext() && c.hasNext();
            }

            @Override
            public R next() {
                return r.apply(a.next(), b.next(), c.next());
            }
        };
    }

    private List<Double> getNumbers(int start, int end, int number) {
        List<Double> values = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            values.add(start + Math.random() * (end - start));
        }
        return values;
    }
}
