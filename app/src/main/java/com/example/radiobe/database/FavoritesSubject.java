package com.example.radiobe.database;

public interface FavoritesSubject {
    void registerFavoriteObserver(RefreshFavorites refreshFavoritesObserver);
    void removeFavoriteObserver(RefreshFavorites refreshFavoritesObserver);
    void notifyFavoriteObservers();
}
