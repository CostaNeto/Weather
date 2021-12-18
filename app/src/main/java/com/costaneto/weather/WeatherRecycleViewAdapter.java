package com.costaneto.weather;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRecycleViewAdapter extends RecyclerView.Adapter<WeatherRecycleViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherRecycleViewModel> weatherRVModelArrayList;

    public WeatherRecycleViewAdapter(Context context, ArrayList<WeatherRecycleViewModel> weatherRVModelArray) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArray;
    }


    @NonNull
    @Override
    public WeatherRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_recycle_view_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull WeatherRecycleViewAdapter.ViewHolder holder, int position) {
        WeatherRecycleViewModel model = weatherRVModelArrayList.get(position);
        holder.card_timeTextView.setText(model.getTime());
        holder.card_temperatureTextView.setText(model.getTemperature() + "\u00b0C");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.card_condition_image_view);
    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView card_timeTextView, card_temperatureTextView, card_wind_speed_text_view;
        private ImageView card_condition_image_view;
        private RelativeLayout cardRelativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRelativeLayout = itemView.findViewById(R.id.cardRelativeLayout);
            card_timeTextView = itemView.findViewById(R.id.card_timeTextView);
            card_temperatureTextView = itemView.findViewById(R.id.card_temperatureTextView);
            card_condition_image_view = itemView.findViewById(R.id.card_condition_image_view);
        }
    }
}
