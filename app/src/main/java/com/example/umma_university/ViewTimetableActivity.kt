package com.example.umma_university

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class ViewTimetableActivity : AppCompatActivity() {

    private lateinit var recyclerTimetable: RecyclerView
    private lateinit var scrollExamCard: ScrollView
    private lateinit var tabLayout: TabLayout
    private val timetableRepository = TimetableRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_timetable)

        recyclerTimetable = findViewById(R.id.recyclerTimetable)
        scrollExamCard = findViewById(R.id.scrollExamCard)
        tabLayout = findViewById(R.id.tabLayout)

        recyclerTimetable.layoutManager = LinearLayoutManager(this)

        // Load Lectures by default
        loadLectures()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { // Lectures
                        recyclerTimetable.visibility = View.VISIBLE
                        scrollExamCard.visibility = View.GONE
                        loadLectures()
                    }
                    1 -> { // Exams
                        recyclerTimetable.visibility = View.VISIBLE
                        scrollExamCard.visibility = View.GONE
                        loadExams()
                    }
                    2 -> { // Exam Card
                        recyclerTimetable.visibility = View.GONE
                        scrollExamCard.visibility = View.VISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadLectures() {
        timetableRepository.getAllTimetableEntries { entries, errorMsg ->
            runOnUiThread {
                if (entries != null) {
                    val items = entries.map {
                        ScheduleItem(it.unitCode, "LECTURE", it.day, it.time, it.venue)
                    }
                    recyclerTimetable.adapter = ScheduleAdapter(items)
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadExams() {
        timetableRepository.getAllExams { exams, errorMsg ->
            runOnUiThread {
                if (exams != null) {
                    if (exams.isEmpty()) {
                        Toast.makeText(this, "No exams scheduled yet.", Toast.LENGTH_SHORT).show()
                    }
                    recyclerTimetable.adapter = ScheduleAdapter(exams)
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    inner class ScheduleAdapter(private val list: List<ScheduleItem>) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvUnit: TextView = itemView.findViewById(R.id.tvTtUnit)
            val tvDayTime: TextView = itemView.findViewById(R.id.tvTtDayTime)
            val tvVenue: TextView = itemView.findViewById(R.id.tvTtVenue)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timetable, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]

            // If it's an exam, add a red [EXAM] tag
            if (item.type == "EXAM") {
                holder.tvUnit.text = "[EXAM] ${item.unitCode}"
                holder.tvUnit.setTextColor(android.graphics.Color.RED)
            } else {
                holder.tvUnit.text = item.unitCode
                holder.tvUnit.setTextColor(android.graphics.Color.BLACK)
            }

            holder.tvDayTime.text = "${item.dayDate} | ${item.time}"
            holder.tvVenue.text = "Venue: ${item.venue}"
        }

        override fun getItemCount(): Int = list.size
    }
}