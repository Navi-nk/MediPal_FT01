package sg.edu.nus.iss.medipal.pojo;

import java.util.Date;

/**
 * Created by divah on 3/25/2017.
 */

public class Temperature extends Measurement {

    private float temperature;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public Temperature(int id, Date measuredOn, float temperature) {
        super(id, measuredOn);
        this.temperature = temperature;
    }

    public Temperature(Date measuredOn, float temperature) {
        super(measuredOn);
        this.temperature = temperature;
    }
}