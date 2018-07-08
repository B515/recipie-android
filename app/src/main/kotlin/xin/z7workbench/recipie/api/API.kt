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
    @POST("/auth/password/change/")
    fun changePassword(@Field("old_password") old_password: String, @Field("new_password1") new_password1: String, @Field("new_password2") new_password2: String)

    @FormUrlEncoded
    @POST("/api/users/")
    fun createUserInfo(@Field("nickname") nickname: String, @Field("gender") gender: Int, @Field("avatar") avatar: String, @Field("user") user: Int)

    fun updateUserInfo()
}

interface RecipeAPI {
    @FormUrlEncoded
    @POST("/api/recipes/")
    fun createRecipe(@Field("title") title: String, @Field("content") content: String)

    @FormUrlEncoded
    @PATCH("/api/recipes/{id}/")
    fun updateRecipe(@Path("id") id: Int, @Field("title") title: String, @Field("content") content: String)

    @GET("/api/recipes/{id}/")
    fun getRecipe(@Path("id") id: Int)

    fun likeRecipe(id: Int)
    fun unlikeRecipe(id: Int)
    fun collectRecipe(id: Int)
    fun uncollectRecipe(id: Int)
    fun createComment(id: Int, content: String)
}
