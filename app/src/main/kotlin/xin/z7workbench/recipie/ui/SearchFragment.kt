package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_histories.view.*
import kotlinx.android.synthetic.main.item_recipe.view.*
import kotlinx.android.synthetic.main.layout_search.view.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.Recipe

class SearchFragment : Fragment() {
    lateinit var v: View
    lateinit var historyAdapter: HistoryAdapter
    lateinit var recipeAdapter: RecipeResultAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.layout_search, container, false)

        historyAdapter = HistoryAdapter(requireActivity().defaultSharedPreferences.getStringSet("history", setOf()).toMutableList())
        recipeAdapter = RecipeResultAdapter()

        v.apply {
            back.setOnClickListener { activity?.onBackPressed() }

            histories.adapter = historyAdapter
            result.adapter = recipeAdapter

            keyword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (charSequence.isEmpty()) {
                        history_layout.visibility = View.VISIBLE
                    } else {
                        history_layout.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(editable: Editable) {}
            })



            keyword.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch(keyword.text.toString())
                }
                true
            }
        }


        return v

    }

    private fun doSearch(keyword: String) {
        val historyList = requireActivity().defaultSharedPreferences.getStringSet("history", setOf()).toMutableList()
        historyList.add(keyword)
        activity!!.defaultSharedPreferences.edit().putStringSet("history", historyList.toSet()).apply()
        RecipieRetrofit.recipe.searchByKeyword(keyword).prepare(context!!).subscribe {
            v.history_layout.visibility = View.GONE

            recipeAdapter.list = it
            if (it.isEmpty()) requireActivity().toast(R.string.no_result)
            recipeAdapter.notifyDataSetChanged()
        }
    }

    inner class HistoryAdapter(val list: MutableList<String>) : RecyclerView.Adapter<HistoryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                HistoryViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_histories, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            holder.v.apply {
                text.text = list[position]
                delete.setOnClickListener {
                    list.removeAt(position)
                    context.defaultSharedPreferences.edit().putStringSet("history", list.toSet()).apply()
                    notifyDataSetChanged()
                }
                text.setOnClickListener {
                    v.keyword.setText(list[position])
                    doSearch(list[position])
                }
            }
        }

    }
    class HistoryViewHolder(val v: View) : RecyclerView.ViewHolder(v)

    class RecipeResultAdapter(var list: List<Recipe> = listOf()) : RecyclerView.Adapter<RecipeResultAdapter.SearchResultViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                SearchResultViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recipe, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
            holder.v.apply {
                title.text = list[position].title
                like_count.text = "${list[position].like_count}${context.getString(R.string.like_tail)}"
                read_count.text = "${list[position].read_count}${context.getString(R.string.read_tail)}"
                description.text = list[position].description
                author.text = list[position].create_by?.nickname ?: "Unknown"

                recipe_view.setOnClickListener {
                    context.startActivity<RecipeActivity>("recipe_id" to list[position].id)
                }
            }
        }

        class SearchResultViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }
}