package com.example.laboratorio1

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReviewViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = DraftRepository(application)

    // Sobrevive a los cambios de configuración.
    var rating by mutableStateOf(1)
        private set

    // Sobrevive a la recreación y a la restauración del proceso.
    val comment: LiveData<String> =
        savedStateHandle.getLiveData("comment", "")

    var ready by mutableStateOf(false)
        private set

    var saving by mutableStateOf(false)
        private set

    var message by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            try {
                // El comentario restaurado tiene prioridad sobre el borrador.
                if (!savedStateHandle.contains("comment_initialized")) {
                    val storedDraft = repository.draft.first()

                    if (comment.value.isNullOrEmpty()) {
                        savedStateHandle["comment"] = storedDraft
                    }

                    savedStateHandle["comment_initialized"] = true
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                message = "No se pudo recuperar el borrador."
            } finally {
                ready = true
            }
        }
    }

    fun updateRating(value: Int) {
        rating = value.coerceIn(1, 5)
    }

    fun updateComment(value: String) {
        savedStateHandle["comment"] = value
        savedStateHandle["comment_initialized"] = true
        message = ""
    }

    fun saveDraft() {
        if (!ready || saving) return

        val content = comment.value.orEmpty()

        viewModelScope.launch {
            saving = true

            try {
                repository.saveDraft(content)
                message = "Borrador guardado correctamente."
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                message = "No se pudo guardar el borrador."
            } finally {
                saving = false
            }
        }
    }

    fun notifyBackBlocked() {
        message = "Hay un comentario en curso. Guárdalo o borra el texto."
    }
}