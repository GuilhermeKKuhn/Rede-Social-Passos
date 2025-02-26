package com.example.redesocial_exercicio.view.TelaPrincipal

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.redesocial_exercicio.databinding.ActivityFormUsernameBinding
import com.example.redesocial_exercicio.view.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class FormUsername : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    private lateinit var binding: ActivityFormUsernameBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSalvarNome.setOnClickListener{ view ->
            salvarUsername(view)
        }
    }

    private fun salvarUsername(view: View){
        val userId = auth.currentUser!!.uid
        val nomeUsuario = binding.edtNomeUsuario.text.toString()

        if (nomeUsuario.isEmpty()) {
            val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
            return
        }

        val userUpdate = hashMapOf(
            "uid" to userId,
            "name" to nomeUsuario,
            "steps" to 0
        )

        db.collection("users").document(userId)
            .set(userUpdate, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                val snackbar = Snackbar.make(view, "Usuário salvo!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.GREEN)
                snackbar.show()
                Navigation.direcionarTelaPrincipal(this)
                finish()
            }
            .addOnFailureListener {
                val snackbar = Snackbar.make(view, "Erro ao salvar usuário!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }
    }
}