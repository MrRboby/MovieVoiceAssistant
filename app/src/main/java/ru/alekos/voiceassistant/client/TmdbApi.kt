package ru.alekos.voiceassistant.client

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.alekos.voiceassistant.model.MovieRecommendations
import ru.alekos.voiceassistant.model.MovieSearchResult

interface TmdbApi {
    @GET("/3/search/movie?api_key=ebf1bbd27fd736df9076f3b777f08d22&language=ru-RU")
    fun searchMovie(@Query("query") query: String): Observable<MovieSearchResult>

    @GET("/3/movie/{movie_id}/recommendations?api_key=ebf1bbd27fd736df9076f3b777f08d22&language=ru-RU")
    fun getRecommendations(@Path("movie_id") movieId: Int): Observable<MovieRecommendations>
}