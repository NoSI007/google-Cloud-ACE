package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getExpandedExplanation(topicTitle: String, contextDetail: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Google Gemini API key is required for AI Deep Dive explanations. Please configure your API key in the Secrets panel in AI Studio."
        }

        val prompt = """
            You are a Google Cloud Certified Associate Cloud Engineer (ACE) tutor and principal architect.
            Explain the following GCP service, concept, or exam tip in detail for a student preparing for the Associate Cloud Engineer certification exam.

            TOPIC / TERM: $topicTitle
            CONTEXT / SUMMARY: $contextDetail

            Please structure your explanation clearly with the following sections:
            1. Core Architecture & Use Cases: What is it, why is it used, and how does it fit into GCP architectures?
            2. Key ACE Exam Trap & Rules: Specific CLI flags (gcloud/kubectl), IAM permissions, or configuration rules tested on the exam.
            3. Real-World Decision Matrix: When to choose this vs alternative GCP services.

            Keep it concise, high-value, and easy to read with bullet points.
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseStr = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext "AI Request failed (${response.code}): ${if (responseStr.contains("API key")) "Invalid or unauthorized API key." else "Please try again later."}"
            }

            val responseJson = JSONObject(responseStr)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNull_Blank()) {
                text!!
            } else {
                "No AI explanation was generated. Please try again."
            }

        } catch (e: Exception) {
            "Unable to generate AI explanation: ${e.localizedMessage ?: "Network error"}"
        }
    }

    private fun String?.isNull_Blank(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
