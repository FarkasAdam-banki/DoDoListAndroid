import Model.Task
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dodolist.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(private var tasks: List<Task>, private val onItemClick: (Task) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitleTextView: TextView = view.findViewById(R.id.taskTitleTextView)
        val deadlineTextView: TextView = view.findViewById(R.id.deadlineTextView)
        val subTaskTextView: TextView = view.findViewById(R.id.subTaskTextView)
        val collaboratorsTextView: TextView = view.findViewById(R.id.collaboratorsTextView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_listitem, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitleTextView.text = task.feladat_nev
        val feladatHatarido = task.feladat_hatarido

        holder.deadlineTextView.text = if (feladatHatarido == "0000-00-00 00:00:00") {
            "Határidő: -"
        } else {
            try {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = format.parse(feladatHatarido)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                "Határidő: ${dateFormat.format(date)}"
            } catch (e: ParseException) {
                "Határidő: -"
            }
        }
        holder.subTaskTextView.text = "Alfeladatok száma: ${task.subTaskCount}"
        holder.collaboratorsTextView.text = "Megosztva: ${task.collaborators}"
        holder.itemView.setOnClickListener { onItemClick(task) }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
