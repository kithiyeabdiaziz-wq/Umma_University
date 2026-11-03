package com.example.umma_university

import android.util.Base64
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ImgBBUploader {
    // Your API key is placed here safely
    private val apiKey = "e22663c7f5bb64485e4466c0a5c62ca9"

    fun uploadImage(imageBytes: ByteArray, onResult: (String?) -> Unit) {
        val client = OkHttpClient()
        val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        val requestBody = FormBody.Builder()
            .add("key", apiKey)
            .add("image", base64Image)
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val jsonResponse = JSONObject(response.body?.string() ?: "")
                        val data = jsonResponse.getJSONObject("data")
                        val url = data.getString("url")
                        onResult(url)
                    } catch (e: Exception) {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            }
        })
    }
}