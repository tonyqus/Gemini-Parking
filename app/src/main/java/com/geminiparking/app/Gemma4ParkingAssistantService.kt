package com.geminiparking.app

import android.graphics.Bitmap
import kotlinx.coroutines.delay
import kotlin.random.Random

data class OwnerLookupResult(
    val phone: String?,
    val building: String,
    val room: String,
)

interface Gemma4ParkingAssistantService {
    suspend fun getOwnerInfoByImage(image: Bitmap): OwnerLookupResult?
}

class MockGemma4ParkingAssistantService : Gemma4ParkingAssistantService {
    private companion object {
        const val MOCK_INFERENCE_DELAY_MS = 1_800L
        const val SECOND_DIGIT_MIN = 3
        const val SECOND_DIGIT_MAX_EXCLUSIVE = 10
    }

    private val mockOwners = listOf(
        OwnerLookupResult(phone = "13800138000", building = "1号门洞", room = "101室"),
        OwnerLookupResult(phone = "13900139000", building = "2号门洞", room = "202室"),
        OwnerLookupResult(phone = null, building = "3号门洞", room = "301室"),
    )

    override suspend fun getOwnerInfoByImage(image: Bitmap): OwnerLookupResult? {
        // Gemma4 model inference / API call placeholder.
        // This mock simulates network + inference latency and then chooses one of two branches:
        // Branch A: Recognized -> returns owner information for the matched plate.
        // Branch B: Not recognized / temporary external vehicle -> returns null.
        // Replace this block with real Gemma4 model inference or backend API integration.
        delay(MOCK_INFERENCE_DELAY_MS)
        val recognized = Random.nextBoolean()
        if (!recognized) return null

        return mockOwners.random().let { owner ->
            if (owner.phone != null) {
                val first = "1"
                val second = Random.nextInt(SECOND_DIGIT_MIN, SECOND_DIGIT_MAX_EXCLUSIVE).toString()
                val rest = (1..9).joinToString(separator = "") { Random.nextInt(0, 10).toString() }
                owner.copy(phone = first + second + rest)
            } else {
                owner
            }
        }
    }
}
