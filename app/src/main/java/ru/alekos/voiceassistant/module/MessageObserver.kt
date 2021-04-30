package ru.alekos.voiceassistant.module

import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import ru.alekos.voiceassistant.R
import ru.alekos.voiceassistant.enumeration.Sender
import ru.alekos.voiceassistant.model.Message

class MessageObserver(
    private val messageList: MessageList,
    private val questionText: EditText,
    private val sendButton: Button,
    private val micButton: ImageView,
    private val textToSpeech: TextToSpeech
) : Observer<String> {

    override fun onSubscribe(d: @NonNull Disposable) {}

    override fun onNext(@NonNull t: @NonNull String?) {
        messageList.messageList.add(Message(t, Sender.Assistant))
        textToSpeech.speak(t, TextToSpeech.QUEUE_FLUSH, null, null)
        messageList.notifyDataSetChanged()
        messageList.scrollToBottom()
        sendButton.isEnabled = true
        questionText.setHint(R.string.question_text)
        micButton.visibility = View.VISIBLE
    }

    override fun onError(e: @NonNull Throwable) {
        onNext(e.message)
    }

    override fun onComplete() {}

}