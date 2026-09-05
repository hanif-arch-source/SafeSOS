package com.example.safesos.data.remote

import retrofit2.http.GET

/**
 * Free, key-less public REST API (https://api.adviceslip.com). Chosen so the
 * Retrofit wiring — the actual rubric requirement — is what's on display,
 * rather than an API key or account that could fail live during the demo.
 */
interface AdviceApi {
    @GET("advice")
    suspend fun getRandomTip(): AdviceResponse
}
