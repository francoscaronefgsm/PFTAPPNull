package com.example.pft_appnull.model

import com.google.gson.annotations.SerializedName

data class Reclamo(@SerializedName("id") val id: Long,
                   @SerializedName("title") val title: String,
                   @SerializedName("description") val description: String,
                   @SerializedName("created") val created: String,
                   @SerializedName("updated") val updated: String,
                   @SerializedName("eventVme") val eventVme: String,
                   @SerializedName("activityApe") val activityApe: String,
                   @SerializedName("semester") val semester: Int,
                   @SerializedName("date") val date: String,
                   @SerializedName("teacher") val teacher: String,
                   @SerializedName("credits") val credits: Int,
                   @SerializedName("status") val status: String,
                   @SerializedName("user") val userId: Long
)
{
    override fun toString(): String {
        return "Título: $title\nTipo: $eventVme\nFecha de Emisión: $created\nCréditos: $credits"
    }

}
