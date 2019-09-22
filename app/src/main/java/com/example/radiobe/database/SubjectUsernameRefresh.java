package com.example.radiobe.database;


public interface SubjectUsernameRefresh {
    void registerUsernameObserver(RefreshUserName refreshUserName);
    void removeUsernameObserver(RefreshUserName refreshUserName);
    void notifyUsernameObservers();

}
