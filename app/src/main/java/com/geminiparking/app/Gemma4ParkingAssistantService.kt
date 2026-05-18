package com.geminiparking.app

import android.graphics.Bitmap
import kotlinx.coroutines.delay
import kotlin.random.Random

interface Gemma4ParkingAssistantService {
    suspend fun getPhoneNumberByImage(image: Bitmap): String?
}

class MockGemma4ParkingAssistantService : Gemma4ParkingAssistantService {
    private companion object {
        const val MOCK_INFERENCE_DELAY_MS = 1_800L
        const val SECOND_DIGIT_MIN = 3
        const val SECOND_DIGIT_MAX_EXCLUSIVE = 10
    }

    override suspend fun getPhoneNumberByImage(image: Bitmap): String? {
        // Gemma4 model inference / API call placeholder.
        // This mock simulates network + inference latency and then chooses one of two branches:
        // Branch A: Recognized -> returns a random 11-digit mobile number.
        // Branch B: Not recognized / temporary external vehicle -> returns null.
        // Replace this block with real Gemma4 model inference or backend API integration.
        delay(MOCK_INFERENCE_DELAY_MS)
        val recognized = Random.nextBoolean()
        if (!recognized) return null

        val first = "1"
        val second = Random.nextInt(SECOND_DIGIT_MIN, SECOND_DIGIT_MAX_EXCLUSIVE).toString()
        val rest = (1..9).joinToString(separator = "") { Random.nextInt(0, 10).toString() }
        return first + second + rest
    }
}
