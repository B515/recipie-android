package xin.z7workbench.recipie.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.app_bar.*
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.Recipe

class RecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val id = intent.extras.getInt("recipe_id")
        val viewModel = ViewModelProviders.of(this)[RecipeViewModel::class.java]
        RecipieRetrofit.recipe.getRecipe(id).prepare(this).subscribe { viewModel.recipe.value = it }
    }
}

class RecipeViewModel : ViewModel() {
    val recipe: MutableLiveData<Recipe?> = MutableLiveData()
}