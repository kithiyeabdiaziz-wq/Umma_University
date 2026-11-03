package com.example.umma_university

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.net.URLEncoder

class ViewMaterialsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_materials)

        val recycler = findViewById<RecyclerView>(R.id.recyclerStudentMaterials)
        recycler.layoutManager = LinearLayoutManager(this)

        val repo = MaterialRepository()

        repo.getAllMaterials { list, errorMsg ->
            runOnUiThread {
                if (list != null) {
                    if (list.isEmpty()) {
                        Toast.makeText(this, "No materials uploaded yet.", Toast.LENGTH_LONG).show()
                    }

                    recycler.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_material, parent, false)
                            return object : RecyclerView.ViewHolder(view) {}
                        }

                        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                            val m = list[position]

                            holder.itemView.findViewById<TextView>(R.id.tvMatTitle).text =
                                getString(R.string.material_title_format, m.unitCode, m.title)
                            holder.itemView.findViewById<TextView>(R.id.tvMatType).text =
                                getString(R.string.tap_to_open, m.fileType)

                            holder.itemView.setOnClickListener {
                                try {
                                    if (m.fileType == "PDF") {
                                        // Use URLEncoder to safely pass the Cloudinary link to Google Docs Viewer
                                        val encodedUrl = URLEncoder.encode(m.fileUrl, "UTF-8")
                                        val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$encodedUrl"
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googleDocsUrl)))
                                    } else {
                                        // Videos, Images, PPTs, and Word docs open normally
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(m.fileUrl))
                                        startActivity(intent)
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(this@ViewMaterialsActivity, "Cannot open this file.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        override fun getItemCount() = list.size
                    }
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}