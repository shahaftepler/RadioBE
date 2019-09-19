package com.example.radiobe.database;

import com.example.radiobe.models.RadioItem;

public interface SubjectUsernameRefresh {
    void registerUsernameObserver(RefreshUserName refreshUserName);
    void removeUsernameObserver(RefreshUserName refreshUserName);
    void notifyUsernameObservers();

}
