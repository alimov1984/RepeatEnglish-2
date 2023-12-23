package ru.alimov.repeatenglish.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private final MutableLiveData<MainActivityUiState> uiState =
            new MutableLiveData(new MainActivityUiState(null, null));

    public MutableLiveData<MainActivityUiState> getUiState() {
        return uiState;
    }

}
