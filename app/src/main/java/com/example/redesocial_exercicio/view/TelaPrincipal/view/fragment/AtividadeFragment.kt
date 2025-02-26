package com.example.redesocial_exercicio.view.TelaPrincipal.view.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.redesocial_exercicio.databinding.FragmentAtividadeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AtividadeFragment : Fragment(), SensorEventListener {

    val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private var sensorManager: SensorManager? = null
    private var _binding: FragmentAtividadeBinding? = null
    private val binding get() = _binding!!

    private var atividadeIniciada = false
    private var primeiroPasso = true
    private var totalPassos = 0f
    private var totalPassosAnterior = 0f

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val trajetoria = mutableListOf<Pair<Double, Double>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAtividadeBinding.inflate(inflater, container, false)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        binding.tvStepCount.text = "0"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1)
            }
        }

        binding.btnStartActivity.setOnClickListener {view ->
           iniciarAtividade(view)
        }

        binding.btnStopActivity.setOnClickListener { view ->
            finalizarAtividade(view)
        }

        return binding.root
    }

    private fun iniciarAtividade(view: View) {
        atividadeIniciada = true
        totalPassosAnterior = totalPassos
        trajetoria.clear()
        binding.tvStepCount.text = "0"

        binding.btnStartActivity.visibility = View.GONE
        binding.btnStopActivity.visibility = View.VISIBLE

        val snackbar = Snackbar.make(view, "Atividade Iniciada!", Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.BLUE)
        snackbar.show()

        capturarLocalizacao()
    }

    private fun finalizarAtividade(view: View) {
        atividadeIniciada = false

        val passosFinal = (totalPassos - totalPassosAnterior).toInt()
        salvarNoFirestore(passosFinal, view, trajetoria)

        binding.btnStartActivity.visibility = View.VISIBLE
        binding.btnStopActivity.visibility = View.GONE
        binding.tvStepCount.text = "0"
        val snackbar = Snackbar.make(view, "Atividade Finalizada!", Snackbar.LENGTH_SHORT)
        snackbar.setTextColor(Color.BLACK)
        snackbar.setBackgroundTint(Color.GREEN)
        snackbar.show()
    }

    private fun capturarLocalizacao() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (atividadeIniciada) {
                    for (location in locationResult.locations) {
                        val novoPonto = Pair(location.latitude, location.longitude)

                        if (trajetoria.isEmpty() || trajetoria.last() != novoPonto) {
                            trajetoria.add(novoPonto)
                        }
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }



    private fun salvarNoFirestore(passos: Int, view: View, trajetoria: List<Pair<Double, Double>>) {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val nomeUsuario = document.getString("name") ?: "Usuário Desconhecido"

                        val trajetoriaMap = trajetoria.map { ponto ->
                            mapOf("lat" to ponto.first, "lng" to ponto.second)
                        }

                        val dados = hashMapOf(
                            "uid" to user.uid,
                            "nomeUsuario" to nomeUsuario,
                            "passos" to passos,
                            "trajetoria" to trajetoriaMap, // Agora no formato correto
                            "timestamp" to System.currentTimeMillis()
                        )

                        db.collection("atividades")
                            .add(dados)
                            .addOnSuccessListener {
                                val snackbar = Snackbar.make(view, "Atividade Salva!", Snackbar.LENGTH_SHORT)
                                snackbar.setTextColor(Color.BLACK)
                                snackbar.setBackgroundTint(Color.GREEN)
                                snackbar.show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Erro ao salvar!", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(requireContext(), "Usuário não encontrado no Firestore!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Erro ao buscar usuário!", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onResume() {
        super.onResume()
        val sensorPassos: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (sensorPassos == null) {
            Toast.makeText(requireContext(), "Sensor não detectado", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, sensorPassos, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (!atividadeIniciada || event == null) return

        totalPassos = event.values[0]

        if (primeiroPasso) {
            totalPassosAnterior = totalPassos
            primeiroPasso = false
        }

        val passosAtuais = (totalPassos - totalPassosAnterior).toInt()

        binding.tvStepCount.text = passosAtuais.toString()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}
