package com.example.redesocial_exercicio.view.TelaPrincipal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.redesocial_exercicio.R
import com.example.redesocial_exercicio.databinding.ActivityTelaPrincipalBinding
import com.example.redesocial_exercicio.view.TelaPrincipal.view.fragment.AtividadeFragment
import com.example.redesocial_exercicio.view.TelaPrincipal.view.fragment.FeedFragment
import com.example.redesocial_exercicio.view.TelaPrincipal.view.fragment.GrupoFragment
import com.example.redesocial_exercicio.view.TelaPrincipal.view.fragment.PerfilFragment

class TelaPrincipal : AppCompatActivity() {

    private lateinit var binding: ActivityTelaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(FeedFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> replaceFragment(FeedFragment())
                R.id.nav_groups -> replaceFragment(GrupoFragment())
                R.id.nav_atividade -> replaceFragment(AtividadeFragment())
                R.id.nav_profile -> replaceFragment(PerfilFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}