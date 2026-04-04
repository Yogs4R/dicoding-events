package com.example.dicoding_events.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetailEventResponse(
    @SerializedName("error")
    val error: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("event")
    val event: ListEventsItem = ListEventsItem()
)


