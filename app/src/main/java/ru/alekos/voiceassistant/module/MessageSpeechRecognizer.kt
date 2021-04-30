package ru.alekos.voiceassistant.module

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import java.util.*

class MessageSpeechRecognizer(
    context: Context,
    questionText: EditText,
    micButton: ImageView,
    sendButton: Button
) {
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        .putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        .putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

    fun startListening() {
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    fun destroy() {
        speechRecognizer.destroy()
    }

    init {
        speechRecognizer.setRecognitionListener(
            MessageRecognitionListener(
                questionText,
                micButton,
                sendButton
            )
        )
    }
}
