package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_recipe_display.view.*
import xin.z7workbench.recipie.R

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val peopleAdapter = PeopleAdapter()
        val llm = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically() = false
        }

        view.apply {
            people.apply {
                adapter = peopleAdapter
                layoutManager = llm
            }

        }

        return view
    }

    class PeopleAdapter(val list: List<String> = mutableListOf("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1")) : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder =
                PeopleViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recipe_display, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            holder.v.apply {
                description.text = list[position]
            }
        }

        class PeopleViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }
}