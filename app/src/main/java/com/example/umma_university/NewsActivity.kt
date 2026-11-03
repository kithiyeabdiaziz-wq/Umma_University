package com.example.umma_university

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NewsActivity : AppCompatActivity() {

    private lateinit var announcementRepository: AnnouncementRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        announcementRepository = AnnouncementRepository()

        val recyclerNews = findViewById<RecyclerView>(R.id.recyclerNews)
        val etInquiry = findViewById<EditText>(R.id.etInquiry)
        val btnSubmitInquiry = findViewById<Button>(R.id.btnSubmitInquiry)

        recyclerNews.layoutManager = LinearLayoutManager(this)

        // Load all announcements
        announcementRepository.getAllAnnouncements { list, errorMsg ->
            runOnUiThread {
                if (list != null) {
                    if (list.isEmpty()) {
                        Toast.makeText(this, "No announcements posted yet.", Toast.LENGTH_SHORT).show()
                    }
                    recyclerNews.adapter = NewsAdapter(list)
                }
            }
        }

        // Handle Inquiry Submission
        btnSubmitInquiry.setOnClickListener {
            val message = etInquiry.text.toString().trim()
            if (message.isEmpty()) {
                Toast.makeText(this, "Please type your inquiry first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            announcementRepository.submitInquiry(message) { success, error ->
                if (success) {
                    Toast.makeText(this, "Inquiry sent successfully!", Toast.LENGTH_LONG).show()
                    etInquiry.text.clear()
                } else {
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    inner class NewsAdapter(private val list: List<Announcement>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvTitle: TextView = v.findViewById(R.id.tvNewsTitle)
            val tvMessage: TextView = v.findViewById(R.id.tvNewsMessage)
            val tvPostedBy: TextView = v.findViewById(R.id.tvNewsPostedBy)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val announcement = list[position]
            holder.tvTitle.text = announcement.title
            holder.tvMessage.text = announcement.message
            holder.tvPostedBy.text = "Posted by: ${announcement.postedBy}"
        }

        override fun getItemCount() = list.size
    }
}