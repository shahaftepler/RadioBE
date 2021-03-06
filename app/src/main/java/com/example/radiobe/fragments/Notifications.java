package com.example.radiobe.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.radiobe.adapters.NotificationsAdapter;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.R;
import com.example.radiobe.database.RefreshNotificationsListener;
import com.example.radiobe.models.NotificationItem;
import com.example.radiobe.models.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment implements RefreshNotificationsListener {
    private TextView noNotifications;

    public Notifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rvNotifications);
        System.out.println(CurrentUser.getInstance().getNotifications());
        System.out.println(CurrentUser.getInstance().getNotificationSenders());
        NotificationsAdapter adapter = new NotificationsAdapter(CurrentUser.getInstance().getNotifications(), CurrentUser.getInstance().getNotificationSenders(), getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        CurrentUser.getInstance().registerNotificationObserver(this);
        noNotifications = view.findViewById(R.id.noNotifications);

        if(CurrentUser.getInstance().getNotifications().size() > 0){
            noNotifications.setVisibility(View.GONE);
        } else {
            noNotifications.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public void refresh(List<NotificationItem> notifications, HashMap<String, User> senders) {
        if(notifications.size() > 0){
            Objects.requireNonNull(getActivity()).runOnUiThread(()->{
                noNotifications.setVisibility(View.GONE);
            });
        } else {
            Objects.requireNonNull(getActivity()).runOnUiThread(()->{
                noNotifications.setVisibility(View.VISIBLE);
            });
        }
    }
}
