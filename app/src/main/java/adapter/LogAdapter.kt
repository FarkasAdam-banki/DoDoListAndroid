package com.example.dodolist.ui.adapters

import Model.LogEntry
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dodolist.R
import com.example.dodolist.utils.getStatusColorClass
import com.example.dodolist.utils.getStatusLabel

class LogAdapter(private var logs: List<LogEntry>) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]
        holder.bind(log)
    }

    override fun getItemCount(): Int = logs.size

    fun updateLogs(newLogs: List<LogEntry>) {
        logs = newLogs
        notifyDataSetChanged()
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val logMessage: TextView = itemView.findViewById(R.id.logMessage)
        private val logTimestamp: TextView = itemView.findViewById(R.id.logTimestamp)
        private val colorCircle: ImageView = itemView.findViewById(R.id.colorCircle)
        private val statusBadge: TextView = itemView.findViewById(R.id.statusBadge)
        private val logText : TextView = itemView.findViewById(R.id.logText)
        private val newValue : TextView = itemView.findViewById(R.id.newValue)

        fun bind(log: LogEntry) {
            logMessage.text = when (log.mezo_nev) {
                "feladat_hatarido" -> "Határidő módosítva"
                "feladat_szin" -> "Szín módosítva"
                "feladat_nev" -> "Feladat név módosítva"
                "feladat_leiras" -> "Feladat leírása módosítva"
                "allapot_id" -> "Állapot módosítva"
                else -> "${log.mezo_nev} módosítva"
            }

            logTimestamp.text = log.modositas_datuma
            if (log.mezo_nev == "feladat_szin") {
                colorCircle.visibility = View.VISIBLE
                colorCircle.setBackgroundColor(android.graphics.Color.parseColor(log.uj_ertek))
                newValue.text = "Új szín:"
                statusBadge.visibility = View.GONE
                logText.visibility = View.GONE
            }else if(log.mezo_nev == "allapot_id"){
                statusBadge.visibility = View.VISIBLE
                statusBadge.text = getStatusLabel(log.uj_ertek)
                newValue.text = "új állapot:"
                statusBadge.setBackgroundColor(getStatusColorClass(log.uj_ertek))
                colorCircle.visibility = View.GONE
                logText.visibility = View.GONE
            }else{
                logText.visibility = View.VISIBLE
                logText.text = log.uj_ertek
                newValue.text = "Új érték:"
                colorCircle.visibility = View.GONE
                statusBadge.visibility = View.GONE
            }
        }
    }
}
