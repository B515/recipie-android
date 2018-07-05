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
    private val server = "http://123.206.13.211"
    private val jwt = JwtInterceptor("")
    private val headers = Headers.Builder()
            .add("Origin", server)
            .add("User-Agent", "")
            .add("Accept", "application/json, text/javascript, */*; q=0.01")
            .add("Accept-Encoding", "gzip, deflate")
            .build()
    val gson: Gson by lazy {
        GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                //.setDateFormat("yyyy-MM-dd' 'HH:mm:ss")
                .create()
    }
    private val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor { it.proceed(it.request().newBuilder().headers(headers).build()) }
            .addInterceptor(jwt)
            .cookieJar(object : CookieJar {
                private val cookieStore = HashMap<String, List<Cookie>>()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore.put(url.host(), cookies)
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
        jwt.token = token
    }

    private inline fun <reified T> create(): T = retrofit.create<T>(T::class.java)

    val auth by lazy { create<AuthAPI>() }
}