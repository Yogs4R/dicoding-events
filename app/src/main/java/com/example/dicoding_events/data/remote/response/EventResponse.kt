package com.example.dicoding_events.data.remote.response

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("error")
    val error: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("listEvents")
    val listEvents: List<ListEventsItem> = listOf()
)

data class ListEventsItem(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("summary")
    val summary: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("imageLogo")
    val imageLogo: String = "",
    @SerializedName("mediaCover")
    val mediaCover: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("ownerName")
    val ownerName: String = "",
    @SerializedName("cityName")
    val cityName: String = "",
    @SerializedName("quota")
    val quota: Int = 0,
    @SerializedName("registrants")
    val registrant: Int = 0,
    @SerializedName("beginTime")
    val beginTime: String = "",
    @SerializedName("endTime")
    val endTime: String = "",
    @SerializedName("link")
    val link: String = ""
)


