package com.example.redesocial_exercicio.view.formLogin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import com.example.redesocial_exercicio.databinding.ActivityFormLoginBinding
import com.example.redesocial_exercicio.view.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore


class FormLogin : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityFormLoginBinding
    private val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.redesocial_exercicio.R.string.client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.textCadastro.setOnClickListener{
            Navigation.direcionarTelaCadastro(this)
        }

        binding.btLoginGoogle.setOnClickListener{
            signInWithGoogle()
        }

        binding.btEntrar.setOnClickListener{view ->

            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()){

                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()

            }else{

                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { login ->
                    if (login.isSuccessful) {
                        verificarUsuario()
                    }
                }.addOnFailureListener { erro ->
                    val mensagemErro = when(erro){
                        is FirebaseAuthInvalidCredentialsException -> "Credenciais Invalidas!"
                        is FirebaseAuthUserCollisionException -> "Este email já foi cadastrado!"
                        is FirebaseNetworkException -> "Sem conexão com a internet!"
                        else -> "Erro ao fazer login!"
                    }

                    val snackbar = Snackbar.make(view, mensagemErro, Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }

            }
        }
    }

    private fun verificarUsuario() {
        val userId = auth.currentUser!!.uid

        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists() && document.contains("name")) {
                        Navigation.direcionarTelaPrincipal(this)
                        finish()
                    } else {
                        Navigation.direcionarTelaUsuario(this)
                        finish()
                    }
                }
                .addOnFailureListener {
                    auth.signOut()
                    val snackbar = Snackbar.make(binding.root, "Erro ao buscar usuário!", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 9001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                   verificarUsuario()
                } else {
                    Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                }
            }
    }



}