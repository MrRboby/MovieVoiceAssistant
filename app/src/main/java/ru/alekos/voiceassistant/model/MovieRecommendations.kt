package ru.alekos.voiceassistant.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MovieRecommendations {
    @SerializedName("results")
    @Expose
    var movieList: List<Result> = ArrayList()

    inner class Result {
        @SerializedName("title")
        @Expose
        var title: String = ""

        @SerializedName("overview")
        @Expose
        var overview: String = ""

        @SerializedName("popularity")
        @Expose
        var popularity: Double = 0.0
    }
}