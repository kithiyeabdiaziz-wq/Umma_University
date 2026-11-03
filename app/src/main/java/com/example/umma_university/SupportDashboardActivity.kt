package com.example.umma_university

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SupportDashboardActivity : AppCompatActivity() {

    private var supportDept: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUid == null) { finish(); return }

        FirebaseFirestore.getInstance().collection("users").document(currentUid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role")
                supportDept = doc.getString("department") ?: ""

                if (role != "support") {
                    Toast.makeText(this, "Access Denied. Support staff only.", Toast.LENGTH_LONG).show()
                    finish()
                } else if (supportDept.isEmpty()) {
                    Toast.makeText(this, "Your account has no department assigned. Contact admin.", Toast.LENGTH_LONG).show()
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

    override fun onResume() {
        super.onResume()
        if (supportDept.isNotEmpty()) refreshCounts()
    }

    private fun setupDashboard() {
        setContentView(R.layout.activity_support_dashboard)

        findViewById<TextView>(R.id.tvSupportDept).text = "Department: $supportDept"

        findViewById<Button>(R.id.btnSupportLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnSupportHelpDesk).setOnClickListener {
            val i = Intent(this, SupportTicketsActivity::class.java)
            i.putExtra("department", supportDept)
            startActivity(i)
        }

        findViewById<Button>(R.id.btnSupportMyProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<Button>(R.id.btnSupportFaqs).setOnClickListener {
            startActivity(Intent(this, FaqActivity::class.java))
        }

        refreshCounts()
    }

    private fun refreshCounts() {
        val pendingTv = findViewById<TextView>(R.id.tvPendingCount)
        val resolvedTv = findViewById<TextView>(R.id.tvResolvedCount)
        val db = FirebaseFirestore.getInstance()

        db.collection("complaints")
            .whereEqualTo("department", supportDept)
            .whereEqualTo("status", "Pending")
            .get()
            .addOnSuccessListener { pendingTv.text = it.size().toString() }

        db.collection("complaints")
            .whereEqualTo("department", supportDept)
            .whereEqualTo("status", "Resolved")
            .get()
            .addOnSuccessListener { resolvedTv.text = it.size().toString() }
    }
}