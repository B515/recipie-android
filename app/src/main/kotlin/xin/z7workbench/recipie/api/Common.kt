package xin.z7workbench.recipie.api

import android.content.Context
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import retrofit2.HttpException
import java.io.IOException

fun <T> Flowable<T>.prepare(context: Context, onError: () -> Unit = {}): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable: Throwable ->
                handleThrowable(throwable, context)
                onError()
                Flowable.empty<T>()
            }
}

fun handleThrowable(throwable: Throwable, context: Context) {
    when (throwable) {
        is HttpException -> {
            context.toast("${throwable.code()} ${throwable.message()}\n${throwable.response().errorBody()?.string()}\nPlease contact WildHunter as soon as possible.")
        }
        is IOException -> {
            context.toast("Network error")
        }
        else -> {
            context.toast(throwable.message.toString())
        }
    }
    throwable.printStackTrace()
}