package adapter

import Model.UserItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dodolist.R

class ShareAdapter(
    private val users: List<UserItem>,
    private val onDeleteClick: (UserItem) -> Unit
) : RecyclerView.Adapter<ShareAdapter.ShareViewHolder>() {

    inner class ShareViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.userName)
        val email: TextView = view.findViewById(R.id.user_email)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_share, parent, false)
        return ShareViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShareViewHolder, position: Int) {

        val user = users[position]
        holder.name.text = user.felhasznalo_nev
        holder.email.text = user.felhasznalo_email

        holder.deleteButton.setOnClickListener {
            onDeleteClick(user)
        }
    }

    override fun getItemCount(): Int = users.size
}
