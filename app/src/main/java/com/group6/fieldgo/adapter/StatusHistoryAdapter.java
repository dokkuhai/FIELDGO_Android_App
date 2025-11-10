package com.group6.fieldgo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.fieldgo.R;
import com.group6.fieldgo.model.StatusHistoryItem;
import java.util.List;

public class StatusHistoryAdapter extends RecyclerView.Adapter<StatusHistoryAdapter.ViewHolder> {

    private List<StatusHistoryItem> items;

    public StatusHistoryAdapter(List<StatusHistoryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_status_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatusHistoryItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvTitle, tvDescription, tvTimestamp;
        View indicatorLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            indicatorLine = itemView.findViewById(R.id.indicatorLine);
        }

        public void bind(StatusHistoryItem item) {
            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());
            tvTimestamp.setText(item.getTimestamp());

            // Icon và màu theo status
            if ("CONFIRMED".equalsIgnoreCase(item.getStatus())) {
                tvIcon.setText("✅");
                tvTitle.setTextColor(itemView.getContext().getResources()
                        .getColor(android.R.color.holo_green_dark));
            } else if ("PENDING".equalsIgnoreCase(item.getStatus())) {
                tvIcon.setText("⏳");
                tvTitle.setTextColor(itemView.getContext().getResources()
                        .getColor(android.R.color.holo_orange_dark));
            } else if ("CANCELLED".equalsIgnoreCase(item.getStatus())) {
                tvIcon.setText("❌");
                tvTitle.setTextColor(itemView.getContext().getResources()
                        .getColor(android.R.color.holo_red_dark));
            }
        }
    }
}



