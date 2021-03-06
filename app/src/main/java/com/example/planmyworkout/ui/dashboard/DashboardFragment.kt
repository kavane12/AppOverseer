package com.example.planmyworkout.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.os.health.TimerStat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planmyworkout.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.playlist_row.view.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        //val intent = Intent(root.context,DashboardActivity::class.java)
        //startActivity(intent)

        val recyclerView: RecyclerView = root.findViewById(R.id.dashboard_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        val da = DashboardAdapter(root.context)
        var sessions: List<DocumentSnapshot> = listOf()
        db.collection("Workout Sessions")
            .whereEqualTo("user_email", FirebaseAuth.getInstance().currentUser?.email.toString())
            .get().addOnSuccessListener {
                sessions = it.documents.toList()
                da.setSessions(sessions)
                recyclerView.adapter = da
            }

        return root
    }

    private class DashboardAdapter(context: Context): RecyclerView.Adapter<DashboardViewHolder>(){

        private val mContext = context
        private var sessions: List<DocumentSnapshot> = listOf()

        fun setSessions(s:List<DocumentSnapshot>){
            sessions = s
        }

        override fun getItemCount(): Int {
            return sessions.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.playlist_row, parent, false)
            return DashboardViewHolder(row)
        }

        override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
            //holder.itemView.exercise_name_textview.text = "HELP"

            val dateFormatter = SimpleDateFormat("MM/dd/YYYY - H:mm")

            val now = Date()
            val logDate = (sessions[position].get("date") as Timestamp).toDate()

            holder.itemView.exercise_name_textview.text = dateFormatter.format(logDate)


            val daysAgo = daysBetween(logDate, now)
            holder.itemView.exercise_sublabel_textview.text = "${daysAgo-1} days ago"
        }

        private fun daysBetween(startDate: java.util.Date?, endDate: java.util.Date?): Int {
            val sDate: Calendar = getDatePart(startDate)
            val eDate: Calendar = getDatePart(endDate)

            var daysBetween = 0
            while (sDate.before(eDate)) {
                sDate.add(Calendar.DAY_OF_MONTH, 1)
                daysBetween++
            }

            return daysBetween
        }

        private fun getDatePart(date: java.util.Date?): Calendar {
            val cal: Calendar = Calendar.getInstance()
            cal.setTime(date)
            cal[Calendar.HOUR_OF_DAY] = 0 // set hour to midnight
            cal[Calendar.MINUTE] = 0 // set minute in hour
            cal[Calendar.SECOND] = 0 // set second in minute
            cal[Calendar.MILLISECOND] = 0 // set millis in second

            val zeroedDate = cal.time // actually computes the new Date
            return cal

        }
    }

    private class DashboardViewHolder(v: View): RecyclerView.ViewHolder(v){

    }
}
