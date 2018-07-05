package xin.z7workbench.recipie

import android.app.Application
import org.jetbrains.anko.defaultSharedPreferences
import xin.z7workbench.recipie.api.RecipieRetrofit

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        RecipieRetrofit.loadToken(defaultSharedPreferences.getString("token", ""))
    }
}