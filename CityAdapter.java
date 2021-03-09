package com.example.android.sqliteweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private ArrayList<RecentCity> cityList;
    private OnItemClickListener clickListner;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListner = listener;
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView cityTV;

        public CityViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            cityTV = itemView.findViewById(R.id.tv_recent_city);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public CityAdapter(ArrayList<RecentCity> cityList) {
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_card, parent, false);
        CityViewHolder cvh = new CityViewHolder(v, clickListner);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        RecentCity currCity = this.cityList.get(position);
        holder.cityTV.setText(currCity.getCityName());
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }
}
