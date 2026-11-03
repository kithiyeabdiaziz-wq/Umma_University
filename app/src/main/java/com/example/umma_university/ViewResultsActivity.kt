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
import java.text.DecimalFormat

class ViewResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_results)

        val resultsRepository = ResultsRepository()
        val recyclerResults = findViewById<RecyclerView>(R.id.recyclerResults)
        val tvGpa = findViewById<TextView>(R.id.tvGpaDisplay)

        recyclerResults.layoutManager = LinearLayoutManager(this)

        resultsRepository.getMyResults { results, errorMsg ->
            runOnUiThread {
                if (results != null) {
                    if (results.isEmpty()) {
                        Toast.makeText(this, "No results found yet.", Toast.LENGTH_LONG).show()
                        tvGpa.text = "GPA: N/A"
                    } else {
                        recyclerResults.adapter = ResultsAdapter(results)

                        // Calculate GPA
                        var totalMarks = 0.0
                        for (r in results) {
                            totalMarks += (r.mark.toDoubleOrNull() ?: 0.0)
                        }
                        val average = totalMarks / results.size
                        val gpa = (average / 100) * 4.0

                        val df = DecimalFormat("#.##")
                        tvGpa.text = "Current GPA: ${df.format(gpa)} / 4.0"
                    }
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    inner class ResultsAdapter(private val resultsList: List<Result>) : RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {
        inner class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvUnit: TextView = itemView.findViewById(R.id.tvResultUnit)
            val tvMark: TextView = itemView.findViewById(R.id.tvResultMark)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
            return ResultsViewHolder(view)
        }

        override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
            val result = resultsList[position]
            holder.tvUnit.text = result.unitCode
            holder.tvMark.text = "${result.mark}%"
        }

        override fun getItemCount(): Int = resultsList.size
    }
}