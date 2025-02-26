package com.example.redesocial_exercicio.view.adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocial_exercicio.databinding.ItemFeedBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

data class Atividade(
    val nomeUsuario: String,
    val passos: Int,
    val data: String,
    val trajetoria: List<Pair<Double, Double>>
)

class FeedAdapter(private val context: Context, private val atividades: List<Atividade>) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root), OnMapReadyCallback {

        private var googleMap: GoogleMap? = null
        private var trajetoria: List<LatLng> = listOf()

        fun bind(atividade: Atividade) {
            binding.tvUserName.text = atividade.nomeUsuario
            binding.tvPassos.text = "Passos: ${atividade.passos}"
            binding.tvData.text = "Data: ${atividade.data}"

            trajetoria = atividade.trajetoria.map { LatLng(it.first, it.second) }

            binding.mapViewFeed.onCreate(Bundle())
            binding.mapViewFeed.getMapAsync(this)
            binding.mapViewFeed.onResume()
        }

        override fun onMapReady(map: GoogleMap) {
            googleMap = map
            googleMap?.uiSettings?.setAllGesturesEnabled(false)
            desenharTrajeto()
        }

        private fun desenharTrajeto() {
            googleMap?.let { map ->
                if (trajetoria.isNotEmpty()) {
                    val polylineOptions = PolylineOptions()
                        .width(8f)
                        .color(Color.BLUE)
                        .addAll(trajetoria)

                    map.addPolyline(polylineOptions)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(trajetoria[0], 15f))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFeedBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(atividades[position])
    }

    override fun getItemCount(): Int = atividades.size

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.mapViewFeed.onPause()
        holder.binding.mapViewFeed.onDestroy()
    }
}
