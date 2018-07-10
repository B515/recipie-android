package xin.z7workbench.recipie.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object RecipieRetrofit {
    private val server = "http://123.207.164.148:1997"
    private val ti = TokenInterceptor("")
    private val headers = Headers.Builder()
            .add("Origin", server)
            .add("Accept", "application/json")
            .build()
    val gson: Gson by lazy {
        GsonBuilder().registerTypeAdapterFactory(PrimaryKeyToNull())
                .create()
    }
    private val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor { it.proceed(it.request().newBuilder().headers(headers).build()) }
            .addInterceptor(ti)
            .cookieJar(object : CookieJar {
                private val cookieStore = HashMap<String, List<Cookie>>()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host()] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url.host()] ?: ArrayList<Cookie>()
                }
            }).build()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(server)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    fun loadToken(token: String) {
        ti.token = token
    }

    private inline fun <reified T> create(): T = retrofit.create<T>(T::class.java)

    val auth by lazy { create<AuthAPI>() }
    val recipe by lazy { create<RecipeAPI>() }
}