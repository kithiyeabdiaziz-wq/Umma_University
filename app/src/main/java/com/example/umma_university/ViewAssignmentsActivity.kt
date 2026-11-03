package com.example.umma_university

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewAssignmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_assignments)

        val recycler = findViewById<RecyclerView>(R.id.recyclerStudentAssignments)
        recycler.layoutManager = LinearLayoutManager(this)

        val assignmentRepository = AssignmentRepository()

        assignmentRepository.getAllAssignments { list, errorMsg ->
            runOnUiThread {
                if (list != null) {
                    if (list.isEmpty()) {
                        Toast.makeText(this, "No assignments posted yet.", Toast.LENGTH_LONG).show()
                    }
                    recycler.adapter = AssignmentAdapter(list)
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Reusing the item_assignment.xml layout we created earlier
    inner class AssignmentAdapter(private val list: List<Assignment>) : RecyclerView.Adapter<AssignmentAdapter.ViewHolder>() {

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvTitle: TextView = v.findViewById(R.id.tvAssignTitle)
            val tvDesc: TextView = v.findViewById(R.id.tvAssignDesc)
            val tvUnitCode: TextView = v.findViewById(R.id.tvAssignTitle) // We will show the unit code in the title
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_assignment, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val a = list[position]
            // Combine title and unit code for display
            holder.tvTitle.text = "[${a.unitCode}] ${a.title}"
            holder.tvDesc.text = a.description
        }

        override fun getItemCount() = list.size
    }
}