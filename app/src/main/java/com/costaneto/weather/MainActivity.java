package com.costaneto.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRelativeLayout, iconHolderRelativeLayout, mainAqiInfoHolder, allAqiInfoHolder;
    private EditText cityInputEditText;
    private RecyclerView weatherRecycleView;
    private ImageView searchImageView, conditionImageView, panelImageView, forecastConditionIcon, forecastConditionIcon2, forecastConditionIcon3, weatherApiSourceImageView;
    private TextView cityNameTextView, temperatureTextView, conditionTextView, feelsLikeTempTextView, forecastDay1TextView, forecastDay2TextView, forecastDay3TextView, aqiDescriptionTextView, aqiIndexValueTextView, pm25IVTextView, pm10IVTextView, coIVTextView, so2IVTextView, no2IVTextView, o3IVTextView, noAqiDataTextView, forecastMaxTempTextView, forecastMaxTempTextView2, forecastMaxTempTextView3, forecastMinTempTextView, forecastMinTempTextView2, forecastMinTempTextView3, waqiTextView;
    private View pm25View, pm10View, coView, so2View, no2View, o3View;
    private ProgressBar loadingScreen;
    private LinearLayout aqiExternalLinkLL;

    private ArrayList<WeatherRecycleViewModel> weatherRVModelArrayList;
    private WeatherRecycleViewAdapter weatherRecycleViewAdapter;

    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int PERMISSION_CODE = 1, isDay = 1;

    private Animation animFadeIn;

    private RequestQueue cityNameRequest, weatherInfoRequest;

    // First API to obtain city name
    private String getNameURL = "", getName_cityName = "";
    private Double longitude, latitude;


    @SuppressLint({"LongLogTag", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        // set full screen, no action bar in the app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        // "Loading/Splash" screen
        loadingScreen = findViewById(R.id.loadingProgressBar);

        // Parent layout
        homeRelativeLayout = findViewById(R.id.homeRelativeLayout);

        // Search
        searchImageView = findViewById(R.id.searchImageView);


        // Main panel
        panelImageView = findViewById(R.id.panelImageView);
        cityNameTextView = findViewById(R.id.cityNameTextView);
        iconHolderRelativeLayout = findViewById(R.id.iconHolderRelativeLayout);
        conditionImageView = findViewById(R.id.conditionImageView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        feelsLikeTempTextView = findViewById(R.id.feelsLikeTempTextView);
        conditionTextView = findViewById(R.id.conditionTextView);

        // Hourly Forecast
        weatherRecycleView = findViewById(R.id.weatherRecycleView);
        cityInputEditText = findViewById(R.id.cityInputEditText);

        // Daily Forecast
        forecastDay1TextView = findViewById(R.id.forecastDay1TextView);
        forecastDay2TextView = findViewById(R.id.forecastDay2TextView);
        forecastDay3TextView = findViewById(R.id.forecastDay3TextView);

        forecastConditionIcon = findViewById(R.id.forecastConditionIcon);
        forecastConditionIcon2 = findViewById(R.id.forecastConditionIcon2);
        forecastConditionIcon3 = findViewById(R.id.forecastConditionIcon3);

        forecastMaxTempTextView = findViewById(R.id.forecastMaxTempTextView);
        forecastMaxTempTextView2 = findViewById(R.id.forecastMaxTempTextView2);
        forecastMaxTempTextView3 = findViewById(R.id.forecastMaxTempTextView3);

        forecastMinTempTextView = findViewById(R.id.forecastMinTempTextView);
        forecastMinTempTextView2 = findViewById(R.id.forecastMinTempTextView2);
        forecastMinTempTextView3 = findViewById(R.id.forecastMinTempTextView3);

        // AQI panel
        allAqiInfoHolder = findViewById(R.id.allAqiInfoHolder);
        mainAqiInfoHolder = findViewById(R.id.mainAqiInfoHolder);

        aqiDescriptionTextView = findViewById(R.id.aqiDescriptionTextView);
        aqiIndexValueTextView = findViewById(R.id.aqiIndexValueTextView);
        pm25IVTextView = findViewById(R.id.pm25IVTextView);
        pm10IVTextView = findViewById(R.id.pm10IVTextView);
        coIVTextView = findViewById(R.id.coIVTextView);
        so2IVTextView = findViewById(R.id.so2IVTextView);
        no2IVTextView = findViewById(R.id.no2IVTextView);
        o3IVTextView = findViewById(R.id.o3IVTextView);
        noAqiDataTextView = findViewById(R.id.noAqiDataTextView);

        pm25View = findViewById(R.id.pm25View);
        pm10View = findViewById(R.id.pm10View);
        coView = findViewById(R.id.coView);
        so2View = findViewById(R.id.so2View);
        no2View = findViewById(R.id.no2View);
        o3View = findViewById(R.id.o3View);

        // Attributions
        aqiExternalLinkLL = findViewById(R.id.aqiExternalLinkLL);
        waqiTextView = findViewById(R.id.waqiTextView);
        weatherApiSourceImageView = findViewById(R.id.weatherApiSourceImageView);


        weatherRVModelArrayList = new ArrayList<>();
        weatherRecycleViewAdapter = new WeatherRecycleViewAdapter(this, weatherRVModelArrayList);
        weatherRecycleView.setAdapter(weatherRecycleViewAdapter);

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        cityNameRequest = Volley.newRequestQueue(this);
        weatherInfoRequest = Volley.newRequestQueue(this);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        getCurrentLocation();

        // Get city name from first API
        getCityName(latitude, longitude);

        new CountDownTimer(2000, 1000) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {

                // Display weather info of current location
                getWeatherInfo(getName_cityName);

                loadingScreen.setVisibility(View.GONE);
                animFadeIn.reset();
                homeRelativeLayout.startAnimation(animFadeIn);
                homeRelativeLayout.setVisibility(View.VISIBLE);
            }
        }.start();


        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityInputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                String citySearch = Objects.requireNonNull(cityInputEditText.getText()).toString();
                cityInputEditText.getText().clear();
                if (citySearch.isEmpty())
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                else {
                    cityNameTextView.setText(citySearch);
                    getWeatherInfo(citySearch);
                }
            }
        });

        aqiExternalLinkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.airnow.gov/aqi/aqi-basics/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        waqiTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://waqi.info/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        weatherApiSourceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.weatherapi.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    } else {
                        // When location is null
                        // Initialize location request
                        LocationRequest locationRequest = LocationRequest.create()
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                Location location1 = locationResult.getLastLocation();
                                latitude = location1.getLatitude();
                                longitude = location1.getLongitude();
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            // When location service is not enabled
            // open location settings
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    @SuppressLint("LongLogTag")
    private void getCityName(Double latitude, Double longitude) {

        getNameURL = "https://api.weatherbit.io/v2.0/current?lat=" + latitude + "&lon=" + longitude + "&key=096c7ef0785d4948a7fee9694f6d4d6f";

        StringRequest myRequest = new StringRequest(Request.Method.GET, getNameURL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        getName_cityName = StringUtils.stripAccents(jsonObject.getJSONArray("data").getJSONObject(0).getString("city_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(MainActivity.this, "Couldn't detect location. Try searching", Toast.LENGTH_LONG).show();
                }
        );

        cityNameRequest.add(myRequest);
    }

    @SuppressLint("LongLogTag")
    private void getWeatherInfo(String cityName) {
        // total day count = 8 (Today + 7 for forecast starting from tomorrow)
        String city = StringUtils.stripAccents(cityName);
        String url = "http://api.weatherapi.com/v1/forecast.json?key=5fc40f1ab42e46f681b174505212010&q=" + city + "&days=8&aqi=yes&alerts=yes";
        cityNameTextView.setText(cityName);

        // Weather Information
        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n", "SimpleDateFormat"})
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,null,
                response -> {
                    weatherRVModelArrayList.clear();

                    try {

                        // Weather info for main Panel
                        String temp1 = response.getJSONObject("current").getString("temp_c");
                        String temp2 = temp1.replace(".", "");
                        String temperature = temp2.substring(0, temp2.length() - 1);
                        temperatureTextView.setText(temperature + "\u00b0C");

                        String feelsLike1 = response.getJSONObject("current").getString("feelslike_c");
                        String feelsLike2 = feelsLike1.replace(".", "");
                        String feelsLikeTemp = feelsLike2.substring(0, feelsLike2.length() - 1);
                        feelsLikeTempTextView.setText(feelsLikeTemp + "\u00b0C");

                        String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                        conditionTextView.setText(condition);

                        String conditionIconUrl = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                        Picasso.get().load("http:".concat(conditionIconUrl)).into(conditionImageView);

                        // Background changes
                        isDay = response.getJSONObject("current").getInt("is_day");
                        if (isDay == 1){
                            iconHolderRelativeLayout.setBackgroundResource(R.drawable.condition_icon_background1);
                            panelImageView.setImageResource(R.drawable.day_panel_background_1);
                        }
                        else {
                            iconHolderRelativeLayout.setBackgroundResource(R.drawable.condition_icon_background2);
                            panelImageView.setImageResource(R.drawable.night_panel_background_1);
                        }


                        // Get information for Hourly Forecast
                        JSONObject forecastObject = response.getJSONObject("forecast");
                        JSONObject forecast0 = forecastObject.getJSONArray("forecastday").getJSONObject(0);
                        JSONArray hourArray = forecast0.getJSONArray("hour");

                        // current hour needs to be compared with forecast hours to find a match (it will start displaying from current hour)
                        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                        // used to count the number of forecast hours of current day displayed, starting from current hour
                        int hourCount = 0;

                        // used in various operations when displaying forecast content
                        String forecastTemp1 = "", forecastTemp2 = "", forecastTemperature = "", forecastConditionIconUrl = "", forecastHour = "", timeFromAPI = "";


                        // get and set hourly weather info

                        //needed for date format parsing
                        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        SimpleDateFormat outputForecast = new SimpleDateFormat("h a");
                        SimpleDateFormat outputComparison = new SimpleDateFormat("H"); // for comparison between current hour and obtained hour

                        for (int i = 0; i < hourArray.length(); i++) {
                            JSONObject hourObject = hourArray.getJSONObject(i);

                            // format the temperature text: remove decimal value and decimal sign (dot)
                            forecastTemp1 = hourObject.getString("temp_c");
                            forecastTemp2 = forecastTemp1.replace(".", "");
                            forecastTemperature = forecastTemp2.substring(0, forecastTemp2.length() - 1);
                            forecastConditionIconUrl = hourObject.getJSONObject("condition").getString("icon");

                            // parse the date obtained to get only the hour
                            timeFromAPI = hourObject.getString("time");
                            forecastHour = "";
                            int comparisonHour = 0;
                            try {
                                Date parsedTime = input.parse(timeFromAPI);
                                assert parsedTime != null;
                                forecastHour = outputForecast.format(parsedTime);
                                comparisonHour = Integer.parseInt(outputComparison.format(parsedTime));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            // start displaying from current hour
                            if (comparisonHour >= currentHour) {
                                if (comparisonHour == currentHour) {
                                    forecastHour = "Now";
                                    forecastTemperature = temperature; // sometimes the forecast temp of current time isn't the same as current temp
                                }
                                weatherRVModelArrayList.add(new WeatherRecycleViewModel(forecastHour, forecastTemperature, forecastConditionIconUrl, isDay));
                                hourCount++; // keep count of number of hours displayed in current day
                            }
                        }

                        // in case hourCount < 24, start request for hours from next day -> forecast1
                        if (hourCount < 24) {
                            JSONObject forecast1 = forecastObject.getJSONArray("forecastday").getJSONObject(1);
                            JSONArray nextDayHourArray = forecast1.getJSONArray("hour");
                            for (int i = 0; i < 24 - hourCount; i++) {
                                JSONObject nextDayHourObject = nextDayHourArray.getJSONObject(i);
                                forecastTemp1 = nextDayHourObject.getString("temp_c");
                                forecastTemp2 = forecastTemp1.replace(".", "");
                                forecastTemperature = forecastTemp2.substring(0, forecastTemp2.length() - 1);
                                forecastConditionIconUrl = nextDayHourObject.getJSONObject("condition").getString("icon");
                                forecastHour = "";
                                timeFromAPI = nextDayHourObject.getString("time");
                                try {
                                    Date parsedTime = input.parse(timeFromAPI);
                                    assert parsedTime != null;
                                    forecastHour = outputForecast.format(parsedTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                weatherRVModelArrayList.add(new WeatherRecycleViewModel(forecastHour, forecastTemperature, forecastConditionIconUrl, isDay));
                            }
                        }

                        weatherRecycleViewAdapter.notifyDataSetChanged();

                        // Get information for Daily Forecast
                        String dateFromAPI, dayOfWeek = "",tempHolder1 = "", tempHolder2 = "", maxTemp ="", minTemp = "";
                        SimpleDateFormat inputForDailyForecast = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE");


                        // --------------------------------- DAY 1
                        // get and set day of week - first day is tomorrow
                        forecastDay1TextView.setText("Today");

                        // get and set condition icon
                        forecastConditionIconUrl = forecast0.getJSONObject("day").getJSONObject("condition").getString("icon");
                        Picasso.get().load("http:".concat(forecastConditionIconUrl)).into(forecastConditionIcon);

                        // get and set max temp
                        tempHolder1 = forecast0.getJSONObject("day").getString("maxtemp_c");
                        tempHolder2 = tempHolder1.replace(".", "");
                        maxTemp = tempHolder2.substring(0, tempHolder2.length() - 1);
                        forecastMaxTempTextView.setText(maxTemp + "\u00b0C");

                        // get and set min temp
                        tempHolder1 = forecast0.getJSONObject("day").getString("mintemp_c");
                        tempHolder2 = tempHolder1.replace(".", "");
                        minTemp = tempHolder2.substring(0, tempHolder2.length() - 1);
                        forecastMinTempTextView.setText(minTemp + "\u00b0C");


                        // --------------------------------- DAY 2
                        JSONObject dailyForecast2 = forecastObject.getJSONArray("forecastday").getJSONObject(1);
                        // get and set day of week
                        forecastDay2TextView.setText("Tomorrow");

                        // get and set condition icon
                        forecastConditionIconUrl = dailyForecast2.getJSONObject("day").getJSONObject("condition").getString("icon");
                        Picasso.get().load("http:".concat(forecastConditionIconUrl)).into(forecastConditionIcon2);

                        // get and set max temp
                        tempHolder1 = dailyForecast2.getJSONObject("day").getString("maxtemp_c");
                        tempHolder2 = tempHolder1.replace(".", "");
                        maxTemp = tempHolder2.substring(0, tempHolder2.length() - 1);
                        forecastMaxTempTextView2.setText(maxTemp + "\u00b0C");

                        // get and set min temp
                        tempHolder1 = dailyForecast2.getJSONObject("day").getString("mintemp_c");
                        tempHolder2 = tempHolder1.replace(".", "");
                        minTemp = tempHolder2.substring(0, tempHolder2.length() - 1);
                        forecastMinTempTextView2.setText(minTemp + "\u00b0C");


                        // --------------------------------- DAY 3
                        JSONObject dailyForecast3 = forecastObject.getJSONArray("forecastday").getJSONObject(2);
                        // get and set day of week
                        dateFromAPI = dailyForecast3.getString("date");
                        try {
                            Date parsedDate = inputForDailyForecast.parse(dateFromAPI);
                            assert  parsedDate != null;
                            dayOfWeek = outputDayOfWeek.format(parsedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        forecastDay3TextView.setText(dayOfWeek);

                        // get and set condition icon
                        forecastConditionIconUrl = dailyForecast3.getJSONObject("day").getJSONObject("condition").getString("icon");
                        Picasso.get().load("http:".concat(forecastConditionIconUrl)).into(forecastConditionIcon3);

                        // get and set max temp
                        tempHolder1 = dailyForecast3.getJSONObject("day").getString("maxtemp_c");
                        tempHolder2 = tempHolder1.replace(".", "");
                        maxTemp = tempHolder2.substring(0, tempHolder2.length() - 1);
                        forecastMaxTempTextView3.setText(maxTemp + "\u00b0C");

                        // get and set min temp
                        tempHolder1 = dailyForecast3.getJSONObject("day").getString("mintemp_c");
                        tempHolder2 = tempHolder1.replace(".", "");
                        minTemp = tempHolder2.substring(0, tempHolder2.length() - 1);
                        forecastMinTempTextView3.setText(minTemp + "\u00b0C");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "Couldn't get weather data", Toast.LENGTH_SHORT).show()
        );
        weatherInfoRequest.add(jsonObjectRequest);


        /*
            Here we get the AQI information.
            We're using a new API, from the World Air Quality Index Project.
            We get the values and change the color of indicators accordingly.
         */
        String aqiUrl = "https://api.waqi.info/feed/" + city + "/?token=ed0060f8729cc5298a60ba3f3f6e408267302afa";
        @SuppressLint("SetTextI18n")
        JsonObjectRequest aqiDataRequest = new JsonObjectRequest(Request.Method.GET, aqiUrl, null, response -> {
            try {
//                Log.e("aqi_response", response.toString());

                // if country is in API's database
                if (response.getString("status").equals("ok")) {

                    allAqiInfoHolder.setVisibility(View.VISIBLE);
                    noAqiDataTextView.setVisibility(View.INVISIBLE);

                    // Overall AQI value
                    String aqiValue = response.getJSONObject("data").getString("aqi"); Log.e("AQI_VALUE", aqiValue);
                    aqiIndexValueTextView.setText(aqiValue);
                    // Colors and text for main aqi info area
                    int aqi = Integer.parseInt(aqiValue);
                    if (aqi >= 0 && aqi <=50) {
                        aqiDescriptionTextView.setText("Good");
                        mainAqiInfoHolder.setBackground(changeDrawableColor(this, R.drawable.extra_info_views_background, Color.parseColor("#7ECF7C")));
                    } else if (aqi > 50 && aqi <= 100) {
                        aqiDescriptionTextView.setText("Moderate");
                        mainAqiInfoHolder.setBackground(changeDrawableColor(this, R.drawable.extra_info_views_background, Color.parseColor("#FCEE70")));
                    } else if (aqi > 100 && aqi <= 150) {
                        aqiDescriptionTextView.setText("Unhealthy for sensitive groups");
                        mainAqiInfoHolder.setBackground(changeDrawableColor(this, R.drawable.extra_info_views_background, Color.parseColor("#FDAD50")));
                    } else if (aqi > 150 && aqi <= 200) {
                        aqiDescriptionTextView.setText("Unhealthy");
                        mainAqiInfoHolder.setBackground(changeDrawableColor(this, R.drawable.extra_info_views_background, Color.parseColor("#FF6262")));
                    } else if (aqi > 200 && aqi <= 300) {
                        aqiDescriptionTextView.setText("Very Unhealthy");
                        mainAqiInfoHolder.setBackground(changeDrawableColor(this, R.drawable.extra_info_views_background, Color.parseColor("#C17EBB")));
                    } else {
                        aqiDescriptionTextView.setText("Hazardous");
                        mainAqiInfoHolder.setBackground(changeDrawableColor(this, R.drawable.extra_info_views_background, Color.parseColor("#FF6262")));
                    }

                    // individual aqi JsonObject
                    JSONObject iaqiJsonObject = response.getJSONObject("data").getJSONObject("iaqi");
                    // check if iaqi values are present in Json file. if yes, then display them.
                    // otherwise, set placeholder text and the according neutral color
                    if (iaqiJsonObject.has("pm25")) {
                        String pm25Value = iaqiJsonObject.getJSONObject("pm25").getString("v"); Log.e("PM25_VALUE", pm25Value);
                        pm25IVTextView.setText(pm25Value);
                        double pm25 = Double.parseDouble(pm25Value);
                        if (pm25 >= 0 && pm25 <= 50)
                            pm25View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#7ECF7C")));
                        else if (pm25 >= 51 && pm25 <= 100)
                            pm25View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FCEE70")));
                        else if (pm25 >= 101 && pm25 <= 150)
                            pm25View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FDAD50")));
                        else if (pm25 >= 151 && pm25 <= 200)
                            pm25View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                        else if (pm25 >= 200 && pm25 <= 300)
                            pm25View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#C17EBB")));
                        else
                            pm25View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                    } else {
                        pm25IVTextView.setText("---");
                        pm25View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#898888")));
                    }
                    if (iaqiJsonObject.has("pm10")) {
                        String pm10Value = iaqiJsonObject.getJSONObject("pm10").getString("v"); Log.e("PM10_VALUE", pm10Value);
                        pm10IVTextView.setText(pm10Value);
                        double pm10 = Double.parseDouble(pm10Value);
                        if (pm10 >= 0 && pm10 <= 50)
                            pm10View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#7ECF7C")));
                        else if (pm10 >= 51 && pm10 <= 100)
                            pm10View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FCEE70")));
                        else if (pm10 >= 101 && pm10 <= 150)
                            pm10View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FDAD50")));
                        else if (pm10 >= 151 && pm10 <= 200)
                            pm10View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                        else if (pm10 >= 200 && pm10 <= 300)
                            pm10View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#C17EBB")));
                        else
                            pm10View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                    } else {
                        pm10IVTextView.setText("---");
                        pm10View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#898888")));
                    }
                    if (iaqiJsonObject.has("co")) {
                        String coValue =iaqiJsonObject.getJSONObject("co").getString("v"); Log.e("CO_VALUE", coValue);
                        coIVTextView.setText(coValue);
                        double co = Double.parseDouble(coValue);
                        if (co >= 0 && co <= 50)
                            coView.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#7ECF7C")));
                        else if (co >= 51 && co <= 100)
                            coView.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FCEE70")));
                        else if (co >= 101 && co <= 150)
                            coView.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FDAD50")));
                        else if (co >= 151 && co <= 200)
                            coView.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                        else if (co >= 200 && co <= 300)
                            coView.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#C17EBB")));
                        else
                            coView.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));

                    } else {
                        coIVTextView.setText("---");
                        coView.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#898888")));
                    }
                    if (iaqiJsonObject.has("so2")) {
                        String so2Value = iaqiJsonObject.getJSONObject("so2").getString("v"); Log.e("SO2_VALUE", so2Value);
                        so2IVTextView.setText(so2Value);
                        double so2 = Double.parseDouble(so2Value);
                        if (so2 >= 0 && so2 <= 50)
                            so2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#7ECF7C")));
                        else if (so2 >= 51 && so2 <= 100)
                            so2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FCEE70")));
                        else if (so2 >= 101 && so2 <= 150)
                            so2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FDAD50")));
                        else if (so2 >= 151 && so2 <= 200)
                            so2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                        else if (so2 >= 200 && so2 <= 300)
                            so2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#C17EBB")));
                        else
                            so2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                    } else {
                        so2IVTextView.setText("---");
                        so2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#898888")));
                    }
                    if (iaqiJsonObject.has("no2")) {
                        String no2Value = iaqiJsonObject.getJSONObject("no2").getString("v"); Log.e("NO2_VALUE", no2Value);
                        no2IVTextView.setText(no2Value);
                        double no2 = Double.parseDouble(no2Value);
                        if (no2 >= 0 && no2 <= 50)
                            no2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#7ECF7C")));
                        else if (no2 >= 51 && no2 <= 100)
                            no2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FCEE70")));
                        else if (no2 >= 101 && no2 <= 150)
                            no2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FDAD50")));
                        else if (no2 >= 151 && no2 <= 200)
                            no2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                        else if (no2 >= 200 && no2 <= 300)
                            no2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#C17EBB")));
                        else
                            no2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                    } else {
                        no2IVTextView.setText("---");
                        no2View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#898888")));
                    }
                    if (iaqiJsonObject.has("o3")) {
                        String o3Value = iaqiJsonObject.getJSONObject("o3").getString("v"); Log.e("O3_VALUE", o3Value);
                        o3IVTextView.setText(o3Value);
                        double o3 = Double.parseDouble(o3Value);
                        if (o3 >= 0 && o3 <= 50)
                            o3View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#7ECF7C")));
                        else if (o3 >= 51 && o3 <= 100)
                            o3View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FCEE70")));
                        else if (o3 >= 101 && o3 <= 150)
                            o3View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FDAD50")));
                        else if (o3 >= 151 && o3 <= 200)
                            o3View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                        else if (o3 >= 200 && o3 <= 300)
                            o3View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#C17EBB")));
                        else
                            o3View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#FF6262")));
                    } else {
                        o3IVTextView.setText("---");
                        o3View.setBackground(changeDrawableColor(this, R.drawable.iaqi_indicator, Color.parseColor("#898888")));
                    }

                }
                // if country isn't in API's database
                else {
                    allAqiInfoHolder.setVisibility(View.INVISIBLE);
                    noAqiDataTextView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(MainActivity.this, "Couldn't get AQI data", Toast.LENGTH_SHORT).show());

        weatherInfoRequest.add(aqiDataRequest);
    }

    public static Drawable changeDrawableColor(Context context,int background, int newColor) {
        Drawable mDrawable = Objects.requireNonNull(ContextCompat.getDrawable(context, background)).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "Please, provide permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}