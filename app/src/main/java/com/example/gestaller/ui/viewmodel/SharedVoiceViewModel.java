package com.example.gestaller.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedVoiceViewModel extends ViewModel {

    public enum VoiceState {
        IDLE,
        LISTENING,
        AWAITING_DATA
    }

    public enum NavigationTarget {
        NONE,
        NEW_CLIENT,
        NEW_VEHICLE,
        NEW_ORDER,
        NEW_SERVICE,
        EDIT_SERVICE,
        LIST_CLIENTS,
        LIST_VEHICLES,
        LIST_ORDERS,
        LIST_SERVICES
    }

    private final MutableLiveData<VoiceState> _voiceState = new MutableLiveData<>(VoiceState.IDLE);
    private final MutableLiveData<String> _recognizedText = new MutableLiveData<>();
    private final MutableLiveData<NavigationTarget> _navigationTarget = new MutableLiveData<>(NavigationTarget.NONE);

    // Getters consistentes con el uso en las Activities
    public LiveData<VoiceState> getState() {
        return _voiceState;
    }

    public LiveData<String> getRecognizedText() {
        return _recognizedText;
    }

    public LiveData<NavigationTarget> getNavigationTarget() {
        return _navigationTarget;
    }

    public void setRecognizedText(String text) {
        _recognizedText.setValue(text);
    }

    public void changeState(VoiceState newState) {
        _voiceState.setValue(newState);
    }

    public void setNavigationTarget(NavigationTarget target) {
        _navigationTarget.setValue(target);
    }

    public void navigationHandled() {
        _navigationTarget.setValue(NavigationTarget.NONE);
    }
}
