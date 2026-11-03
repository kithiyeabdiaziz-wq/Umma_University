package com.example.umma_university

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role") ?: "student"
                                onResult(true, role)
                            }
                            .addOnFailureListener { e ->
                                onResult(false, "Failed to get user role: ${e.message}")
                            }
                    } else {
                        onResult(false, "User ID is null")
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun register(user: User, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val newUser = user.copy(uid = uid)
                        db.collection("users").document(uid).set(newUser)
                            .addOnSuccessListener { onResult(true, newUser.role) }
                            .addOnFailureListener { e -> onResult(false, e.message) }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // NEW: Change Password Function
    fun changePassword(oldPassword: String, newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user == null || user.email == null) {
            onResult(false, "Not logged in")
            return
        }

        // Re-authenticate the user first (Firebase requires this for sensitive actions)
        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, oldPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                // If old password is correct, update to new password
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e ->
                onResult(false, "Incorrect current password: ${e.message}")
            }
    }

    fun logout() {
        auth.signOut()
    }
}