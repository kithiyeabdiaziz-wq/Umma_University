package com.example.umma_university

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HelpDeskActivity : AppCompatActivity() {

    private lateinit var complaintRepository: ComplaintRepository
    private lateinit var adapter: ComplaintAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_desk)

        complaintRepository = ComplaintRepository()
        val etComplaint = findViewById<EditText>(R.id.etComplaint)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitComplaint)
        val recycler = findViewById<RecyclerView>(R.id.recyclerComplaints)
        val spinnerDept = findViewById<Spinner>(R.id.spinnerDepartment)

        // UNIVERSITY DEPARTMENTS LIST
        val departments = listOf(
            "Registry",
            "Finance",
            "ICT",
            "Cleaning & Sanitation",
            "Welfare & Counselling",
            "Library",
            "Hostels & Accommodation",
            "Security",
            "Sports & Recreation",
            "Academic Affairs"
        )

        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, departments)
        spinnerDept.adapter = adapterSpinner

        recycler.layoutManager = LinearLayoutManager(this)
        loadComplaints(recycler)

        btnSubmit.setOnClickListener {
            val msg = etComplaint.text.toString().trim()
            val selectedDept = spinnerDept.selectedItem.toString()

            if (msg.isEmpty()) {
                Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass the selected department to the repository
            complaintRepository.submitComplaint(msg, selectedDept) { success, error ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Request submitted to $selectedDept!", Toast.LENGTH_SHORT).show()
                        etComplaint.text.clear()
                        loadComplaints(recycler)
                    } else {
                        Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadComplaints(recycler: RecyclerView) {
        complaintRepository.getMyComplaints { list, error ->
            runOnUiThread {
                if (list != null) {
                    adapter = ComplaintAdapter(list)
                    recycler.adapter = adapter
                }
            }
        }
    }

    inner class ComplaintAdapter(private val list: List<Complaint>) : RecyclerView.Adapter<ComplaintAdapter.ViewHolder>() {
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val msg: TextView = v.findViewById(R.id.tvComplaintMsg)
            val status: TextView = v.findViewById(R.id.tvComplaintStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_complaint, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val c = list[position]
            holder.msg.text = c.message

            // Show both the department and the status
            holder.status.text = "Dept: ${c.department} | Status: ${c.status}"

            if (c.status == "Resolved") {
                holder.status.setTextColor(android.graphics.Color.parseColor("#006400")) // Green
            } else if (c.status == "Rejected") {
                holder.status.setTextColor(android.graphics.Color.parseColor("#FF0000")) // Red
            } else {
                holder.status.setTextColor(android.graphics.Color.parseColor("#FF9800")) // Orange
            }
        }

        override fun getItemCount() = list.size
    }
}