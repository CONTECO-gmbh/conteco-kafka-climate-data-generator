package gmbh.conteco.kafka.climatedatagenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    Double temperature;
    Double wind;
    Double rain;
}
