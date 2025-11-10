package com.group6.fieldgo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.fieldgo.R;
import com.group6.fieldgo.model.Booking;
import com.group6.fieldgo.view.BookingDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookings = new ArrayList<>();
    private OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onBookingClick(Booking booking);
        void onCancelClick(Booking booking);
    }

    public BookingAdapter(OnBookingActionListener listener) {
        this.listener = listener;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings.clear();
        if (bookings != null) {
            this.bookings.addAll(bookings);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking, listener);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourtName, tvAddress, tvDate, tvTimeslot, tvPrice, tvStatus;
        Button btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourtName = itemView.findViewById(R.id.tvCourtName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTimeslot = itemView.findViewById(R.id.tvTimeslot);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }

        public void bind(Booking booking, OnBookingActionListener listener) {
            tvCourtName.setText(booking.getCourtName());
            tvAddress.setText(booking.getAddress());
            tvDate.setText("Ngày: " + booking.getBookingDate());

            String timeslot = booking.getTimeslot() != null ? booking.getTimeslot() : "Chưa xác định";
            tvTimeslot.setText("Giờ: " + timeslot);

            tvPrice.setText(String.format("%.0f VNĐ", booking.getPrice()));
            tvStatus.setText(booking.getStatus());

            // Chỉ hiện nút Hủy nếu status KHÔNG phải CANCELLED
            if (!"CANCELLED".equalsIgnoreCase(booking.getStatus())) {
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setOnClickListener(v -> listener.onCancelClick(booking));
            } else {
                btnCancel.setVisibility(View.GONE);
            }

            // Click vào item → mở DetailActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), BookingDetailActivity.class);
                intent.putExtra(BookingDetailActivity.EXTRA_BOOKING, booking);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
