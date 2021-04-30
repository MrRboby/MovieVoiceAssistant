package ru.alekos.voiceassistant.service

import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.alekos.voiceassistant.client.TmdbApi
import ru.alekos.voiceassistant.model.MovieRecommendations
import ru.alekos.voiceassistant.model.MovieSearchResult

object TmdbService {

    private val api = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(TmdbApi::class.java)

    fun searchMovie(query: String): Observable<MovieSearchResult> = api.searchMovie(query)

    fun getRecommendations(movieId: Int): Observable<MovieRecommendations> = api.getRecommendations(movieId)
}