package xin.z7workbench.recipie.api

import io.reactivex.Flowable
import retrofit2.http.*
import xin.z7workbench.recipie.entity.Token

interface AuthAPI {
    @FormUrlEncoded
    @POST("/auth/login/")
    fun login(@Field("username") username: String, @Field("email") email: String, @Field("password") password: String): Flowable<Token>

    @FormUrlEncoded
    @POST("/auth/registration/")
    fun register(@Field("username") username: String, @Field("email") email: String, @Field("password1") password1: String, @Field("password2") password2: String): Flowable<Token>

    @FormUrlEncoded
    @POST("/users/")
    fun createUserInfo(@Field("nickname") nickname: String, @Field("gender") gender: Int, @Field("avatar") avatar: String, @Field("user") user: Int)

    fun updateUserInfo()
}

interface RecipeAPI {
    @FormUrlEncoded
    @POST("/recipes/")
    fun createRecipe(@Field("title") title: String, @Field("content") content: String)

    @FormUrlEncoded
    @PATCH("/recipes/{id}/")
    fun updateRecipe(@Path("id") id: Int, @Field("title") title: String, @Field("content") content: String)

    @GET("/recipes/{id}/")
    fun getRecipe(@Path("id") id: Int)

    fun likeRecipe()
    fun unlikeRecipe()
    fun collectRecipe()
    fun uncollectRecipe()
    fun createComment()
}
