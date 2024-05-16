package com.example.daswetter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daswetter.models.Hour;
import com.example.daswetter.utils.Temperatures;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HourlyRVA extends RecyclerView.Adapter<HourlyRVA.ViewHolder> {

    private List<Hour> elements;

    private Context context;

    private Temperatures.Unit unit;

    public HourlyRVA(Context context, Temperatures.Unit unit) {
        this.context = context;
        this.unit = unit;
    }

    public void setElements(List<Hour> elements) {
        this.elements = new ArrayList<>(elements);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(elements == null)
            return;

        Hour hour = elements.get(position);
        String time = hour.getTime();
        holder.time.setText(LocalDateTime.parse(time, DateTimeFormatter.ofPattern("y-M-d k:m"))
                        .format(DateTimeFormatter.ofPattern("h:mm")));

        String iconPath = hour.getCondition().getIcon();
        int index = iconPath.indexOf("64x64") + 6;
        String resource = iconPath.substring(index)
                .replace("/", "")
                .replace(".png", "");
        int resourceID = context.getResources().getIdentifier(resource, "drawable", context.getPackageName());
        holder.icon.setImageResource(resourceID);

        holder.temp.setText(Temperatures.formatTemperature(hour.getTemp(unit)));
        holder.precip.setText(String.format("%d%%", hour.getChance_of_rain()));
        holder.windSpeed.setText(String.format("%.1f mph", hour.getWindSpeed()));
    }

    @Override
    public int getItemCount() {
        return 24;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time;
        private ImageView icon;
        private TextView temp;
        private TextView precip;
        private TextView windSpeed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            icon = itemView.findViewById(R.id.icon);
            temp = itemView.findViewById(R.id.temp);
            precip = itemView.findViewById(R.id.precip);
            windSpeed = itemView.findViewById(R.id.windSpeed);
        }
    }
}
