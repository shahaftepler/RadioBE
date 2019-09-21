package com.example.radiobe.database;

public interface SubjectProfilePictureRefresh {
    void registerProfilePictureObserver(RefreshProfilePicture refreshProfilePicture);
    void removeProfilePictureObserver(RefreshProfilePicture refreshProfilePicture);
    void notifyProfilePictureObservers();
}
