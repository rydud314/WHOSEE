package com.example.seesaw.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface ChatGPTApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer " // api키는 Bearer 뒤에 공백 한칸 띄고 입력합니다.
    )
    @POST("v1/chat/completions")
    fun getChatResponse(@Body request: ChatGPTRequest?): Call<ChatGPTResponse?>?
}