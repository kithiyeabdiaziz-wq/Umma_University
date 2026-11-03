package com.example.umma_university

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminHelpDeskActivity : AppCompatActivity() {

    private lateinit var complaintRepository: ComplaintRepository
    private lateinit var adapter: AdminComplaintAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_help_desk)

        complaintRepository = ComplaintRepository()
        val recycler = findViewById<RecyclerView>(R.id.recyclerAdminComplaints)
        recycler.layoutManager = LinearLayoutManager(this)

        loadComplaints(recycler)
    }

    private fun loadComplaints(recycler: RecyclerView) {
        complaintRepository.getAllComplaints { list, errorMsg ->
            runOnUiThread {
                if (list != null) {
                    adapter = AdminComplaintAdapter(list)
                    recycler.adapter = adapter
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    inner class AdminComplaintAdapter(
        private val list: List<Pair<String, Complaint>>
    ) : RecyclerView.Adapter<AdminComplaintAdapter.ViewHolder>() {

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val reg: TextView = v.findViewById(R.id.tvAdminCompReg)
            val msg: TextView = v.findViewById(R.id.tvAdminCompMsg)
            val status: TextView = v.findViewById(R.id.tvAdminCompStatus)
            val btnResolve: Button = v.findViewById(R.id.btnResolveComplaint)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_admin_complaint, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (docId, complaint) = list[position]

            holder.reg.text = "Student: ${complaint.studentRegNumber}"
            holder.msg.text = complaint.message
            holder.status.text = "Status: ${complaint.status}"

            if (complaint.status == "Resolved") {
                holder.status.setTextColor(android.graphics.Color.parseColor("#006400")) // green
                holder.btnResolve.visibility = View.GONE
            } else {
                holder.status.setTextColor(android.graphics.Color.parseColor("#FF9800")) // orange
                holder.btnResolve.visibility = View.VISIBLE
            }

            holder.btnResolve.setOnClickListener {
                complaintRepository.resolveComplaint(docId) { success, _ ->
                    if (success) {
                        Toast.makeText(
                            this@AdminHelpDeskActivity,
                            "Marked as Resolved!",
                            Toast.LENGTH_SHORT
                        ).show()
                        recreate()
                    }
                }
            }
        }

        override fun getItemCount() = list.size
    }
}