package com.example.redesocial_exercicio.view.TelaPrincipal.view.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.example.redesocial_exercicio.databinding.FragmentVizugrupoBinding
import com.example.redesocial_exercicio.view.TelaPrincipal.view.adapter.RankingAdapter

class VisualizarGrupoFragment : Fragment() {
    private var _binding: FragmentVizugrupoBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private var groupId: String? = null
    private val rankingList = mutableListOf<Pair<String, Int>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVizugrupoBinding.inflate(inflater, container, false)

        groupId = arguments?.getString("groupId")
        carregarDadosGrupo()

        return binding.root
    }

    private fun carregarDadosGrupo() {
        groupId?.let { id ->
            firestore.collection("grupos").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.tvNomeGrupo.text = document.getString("nome")
                        binding.tvDescricaoGrupo.text = document.getString("descricao")
                        carregarParticipantes(id)
                    }
                }
        }
    }

    private fun carregarParticipantes(groupId: String) {
        firestore.collection("grupos").document(groupId).collection("membros")
            .get()
            .addOnSuccessListener { documents ->
                rankingList.clear()

                if (documents.isEmpty) {
                    atualizarRanking()
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val userId = document.getString("userId") ?: continue
                    val passosInicio = document.getLong("passosInicio")?.toInt() ?: 0

                    firestore.collection("users").document(userId).get()
                        .addOnSuccessListener { userDoc ->
                            val nomeUsuario = userDoc.getString("name") ?: "Usuário desconhecido"

                            firestore.collection("atividades")
                                .whereEqualTo("uid", userId)
                                .get()
                                .addOnSuccessListener { atividades ->
                                    var passosAtuais = 0

                                    for (atividade in atividades) {
                                        passosAtuais += atividade.getLong("passos")?.toInt() ?: 0
                                    }

                                    val passosContabilizados = passosAtuais - passosInicio
                                    if (passosContabilizados > 0) {
                                        rankingList.add(Pair(nomeUsuario, passosContabilizados))
                                    }

                                    if (rankingList.size == documents.size()) {
                                        atualizarRanking()
                                    }
                                }
                        }
                }
            }
    }

    private fun atualizarRanking() {
        rankingList.sortByDescending { it.second }

        val adapter = RankingAdapter(rankingList)
        binding.recyclerViewRanking.adapter = adapter
        binding.recyclerViewRanking.layoutManager = LinearLayoutManager(requireContext())
    }


}
