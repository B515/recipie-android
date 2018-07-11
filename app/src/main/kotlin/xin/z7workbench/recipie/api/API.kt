package xin.z7workbench.recipie.api

import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import xin.z7workbench.recipie.entity.*

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

    @POST("/api/users/{id}/follow/")
    fun follow(@Path("id") id: Int): Flowable<Result>

    @POST("/api/users/{id}/unfollow/")
    fun unfollow(@Path("id") id: Int): Flowable<Result>

    @GET("/api/users/followers/")
    fun getMyFollowers(): Flowable<List<UserInfo>>
}

interface RecipeAPI {
    @FormUrlEncoded
    @POST("/api/recipes/")
    fun createRecipe(@Field("title") title: String, @Field("content") content: String, @Field("description") description: String, @Field("tag") tags: String, @Field("create_by") create_by: Int = 1): Flowable<Recipe>

    @FormUrlEncoded
    @PATCH("/api/recipes/{id}/")
    fun updateRecipe(@Path("id") id: Int, @Field("title") title: String, @Field("content") content: String, @Field("description") description: String, @Field("tag") tags: String): Flowable<Recipe>

    @GET("/api/recipes/")
    fun getAllRecipes(): Flowable<List<Recipe>>

    @GET("/api/recipes/{id}/")
    fun getRecipe(@Path("id") id: Int): Flowable<Recipe>

    @GET("/api/recipes/search_by_keyword/")
    fun searchByKeyword(@Query("keyword") keyword: String): Flowable<List<Recipe>>

    @POST("/api/recipes/{id}/like/")
    fun likeRecipe(@Path("id") id: Int): Flowable<Result>

    @POST("/api/recipes/{id}/unlike/")
    fun unlikeRecipe(@Path("id") id: Int): Flowable<Result>

    @POST("/api/recipes/{id}/collect/")
    fun collectRecipe(@Path("id") id: Int): Flowable<Result>

    @POST("/api/recipes/{id}/uncollect/")
    fun uncollectRecipe(@Path("id") id: Int): Flowable<Result>

    @FormUrlEncoded
    @POST("/api/tags/")
    fun createTag(@Field("title") title: String, @Field("description") description: String): Flowable<Tag>

    @GET("/api/tags/")
    fun getAllTags(): Flowable<List<Tag>>

    @GET("/api/tags/{id}/")
    fun getTag(@Path("id") id: Int): Flowable<Tag>

    @FormUrlEncoded
    @POST("/api/comments/")
    fun createComment(@Field("recipe") id: Int, @Field("content") content: String, @Field("userinfo") userinfo: Int = 1): Flowable<Comment>

    @POST("/api/comments/{id}/like/")
    fun likeComment(@Path("id") id: Int): Flowable<Result>

    @POST("/api/comments/{id}/unlike/")
    fun unlikeComment(@Path("id") id: Int): Flowable<Result>

    @Multipart
    @POST("/api/files/")
    fun uploadFile(@Part("owner") owner: RequestBody, @Part file: MultipartBody.Part): Flowable<File>
}
