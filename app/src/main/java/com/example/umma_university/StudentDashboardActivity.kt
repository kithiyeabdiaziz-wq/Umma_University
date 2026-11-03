package com.example.umma_university

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

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var studentRepository: StudentRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var announcementRepository: AnnouncementRepository
    private lateinit var registrationRepository: RegistrationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        studentRepository = StudentRepository()
        authRepository = AuthRepository()
        announcementRepository = AnnouncementRepository()
        registrationRepository = RegistrationRepository()

        // Find all views
        val recyclerUnits = findViewById<RecyclerView>(R.id.recyclerUnits)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val tvAnnouncement = findViewById<TextView>(R.id.tvAnnouncement)

        val btnViewResults = findViewById<Button>(R.id.btnViewResults)
        val btnAcademicInfo = findViewById<Button>(R.id.btnAcademicInfo)
        val btnViewTimetable = findViewById<Button>(R.id.btnViewTimetable)
        val btnViewFees = findViewById<Button>(R.id.btnViewFees)
        val btnMyUnits = findViewById<Button>(R.id.btnMyUnits)
        val btnViewAttendance = findViewById<Button>(R.id.btnViewAttendance) // NEW
        val btnHelpDesk = findViewById<Button>(R.id.btnHelpDesk)
        val btnViewAssignments = findViewById<Button>(R.id.btnViewAssignments)
        val btnCourseMaterials = findViewById<Button>(R.id.btnCourseMaterials)
        val btnLibrary = findViewById<Button>(R.id.btnLibrary)
        val btnNews = findViewById<Button>(R.id.btnNews)
        val btnCampusFacilities = findViewById<Button>(R.id.btnCampusFacilities)
        val btnCareerAlumni = findViewById<Button>(R.id.btnCareerAlumni)

        recyclerUnits.layoutManager = LinearLayoutManager(this)

        // Fetch Announcement
        announcementRepository.getLatestAnnouncement { announcement, _ ->
            runOnUiThread {
                if (announcement != null) {
                    tvAnnouncement.text = getString(R.string.announcement_format, announcement.title, announcement.message)
                    tvAnnouncement.visibility = View.VISIBLE
                } else {
                    tvAnnouncement.visibility = View.GONE
                }
            }
        }

        // Fetch Available Units
        studentRepository.getAllUnits { units, errorMsg ->
            runOnUiThread {
                if (units != null) {
                    recyclerUnits.adapter = UnitAdapter(units)
                } else {
                    Toast.makeText(this, "Error loading units: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Button Click Listeners
        btnViewResults.setOnClickListener {
            startActivity(Intent(this, ViewResultsActivity::class.java))
        }

        btnAcademicInfo.setOnClickListener {
            startActivity(Intent(this, AcademicInfoActivity::class.java))
        }

        btnViewTimetable.setOnClickListener {
            startActivity(Intent(this, ViewTimetableActivity::class.java))
        }

        btnViewFees.setOnClickListener {
            startActivity(Intent(this, ViewFeesActivity::class.java))
        }

        btnMyUnits.setOnClickListener {
            startActivity(Intent(this, MyUnitsActivity::class.java))
        }

        // Open Attendance
        btnViewAttendance.setOnClickListener {
            startActivity(Intent(this, ViewAttendanceActivity::class.java))
        }

        btnHelpDesk.setOnClickListener {
            startActivity(Intent(this, HelpDeskActivity::class.java))
        }

        btnViewAssignments.setOnClickListener {
            startActivity(Intent(this, ViewAssignmentsActivity::class.java))
        }

        btnCourseMaterials.setOnClickListener {
            startActivity(Intent(this, ViewMaterialsActivity::class.java))
        }

        btnLibrary.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        btnNews.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }

        btnCampusFacilities.setOnClickListener {
            startActivity(Intent(this, CampusFacilitiesActivity::class.java))
        }

        btnCareerAlumni.setOnClickListener {
            startActivity(Intent(this, CareerAlumniActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnLogout.setOnClickListener {
            authRepository.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    inner class UnitAdapter(private val unitList: List<CourseUnit>) : RecyclerView.Adapter<UnitAdapter.UnitViewHolder>() {

        inner class UnitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvCode: TextView = itemView.findViewById(R.id.tvItemUnitCode)
            val tvName: TextView = itemView.findViewById(R.id.tvItemUnitName)
            val tvCourse: TextView = itemView.findViewById(R.id.tvItemUnitCourse)
            val btnRegister: Button = itemView.findViewById(R.id.btnRegisterUnit)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_unit, parent, false)
            return UnitViewHolder(view)
        }

        override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
            val unit = unitList[position]
            holder.tvCode.text = unit.unitCode
            holder.tvName.text = unit.unitName
            holder.tvCourse.text = getString(R.string.course_format, unit.courseCode)

            holder.btnRegister.setOnClickListener {
                registrationRepository.registerUnit(unit) { success, errorMsg ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this@StudentDashboardActivity, getString(R.string.registered_success, unit.unitCode), Toast.LENGTH_SHORT).show()
                            holder.btnRegister.text = getString(R.string.registered_button)
                            holder.btnRegister.isEnabled = false
                        } else {
                            Toast.makeText(this@StudentDashboardActivity, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int = unitList.size
    }
}