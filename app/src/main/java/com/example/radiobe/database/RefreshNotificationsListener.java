package com.example.radiobe.database;

import com.example.radiobe.models.NotificationItem;
import com.example.radiobe.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RefreshNotificationsListener {
    void refresh(List<NotificationItem> notifications , HashMap<String, User> senders);
}
