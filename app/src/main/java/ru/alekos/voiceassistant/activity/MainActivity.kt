package ru.alekos.voiceassistant.activity

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.alekos.voiceassistant.R
import ru.alekos.voiceassistant.dao.MessageDao
import ru.alekos.voiceassistant.database.AppDatabase
import ru.alekos.voiceassistant.enumeration.Sender
import ru.alekos.voiceassistant.model.Message
import ru.alekos.voiceassistant.entity.MessageEntity
import ru.alekos.voiceassistant.module.MessageList
import ru.alekos.voiceassistant.module.MessageObserver
import ru.alekos.voiceassistant.module.MessageSpeechRecognizer
import ru.alekos.voiceassistant.service.QuestionService
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var questionText: EditText
    private lateinit var micButton: ImageView
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var speechRecognizer: MessageSpeechRecognizer
    private lateinit var messageList: MessageList
    private lateinit var questionService: QuestionService
    private lateinit var database: AppDatabase
    private lateinit var messageDao: MessageDao

    private lateinit var sPref: SharedPreferences
    private val APP_PREFERENCES = "mysettings"
    private var isLight = true
    private val THEME = "THEME"

    override fun onCreate(savedInstanceState: Bundle?) {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        sendButton = findViewById(R.id.sendButton)
        questionText = findViewById(R.id.questionField)
        micButton = findViewById(R.id.micButton)
        messageList = MessageList(findViewById(R.id.chatWindow), applicationContext)

        textToSpeech = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
            }
        }

        speechRecognizer =
            MessageSpeechRecognizer(applicationContext, questionText, micButton, sendButton)

        sendButton.setOnClickListener { onSend() }

        micButton.setOnClickListener {
            micButton.setImageResource(R.drawable.mic_on)
            speechRecognizer.startListening()
        }

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "voice_assistant"
        )
            .fallbackToDestructiveMigration()
            .build()

        questionService = QuestionService(applicationContext, database).also {
            it.subscribe(
                MessageObserver(
                    messageList,
                    questionText,
                    sendButton,
                    micButton,
                    textToSpeech
                )
            )
        }

        messageDao = database.getMessageDao()

        Observable.fromCallable {
            messageDao
                .getAllMessages()
                .map { entity ->
                    return@map try {
                        Message(entity)
                    } catch (e: Exception) {
                        null
                    }
                }
                .filter { Objects.nonNull(it) }
                .requireNoNulls()
                .toMutableList()

        }.map {
            if (it.isEmpty()) {
                return@map mutableListOf(Message("Здравствуй! Попробуй рассказать мне о своих любимых фильмах! Скажи \"Я посмотрел фильм...\" и название фильма!\n" +
                        "Я получаю информацию о фильмах из базы данных TMDb, которая составляется пользователями со всего света.", Sender.Assistant))
            }
            else {
                return@map it
            }
        }
            .subscribeOn(Schedulers.io())
            .subscribe {
                messageList.messageList = it
                messageList.scrollToBottom()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_theme, menu)
        isLight = sPref.getBoolean(THEME, true)
        setDayNightTheme(menu?.findItem(R.id.theme_settings))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.theme_settings) {
            isLight = !isLight
            setDayNightTheme(item)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(
            "messageHistory",
            ArrayList<Parcelable>(messageList.messageList)
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        messageList.messageList = savedInstanceState.getParcelableArrayList("messageHistory") ?: ArrayList()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val recordAudioPermissionIndex =
            permissions.indexOf("android.permission.RECORD_AUDIO")
        if (recordAudioPermissionIndex == -1 || grantResults[recordAudioPermissionIndex] == PackageManager.PERMISSION_DENIED) {
            micButton.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        sPref
            .edit()
            .putBoolean(THEME, isLight)
            .apply()

        Observable.fromCallable {
            messageDao.deleteAll()
            messageDao.insertAll(messageList.messageList
                .map { MessageEntity(it) }
                .toList())
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        //database.close()
    }

    private fun setDayNightTheme(themeMenu: MenuItem?) {
        if (isLight) {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            themeMenu?.title = getString(R.string.night_settings)
        } else {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            themeMenu?.title = getString(R.string.day_settings)
        }
    }

    private fun onSend() {
        val text = questionText.text.toString()
        questionText.setText("")
        if (text.isEmpty()) return
        messageList.messageList.add(Message(text, Sender.User))
        sendButton.isEnabled = false
        questionText.setHint(R.string.thinkng_about_it)
        micButton.visibility = View.INVISIBLE
        questionService.getAnswer(text)
    }
}