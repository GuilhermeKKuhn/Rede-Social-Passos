package com.example.redesocial_exercicio.view.navigation

import android.content.Context
import android.content.Intent
import com.example.redesocial_exercicio.view.formCadastro.FormCadastro
import com.example.redesocial_exercicio.view.formLogin.FormLogin
import com.example.redesocial_exercicio.view.TelaPrincipal.FormUsername
import com.example.redesocial_exercicio.view.TelaPrincipal.TelaPrincipal

object Navigation {


    fun direcionarTelaPrincipal(context: Context){
        val intent = Intent(context, TelaPrincipal::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    fun direcionarTelaLogin(context: Context){
        val intent = Intent(context, FormLogin::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    fun direcionarTelaCadastro(context: Context){
        val intent = Intent(context, FormCadastro::class.java)
        context.startActivity(intent)
    }

    fun direcionarTelaUsuario(context: Context){
        val intent = Intent(context, FormUsername::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

}