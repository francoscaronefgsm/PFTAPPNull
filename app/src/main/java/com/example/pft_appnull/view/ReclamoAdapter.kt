package com.example.pft_appnull.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pft_appnull.R
import com.example.pft_appnull.model.Reclamo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ReclamoAdapter(private val reclamos: List<Reclamo>, private val onReclamoClicked: (Reclamo) -> Unit) : RecyclerView.Adapter<ReclamoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tituloTextView: TextView = view.findViewById(R.id.tituloTextView)
        val tipoTextView: TextView = view.findViewById(R.id.tipoTextView)
        val fechaTextView: TextView = view.findViewById(R.id.fechaReclamoTextView)

        // ... otros elementos de la vista
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reclamo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reclamo = reclamos[position]
        holder.tituloTextView.text = reclamo.title
        if(reclamo.activityApe.isNotEmpty()){
            holder.tipoTextView.text = reclamo.activityApe
        }else{
            holder.tipoTextView.text = reclamo.eventVme
        }
        holder.fechaTextView.text = reclamo.date

        holder.itemView.setOnClickListener {
            onReclamoClicked(reclamo)
        }
    }

    override fun getItemCount() = reclamos.size


    fun formatIsoDate(dateStr: String): String {
        val isoFormatterWithMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        isoFormatterWithMillis.timeZone = TimeZone.getTimeZone("UTC")

        val isoFormatterWithoutMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        isoFormatterWithoutMillis.timeZone = TimeZone.getTimeZone("UTC")

        try {
            // Primero intenta con el formato que incluye milisegundos
            val date = isoFormatterWithMillis.parse(dateStr)
            val friendlyFormatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            return friendlyFormatter.format(date)
        } catch (e: ParseException) {
            // Si falla, intenta con el formato sin milisegundos
            val date = isoFormatterWithoutMillis.parse(dateStr)
            val friendlyFormatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            return friendlyFormatter.format(date)
        }
    }



}

