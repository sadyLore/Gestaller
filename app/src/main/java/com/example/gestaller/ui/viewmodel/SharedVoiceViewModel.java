package com.example.gestaller.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedVoiceViewModel extends ViewModel {

    // 1. --- Estados de la Interfaz de Voz ---
    public enum VoiceState {
        IDLE,       // El sistema no está escuchando
        LISTENING,  // El sistema está escuchando para un comando inicial
        AWAITING_DATA // El sistema espera el dictado de un campo específico
    }

    // 2. --- Destinos de Navegación (Integral) ---
    public enum NavigationTarget {
        NONE,

        // Comandos de Creación
        NEW_CLIENT,    // "Añadir cliente"
        NEW_VEHICLE,   // "Añadir vehículo/auto"
        NEW_ORDER,     // "Añadir orden/trabajo"
        NEW_SERVICE,   // "Añadir servicio"

        // --- Comandos de Visualización ---
        LIST_CLIENTS,  // "Mostrar lista de clientes"
        LIST_VEHICLES, // "Mostrar lista de vehículos/autos"
        LIST_ORDERS,   // "Mostrar lista de órdenes/trabajos"
        LIST_SERVICES  // "Mostrar lista de servicios"
    }

    // 3. --- LiveData para la comunicación ---

    // Comunica el estado actual del sistema de voz (IDLE, LISTENING, etc.)
    private final MutableLiveData<VoiceState> _voiceState = new MutableLiveData<>(VoiceState.IDLE);
    public final LiveData<VoiceState> voiceState = _voiceState;

    // Comunica el texto reconocido por el sistema de voz
    private final MutableLiveData<String> _recognizedText = new MutableLiveData<>();
    public final LiveData<String> recognizedText = _recognizedText;

    // Indica a qué pantalla se debe navegar
    private final MutableLiveData<NavigationTarget> _navigationTarget = new MutableLiveData<>(NavigationTarget.NONE);
    public final LiveData<NavigationTarget> navigationTarget = _navigationTarget;

    // 4. --- Funciones para controlar el flujo ---

    /**
     * Actualiza el texto reconocido.
     * @param text El texto proveniente del reconocimiento de voz.
     */
    public void setRecognizedText(String text) {
        _recognizedText.setValue(text);
    }

    /**
     * Cambia el estado actual del sistema de voz.
     * @param newState El nuevo estado (IDLE, LISTENING, AWAITING_DATA).
     */
    public void changeState(VoiceState newState) {
        _voiceState.setValue(newState);
    }

    /**
     * Establece el destino de navegación.
     * @param target La pantalla a la que se debe navegar.
     */
    public void setNavigationTarget(NavigationTarget target) {
        _navigationTarget.setValue(target);
    }

    /**
     * Resetea el destino de navegación una vez que se ha gestionado.
     * Esto evita que se navegue varias veces por el mismo evento.
     */
    public void navigationHandled() {
        _navigationTarget.setValue(NavigationTarget.NONE);
    }
}
