package com.example.umma_university

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class AddAssignmentActivity : AppCompatActivity() {

    private lateinit var repo: AssignmentRepository
    private lateinit var recycler: RecyclerView
    private var unitCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_assignment)

        unitCode = intent.getStringExtra("UNIT_CODE") ?: ""
        val tvUnit = findViewById<TextView>(R.id.tvAssignUnit)
        val etTitle = findViewById<EditText>(R.id.etAssignTitle)
        val etDesc = findViewById<EditText>(R.id.etAssignDesc)
        val btnPost = findViewById<Button>(R.id.btnPostAssignment)

        recycler = findViewById(R.id.recyclerAssignments)

        tvUnit.text = "Assignments for $unitCode"
        recycler.layoutManager = LinearLayoutManager(this)

        repo = AssignmentRepository()
        val lecturerEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        loadAssignments()

        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val a = Assignment(unitCode, title, desc, lecturerEmail)
            repo.addAssignment(a) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Assignment Posted!", Toast.LENGTH_SHORT).show()
                        etTitle.text.clear()
                        etDesc.text.clear()
                        loadAssignments()
                    } else {
                        Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun loadAssignments() {
        repo.getAssignmentsForUnit(unitCode) { list, errorMsg ->
            runOnUiThread {
                if (list != null) {
                    recycler.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                            val view = LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_assignment, parent, false)
                            return object : RecyclerView.ViewHolder(view) {}
                        }

                        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                            val a = list[position]
                            holder.itemView.findViewById<TextView>(R.id.tvAssignTitle).text = a.title
                            holder.itemView.findViewById<TextView>(R.id.tvAssignDesc).text = a.description
                        }

                        override fun getItemCount() = list.size
                    }
                } else {
                    Toast.makeText(this@AddAssignmentActivity, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}