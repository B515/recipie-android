package xin.z7workbench.recipie.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by Yun on 2017.8.17.
 */

class TokenInterceptor(var token: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (token == "") return chain.proceed(chain.request())
        return chain.proceed(chain.request()
                .newBuilder()
                .header("Authorization", "Token $token")
                .build())
    }

}
