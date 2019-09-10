package com.example.radiobe.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.radiobe.adapters.NotificationsAdapter;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.R;
import com.example.radiobe.models.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment {
    NotificationsAdapter adapter;
    RecyclerView recyclerView;

    public Notifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvNotifications);
        System.out.println(CurrentUser.getInstance().getNotifications());
        System.out.println(CurrentUser.getInstance().getNotificationSenders());
        adapter = new NotificationsAdapter(CurrentUser.getInstance().getNotifications(),  CurrentUser.getInstance().getNotificationSenders(),getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.initNotificationListener(getActivity());


    }
}
