package ru.alekos.voiceassistant.module

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import ru.alekos.voiceassistant.R

internal class MessageRecognitionListener(
    private val questionText: EditText,
    private val micButton: ImageView,
    private val sendButton: Button
) : RecognitionListener {
    override fun onReadyForSpeech(params: Bundle) {}

    override fun onBeginningOfSpeech() {
        questionText.setText("")
        questionText.setHint(R.string.question_listening)
    }

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray) {}

    override fun onEndOfSpeech() {
        micButton.setImageResource(R.drawable.mic_off)
        questionText.setHint(R.string.question_text)
    }

    override fun onError(error: Int) {
        micButton.setImageResource(R.drawable.mic_off)
        questionText.setHint(R.string.question_text)
    }

    override fun onResults(results: Bundle) {
        val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        questionText.setText(data?.get(0) ?: "")
        questionText.setHint(R.string.question_text)
        sendButton.callOnClick()
    }

    override fun onPartialResults(partialResults: Bundle) {}

    override fun onEvent(eventType: Int, params: Bundle) {}
}
