package gmbh.conteco.kafka.climatedatagenerator.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gmbh.conteco.kafka.climatedatagenerator.model.Weather;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class WeatherSerializer implements Serializer<Weather> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, Weather weather) {
        if (weather == null) {
            return null;
        } else {
            try {
                return objectMapper.writeValueAsBytes(weather);
            } catch (JsonProcessingException e) {
                throw new SerializationException("Error serializing Weather to byte[].");
            }
        }
    }
}
