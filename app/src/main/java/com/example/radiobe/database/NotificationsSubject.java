package com.example.radiobe.database;

public interface NotificationsSubject {
    void registerNotificationObserver(RefreshNotificationsListener refreshNotificationsListener);
    void removeNotificationObserver(RefreshNotificationsListener refreshNotificationsListener);
    void notifyNotificationObservers();
}
