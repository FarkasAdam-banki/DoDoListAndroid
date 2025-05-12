package com.example.dodolist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dodolist.R
import com.example.dodolist.model.Subtask
import com.example.dodolist.utils.getStatusColorClass


interface SubtaskUpdateListener {
    fun onSubtaskUpdate(subtask: Subtask)
}

class SubtaskAdapter(
    private var subtasks: MutableList<Subtask>,
    private val onDeleteClick: (Subtask) -> Unit,
    private val updateListener: SubtaskUpdateListener
) : RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subtask, parent, false)
        return SubtaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        holder.bind(subtasks[position])
    }

    override fun getItemCount(): Int = subtasks.size

    fun updateSubtasks(newSubtasks: List<Subtask>) {
        subtasks.clear()
        subtasks.addAll(newSubtasks)
        notifyDataSetChanged()
    }

    inner class SubtaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<EditText>(R.id.subtaskTitleTextView)
        private val spinner = itemView.findViewById<Spinner>(R.id.statusSpinner)
        private val desc = itemView.findViewById<EditText>(R.id.descriptionInputEditText)
        private val deleteButton = itemView.findViewById<TextView>(R.id.deleteTask)

        fun bind(subtask: Subtask) {
            title.setText(subtask.alfeladat_nev)
            desc.setText(subtask.alfeladat_leiras)

            title.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val newTitle = title.text.toString()
                    if (newTitle != subtask.alfeladat_nev) {
                        subtask.alfeladat_nev = newTitle
                        updateListener.onSubtaskUpdate(subtask)
                    }
                }
            }

            desc.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val newDesc = desc.text.toString()
                    if (newDesc != subtask.alfeladat_leiras) {
                        subtask.alfeladat_leiras = newDesc
                        updateListener.onSubtaskUpdate(subtask)
                    }
                }
            }

            val statuses = listOf("Teendő", "Folyamatban", "Kész")
            val statusIds = listOf("1", "2", "3")

            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_dropdown_item, statuses)

            spinner.adapter = adapter
            spinner.setSelection(statusIds.indexOf(subtask.allapot_id))
            spinner.setBackgroundColor(getStatusColorClass(subtask.allapot_id))

            spinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, pos: Int, id: Long) {
                    val newStatus = statusIds[pos]
                    if (newStatus != subtask.allapot_id) {
                        subtask.allapot_id = newStatus
                        updateListener.onSubtaskUpdate(subtask)
                    }
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
            })

            deleteButton.setOnClickListener {
                onDeleteClick(subtask)
            }
        }
    }

}


