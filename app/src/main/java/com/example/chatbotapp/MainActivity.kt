package com.example.chatbotapp

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.example.myapplication.Message
import com.example.myapplication.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var chatbotTextApi: ChatbotTextApi
    private lateinit var chatbotImageApi: ChatbotImageApi
    private lateinit var editTextText: TextInputEditText
    private lateinit var sendButton: MaterialButton
    private lateinit var messagesList: MessagesList
    private lateinit var us: User
    private lateinit var bot: User
    private lateinit var adapter: MessagesListAdapter<Message>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        chatbotTextApi = ChatbotTextApi(this)
        chatbotImageApi = ChatbotImageApi(this)
        editTextText = findViewById(R.id.editTextText)
        sendButton = findViewById(R.id.sendButton)
        messagesList = findViewById(R.id.messagesList)

        val imageLoader: ImageLoader =
            ImageLoader { imageView, url, _ -> Picasso.get().load(url).into(imageView); }
        adapter = MessagesListAdapter<Message>("1", imageLoader)
        messagesList.setAdapter(adapter)

        us = User("1", "User", "")
        bot = User("2", "Bot", "")

        sendButton.setOnClickListener() {
            //Log.d("MainActivity", "Send button clicked")
            val inputText = editTextText.text.toString().trim()
            val message = Message("1", inputText, us, Calendar.getInstance().time, "")
            adapter.addToStart(message, true)
            Log.d("msg is", message.text)

            if (inputText.startsWith("Generate image:")) {
                // Remove "image:" prefix and trim the remaining text
                val imagePrompt = inputText.removePrefix("Generate image:").trim()
                chatbotImageApi.generateImage(imagePrompt) { imageUrl, error ->
                    if (error != null) {
                        // Handle error
                        val errorMessage = Message(
                            "2",
                            "Failed to generate image: ${error.message}",
                            bot,
                            Calendar.getInstance().time,
                            ""

                        )
                        adapter.addToStart(errorMessage, true)
                    } else {
                        // Handle display image URL
                        val imageMessage =
                            Message("2", "Image:", bot, Calendar.getInstance().time, imageUrl)
                        adapter.addToStart(imageMessage, true)
                    }
                }
            } else {
                // Call the text API
                chatbotTextApi.generateText(inputText) { response, error ->
                    if (error != null) {
                        // Handle error
                        val errorMessage = Message(
                            "2",
                            "Error: ${error.message}",
                            bot,
                            Calendar.getInstance().time,
                            ""
                        )
                        adapter.addToStart(errorMessage, true)
                    } else {
                        val botResponse = Message(
                            "2",
                            response?.answer ?: "Sorry, I didn't get that.",
                            bot,
                            Calendar.getInstance().time,
                            ""
                        )
                        adapter.addToStart(botResponse, true)
                    }
                }
            }
            clearInput()
        }
    }
    private fun clearInput() {
        editTextText.text?.clear()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editTextText.windowToken, 0)
    }
    private fun getErrorMessage(error: Exception): String {
        return when (error) {
            is AuthFailureError -> getString(R.string.error_authentication_failed, error.message)
            is NetworkError -> getString(R.string.error_network, error.message)
            is TimeoutError -> getString(R.string.error_timeout)
            is ServerError -> getString(R.string.error_server)
            else -> getString(R.string.error_unknown, error.message)
        }
    }
}

