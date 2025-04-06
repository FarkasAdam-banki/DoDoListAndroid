import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import Model.Invitation
import com.example.dodolist.R

class InvitationAdapter(
    private var invitations: List<Invitation>,
    private val onAccept: (Invitation) -> Unit,
    private val onReject: (Invitation) -> Unit
) : RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_invitation, parent, false)
        return InvitationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        val invitation = invitations[position]
        holder.bind(invitation)
    }

    override fun getItemCount(): Int = invitations.size

    fun updateInvites(newInvites: List<Invitation>) {
        this.invitations = newInvites
        notifyDataSetChanged()
    }

    inner class InvitationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.task_name)
        private val acceptButton: Button = itemView.findViewById(R.id.accept_button)
        private val rejectButton: Button = itemView.findViewById(R.id.reject_button)

        fun bind(invitation: Invitation) {
            taskName.text = "Meghívást kaptál ehhez a feladathoz: ${invitation.feladat_nev}"

            acceptButton.setOnClickListener { onAccept(invitation) }
            rejectButton.setOnClickListener { onReject(invitation) }
        }
    }
}
