package com.example.redesocial_exercicio.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocial_exercicio.databinding.ItemGrupoBinding

data class Grupo(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val participantes: Int = 0
)

class GrupoAdapter(
    private val context: Context,
    private val grupos: List<Grupo>,
    private val onEntrarClick: (Grupo) -> Unit,
    private val onGrupoClick: (Grupo) -> Unit
) : RecyclerView.Adapter<GrupoAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemGrupoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(grupo: Grupo) {
            binding.tvNomeGrupo.text = grupo.nome
            binding.tvDescricaoGrupo.text = grupo.descricao
            binding.tvQtdParticipantes.text = "Membros: ${grupo.participantes}"

            binding.btnEntrarGrupo.setOnClickListener {
                onEntrarClick(grupo)
            }

            binding.root.setOnClickListener {
                onGrupoClick(grupo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGrupoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(grupos[position])
    }

    override fun getItemCount(): Int = grupos.size
}


