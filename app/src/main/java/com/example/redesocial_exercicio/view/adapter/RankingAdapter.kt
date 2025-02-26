package com.example.redesocial_exercicio.view.TelaPrincipal.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocial_exercicio.R

class RankingAdapter(private val rankingList: List<Pair<String, Int>>) :
    RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeUsuario: TextView = itemView.findViewById(R.id.tvNomeUsuario)
        val tvPassos: TextView = itemView.findViewById(R.id.tvPassos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return RankingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val (nome, passos) = rankingList[position]
        holder.tvNomeUsuario.text = nome
        holder.tvPassos.text = "$passos passos"
    }

    override fun getItemCount() = rankingList.size
}
