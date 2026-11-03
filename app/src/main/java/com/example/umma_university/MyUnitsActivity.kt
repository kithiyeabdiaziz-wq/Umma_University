package com.example.umma_university

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyUnitsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_units)

        val recyclerMyUnits = findViewById<RecyclerView>(R.id.recyclerMyUnits)
        recyclerMyUnits.layoutManager = LinearLayoutManager(this)

        val registrationRepository = RegistrationRepository()

        registrationRepository.getMyRegisteredUnits { units, errorMsg ->
            runOnUiThread {
                if (units != null) {
                    if (units.isEmpty()) {
                        Toast.makeText(this, "You haven't registered for any units yet.", Toast.LENGTH_LONG).show()
                    }
                    recyclerMyUnits.adapter = MyUnitsAdapter(units)
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    inner class MyUnitsAdapter(private val list: List<Registration>) : RecyclerView.Adapter<MyUnitsAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvCode: TextView = itemView.findViewById(R.id.tvItemUnitCode)
            val tvName: TextView = itemView.findViewById(R.id.tvItemUnitName)
            val tvCourse: TextView = itemView.findViewById(R.id.tvItemUnitCourse)
            val btnRegister: View = itemView.findViewById(R.id.btnRegisterUnit)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_unit, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val reg = list[position]

            holder.tvCode.text = reg.unitCode
            holder.tvName.text = reg.unitName
            holder.tvCourse.text = "Course: ${reg.courseCode}"

            // Force visibility
            holder.tvCode.setTextColor(Color.BLACK)
            holder.tvName.setTextColor(Color.BLACK)
            holder.tvCourse.setTextColor(Color.DKGRAY)

            holder.btnRegister.visibility = View.GONE
            holder.itemView.setBackgroundColor(Color.parseColor("#F0F0F0"))
        }

        override fun getItemCount(): Int = list.size
    }
}