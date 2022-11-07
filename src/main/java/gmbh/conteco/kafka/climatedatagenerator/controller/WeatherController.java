package gmbh.conteco.kafka.climatedatagenerator.controller;

import gmbh.conteco.kafka.climatedatagenerator.model.Weather;
import gmbh.conteco.kafka.climatedatagenerator.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class WeatherController {
    private WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
   public List<Weather> send() throws IOException {
        return weatherService.sendWeatherDataToKafka();
   }
}
