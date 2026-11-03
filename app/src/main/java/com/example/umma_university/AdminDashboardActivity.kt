package com.example.umma_university

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var adminRepository: AdminRepository
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUid == null) {
            finish()
            return
        }

        FirebaseFirestore.getInstance().collection("users").document(currentUid).get()
            .addOnSuccessListener { document ->
                if (document.getString("role") != "admin") {
                    Toast.makeText(this, "Access Denied. Admins only.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    setupDashboard()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error verifying role.", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun setupDashboard() {
        setContentView(R.layout.activity_admin_dashboard)

        adminRepository = AdminRepository()
        authRepository = AuthRepository()

        val btnAdminLogout = findViewById<Button>(R.id.btnAdminLogout)
        btnAdminLogout.setOnClickListener {
            authRepository.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val tabLayout = findViewById<TabLayout>(R.id.adminTabLayout)
        val layoutCourse = findViewById<LinearLayout>(R.id.layoutCourse)
        val layoutUnit = findViewById<LinearLayout>(R.id.layoutUnit)
        val layoutLecturer = findViewById<LinearLayout>(R.id.layoutLecturer)
        val layoutStaff = findViewById<LinearLayout>(R.id.layoutStaff)
        val layoutAnnouncement = findViewById<LinearLayout>(R.id.layoutAnnouncement)
        val layoutTimetable = findViewById<LinearLayout>(R.id.layoutTimetable)
        val layoutFees = findViewById<LinearLayout>(R.id.layoutFees)
        val layoutHelpDesk = findViewById<LinearLayout>(R.id.layoutHelpDesk)
        val layoutReports = findViewById<LinearLayout>(R.id.layoutReports)
        val layoutAdmissions = findViewById<LinearLayout>(R.id.layoutAdmissions)

        tabLayout.addTab(tabLayout.newTab().setText("Courses"))
        tabLayout.addTab(tabLayout.newTab().setText("Units"))
        tabLayout.addTab(tabLayout.newTab().setText("Lecturers"))
        tabLayout.addTab(tabLayout.newTab().setText("Staff"))
        tabLayout.addTab(tabLayout.newTab().setText("News"))
        tabLayout.addTab(tabLayout.newTab().setText("Timetable"))
        tabLayout.addTab(tabLayout.newTab().setText("Fees"))
        tabLayout.addTab(tabLayout.newTab().setText("Help Desk"))
        tabLayout.addTab(tabLayout.newTab().setText("Reports"))
        tabLayout.addTab(tabLayout.newTab().setText("Admissions"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                layoutCourse.visibility = View.GONE
                layoutUnit.visibility = View.GONE
                layoutLecturer.visibility = View.GONE
                layoutStaff.visibility = View.GONE
                layoutAnnouncement.visibility = View.GONE
                layoutTimetable.visibility = View.GONE
                layoutFees.visibility = View.GONE
                layoutHelpDesk.visibility = View.GONE
                layoutReports.visibility = View.GONE
                layoutAdmissions.visibility = View.GONE

                when (tab?.position) {
                    0 -> layoutCourse.visibility = View.VISIBLE
                    1 -> layoutUnit.visibility = View.VISIBLE
                    2 -> layoutLecturer.visibility = View.VISIBLE
                    3 -> layoutStaff.visibility = View.VISIBLE
                    4 -> layoutAnnouncement.visibility = View.VISIBLE
                    5 -> layoutTimetable.visibility = View.VISIBLE
                    6 -> layoutFees.visibility = View.VISIBLE
                    7 -> layoutHelpDesk.visibility = View.VISIBLE
                    8 -> layoutReports.visibility = View.VISIBLE
                    9 -> layoutAdmissions.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // --- 1. COURSE ---
        val etCourseCode = findViewById<EditText>(R.id.etCourseCode)
        val etCourseTitle = findViewById<EditText>(R.id.etCourseTitle)
        val etCourseDept = findViewById<EditText>(R.id.etCourseDept)
        val btnSaveCourse = findViewById<Button>(R.id.btnSaveCourse)

        btnSaveCourse.setOnClickListener {
            val code = etCourseCode.text.toString().trim().uppercase()
            val title = etCourseTitle.text.toString().trim()
            val dept = etCourseDept.text.toString().trim()

            if (code.isEmpty() || title.isEmpty() || dept.isEmpty()) {
                Toast.makeText(this, "Fill all course fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newCourse = Course(courseCode = code, title = title, department = dept)
            adminRepository.addCourse(newCourse) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Course Added!", Toast.LENGTH_SHORT).show()
                        etCourseCode.text.clear(); etCourseTitle.text.clear(); etCourseDept.text.clear()
                    } else Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }

        // --- 2. UNIT ---
        val etUnitCode = findViewById<EditText>(R.id.etUnitCode)
        val etUnitName = findViewById<EditText>(R.id.etUnitName)
        val etUnitCourseCode = findViewById<EditText>(R.id.etUnitCourseCode)
        val etUnitLecturerEmail = findViewById<EditText>(R.id.etUnitLecturerEmail)
        val btnSaveUnit = findViewById<Button>(R.id.btnSaveUnit)

        btnSaveUnit.setOnClickListener {
            val unitCode = etUnitCode.text.toString().trim().uppercase()
            val unitName = etUnitName.text.toString().trim()
            val courseCode = etUnitCourseCode.text.toString().trim().uppercase()
            val lecturerEmail = etUnitLecturerEmail.text.toString().trim().lowercase()

            if (unitCode.isEmpty() || unitName.isEmpty() || courseCode.isEmpty() || lecturerEmail.isEmpty()) {
                Toast.makeText(this, "Fill all unit fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUnit = CourseUnit(unitCode = unitCode, unitName = unitName, courseCode = courseCode, lecturerEmail = lecturerEmail)
            adminRepository.addUnit(newUnit) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Unit Added!", Toast.LENGTH_SHORT).show()
                        etUnitCode.text.clear(); etUnitName.text.clear(); etUnitCourseCode.text.clear(); etUnitLecturerEmail.text.clear()
                    } else Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }

        // --- 3. LECTURER ---
        val etLecName = findViewById<EditText>(R.id.etLecName)
        val etLecEmail = findViewById<EditText>(R.id.etLecEmail)
        val etLecPassword = findViewById<EditText>(R.id.etLecPassword)
        val etLecDept = findViewById<EditText>(R.id.etLecDept)
        val btnSaveLecturer = findViewById<Button>(R.id.btnSaveLecturer)

        btnSaveLecturer.setOnClickListener {
            val name = etLecName.text.toString().trim()
            val email = etLecEmail.text.toString().trim().lowercase()
            val password = etLecPassword.text.toString()
            val dept = etLecDept.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || dept.isEmpty()) {
                Toast.makeText(this, "Fill all lecturer fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newLecturer = User(email = email, name = name, role = "lecturer", regNumber = "", department = dept)
            authRepository.register(newLecturer, password) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Lecturer Registered!", Toast.LENGTH_SHORT).show()
                        etLecName.text.clear(); etLecEmail.text.clear(); etLecPassword.text.clear(); etLecDept.text.clear()
                    } else Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }

        // --- 3b. SUPPORT STAFF ---
        val etStaffName = findViewById<EditText>(R.id.etStaffName)
        val etStaffEmail = findViewById<EditText>(R.id.etStaffEmail)
        val etStaffPassword = findViewById<EditText>(R.id.etStaffPassword)
        val etStaffDept = findViewById<EditText>(R.id.etStaffDept)
        val btnSaveStaff = findViewById<Button>(R.id.btnSaveStaff)

        btnSaveStaff.setOnClickListener {
            val name = etStaffName.text.toString().trim()
            val email = etStaffEmail.text.toString().trim().lowercase()
            val password = etStaffPassword.text.toString()
            val dept = etStaffDept.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || dept.isEmpty()) {
                Toast.makeText(this, "Fill all support staff fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newStaff = User(email = email, name = name, role = "support", regNumber = "", department = dept)
            authRepository.register(newStaff, password) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Support Staff Registered!", Toast.LENGTH_SHORT).show()
                        etStaffName.text.clear(); etStaffEmail.text.clear(); etStaffPassword.text.clear(); etStaffDept.text.clear()
                    } else Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }

        // --- 4. ANNOUNCEMENT ---
        val etAnnouncementTitle = findViewById<EditText>(R.id.etAnnouncementTitle)
        val etAnnouncementMessage = findViewById<EditText>(R.id.etAnnouncementMessage)
        val btnPostAnnouncement = findViewById<Button>(R.id.btnPostAnnouncement)

        btnPostAnnouncement.setOnClickListener {
            val title = etAnnouncementTitle.text.toString().trim()
            val message = etAnnouncementMessage.text.toString().trim()

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Fill all announcement fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newAnnouncement = Announcement(title = title, message = message, postedBy = "Admin")
            adminRepository.postAnnouncement(newAnnouncement) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Announcement Posted!", Toast.LENGTH_SHORT).show()
                        etAnnouncementTitle.text.clear(); etAnnouncementMessage.text.clear()
                    } else Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }

        // --- 5. TIMETABLE & EXAM ---
        val etTtUnitCode = findViewById<EditText>(R.id.etTtUnitCode)
        val etTtDay = findViewById<EditText>(R.id.etTtDay)
        val etTtTime = findViewById<EditText>(R.id.etTtTime)
        val etTtVenue = findViewById<EditText>(R.id.etTtVenue)
        val btnSaveTimetable = findViewById<Button>(R.id.btnSaveTimetable)

        btnSaveTimetable.setOnClickListener {
            val unitCode = etTtUnitCode.text.toString().trim().uppercase()
            val day = etTtDay.text.toString().trim()
            val time = etTtTime.text.toString().trim()
            val venue = etTtVenue.text.toString().trim()

            if (unitCode.isEmpty() || day.isEmpty() || time.isEmpty() || venue.isEmpty()) {
                Toast.makeText(this, "Fill all timetable fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newEntry = TimetableEntry(unitCode = unitCode, day = day, time = time, venue = venue)
            adminRepository.addTimetableEntry(newEntry) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Timetable Entry Added!", Toast.LENGTH_SHORT).show()
                        etTtUnitCode.text.clear(); etTtDay.text.clear(); etTtTime.text.clear(); etTtVenue.text.clear()
                    } else Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }

        val etExamUnitCode = findViewById<EditText>(R.id.etExamUnitCode)
        val etExamDate = findViewById<EditText>(R.id.etExamDate)
        val etExamTime = findViewById<EditText>(R.id.etExamTime)
        val etExamVenue = findViewById<EditText>(R.id.etExamVenue)
        val btnSaveExam = findViewById<Button>(R.id.btnSaveExam)

        btnSaveExam.setOnClickListener {
            val unitCode = etExamUnitCode.text.toString().trim().uppercase()
            val date = etExamDate.text.toString().trim()
            val time = etExamTime.text.toString().trim()
            val venue = etExamVenue.text.toString().trim()

            if (unitCode.isEmpty() || date.isEmpty() || time.isEmpty() || venue.isEmpty()) {
                Toast.makeText(this, "Fill all exam fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newExam = ScheduleItem(unitCode, "EXAM", date, time, venue)
            FirebaseFirestore.getInstance().collection("exams").add(newExam)
                .addOnSuccessListener {
                    Toast.makeText(this, "Exam Added!", Toast.LENGTH_SHORT).show()
                    etExamUnitCode.text.clear(); etExamDate.text.clear(); etExamTime.text.clear(); etExamVenue.text.clear()
                }
                .addOnFailureListener { e -> Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show() }
        }

        // --- 6. FEES ---
        val etFeeRegNumber = findViewById<EditText>(R.id.etFeeRegNumber)
        val etFeeTotal = findViewById<EditText>(R.id.etFeeTotal)
        val etFeePaid = findViewById<EditText>(R.id.etFeePaid)
        val etFeeSemester = findViewById<EditText>(R.id.etFeeSemester)
        val btnSaveFees = findViewById<Button>(R.id.btnSaveFees)

        btnSaveFees.setOnClickListener {
            val regNum = etFeeRegNumber.text.toString().trim().uppercase()
            val totalStr = etFeeTotal.text.toString().trim()
            val paidStr = etFeePaid.text.toString().trim()
            val semester = etFeeSemester.text.toString().trim()

            if (regNum.isEmpty() || totalStr.isEmpty() || paidStr.isEmpty() || semester.isEmpty()) {
                Toast.makeText(this, "Fill all fee fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val total = totalStr.toDouble()
                val paid = paidStr.toDouble()

                if (total <= 0.0) {
                    Toast.makeText(this, "Total fees must be greater than 0", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (paid < 0.0) {
                    Toast.makeText(this, "Amount paid cannot be negative", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (paid > total) {
                    Toast.makeText(this, "Amount paid cannot exceed total fees", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val balance = total - paid
                val newFee = FeeRecord(regNum, total, paid, balance, semester)

                adminRepository.addFeeRecord(newFee) { success, errorMsg ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Fee Record Saved!", Toast.LENGTH_SHORT).show()
                            etFeeRegNumber.text.clear(); etFeeTotal.text.clear(); etFeePaid.text.clear(); etFeeSemester.text.clear()
                        } else {
                            Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter valid numbers for fees", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 7. HELP DESK ---
        val btnOpenAdminHelpDesk = findViewById<Button>(R.id.btnOpenAdminHelpDesk)
        btnOpenAdminHelpDesk.setOnClickListener {
            startActivity(Intent(this, AdminHelpDeskActivity::class.java))
        }

        // --- 8. REPORTS ---
        val btnGenerateReports = findViewById<Button>(R.id.btnGenerateReports)
        val tvReportResults = findViewById<TextView>(R.id.tvReportResults)

        btnGenerateReports.setOnClickListener {
            tvReportResults.text = "Generating reports, please wait..."
            val db = FirebaseFirestore.getInstance()

            db.collection("users").whereEqualTo("role", "student").get().addOnSuccessListener { studentDocs ->
                val totalStudents = studentDocs.size()

                db.collection("fees").get().addOnSuccessListener { feeDocs ->
                    var totalCollected = 0.0
                    for (doc in feeDocs) {
                        totalCollected += (doc.getDouble("amountPaid") ?: 0.0)
                    }

                    db.collection("results").get().addOnSuccessListener { resultDocs ->
                        var totalMarks = 0.0
                        var resultCount = 0
                        for (doc in resultDocs) {
                            val markStr = doc.getString("mark")
                            if (markStr != null) {
                                totalMarks += markStr.toDoubleOrNull() ?: 0.0
                                resultCount++
                            }
                        }
                        val averageMark = if (resultCount > 0) totalMarks / resultCount else 0.0

                        // FIXED: Used safe string formatting to clear all warnings
                        tvReportResults.text = String.format(Locale.US,
                            "📊 UMMA UNIVERSITY SYSTEM REPORT\n\n" +
                                    "Total Enrolled Students: %d\n\n" +
                                    "Total Fees Collected: KES %,.2f\n\n" +
                                    "Average Academic Performance: %.1f%%\n\n" +
                                    "(Report generated successfully)",
                            totalStudents, totalCollected, averageMark
                        )
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Report Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 9. ADMISSIONS (NEW) ---
        val btnOpenAdmissions = findViewById<Button>(R.id.btnOpenAdmissions)
        btnOpenAdmissions.setOnClickListener {
            startActivity(Intent(this, AdminAdmissionsActivity::class.java))
        }
    }
}