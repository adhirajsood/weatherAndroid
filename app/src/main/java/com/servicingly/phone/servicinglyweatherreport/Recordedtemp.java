package com.servicingly.phone.servicinglyweatherreport;

/**
 * Created by adhiraj on 11/8/15.
 */
import com.orm.SugarRecord;

/**
 * Created by adhiraj on 11/8/15.
 */
public class Recordedtemp extends SugarRecord<Recordedtemp> {
    protected String locationName;
    protected String temperature;
    protected Long locationtime;

    public Recordedtemp(){

    }

    public Recordedtemp(String locationName, String temperature,Long locationtime){
        this.locationName = locationName;
        this.temperature = temperature;
        this.locationtime = locationtime;
    }



    protected String getTemperature(){
        return this.temperature;
    }

    protected String getLocationName(){
        return this.locationName;
    }

}