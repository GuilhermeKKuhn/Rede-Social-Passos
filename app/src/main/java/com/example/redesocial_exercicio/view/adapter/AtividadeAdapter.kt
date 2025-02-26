package com.example.redesocial_exercicio.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocial_exercicio.databinding.ItemAtividadeBinding

data class AtividadePerfil(
    val descricao: String = "",
    val passos: Int = 0,
    val data: String = ""
)

class AtividadeAdapter(private var atividades: MutableList<AtividadePerfil>) :
    RecyclerView.Adapter<AtividadeAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAtividadeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(atividade: AtividadePerfil) {
            binding.tvDescricao.text = atividade.descricao // Adicionando a descrição
            binding.tvPassos.text = "${atividade.passos} passos"
            binding.tvData.text = atividade.data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAtividadeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(atividades[position])
    }

    override fun getItemCount(): Int = atividades.size

    fun atualizarLista(novaLista: List<AtividadePerfil>) {
        atividades.clear()
        atividades.addAll(novaLista)
        notifyDataSetChanged() // Notifica o adapter que os dados mudaram
    }
}

