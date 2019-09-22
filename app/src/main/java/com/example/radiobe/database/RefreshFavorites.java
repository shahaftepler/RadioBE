package com.example.radiobe.database;

import com.example.radiobe.models.RadioItem;
import java.util.List;

public interface RefreshFavorites {
    void refresh(List<RadioItem> favorites);
}
