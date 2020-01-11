package com.example.app_usuario_v2.ui.send;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class SendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("cerrar sesion");
    }

    public LiveData<String> getText() {
        return mText;
    }
}