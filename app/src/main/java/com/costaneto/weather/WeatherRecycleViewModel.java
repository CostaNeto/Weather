package com.costaneto.weather;

//The RecycleView model is a value holder
//It stores the values to be displayed
//in each item of the RecycleView

public class WeatherRecycleViewModel {

    private String time;
    private String temperature;
    private String icon;
    public int isDay;

    public WeatherRecycleViewModel(String time, String temperature, String icon, int isDay) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
        this.isDay = isDay;
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }

    public int getIsDay() {
        return isDay;
    }

}
