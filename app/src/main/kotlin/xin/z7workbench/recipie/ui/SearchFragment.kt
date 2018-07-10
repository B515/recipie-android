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
import kotlinx.android.synthetic.main.layout_search.view.*
import org.jetbrains.anko.defaultSharedPreferences
import xin.z7workbench.recipie.R

class SearchFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_search, container, false)

        view.apply {
            back.setOnClickListener { activity?.onBackPressed() }

            keyword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    val historyList = requireActivity().defaultSharedPreferences.getStringSet("history", setOf()).toList().distinct()
                    if (charSequence.isEmpty()) {
                        // TODO update history
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


        return view

    }

    private fun doSearch(keyword: String) {

    }

    class HistoryAdapter(val list: List<String>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getItemCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        class HistoryViewHolder(v: View) : RecyclerView.ViewHolder(v)
    }
}