package com.group6.fieldgo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.fieldgo.R; // R là class tự động sinh của Android
import com.group6.fieldgo.model.FeedbackItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private final List<FeedbackItem> feedbackList;
    private boolean isLoadingAdded = false;

    public FeedbackAdapter(List<FeedbackItem> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @Override
    public int getItemViewType(int position) {
        // Nếu là mục cuối cùng và đang trong trạng thái tải, trả về VIEW_TYPE_LOADING
        return (position == feedbackList.size() - 1 && isLoadingAdded) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.item_feedback, parent, false);
            return new FeedbackViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            ((FeedbackViewHolder) holder).bind(feedbackList.get(position));
        }
        // LoadingViewHolder không cần bind
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    // --- Phương thức hỗ trợ phân trang ---

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<FeedbackItem> list) {
        this.feedbackList.clear();
        this.feedbackList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(List<FeedbackItem> newFeedbacks) {
        int oldSize = feedbackList.size();
        feedbackList.addAll(newFeedbacks);
        notifyItemRangeInserted(oldSize, newFeedbacks.size());
    }

    public void addLoadingFooter() {
        if (!isLoadingAdded) {
            isLoadingAdded = true;
            feedbackList.add(null); // Sử dụng null để đánh dấu loading footer
            notifyItemInserted(feedbackList.size() - 1);
        }
    }

    public void removeLoadingFooter() {
        if (isLoadingAdded && !feedbackList.isEmpty()) {
            isLoadingAdded = false;
            int position = feedbackList.size() - 1;
            FeedbackItem item = feedbackList.get(position);

            // Chỉ xóa item cuối nếu nó là null (loading indicator)
            if (item == null) {
                feedbackList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    // --- ViewHolders ---

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName, tvComment, tvRating, tvCreatedAt;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
        }

        public void bind(FeedbackItem item) {
            tvUserName.setText(item.getUserName());
            tvComment.setText(item.getComment());
            tvRating.setText("⭐ " + item.getRating() + "/5");
            tvCreatedAt.setText(formatTimestamp(item.getCreatedAt()));
        }

        private String formatTimestamp(String timestamp) {
            try {
                // Định dạng input: "2025-11-10T13:15:02.909673Z" (chỉ cần lấy 19 ký tự đầu)
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(timestamp.substring(0, 19));

                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                return outputFormat.format(date);
            } catch (Exception e) {
                return timestamp;
            }
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            // Có thể thêm findViewById cho ProgressBar nếu cần tùy chỉnh
        }
    }
}