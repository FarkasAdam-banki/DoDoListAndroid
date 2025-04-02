package adapter

import android.content.Context
import android.widget.ArrayAdapter

class StatusAdapter(context: Context) : ArrayAdapter<String>(
    context,
    android.R.layout.simple_spinner_dropdown_item,
    listOf("Teendő", "Folyamatban", "Kész")
)
