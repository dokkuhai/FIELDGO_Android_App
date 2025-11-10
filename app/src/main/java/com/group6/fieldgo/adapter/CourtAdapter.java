package com.group6.fieldgo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.group6.fieldgo.R;
import com.group6.fieldgo.model.Court;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.CourtViewHolder> {

    private final Context context;
    private final List<Court> courtList;

    public CourtAdapter(Context context, List<Court> courtList) {
        this.context = context;
        this.courtList = courtList;
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_court, parent, false);
        return new CourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {
        Court court = courtList.get(position);

        // Định dạng tiền tệ
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String priceFormatted = formatter.format(court.getPrice()) + " đ/giờ";

        holder.tvName.setText(court.getName());
        holder.tvVenueSport.setText(court.getSportType() + " - " + court.getVenueName());
        holder.tvLocation.setText(court.getProvinceName() + ", " + court.getWardName());
        holder.tvPrice.setText(priceFormatted);

        // Tải ảnh bằng Glide
        Glide.with(context)
                .load(court.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.anhsan)
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return courtList.size();
    }

    // Phương thức phân trang
    public void addCourts(List<Court> newCourts) {
        int startPosition = courtList.size();
        courtList.addAll(newCourts);
        notifyItemRangeInserted(startPosition, newCourts.size());
    }

    public static class CourtViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvVenueSport, tvLocation, tvPrice;

        public CourtViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCourtImage);
            tvName = itemView.findViewById(R.id.tvCourtName);
            tvVenueSport = itemView.findViewById(R.id.tvVenueSport);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}