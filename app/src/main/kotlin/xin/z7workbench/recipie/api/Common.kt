package xin.z7workbench.recipie.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
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

class PrimaryKeyToNull : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, type)

        return object : TypeAdapter<T>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T) = delegate.write(out, value)

            @Throws(IOException::class)
            override fun read(i: JsonReader): T? {
                return try {
                    delegate.read(i)
                } catch (e: JsonSyntaxException) {
                    gson.getAdapter(Int::class.java).read(i)
                    null
                }
            }
        }
    }
}