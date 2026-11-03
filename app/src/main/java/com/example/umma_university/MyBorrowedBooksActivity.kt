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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyBorrowedBooksActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_borrowed_books)

        recycler = findViewById(R.id.recyclerMyBorrowed)
        recycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()

        // Add a slight delay to ensure Firebase has finished saving the "Borrowed" status
        recycler.postDelayed({
            fetchBorrowedBooks()
        }, 1000) // 1 second delay
    }

    private fun fetchBorrowedBooks() {
        val libraryRepository = LibraryRepository()

        libraryRepository.getMyBorrowedBooks { books, errorMsg ->
            runOnUiThread {
                if (books != null) {
                    if (books.isEmpty()) {
                        Toast.makeText(this, "You have not borrowed any books.", Toast.LENGTH_SHORT).show()
                    }
                    recycler.adapter = BorrowedAdapter(books)
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    inner class BorrowedAdapter(private val list: List<LibraryBook>) : RecyclerView.Adapter<BorrowedAdapter.ViewHolder>() {
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvTitle: TextView = v.findViewById(R.id.tvBookTitle)
            val tvAuthor: TextView = v.findViewById(R.id.tvBookAuthor)
            val tvLocation: TextView = v.findViewById(R.id.tvBookLocation)
            val tvStatus: TextView = v.findViewById(R.id.tvBookStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_library_book, parent, false)
            // Hide the action button in this view
            v.findViewById<View>(R.id.btnAction).visibility = View.GONE
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val book = list[position]
            holder.tvTitle.text = book.title
            holder.tvAuthor.text = "By: ${book.author}"
            holder.tvLocation.text = "Location: ${book.location}"

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dueDateStr = dateFormat.format(Date(book.dueDate))

            val today = Calendar.getInstance().timeInMillis
            val diff = book.dueDate - today
            val daysLeft = TimeUnit.MILLISECONDS.toDays(diff)

            if (daysLeft < 0) {
                // Overdue
                val daysOverdue = Math.abs(daysLeft)
                val fine = daysOverdue * 20 // 20 KES fine per day
                holder.tvStatus.text = "OVERDUE by $daysOverdue days!\nDue Date was: $dueDateStr\nFine: KES $fine"
                holder.tvStatus.setTextColor(android.graphics.Color.RED)
            } else {
                // On time
                holder.tvStatus.text = "Due Date: $dueDateStr\n($daysLeft days left)"
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#006400"))
            }
        }

        override fun getItemCount() = list.size
    }
}