package com.example.radiobe.database;

import com.example.radiobe.models.RadioItem;

public interface SubjectServerUpdates {
    void registerServerObserver(UpdateServer updateServerObserver);
    void removeServerObserver(UpdateServer updateServerObserver);
    void notifyServerObservers(RadioItem item , String method);
}
