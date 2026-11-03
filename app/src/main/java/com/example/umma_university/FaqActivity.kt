package com.example.umma_university

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FaqActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        val tv = findViewById<TextView>(R.id.tvFaqContent)
        tv.text = """
            FREQUENTLY ASKED QUESTIONS

            Q: How do I reset my password?
            A: On the login screen, tap "Forgot password" and check your email.

            Q: How long does complaint resolution take?
            A: Typically 2-3 working days.

            Q: Who do I contact in an emergency?
            A: Campus Security: 0800-724-000

            STUDENT WELFARE CONTACTS

            Counselling: counselling@umma.ac.ke
            Health Services: health@umma.ac.ke
            Disability Support: support@umma.ac.ke
            Dean of Students: dean@umma.ac.ke

            EMERGENCY

            Security: 0800-724-000
            Ambulance: 999
        """.trimIndent()
    }
}