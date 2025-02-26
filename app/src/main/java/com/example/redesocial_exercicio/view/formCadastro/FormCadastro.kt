package com.example.redesocial_exercicio.view.formCadastro

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.redesocial_exercicio.databinding.ActivityFormCadastroBinding
import com.example.redesocial_exercicio.view.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class FormCadastro : AppCompatActivity() {

    private lateinit var binding: ActivityFormCadastroBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btCadastrar.setOnClickListener { view ->

            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmaSenha = binding.editComfirmSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()){

                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()

            }else if (!(senha.equals(confirmaSenha))) {

                val snackbar = Snackbar.make(view, "As senhas não coincidem!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()

            }else{

                auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener { cadastro ->
                    if (cadastro.isSuccessful){

                        val snackbar = Snackbar.make(view, "Usuario Cadastrado com Sucesso!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.GREEN)
                        snackbar.show()
                        binding.editEmail.setText("")
                        binding.editSenha.setText("")
                        binding.editComfirmSenha.setText("")
                        Navigation.direcionarTelaLogin(this)

                    }
                }.addOnFailureListener { erro ->

                    val mensagemErro = when(erro){
                        is FirebaseAuthWeakPasswordException -> "A senha deve ter pelo menos 6 caracteres!"
                        is FirebaseAuthInvalidCredentialsException -> "Digite um email valido!"
                        is FirebaseAuthUserCollisionException -> "Este email já foi cadastrado!"
                        is FirebaseNetworkException -> "Sem conexão com a internet!"
                        else -> "Erro ao cadastrar usuario"
                    }

                    val snackbar = Snackbar.make(view, mensagemErro, Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
        }

    }
}