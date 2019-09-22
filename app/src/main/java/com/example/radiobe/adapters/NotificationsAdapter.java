package com.example.radiobe.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.database.RefreshNotificationsListener;
import com.example.radiobe.models.NotificationItem;
import com.example.radiobe.models.User;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> implements RefreshNotificationsListener{
    //Properties
    private List<NotificationItem> notificationItemList;
    private Context context;
    private HashMap<String, User> notificationSenders;
    private Activity activity;

    //Constructor
    public NotificationsAdapter(List<NotificationItem> notificationItemList,HashMap<String, User> notificationSenders , Activity activity) {
        this.notificationItemList = notificationItemList;
        this.context = activity;
        this.notificationSenders = notificationSenders;
        CurrentUser.getInstance().registerNotificationObserver(this);
        this.activity = activity;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =  LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_notification, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notificationItem = notificationItemList.get(position);
        User user = notificationSenders.get(notificationItem.getUid());
        System.out.println(notificationSenders);
        System.out.println(user);
        holder.tvNotificationTime.setText(DateFormat.format("dd/MM/yyyy", new Date(notificationItem.getCreationDate())).toString());
        holder.tvDescription.setText(notificationItem.getDescription());
        holder.tvTitle.setText(notificationItem.getTitle());
        assert user != null;
        holder.ivNotification.setImageBitmap(user.getProfileImage());

    }

    @Override
    public int getItemCount() {
        return notificationItemList.size();
    }

    @Override
    public void refresh(List<NotificationItem> notifications, HashMap<String, User> senders) {
        System.out.println("Inside Refresh Notifications");
        notificationItemList = notifications;
        notificationSenders = senders;
        //todo: find out if there's a better way than holding the activity.
        activity.runOnUiThread(this::notifyDataSetChanged);
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder{
        ImageView ivNotification;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvNotificationTime;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNotification = itemView.findViewById(R.id.ivImageNotif);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvDescription = itemView.findViewById(R.id.tvNotifDescription);
            tvNotificationTime = itemView.findViewById(R.id.tvNotifTime);
        }
    }


}
