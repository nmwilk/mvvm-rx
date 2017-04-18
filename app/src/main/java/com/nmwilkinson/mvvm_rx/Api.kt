package com.nmwilkinson.mvvm_rx

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class Api {
    private val ipService: IpService

    init {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }
        val client = builder.build()
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(client)
                .baseUrl("https://ipvigilante.com")
                .build()
        ipService = retrofit.create(IpService::class.java)
    }

    fun ipDetails(ip: String): Single<String> {
        return ipService.ipDetails(ip).map { "${it.data!!.countryName!!} - ${it.data.cityName ?: "unknown city"}" }.singleOrError()
    }

    interface IpService {
        @GET("/{ip}")
        abstract fun ipDetails(@Path("ip") ip: String): Observable<IpDetails>
    }
}

class IpDetails(val status: String?, val data: Data?)

class Data(@SerializedName("country_name") val countryName: String?,
           @SerializedName("city_name") val cityName: String?)
