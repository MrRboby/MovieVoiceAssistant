package ru.alekos.voiceassistant.service

import android.content.Context
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.alekos.voiceassistant.R
import ru.alekos.voiceassistant.database.AppDatabase
import ru.alekos.voiceassistant.entity.MovieEntity
import ru.alekos.voiceassistant.model.MovieRecommendations
import ru.alekos.voiceassistant.model.MovieSearchResult
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

class QuestionService(private val context: Context, private val appDatabase: AppDatabase) {

    private val answers: HashMap<String, ((String) -> Observable<String>)> = HashMap()

    private val behaviorSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private var movieContext: MovieEntity? = null

    init {
        answers[context.getString(R.string.input_hello)] = {
            Observable.just(
                context.getString(R.string.output_hello)
            )
        }

        answers[context.getString(R.string.input_howareyou)] = {
            Observable.just(
                context.getString(R.string.output_howareyou)
            )
        }

        answers[context.getString(R.string.input_whatareyoudoing)] = {
            Observable.just(
                context.getString(R.string.output_whatareyoudoing)
            )
        }

        answers[context.getString(R.string.input_iwatchedmovie)] = { input ->
            Observable.create { emitter: ObservableEmitter<String> ->
                try {
                    val title = input.substring(context.getString(R.string.input_iwatchedmovie).length + 1).trim { it <= ' ' }
                    if (title.isEmpty())
                        throw Exception()
                    emitter.onNext(title)
                    emitter.onComplete()
                }
                catch (e: Exception) {
                    emitter.onError(Exception(context.getString(R.string.output_movie_unknown)))
                }
            }
                .flatMap(TmdbService::searchMovie)
                .map{movieSearchResult: MovieSearchResult ->
                    if (movieSearchResult.movieList.isEmpty()) {
                        throw Exception(context.getString(R.string.output_movie_unknown))
                    }
                    val movie = movieSearchResult.movieList[0]
                    movieContext = MovieEntity(null, movie.tmdbId, movie.title, 0)
                    return@map String.format(context.getString(R.string.output_movie_found), movie.title)
                }
        }

        answers[context.getString(R.string.input_score)] = { input ->
            Observable.create { emitter: ObservableEmitter<Int> ->
                try {
                    val score = input.substring(context.getString(R.string.input_score).length + 1).trim { it <= ' ' }
                    if (score.isEmpty())
                        throw Exception()
                    emitter.onNext(score.toInt())
                    emitter.onComplete()
                }
                catch (e: Exception) {
                    emitter.onError(Exception(context.getString(R.string.output_unknown)))
                }
            }
                .map{score: Int ->
                    if (score !in 0..10)
                        throw Exception(context.getString(R.string.output_scoreincorrect))
                    movieContext?.score = score
                    movieContext?.let { appDatabase.getMovieDao().insert(it) }
                    val title = movieContext?.title
                    movieContext = null
                    return@map String.format(context.getString(R.string.output_scored), title, score)
                }
        }
        
        answers[context.getString(R.string.input_advicemovie)] = {
            Observable.create { emitter: ObservableEmitter<Int> ->
                val movies = appDatabase.getMovieDao().getFavoriteMoviesOrderedByRating()
                if (movies.isEmpty()) {
                    emitter.onError(Exception(context.getString(R.string.output_recommendationunknown)))
                }
                emitter.onNext(movies[Random().nextInt(movies.size)].tmdbId)
                emitter.onComplete()
            }
                .flatMap(TmdbService::getRecommendations)
                .map { recommendations: MovieRecommendations ->
                    if (recommendations.movieList.isEmpty())
                        throw Exception()
                    val list = recommendations.movieList.sortedByDescending(MovieRecommendations.Result::popularity)
                    val movieNames = appDatabase.getMovieDao().getMovies().map(MovieEntity::title).toSet()
                    for (recommendation in list) {
                        if (recommendation.title in movieNames) {
                            continue
                        }
                        if (recommendation.overview != "") {
                            return@map String.format(context.getString(R.string.output_recommendationwithdescription), recommendation.title, recommendation.overview)
                        }
                        else{
                            return@map String.format(context.getString(R.string.output_recommendation_withoutdescription), recommendation.title)
                        }
                    }
                    return@map context.getString(R.string.output_recommendationallseen)
                }
        }

    }

    fun subscribe(observer: Observer<String>) {
        behaviorSubject.subscribe(observer)
    }

    fun getAnswer(input: String) {
        val inp = input.toLowerCase(Locale.getDefault())
        var observable = Observable.just(context.getString(R.string.output_unknown))
        for (answer in answers.keys) {
            if (inp.contains(answer)) {
                if (answer == "оценка" && movieContext == null || answer != "оценка" && movieContext != null) {
                    continue
                }
                observable = answers[answer]?.let { it(inp) }
                break
            }
        }
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { it.message }
            .subscribe(behaviorSubject::onNext)
    }
}