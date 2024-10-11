package com.example.chatbotapp

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class ChatbotTextApi(private val context: Context) {

    companion object {
        private const val API_URL = "https://api.openai.com/v1/chat/completions"
        private const val MODEL = "gpt-3.5-turbo"
        private const val TEMPERATURE = 0
        private const val TOKEN = ""
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun generateText(input: String, callback: (ChatbotTextResponse?, Exception?) -> Unit) {
        val jsonBody = JSONObject().apply {
            put("model", MODEL)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", input)
                })
            })
            put("temperature", TEMPERATURE)
        }

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            API_URL,
            jsonBody,
            Response.Listener { response ->
                try {
                    val answer = response.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                    val chatbotResponse = ChatbotTextResponse(answer)
                    callback(chatbotResponse, null)
                } catch (e: Exception) {
                    callback(null, e)
                }
            },
            Response.ErrorListener { error ->
                callback(null, error)
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return HashMap<String, String>().apply {
                    put("Content-Type", "application/json")
                    put("Authorization", "Bearer $TOKEN")
                }
            }

            override fun getRetryPolicy(): DefaultRetryPolicy {
                return DefaultRetryPolicy(
                    40000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            }
        }

        requestQueue.add(request)
    }
}

data class ChatbotTextResponse(val answer: String)
