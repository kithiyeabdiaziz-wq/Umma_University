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
import com.google.firebase.firestore.FirebaseFirestore

class AdminAdmissionsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recycler: RecyclerView
    private val appList = mutableListOf<Pair<String, AdmissionApplication>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_admissions)

        recycler = findViewById(R.id.recyclerAdmissions)
        recycler.layoutManager = LinearLayoutManager(this)

        loadApplications()
    }

    private fun loadApplications() {
        db.collection("admissions").get().addOnSuccessListener { snap ->
            appList.clear()
            for (doc in snap) {
                val app = doc.toObject(AdmissionApplication::class.java)
                if (app != null) appList.add(doc.id to app)
            }
            recycler.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                    val v = LayoutInflater.from(parent.context).inflate(R.layout.item_admission_admin, parent, false)
                    return object : RecyclerView.ViewHolder(v) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    val (docId, app) = appList[position]
                    holder.itemView.findViewById<TextView>(R.id.tvAdmName).text = app.fullName

                    // FIXED: Use string resources instead of concatenation to clear warnings
                    holder.itemView.findViewById<TextView>(R.id.tvAdmCourse).text =
                        getString(R.string.course_format, app.course)
                    holder.itemView.findViewById<TextView>(R.id.tvAdmStatus).text =
                        getString(R.string.status_format, app.status)

                    holder.itemView.findViewById<Button>(R.id.btnApprove).setOnClickListener {
                        updateStatus(docId, "Approved")
                    }
                    holder.itemView.findViewById<Button>(R.id.btnReject).setOnClickListener {
                        updateStatus(docId, "Rejected")
                    }
                }
                override fun getItemCount() = appList.size
            }
        }
    }

    private fun updateStatus(docId: String, status: String) {
        db.collection("admissions").document(docId).update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Application $status", Toast.LENGTH_SHORT).show()
                loadApplications()
            }
    }
}