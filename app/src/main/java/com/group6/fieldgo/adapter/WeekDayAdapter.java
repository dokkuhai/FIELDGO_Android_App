// adapter/WeekDayAdapter.java
package com.group6.fieldgo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.fieldgo.R;
import com.group6.fieldgo.model.SlotResponse;
import java.util.List;

public class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.ViewHolder> {

    private final List<SlotResponse.WeekDay> days;
    private int selectedPos = 0;
    private final OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(int position);
    }

    public WeekDayAdapter(List<SlotResponse.WeekDay> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weekday, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SlotResponse.WeekDay day = days.get(position);
        holder.tvDay.setText(day.getDay().substring(0, 3));
        holder.tvDate.setText(day.getDate().substring(8));

        boolean isSelected = selectedPos == position;
        holder.itemView.setBackgroundResource(isSelected ? R.drawable.bg_day_selected : R.drawable.bg_day_normal);
        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPos;
            selectedPos = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPos);
            listener.onDayClick(selectedPos);
        });
    }

    @Override public int getItemCount() { return days.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvDate;
        ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}