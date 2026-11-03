package com.example.umma_university

import android.app.AlertDialog
import android.content.Intent
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

class LecturerDashboardActivity : AppCompatActivity() {

    private lateinit var lecturerRepository: LecturerRepository
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturer_dashboard)

        lecturerRepository = LecturerRepository()
        authRepository = AuthRepository()

        val recyclerLecUnits = findViewById<RecyclerView>(R.id.recyclerLecUnits)
        val btnLogoutLec = findViewById<Button>(R.id.btnLogoutLec)
        val btnLecProfile = findViewById<Button>(R.id.btnLecProfile) // NEW

        recyclerLecUnits.layoutManager = LinearLayoutManager(this)

        lecturerRepository.getMyUnits { units, errorMsg ->
            runOnUiThread {
                if (units != null) {
                    recyclerLecUnits.adapter = LecUnitAdapter(units)
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }

        // NEW: Open Profile
        btnLecProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnLogoutLec.setOnClickListener {
            authRepository.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    inner class LecUnitAdapter(private val unitList: List<CourseUnit>) : RecyclerView.Adapter<LecUnitAdapter.LecUnitViewHolder>() {

        inner class LecUnitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvCode: TextView = itemView.findViewById(R.id.tvLecItemUnitCode)
            val tvName: TextView = itemView.findViewById(R.id.tvLecItemUnitName)
            val tvContact: TextView = itemView.findViewById(R.id.tvLecContact)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LecUnitViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lecturer_unit, parent, false)
            return LecUnitViewHolder(view)
        }

        override fun onBindViewHolder(holder: LecUnitViewHolder, position: Int) {
            val unit = unitList[position]
            holder.tvCode.text = unit.unitCode
            holder.tvName.text = unit.unitName

            // Display the contact info on the dashboard list
            holder.tvContact.text = "Contact: ${unit.lecturerEmail}\nOffice: Room 402"

            holder.itemView.setOnClickListener {
                val options = arrayOf("Enter Marks", "Take Attendance", "Post Assignment", "Upload Material", "View Office Hours")

                AlertDialog.Builder(this@LecturerDashboardActivity)
                    .setTitle("Manage ${unit.unitCode}")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> { // Enter Marks
                                val intent = Intent(this@LecturerDashboardActivity, EnterMarksActivity::class.java)
                                intent.putExtra("UNIT_CODE", unit.unitCode)
                                startActivity(intent)
                            }
                            1 -> { // Attendance
                                val intent = Intent(this@LecturerDashboardActivity, AttendanceActivity::class.java)
                                intent.putExtra("UNIT_CODE", unit.unitCode)
                                startActivity(intent)
                            }
                            2 -> { // Assignment
                                val intent = Intent(this@LecturerDashboardActivity, AddAssignmentActivity::class.java)
                                intent.putExtra("UNIT_CODE", unit.unitCode)
                                startActivity(intent)
                            }
                            3 -> { // Upload Material
                                val intent = Intent(this@LecturerDashboardActivity, AddMaterialActivity::class.java)
                                intent.putExtra("UNIT_CODE", unit.unitCode)
                                startActivity(intent)
                            }
                            4 -> { // View Office Hours
                                AlertDialog.Builder(this@LecturerDashboardActivity)
                                    .setTitle("Office Hours & Contact")
                                    .setMessage("Consultation Hours for ${unit.unitCode}:\n\nMonday: 10:00 AM - 12:00 PM\nWednesday: 2:00 PM - 4:00 PM\n\nLocation: Room 402\nEmail: ${unit.lecturerEmail}")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    }
                    .show()
            }
        }

        override fun getItemCount(): Int = unitList.size
    }
}