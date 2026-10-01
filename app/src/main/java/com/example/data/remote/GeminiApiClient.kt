package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String? = "user",
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    suspend fun askTutor(
        studentClass: Int,
        board: String,
        userQuery: String,
        history: List<Pair<String, String>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        // If no internet, invalid key, or offline: Provide offline scholar knowledge base answer!
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineSmartTutorAnswer(studentClass, board, userQuery)
        }

        val systemPrompt = """
You are Dr. Athena, an elite, warm, and hyper-encouraging AI Tutor for Class $studentClass ($board board) students on ScholarQuest.
Your duties:
1. Explain doubts step-by-step with simple analogies, crisp mathematical formulas, and real-life examples.
2. If the student asks for a study plan, give a structured timetable with daily slots, revision tricks, and high-yield chapters.
3. If they ask for practice questions, provide 2 challenging exam-standard questions with hints and solutions.
4. Keep the tone inspiring, gamified, and easy to read with bullet points and bold highlights.
        """.trimIndent()

        val contentsList = mutableListOf<GeminiContent>()
        for ((q, a) in history) {
            contentsList.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = q))))
            contentsList.add(GeminiContent(role = "model", parts = listOf(GeminiPart(text = a))))
        }
        contentsList.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = userQuery))))

        val requestPayload = GeminiRequest(
            contents = contentsList,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        try {
            val jsonString = requestAdapter.toJson(requestPayload)
            val requestBody = jsonString.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val parsed = responseAdapter.fromJson(responseBody)
                val reply = parsed?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!reply.isNullOrBlank()) {
                    return@withContext reply
                }
            }
            // Fallback if API returned error
            return@withContext getOfflineSmartTutorAnswer(studentClass, board, userQuery)
        } catch (e: Exception) {
            return@withContext getOfflineSmartTutorAnswer(studentClass, board, userQuery)
        }
    }

    private fun getOfflineSmartTutorAnswer(grade: Int, board: String, query: String): String {
        val lower = query.lowercase()
        return when {
            lower.contains("study plan") || lower.contains("schedule") || lower.contains("timetable") -> """
📅 **Personalized Smart Study Plan (Class $grade - $board)**:

🌟 **Philosophy:** 50/10 Pomodoro Method + Active Recall.

⏰ **Daily Blueprint:**
• **6:00 AM - 7:30 AM (Peak Focus):** High-Cognitive Subject (Maths / Physics numericals or derivations).
• **5:00 PM - 6:30 PM (Concept Mastery):** Chemistry / Biology / Science topic notes & NCERT in-depth reading.
• **7:30 PM - 8:30 PM (Competitive Practice):** Solve 15-20 Questions from the Competitive Question Bank.
• **9:30 PM - 10:15 PM (Active Recall):** Flashcards revision, PYQ analysis & formula diary update.

🏆 **Weekend Quest:**
Attempt 1 full Past Year Paper under exam timed conditions on ScholarQuest to build speed and accuracy!
            """.trimIndent()

            lower.contains("ohm") || lower.contains("electricity") || lower.contains("resistor") -> """
⚡ **Ohm's Law & Circuit Concepts (Class $grade)**:

• **Statement:** Potential difference (V) across conductor ends is directly proportional to current (I) at constant temperature:
  **V = I · R**
• **Resistors in Series:** Current remains identical:
  **R_eq = R₁ + R₂ + R₃**
• **Resistors in Parallel:** Voltage remains identical across each branch:
  **1 / R_eq = 1 / R₁ + 1 / R₂ + 1 / R₃**
• **Pro-Tip:** If a wire is stretched to double its length, its area halves, so resistance increases by **4 times**!
            """.trimIndent()

            lower.contains("photosynthesis") || lower.contains("chlorophyll") || lower.contains("plant") -> """
🌿 **Photosynthesis Step-by-Step (Class $grade Biology)**:

**Master Equation:**
6CO₂ + 12H₂O --[Sunlight / Chlorophyll]--> C₆H₁₂O₆ + 6O₂ + 6H₂O

**3 Crucial Stages for Exams:**
1. **Light Absorption:** Chlorophyll pigments capture solar photons.
2. **Photolysis:** Water molecules split into protons, electrons, and O₂ gas.
3. **Reduction:** Chemical energy (ATP & NADPH) reduces CO₂ into glucose carbohydrates.
• *Note:* Desert plants absorb CO₂ at night as malic acid to prevent stomatal water loss!
            """.trimIndent()

            lower.contains("quadratic") || lower.contains("roots") || lower.contains("discriminant") -> """
📐 **Quadratic Equations Mastery**:

• **Standard Form:** ax² + bx + c = 0
• **Quadratic Formula:** x = [-b ± √(b² - 4ac)] / (2a)
• **Discriminant (D = b² - 4ac):**
  - **D > 0:** Two distinct real roots.
  - **D = 0:** Two identical real roots (x = -b / 2a).
  - **D < 0:** No real roots (complex conjugates).
• **Sum of roots:** α + β = -b/a
• **Product of roots:** α · β = c/a
            """.trimIndent()

            lower.contains("redox") || lower.contains("oxidation") || lower.contains("reduction") -> """
🧪 **Redox Reactions Explained Simply**:

• **Oxidation:** Gain of Oxygen, Loss of Hydrogen, or **Loss of Electrons (LEO)**.
• **Reduction:** Loss of Oxygen, Gain of Hydrogen, or **Gain of Electrons (GER)**.
• **Rule of Thumb:**
  The substance oxidized is the **Reducing Agent**.
  The substance reduced is the **Oxidizing Agent**.
• **Classic Example:**
  CuO + H₂ → Cu + H₂O
  CuO loses O → Reduced (Oxidizing Agent: CuO)
  H₂ gains O → Oxidized (Reducing Agent: H₂)
            """.trimIndent()

            lower.contains("pyq") || lower.contains("exam") || lower.contains("tips") || lower.contains("board") -> """
🎯 **Topper Exam Strategy for Class $grade ($board)**:

1. **Step-Marking Awareness:** Always write formulas first, state given values with units, and box the final answer.
2. **Time Division:** Allocate 1.5 minutes per mark in 3-hour papers (e.g., 3-mark questions should take ~4.5 mins).
3. **Diagrams Rule:** Draw diagrams with pencil and label with horizontal parallel lines.
4. **Offline Practice:** Revise your bookmarked notes and solve at least 20 competitive questions daily on ScholarQuest!
            """.trimIndent()

            else -> """
💡 **Dr. Athena's Expert Guidance (Class $grade - $board)**:

Regarding: *"query"*

Here are the key academic principles to master:
1. **Core Concept:** Break down complex problems into fundamental axioms and known variables.
2. **Standard Formula & Units:** Always verify dimensional consistency before calculating.
3. **Common Board Trap:** Examiners frequently test edge cases where boundary conditions deviate.
4. **Actionable Next Step:** Check out the **Topic Notes** and test yourself in the **100+ Competitive Question Bank** in this chapter!

Feel free to ask me for any specific derivations, formulas, or personalized study plans!
            """.trimIndent()
        }
    }
}
