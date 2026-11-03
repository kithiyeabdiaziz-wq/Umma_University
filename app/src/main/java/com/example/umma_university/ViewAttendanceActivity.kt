package com.example.umma_university

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewAttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_attendance)

        val recycler = findViewById<RecyclerView>(R.id.recyclerAttendance)
        val tvSummary = findViewById<TextView>(R.id.tvAttendanceSummary)
        recycler.layoutManager = LinearLayoutManager(this)

        val repo = AttendanceRepository()

        repo.getMyAttendance { list, errorMsg ->
            runOnUiThread {
                if (list != null) {
                    if (list.isEmpty()) {
                        Toast.makeText(this, "No attendance records found yet.", Toast.LENGTH_LONG).show()
                        tvSummary.text = "Total Classes Attended: 0"
                    } else {
                        // Calculate total
                        tvSummary.text = "Total Classes Attended: ${list.size}"

                        recycler.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendance_record, parent, false)
                                return object : RecyclerView.ViewHolder(view) {}
                            }

                            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                                val record = list[position]
                                holder.itemView.findViewById<TextView>(R.id.tvAttUnit).text = record.unitCode
                                holder.itemView.findViewById<TextView>(R.id.tvAttDate).text = "Date: ${record.date}"

                                val tvStatus = holder.itemView.findViewById<TextView>(R.id.tvAttStatus)
                                tvStatus.text = record.status
                                if (record.status == "Present") {
                                    tvStatus.setTextColor(Color.parseColor("#4CAF50")) // Green
                                } else {
                                    tvStatus.setTextColor(Color.parseColor("#F44336")) // Red
                                }
                            }
                            override fun getItemCount() = list.size
                        }
                    }
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}