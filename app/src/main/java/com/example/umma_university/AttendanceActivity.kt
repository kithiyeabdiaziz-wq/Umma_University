package com.example.umma_university

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        val unitCode = intent.getStringExtra("UNIT_CODE") ?: ""
        val tvTitle = findViewById<TextView>(R.id.tvAttendanceTitle)
        val recycler = findViewById<RecyclerView>(R.id.recyclerAttendance)

        tvTitle.text = "Attendance for $unitCode"
        recycler.layoutManager = LinearLayoutManager(this)

        val db = FirebaseFirestore.getInstance()
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Fetch students directly
        db.collection("users").whereEqualTo("role", "student").get()
            .addOnSuccessListener { result ->
                val studentList = mutableListOf<User>()
                for (doc in result) {
                    val student = doc.toObject(User::class.java)
                    if (student != null) studentList.add(student)
                }

                recycler.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
                        return object : RecyclerView.ViewHolder(view) {}
                    }

                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        val student = studentList[position]
                        holder.itemView.findViewById<TextView>(R.id.tvAttStudentName).text = "${student.name}\n${student.regNumber}"

                        val cb = holder.itemView.findViewById<CheckBox>(R.id.cbPresent)

                        // Create safe ID for Firestore
                        val safeReg = student.regNumber.replace("/", "-")
                        val docId = "${dateStr}_${unitCode}_${safeReg}"

                        // Check if already marked present
                        db.collection("attendance").document(docId).get().addOnSuccessListener { attDoc ->
                            if (attDoc.exists()) {
                                cb.isChecked = true
                            }
                        }

                        cb.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                val attendanceData = hashMapOf(
                                    "studentRegNumber" to student.regNumber,
                                    "unitCode" to unitCode,
                                    "status" to "Present",
                                    "date" to dateStr
                                )
                                db.collection("attendance").document(docId).set(attendanceData)
                                Toast.makeText(this@AttendanceActivity, "${student.name} marked Present", Toast.LENGTH_SHORT).show()
                            } else {
                                db.collection("attendance").document(docId).delete()
                                Toast.makeText(this@AttendanceActivity, "${student.name} marked Absent", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun getItemCount() = studentList.size
                }
            }
    }
}