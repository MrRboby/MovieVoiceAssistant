package ru.alekos.voiceassistant.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MovieSearchResult {
    @SerializedName("results")
    @Expose
    var movieList: List<Result> = ArrayList()

    inner class Result {
        @SerializedName("id")
        @Expose
        var tmdbId: Int = 0

        @SerializedName("title")
        @Expose
        var title: String = ""
    }
}