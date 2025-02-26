package com.example.redesocial_exercicio.view.TelaPrincipal.view.fragment



import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redesocial_exercicio.R
import com.example.redesocial_exercicio.databinding.DialogGrupoBinding
import com.example.redesocial_exercicio.databinding.FragmentGrupoBinding
import com.example.redesocial_exercicio.view.adapter.Grupo
import com.example.redesocial_exercicio.view.adapter.GrupoAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GrupoFragment : Fragment() {

    private var _binding: FragmentGrupoBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var grupoAdapter: GrupoAdapter
    private val listaGrupos = mutableListOf<Grupo>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGrupoBinding.inflate(inflater, container, false)

        binding.recyclerViewGrupos.layoutManager = LinearLayoutManager(requireContext())

        carregarGrupos()

        binding.btnCriarGrupo.setOnClickListener {
            mostrarDialogoCriarGrupo()
        }

        return binding.root
    }

    private fun carregarGrupos() {
        db.collection("grupos")
            .get()
            .addOnSuccessListener { documents ->
                listaGrupos.clear()
                for (document in documents) {
                    val grupo = document.toObject(Grupo::class.java).copy(id = document.id)
                    listaGrupos.add(grupo)
                }
                grupoAdapter = GrupoAdapter(requireContext(), listaGrupos,
                    onEntrarClick = { grupo -> entrarNoGrupo(grupo) },
                    onGrupoClick = { grupo -> abrirTelaGrupo(grupo.id) }
                )
                binding.recyclerViewGrupos.adapter = grupoAdapter
            }
    }

    private fun abrirTelaGrupo(groupId: String) {
        val fragment = VisualizarGrupoFragment().apply {
            arguments = Bundle().apply {
                putString("groupId", groupId)
            }
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }



    private fun mostrarDialogoCriarGrupo() {
        val dialogBinding = DialogGrupoBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSalvarGrupo.setOnClickListener {
            val nomeGrupo = dialogBinding.editNomeGrupo.text.toString().trim()
            val descricaoGrupo = dialogBinding.editDescricaoGrupo.text.toString().trim()

            if (nomeGrupo.isNotEmpty() && descricaoGrupo.isNotEmpty()) {
                criarGrupo(nomeGrupo, descricaoGrupo)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }


    private fun criarGrupo(nome: String, descricao: String) {
        if (nome.isEmpty() || descricao.isEmpty()) return

        val grupo = hashMapOf(
            "nome" to nome,
            "descricao" to descricao,
            "participantes" to 0
        )

        db.collection("grupos")
            .add(grupo)
            .addOnSuccessListener {
                carregarGrupos()
            }
    }

    private fun entrarNoGrupo(grupo: Grupo) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val grupoRef = db.collection("grupos").document(grupo.id)
            val membroRef = grupoRef.collection("membros").document(userId)

            membroRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    db.collection("atividades")
                        .whereEqualTo("uid", userId)
                        .get()
                        .addOnSuccessListener { atividades ->
                            var passosAtuais = 0
                            for (atividade in atividades) {
                                passosAtuais += atividade.getLong("passos")?.toInt() ?: 0
                            }

                            val membroData = mapOf(
                                "userId" to userId,
                                "passosInicio" to passosAtuais
                            )

                            membroRef.set(membroData)
                            grupoRef.update("participantes", grupo.participantes + 1)
                                .addOnSuccessListener { carregarGrupos() }
                        }
                }
            }
        }
    }



}
