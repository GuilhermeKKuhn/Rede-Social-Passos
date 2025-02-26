package com.example.redesocial_exercicio.view.TelaPrincipal.view.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redesocial_exercicio.databinding.FragmentPerfilBinding
import com.example.redesocial_exercicio.view.adapter.Atividade
import com.example.redesocial_exercicio.view.adapter.AtividadeAdapter
import com.example.redesocial_exercicio.view.adapter.AtividadePerfil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerfilFragment : Fragment() {
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var atividadesAdapter: AtividadeAdapter
    private val listaAtividades = mutableListOf<AtividadePerfil>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        binding.recyclerAtividades.layoutManager = LinearLayoutManager(requireContext())
        atividadesAdapter = AtividadeAdapter(listaAtividades)
        binding.recyclerAtividades.adapter = atividadesAdapter

        carregarDadosUsuario()
        carregarAtividadesRecentes()

        return binding.root
    }


    private fun carregarDadosUsuario() {
        val user = auth.currentUser
        user?.let { usuario ->
            firestore.collection("users")
                .document(usuario.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val nome = document.getString("name") ?: "Usuário"
                        binding.tvNomeUsuario.text = nome
                    } else {
                        binding.tvNomeUsuario.text = "Usuário não encontrado"
                    }
                }
                .addOnFailureListener {
                    binding.tvNomeUsuario.text = "Erro ao carregar nome"
                }
            binding.tvEmailUsuario.text = usuario.email ?: "Email não disponível"

            firestore.collection("atividades")
                .whereEqualTo("uid", usuario.uid)
                .get()
                .addOnSuccessListener { atividades ->
                    var totalPassos = 0
                    for (atividade in atividades) {
                        totalPassos += atividade.getLong("passos")?.toInt() ?: 0
                    }
                    binding.tvTotalPassos.text = "Total de passos: $totalPassos"
                }
                .addOnFailureListener {
                    binding.tvTotalPassos.text = "Erro ao carregar passos"
                }
        }
    }



    private fun carregarAtividadesRecentes() {
        val user = auth.currentUser
        user?.let {
            firestore.collection("atividades")
                .whereEqualTo("uid", it.uid)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener { documents ->
                    val novaLista = mutableListOf<AtividadePerfil>()
                    for (document in documents) {
                        val passos = document.getLong("passos")?.toInt() ?: 0
                        val timestamp = document.getLong("timestamp") ?: 0L
                        val dataFormatada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))


                        val descricao = when {
                            passos < 1000 -> "Caminhada curta"
                            passos < 5000 -> "Atividade moderada"
                            else -> "Caminhada longa"
                        }

                        val atividade = AtividadePerfil(descricao, passos, dataFormatada)
                        novaLista.add(atividade)
                    }
                    atividadesAdapter.atualizarLista(novaLista)
                }
                .addOnFailureListener { e ->
                    println("Erro ao carregar atividades: ${e.message}")
                }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
