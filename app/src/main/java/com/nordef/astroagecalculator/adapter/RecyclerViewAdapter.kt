package com.nordef.astroagecalculator.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.nordef.astroagecalculator.R
import com.nordef.astroagecalculator.model.ListItemPlanetsData
import java.text.DecimalFormat


class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    var arrayList = ArrayList<ListItemPlanetsData>()
    var context: Context

    constructor(context: Context, arrayList: ArrayList<ListItemPlanetsData>) {
        this.context = context
        this.arrayList = arrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_rv_world_planet,
            null
        )
        return ViewHolder1(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                val holder1 = holder as ViewHolder1
                val planetsData = arrayList[position]
                val df = DecimalFormat("#.#")

                holder1.iv_planets.setImageResource(planetsData.image)
                holder1.tv_name.text = planetsData.name
                holder1.tv_days.text = df.format(planetsData.rotation_period).toString()
                holder1.tv_years.text = df.format(planetsData.orbit_period).toString()

                val animation = AnimationUtils.loadAnimation(context, R.anim.rotate)
                holder1.iv_planets.startAnimation(animation)
            }
        }
    }

    //is called to display animation when the view disappear and reappear again
    //in other world, to keep animation running
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is ViewHolder1) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.rotate)
            holder.iv_planets.startAnimation(animation)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    class ViewHolder1(view: View) : RecyclerView.ViewHolder(view) {

        var tv_days: TextView = view.findViewById(R.id.tv_days)
        var tv_years: TextView = view.findViewById(R.id.tv_years)
        var tv_name: TextView = view.findViewById(R.id.tv_name)
        var iv_planets: ImageView = view.findViewById(R.id.iv_planets)

    }
}