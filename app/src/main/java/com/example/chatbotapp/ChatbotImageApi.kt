package com.example.chatbotapp

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
class ChatbotImageApi(private val context: Context) {

    companion object {
        private const val API_URL = "https://api.openai.com/v1/images/generations"
        private const val TOKEN = ""
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun generateImage(input: String, callback: (String?, Exception?) -> Unit) {
        val jsonBody = JSONObject().apply {
            put("prompt", input)
            put("n", 1)
            put("size", "1024x1024")
        }

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            API_URL,
            jsonBody,
            Response.Listener { response ->
                try {
                    val imageUrl = response.getJSONArray("data")
                        .getJSONObject(0)
                        .getString("url")
                    callback(imageUrl, null)
                } catch (e: Exception) {
                    callback(null, e)
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()

                val errorMessage = when (error) {
                    is AuthFailureError -> "Authentication failed: ${error.message}"
                    is NetworkError -> "Network error: ${error.message}"
                    is com.android.volley.ParseError -> "Parsing error: ${error.message}"
                    is ServerError -> {
                        val statusCode = error.networkResponse?.statusCode
                        "Server error: ${error.message}\nStatus code: $statusCode"
                    }
                    is TimeoutError -> "Request timed out: ${error.message}"
                    else -> "Unknown error: ${error.message}"
                }

                callback(null, Exception(errorMessage))
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer $TOKEN"
                return headers
            }

            override fun getRetryPolicy(): RetryPolicy {
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
