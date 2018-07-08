package xin.z7workbench.recipie.api

import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import xin.z7workbench.recipie.entity.Token

interface AuthAPI {
    @FormUrlEncoded
    @POST("/auth/login")
    fun login(@Field("username") username: String, @Field("email") email: String, @Field("password") password: String): Flowable<Token>

    @FormUrlEncoded
    @POST("/auth/registration")
    fun register(@Field("username") username: String, @Field("email") email: String, @Field("password1") password1: String, @Field("password2") password2: String): Flowable<String>

}