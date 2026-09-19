package com.example.tictactoegame.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tictactoegame.utils.SideEffects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(MainContract.State())
    val state = _state.asStateFlow()

    private val _sideEffects = Channel<SideEffects>()
    val sideEffects = _sideEffects.receiveAsFlow()

    fun onEvent(event: MainContract.Event) {
        when(event) {
            is MainContract.Event.ClickExit -> {
                _state.update {
                    it.copy(
                        dialogState = MainContract.DialogState.Exit
                    )
                }
            }
            is MainContract.Event.DismissDialog -> {
                _state.update {
                    it.copy(
                        dialogState = MainContract.DialogState.Hidden
                    )
                }
            }
            is MainContract.Event.ConfirmDialogAction -> {
                when(state.value.dialogState) {
                    is MainContract.DialogState.Hidden -> {}
                    is MainContract.DialogState.Exit -> {
                        viewModelScope.launch { _sideEffects.send(SideEffects.NavigateTo) }
                    }
                }
            }
        }
    }
}