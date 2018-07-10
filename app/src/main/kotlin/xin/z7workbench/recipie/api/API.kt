package xin.z7workbench.recipie.api

import io.reactivex.Flowable
import retrofit2.http.*
import xin.z7workbench.recipie.entity.Comment
import xin.z7workbench.recipie.entity.Recipe
import xin.z7workbench.recipie.entity.Token
import xin.z7workbench.recipie.entity.UserInfo

interface AuthAPI {
    @FormUrlEncoded
    @POST("/auth/login/")
    fun login(@Field("username") username: String, @Field("password") password: String): Flowable<Token>

    @FormUrlEncoded
    @POST("/auth/registration/")
    fun register(@Field("username") username: String, @Field("password1") password1: String, @Field("password2") password2: String): Flowable<Token>

    @FormUrlEncoded
    @POST("/auth/password/change/")
    fun changePassword(@Field("old_password") old_password: String, @Field("new_password1") new_password1: String, @Field("new_password2") new_password2: String): Flowable<String>

    @FormUrlEncoded
    @POST("/api/users/")
    fun createUserInfo(@Field("nickname") nickname: String, @Field("gender") gender: Int, @Field("avatar") avatar: String): Flowable<UserInfo>

    @FormUrlEncoded
    @POST("/api/users/update_me/")
    fun updateMyUserInfo(@Field("nickname") nickname: String, @Field("gender") gender: Int, @Field("avatar") avatar: String): Flowable<UserInfo>

    @GET("/api/users/me/")
    fun getMyUserInfo(): Flowable<UserInfo>

    @GET("/api/users/{id}/")
    fun getUserInfo(@Path("id") id: Int): Flowable<UserInfo>
}

interface RecipeAPI {
    @FormUrlEncoded
    @POST("/api/recipes/")
    fun createRecipe(@Field("title") title: String, @Field("content") content: String, @Field("description") description: String, @Field("tag") tags: String): Flowable<Recipe>

    @FormUrlEncoded
    @PATCH("/api/recipes/{id}/")
    fun updateRecipe(@Path("id") id: Int, @Field("title") title: String, @Field("content") content: String, @Field("description") description: String, @Field("tag") tags: String): Flowable<Recipe>

    @GET("/api/recipes/{id}/")
    fun getRecipe(@Path("id") id: Int): Flowable<Recipe>

    @FormUrlEncoded
    @POST("/")
    fun likeRecipe(@Field("id") id: Int): Flowable<String>

    @FormUrlEncoded
    @POST("/")
    fun unlikeRecipe(@Field("id") id: Int): Flowable<String>

    @FormUrlEncoded
    @POST("/")
    fun collectRecipe(@Field("id") id: Int): Flowable<String>

    @FormUrlEncoded
    @POST("/")
    fun uncollectRecipe(@Field("id") id: Int): Flowable<String>

    @FormUrlEncoded
    @POST("/api/comments/")
    fun createComment(@Field("recipe") id: Int, @Field("content") content: String): Flowable<Comment>
}
