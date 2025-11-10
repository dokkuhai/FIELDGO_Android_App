// adapter/SlotAdapter.java
package com.group6.fieldgo.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.fieldgo.R;
import com.group6.fieldgo.model.SlotResponse;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.ViewHolder> {

    private final List<SlotResponse.Slot> slots;
    private final List<SlotResponse.WeekDay> weekDays;
    private final HashMap<String, SlotResponse.BookedSlot> bookedMap;
    private final OnSlotClickListener listener;
    private String selectedDate;
    private int selectedSlotId = -1;

    public interface OnSlotClickListener {
        void onSlotClick(String date, SlotResponse.Slot slot);
    }

    public SlotAdapter(
            List<SlotResponse.Slot> slots,
            List<SlotResponse.WeekDay> weekDays,
            List<SlotResponse.BookedSlot> booked,
            OnSlotClickListener listener) {

        this.slots = slots;
        this.weekDays = weekDays;
        this.listener = listener;
        this.bookedMap = new HashMap<>();

        // Tạo map: "2025-11-12_1" → booked slot
        for (SlotResponse.BookedSlot b : booked) {
            bookedMap.put(b.getDate() + "_" + b.getSlotId(), b);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slot_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SlotResponse.Slot slot = slots.get(position);
        holder.bind(slot, this); // TRUYỀN 'this' (adapter) VÀO
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    // Cập nhật ngày được chọn từ WeekDayAdapter
    public void setSelectedDate(String date) {
        this.selectedDate = date;
        notifyDataSetChanged();
    }

    // Cập nhật slot được chọn (nếu cần từ bên ngoài)
    public void setSelectedSlot(String date, int slotId) {
        this.selectedDate = date;
        this.selectedSlotId = slotId;
        notifyDataSetChanged();
    }

    // ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView[] cells = new TextView[7];

        ViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            cells[0] = itemView.findViewById(R.id.cell0);
            cells[1] = itemView.findViewById(R.id.cell1);
            cells[2] = itemView.findViewById(R.id.cell2);
            cells[3] = itemView.findViewById(R.id.cell3);
            cells[4] = itemView.findViewById(R.id.cell4);
            cells[5] = itemView.findViewById(R.id.cell5);
            cells[6] = itemView.findViewById(R.id.cell6);
        }

        void bind(SlotResponse.Slot slot, SlotAdapter adapter) {
            tvTime.setText(slot.getTimeRange());

            for (int i = 0; i < cells.length; i++) {
                String date = adapter.weekDays.get(i).getDate();
                String key = date + "_" + slot.getId();
                boolean isBooked = adapter.bookedMap.containsKey(key);
                boolean isSelected = adapter.selectedDate != null
                        && adapter.selectedDate.equals(date)
                        && adapter.selectedSlotId == slot.getId();

                TextView cell = cells[i];

                cell.setText(isBooked
                        ? "ĐÃ ĐẶT"
                        : NumberFormat.getInstance(new Locale("vi", "VN")).format(slot.getPrice()) + "đ");

                cell.setTextColor(isBooked
                        ? Color.parseColor("#B71C1C")
                        : isSelected ? Color.WHITE : Color.parseColor("#1B5E20"));

                cell.setBackgroundResource(isBooked
                        ? R.drawable.bg_booked
                        : isSelected ? R.drawable.bg_selected : R.drawable.bg_available);

                cell.setEnabled(!isBooked);
                cell.setOnClickListener(isBooked ? null : v -> {
                    adapter.selectedDate = date;
                    adapter.selectedSlotId = slot.getId();
                    adapter.notifyDataSetChanged();
                    adapter.listener.onSlotClick(date, slot);
                });
            }
        }

    }
}