package com.example.radiobe.database;

import com.example.radiobe.models.RadioItem;

public interface UpdateServer {

    void updateLikes(RadioItem item);
    void updateComments(RadioItem item);
    void updateViews(RadioItem item);
}
