package com.example.mp_finalproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mp_finalproject.model.User;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public void setUser (User user) {
        userLiveData.setValue(user);
    }

    public LiveData<User> getUser () {
        return userLiveData;
    }
}
