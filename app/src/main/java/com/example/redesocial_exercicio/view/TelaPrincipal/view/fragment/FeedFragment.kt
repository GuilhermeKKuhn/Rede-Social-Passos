package com.example.redesocial_exercicio.view.TelaPrincipal.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redesocial_exercicio.databinding.FragmentFeedBinding
import com.example.redesocial_exercicio.view.adapter.Atividade
import com.example.redesocial_exercicio.view.adapter.FeedAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: FeedAdapter
    private val atividades = mutableListOf<Atividade>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(requireContext())

        carregarAtividades()

        return binding.root
    }

    private fun carregarAtividades() {
        firestore.collection("atividades")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                atividades.clear()

                for (document in documents) {
                    val nomeUsuario = document.getString("nomeUsuario") ?: "Desconhecido"
                    val passos = document.getLong("passos")?.toInt() ?: 0
                    val timestamp = document.getLong("timestamp") ?: 0L
                    val trajetoria = mutableListOf<Pair<Double, Double>>()

                    val trajetoriaFirestore = document.get("trajetoria") as? List<HashMap<String, Double>>
                    trajetoriaFirestore?.forEach { ponto ->
                        val lat = ponto["lat"] ?: 0.0
                        val lng = ponto["lng"] ?: 0.0
                        trajetoria.add(Pair(lat, lng))
                    }

                    val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                        Date(timestamp)
                    )
                    atividades.add(Atividade(nomeUsuario, passos, dataFormatada, trajetoria))
                }

                adapter = FeedAdapter(requireContext(), atividades)
                binding.recyclerViewFeed.adapter = adapter
            }
    }
}
