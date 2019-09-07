package com.example.radiobe.adapters;

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

import java.sql.Ref;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>{
    /*Properties*/
    List<NotificationItem> notificationItemList;
    Context context;
    RefreshNotificationsListener refreshNotificationsListener;
    HashMap<String, User> notificationSenders;

/*Constructor*/
    public NotificationsAdapter(List<NotificationItem> notificationItemList,HashMap<String, User> notificationSenders ,Context context) {
        this.notificationItemList = notificationItemList;
        this.context = context;
        this.notificationSenders = notificationSenders;
//        System.out.println(notificationItemList);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =  LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_notification, parent, false);
        NotificationViewHolder notificationViewHolder = new NotificationViewHolder(view);

        return notificationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notificationItem = notificationItemList.get(position);
        User user = notificationSenders.get(notificationItem.getUid());
        System.out.println(notificationItemList);
        System.out.println("---------------------------");

        System.out.println(user);
        holder.tvNotificationTime.setText(DateFormat.format("dd/MM/yyyy", new Date(notificationItem.getCreationDate())).toString());
        holder.tvDescription.setText(notificationItem.getDescription());
        holder.tvTitle.setText(notificationItem.getTitle());
//        holder.ivNotification.setImageBitmap(user.getProfileImage());
//        holder.ivNotification.setImageResource(notificationItem.getImageURL());

    }

    @Override
    public int getItemCount() {
        return notificationItemList.size();
    }

    public void initNotificationListener(){
        refreshNotificationsListener = new RefreshNotificationsListener() {
            @Override
            public void refresh(List<NotificationItem> notifications, HashMap<String, User> senders) {
                notificationItemList = notifications;
                notificationSenders = senders;
                notifyDataSetChanged();
            }

        };

        CurrentUser.getInstance().setNotificationsListener(refreshNotificationsListener);
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder{
        ImageView ivNotification;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvNotificationTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNotification = itemView.findViewById(R.id.ivImageNotif);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvDescription = itemView.findViewById(R.id.tvNotifDescription);
            tvNotificationTime = itemView.findViewById(R.id.tvNotifTime);
        }
    }


}
