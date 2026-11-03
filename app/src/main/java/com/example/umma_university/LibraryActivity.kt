package com.example.umma_university

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

class LibraryActivity : AppCompatActivity() {

    private lateinit var libraryRepository: LibraryRepository
    private lateinit var adapter: BookAdapter
    private var allBooks: List<LibraryBook> = emptyList()
    private lateinit var recyclerLibrary: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        libraryRepository = LibraryRepository()

        recyclerLibrary = findViewById(R.id.recyclerLibrary)
        val etSearchBooks = findViewById<EditText>(R.id.etSearchBooks)
        val btnMyBorrowedBooks = findViewById<Button>(R.id.btnMyBorrowedBooks)

        recyclerLibrary.layoutManager = LinearLayoutManager(this)

        loadBooks()

        // Search functionality
        etSearchBooks.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filtered = allBooks.filter {
                    it.title.lowercase().contains(query) || it.author.lowercase().contains(query)
                }
                adapter = BookAdapter(filtered)
                recyclerLibrary.adapter = adapter
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnMyBorrowedBooks.setOnClickListener {
            startActivity(Intent(this, MyBorrowedBooksActivity::class.java))
        }
    }

    // Refresh the book list every time this screen opens
    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    private fun loadBooks() {
        libraryRepository.getAllBooks { books, errorMsg ->
            runOnUiThread {
                if (books != null) {
                    allBooks = books
                    adapter = BookAdapter(books)
                    recyclerLibrary.adapter = adapter
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    inner class BookAdapter(private val list: List<LibraryBook>) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvTitle: TextView = v.findViewById(R.id.tvBookTitle)
            val tvAuthor: TextView = v.findViewById(R.id.tvBookAuthor)
            val tvLocation: TextView = v.findViewById(R.id.tvBookLocation)
            val tvStatus: TextView = v.findViewById(R.id.tvBookStatus)
            val btnAction: Button = v.findViewById(R.id.btnAction)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_library_book, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val book = list[position]
            holder.tvTitle.text = book.title
            holder.tvAuthor.text = "By: ${book.author}"
            holder.tvLocation.text = "Location: ${book.location}"

            if (book.status == "Available") {
                holder.tvStatus.text = "Status: Available"
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#006400"))
                holder.btnAction.text = "Borrow"
                holder.btnAction.isEnabled = true

                holder.btnAction.setOnClickListener {
                    holder.btnAction.isEnabled = false // Prevent double clicks
                    holder.btnAction.text = "Borrowing..."

                    libraryRepository.borrowBook(book.bookId) { success, _ ->
                        runOnUiThread {
                            if (success) {
                                Toast.makeText(this@LibraryActivity, "Book Borrowed successfully!", Toast.LENGTH_LONG).show()
                                loadBooks() // Refresh the list immediately
                            } else {
                                Toast.makeText(this@LibraryActivity, "Failed to borrow.", Toast.LENGTH_SHORT).show()
                                holder.btnAction.isEnabled = true
                                holder.btnAction.text = "Borrow"
                            }
                        }
                    }
                }
            } else {
                holder.tvStatus.text = "Status: Borrowed"
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF0000"))
                holder.btnAction.text = "Reserve"
                holder.btnAction.isEnabled = true

                holder.btnAction.setOnClickListener {
                    libraryRepository.reserveBook(book.bookId) { success, _ ->
                        if (success) {
                            Toast.makeText(this@LibraryActivity, "Book Reserved! You will be notified.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        override fun getItemCount() = list.size
    }
}