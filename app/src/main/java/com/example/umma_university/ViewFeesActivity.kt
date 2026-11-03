package com.example.umma_university

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.DecimalFormat

class ViewFeesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_fees)

        val feeRepository = FeeRepository()

        val tvFeeSemester = findViewById<TextView>(R.id.tvFeeSemester)
        val tvFeeTotal = findViewById<TextView>(R.id.tvFeeTotal)
        val tvFeePaid = findViewById<TextView>(R.id.tvFeePaid)
        val tvFeeBalance = findViewById<TextView>(R.id.tvFeeBalance)
        val btnPayNow = findViewById<Button>(R.id.btnPayNow)
        val tvPaymentMessage = findViewById<TextView>(R.id.tvPaymentMessage)

        val formatter = DecimalFormat("#,###.00")

        feeRepository.getMyFeeRecord { feeRecord, errorMsg ->
            runOnUiThread {
                if (feeRecord != null) {
                    tvFeeSemester.text = "Semester: ${feeRecord.semester}"
                    tvFeeTotal.text = "KES ${formatter.format(feeRecord.totalFees)}"
                    tvFeePaid.text = "KES ${formatter.format(feeRecord.amountPaid)}"
                    tvFeeBalance.text = "KES ${formatter.format(feeRecord.balance)}"

                    if (feeRecord.balance <= 0.0) {
                        tvFeeBalance.setTextColor(android.graphics.Color.GREEN)
                        tvFeeBalance.text = "KES 0.00 (Fully Paid)"
                        btnPayNow.visibility = Button.GONE
                    }

                    // Show Payment Popup
                    btnPayNow.setOnClickListener {
                        showPaymentPopup(tvPaymentMessage)
                    }

                } else {
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showPaymentPopup(tvPaymentMessage: TextView) {
        val dialog = BottomSheetDialog(this)
        // THIS LINE CONNECTS THE KOTLIN TO THE XML
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_payment_gateway, null)
        dialog.setContentView(view)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupPayment)
        val layoutMpesa = view.findViewById<LinearLayout>(R.id.layoutMpesa)
        val layoutCard = view.findViewById<LinearLayout>(R.id.layoutCard)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirmPayment)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        // Toggle fields based on selected radio button
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioMpesa) {
                layoutMpesa.visibility = View.VISIBLE
                layoutCard.visibility = View.GONE
            } else {
                layoutMpesa.visibility = View.GONE
                layoutCard.visibility = View.VISIBLE
            }
        }

        btnConfirm.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId

            // Validation
            if (selectedId == R.id.radioMpesa) {
                val phone = view.findViewById<EditText>(R.id.etMpesaPhone).text.toString().trim()
                if (phone.isEmpty() || phone.length < 10) {
                    Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                val card = view.findViewById<EditText>(R.id.etCardNumber).text.toString().trim()
                val cvv = view.findViewById<EditText>(R.id.etCardCVV).text.toString().trim()
                if (card.length < 16 || cvv.length < 3) {
                    Toast.makeText(this, "Enter valid card details", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Simulate Payment Processing
            btnConfirm.text = "Processing..."
            btnConfirm.isEnabled = false
            progressBar.visibility = View.VISIBLE

            btnConfirm.postDelayed({
                dialog.dismiss()
                tvPaymentMessage.text = "Receipt: RCPT-${System.currentTimeMillis()}\nPayment simulated successfully."
                Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show()
            }, 2000) // 2-second delay
        }

        dialog.show()
    }
}