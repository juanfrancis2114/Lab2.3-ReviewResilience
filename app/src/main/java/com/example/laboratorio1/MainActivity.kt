package com.example.laboratorio1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val tag = "LIFECYCLE_DEBUG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate: Activity creada")

        val reviewViewModel =
            ViewModelProvider(this)[ReviewViewModel::class.java]

        setContent {
            MaterialTheme {
                val comment by reviewViewModel.comment.observeAsState("")

                BackHandler(enabled = comment.isNotEmpty()) {
                    reviewViewModel.notifyBackBlocked()
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Reseña del producto",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Producto: Audífonos inalámbricos",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Puntaje: ${reviewViewModel.rating} de 5"
                    )

                    Slider(
                        value = reviewViewModel.rating.toFloat(),
                        onValueChange = { value ->
                            reviewViewModel.updateRating(value.roundToInt())
                        },
                        valueRange = 1f..5f,
                        steps = 3,
                        enabled = reviewViewModel.ready
                    )

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { value ->
                            reviewViewModel.updateComment(value)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Comentario") },
                        placeholder = {
                            Text("Escribe tu opinión sobre el producto")
                        },
                        minLines = 4,
                        enabled = reviewViewModel.ready
                    )

                    Button(
                        onClick = { reviewViewModel.saveDraft() },
                        enabled = reviewViewModel.ready &&
                                !reviewViewModel.saving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (reviewViewModel.saving) {
                                "Guardando..."
                            } else {
                                "Guardar Borrador"
                            }
                        )
                    }

                    if (!reviewViewModel.ready) {
                        Text("Recuperando borrador...")
                    }

                    if (reviewViewModel.message.isNotEmpty()) {
                        Text(reviewViewModel.message)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Si hay un comentario escrito, Atrás " +
                                "mantendrá abierta la aplicación."
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart: Activity visible")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume: Activity interactuable")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause: Activity pierde el foco")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop: Activity no visible")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy: Activity destruida")
    }
}