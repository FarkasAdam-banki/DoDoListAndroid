package adapter

import Model.TaskDetails
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dodolist.ui.fragments.LogFragment
import com.example.dodolist.ui.fragments.ShareFragment
import com.example.dodolist.ui.fragments.SubTasksFragment
import com.example.dodolist.ui.fragments.TaskDescriptionFragment
import com.example.dodolist.ui.theme.TaskSettingsFragment

class TaskPagerAdapter(activity: FragmentActivity, private val taskDetails: TaskDetails?) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TaskSettingsFragment().apply {
                val bundle = Bundle().apply {
                    putParcelable("TASK_DETAILS", taskDetails)
                }
                arguments = bundle
            }
            1 -> TaskDescriptionFragment().apply {
                val bundle = Bundle().apply {
                    putString("TASK_DESCRIPTION", taskDetails?.feladat_leiras)
                    putInt("TASK_ID", taskDetails?.feladat_id ?: -1)
                }
                arguments = bundle
            }
            2 -> SubTasksFragment()
            3 -> ShareFragment().apply {
                val bundle = Bundle().apply {
                    putInt("TASK_ID", taskDetails?.feladat_id ?: -1)
                }
                arguments = bundle
            }
            4 -> LogFragment().apply {
                val bundle = Bundle().apply {
                    putInt("TASK_ID", taskDetails?.feladat_id ?: -1)
                }
                arguments = bundle
            }
            else -> TaskSettingsFragment()
        }
    }
}