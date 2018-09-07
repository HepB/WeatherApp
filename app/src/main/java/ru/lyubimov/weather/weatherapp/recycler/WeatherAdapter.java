package ru.lyubimov.weather.weatherapp.recycler;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.lyubimov.weather.weatherapp.R;
import ru.lyubimov.weather.weatherapp.ViewUtils;
import ru.lyubimov.weather.weatherapp.model.Weather;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherHolder> {

    private List<Weather> weathers;
    private Activity activity;
    private boolean multiSelect = false;
    private List<Weather> selectedItems = new ArrayList<>();
    private ActionMode actionMode;

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            actionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            for (Weather intItem : selectedItems) {
                weathers.remove(intItem);
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    public WeatherAdapter(List<Weather> weathers, Activity activity) {
        this.weathers = weathers;
        this.activity = activity;
    }

    @NonNull
    @Override
    public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        return new WeatherHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
        Weather weather = weathers.get(position);
        holder.bind(weather);
    }

    @Override
    public int getItemCount() {
        return weathers.size();
    }

    class WeatherHolder extends RecyclerView.ViewHolder {
        private Weather mWeather;

        private TextView mDateInfo;
        private ImageView mWeatherInfo;
        private TextView mTempInfo;
        private ConstraintLayout mLayout;

        private WeatherHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.time_stamp_item, parent, false));
            mDateInfo = itemView.findViewById(R.id.date_info);
            mWeatherInfo = itemView.findViewById(R.id.weather_ico);
            mTempInfo = itemView.findViewById(R.id.temp_text);
            mLayout = itemView.findViewById(R.id.layout);
        }

        private void bind(final Weather weather) {
            mWeather = weather;
            ViewUtils.setTimeStamp(activity.getResources(), mDateInfo, mWeather.getDateStamp());
            ViewUtils.setWeatherIcon(activity.getApplicationContext(), mWeatherInfo, mWeather.getConditions().get(0).getIconName());
            ViewUtils.setTemperatureInformation(activity.getResources(), mTempInfo, mWeather.getTemperature());

            if (selectedItems.contains(weather)) {
                mLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorRowSelected));
            } else {
                mLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorBackgroundDay));
            }

            itemView.setOnLongClickListener(view -> {
                ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                selectItem(weather);
                return true;
            });

            itemView.setOnClickListener(view -> selectItem(weather));
        }

        private void selectItem(Weather weather) {
            if (multiSelect) {
                if (selectedItems.contains(weather)) {
                    selectedItems.remove(weather);
                    mLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorBackgroundDay));
                    if(selectedItems.isEmpty()) actionMode.finish();
                } else {
                    selectedItems.add(weather);
                    mLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorRowSelected));
                }
            }
        }
    }
}
