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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SupportTicketsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recycler: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: SupportTicketAdapter
    private var department: String = ""
    private val items = mutableListOf<Pair<String, Complaint>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_tickets)

        department = intent.getStringExtra("department") ?: ""
        if (department.isEmpty()) {
            Toast.makeText(this, "No department assigned", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        db = FirebaseFirestore.getInstance()

        recycler = findViewById(R.id.recyclerSupportTickets)
        tvEmpty = findViewById(R.id.tvEmptyTickets)

        adapter = SupportTicketAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        loadTickets()
    }

    private fun loadTickets() {
        db.collection("complaints")
            .whereEqualTo("department", department)
            .get()
            .addOnSuccessListener { snap ->
                items.clear()
                for (doc in snap) {
                    val c = doc.toObject(Complaint::class.java)
                    if (c != null) items.add(doc.id to c)
                }

                items.sortBy { it.second.status != "Pending" }
                adapter.notifyDataSetChanged()

                tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "Load error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateStatus(docId: String, newStatus: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        // Simplified patch to prevent crashes
        val patch = mapOf(
            "status" to newStatus,
            "resolvedByRole" to "support",
            "resolvedByUid" to uid,
            "resolvedByDept" to department
        )

        db.collection("complaints").document(docId).update(patch)
            .addOnSuccessListener {
                Toast.makeText(this, "Marked $newStatus", Toast.LENGTH_SHORT).show()
                loadTickets()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    inner class SupportTicketAdapter : RecyclerView.Adapter<SupportTicketAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val reg: TextView = v.findViewById(R.id.tvSupReg)
            val msg: TextView = v.findViewById(R.id.tvSupMsg)
            val status: TextView = v.findViewById(R.id.tvSupStatus)
            val btnApprove: Button = v.findViewById(R.id.btnSupApprove)
            val btnReject: Button = v.findViewById(R.id.btnSupReject)
            val btnResolve: Button = v.findViewById(R.id.btnSupResolve)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_support_ticket, parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) {
            val (docId, c) = items[position]

            holder.reg.text = "Student: ${c.studentRegNumber}"
            holder.msg.text = c.message
            holder.status.text = "Status: ${c.status}"

            val pending = c.status.equals("Pending", ignoreCase = true)

            when (c.status) {
                "Resolved" -> holder.status.setTextColor(0xFF4CAF50.toInt())
                "Rejected" -> holder.status.setTextColor(0xFFF44336.toInt())
                else -> holder.status.setTextColor(0xFFFF9800.toInt())
            }

            holder.btnApprove.visibility = if (pending) View.VISIBLE else View.GONE
            holder.btnReject.visibility = if (pending) View.VISIBLE else View.GONE
            holder.btnResolve.visibility = if (pending) View.VISIBLE else View.GONE

            holder.btnApprove.setOnClickListener { updateStatus(docId, "Approved") }
            holder.btnReject.setOnClickListener { updateStatus(docId, "Rejected") }
            holder.btnResolve.setOnClickListener { updateStatus(docId, "Resolved") }
        }

        override fun getItemCount() = items.size
    }
}