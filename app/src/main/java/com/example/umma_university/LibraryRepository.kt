package com.example.umma_university

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class LibraryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Populate the database with sample books (Run this once from Admin or MainActivity)
    fun seedBooks() {
        val books = listOf(
            LibraryBook("b1", "Introduction to Algorithms", "Cormen", "Shelf 1, Sec A"),
            LibraryBook("b2", "Clean Code", "Robert Martin", "Shelf 2, Sec A"),
            LibraryBook("b3", "Database System Concepts", "Silberschatz", "Shelf 3, Sec B"),
            LibraryBook("b4", "Android Programming", "Bill Phillips", "Shelf 1, Sec C"),
            LibraryBook("b5", "Artificial Intelligence", "Stuart Russell", "Shelf 4, Sec A")
        )
        for (book in books) {
            db.collection("library").document(book.bookId).set(book)
        }
    }

    // Fetch all books for searching
    fun getAllBooks(onResult: (List<LibraryBook>?, String?) -> Unit) {
        db.collection("library").get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<LibraryBook>()
                for (doc in result) {
                    val book = doc.toObject(LibraryBook::class.java)
                    if (book != null) list.add(book)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    // Borrow a book
    fun borrowBook(bookId: String, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "Not logged in")

        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val regNumber = doc.getString("regNumber") ?: ""

            // Calculate due date (14 days from now)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 14)
            val dueDate = calendar.timeInMillis

            // FIXED SYNTAX: Passing fields and values correctly as varargs
            db.collection("library").document(bookId)
                .update(
                    "status", "Borrowed",
                    "borrowedBy", regNumber,
                    "dueDate", dueDate
                )
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { e -> onResult(false, e.message) }
        }.addOnFailureListener { e ->
            onResult(false, e.message)
        }
    }

    // Fetch only the books borrowed by the logged-in student
    fun getMyBorrowedBooks(onResult: (List<LibraryBook>?, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null, "Not logged in")

        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val regNumber = doc.getString("regNumber") ?: ""

            db.collection("library").whereEqualTo("borrowedBy", regNumber).get()
                .addOnSuccessListener { result ->
                    val list = mutableListOf<LibraryBook>()
                    for (document in result) {
                        val book = document.toObject(LibraryBook::class.java)
                        if (book != null) list.add(book)
                    }
                    onResult(list, null)
                }
                .addOnFailureListener { e -> onResult(null, e.message) }
        }.addOnFailureListener { e ->
            onResult(null, e.message)
        }
    }

    // Reserve a borrowed book
    fun reserveBook(bookId: String, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "Not logged in")
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val regNumber = doc.getString("regNumber") ?: ""
            // We just add a reservation document
            db.collection("reservations").add(
                mapOf("bookId" to bookId, "studentReg" to regNumber, "timestamp" to System.currentTimeMillis())
            ).addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { e -> onResult(false, e.message) }
        }.addOnFailureListener { e ->
            onResult(false, e.message)
        }
    }
}