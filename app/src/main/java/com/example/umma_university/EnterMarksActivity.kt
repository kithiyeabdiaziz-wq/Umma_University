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
import com.google.firebase.firestore.FirebaseFirestore

class EnterMarksActivity : AppCompatActivity() {

    private var unitCode: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_marks)

        // Get the unit code passed from the dashboard
        unitCode = intent.getStringExtra("UNIT_CODE") ?: ""

        val tvMarksTitle = findViewById<TextView>(R.id.tvMarksTitle)
        val recyclerStudents = findViewById<RecyclerView>(R.id.recyclerStudents)

        tvMarksTitle.text = "Enter Marks for $unitCode"
        recyclerStudents.layoutManager = LinearLayoutManager(this)

        // Load all students directly from Firebase
        db.collection("users").whereEqualTo("role", "student").get()
            .addOnSuccessListener { result ->
                val studentList = mutableListOf<User>()
                for (doc in result) {
                    val student = doc.toObject(User::class.java)
                    if (student != null) studentList.add(student)
                }
                recyclerStudents.adapter = StudentAdapter(studentList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    inner class StudentAdapter(private val studentList: List<User>) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

        inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView = itemView.findViewById(R.id.tvStudentName)
            val etMark: EditText = itemView.findViewById(R.id.etStudentMark)
            val btnSave: Button = itemView.findViewById(R.id.btnSaveMark)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_mark, parent, false)
            return StudentViewHolder(view)
        }

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
            val student = studentList[position]
            holder.tvName.text = "${student.name}\n${student.regNumber}"

            holder.btnSave.setOnClickListener {
                val mark = holder.etMark.text.toString().trim()
                if (mark.isEmpty()) {
                    Toast.makeText(this@EnterMarksActivity, "Enter a mark first", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // FIX: Replace slashes with dashes so Firestore doesn't create folders
                val safeRegNumber = student.regNumber.replace("/", "-")
                val docId = "${safeRegNumber}_${unitCode}"

                val resultData = hashMapOf(
                    "regNumber" to student.regNumber, // Keep original format for the student app
                    "unitCode" to unitCode,
                    "mark" to mark
                )

                // Save directly to Firestore
                db.collection("results").document(docId).set(resultData)
                    .addOnSuccessListener {
                        Toast.makeText(this@EnterMarksActivity, "Mark saved for ${student.name}", Toast.LENGTH_SHORT).show()
                        holder.etMark.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@EnterMarksActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }

        override fun getItemCount(): Int = studentList.size
    }
}