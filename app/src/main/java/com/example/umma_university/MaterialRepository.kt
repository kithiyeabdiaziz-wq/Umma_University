package com.example.umma_university

import com.google.firebase.firestore.FirebaseFirestore

class MaterialRepository {
    private val db = FirebaseFirestore.getInstance()

    fun addMaterial(material: CourseMaterial, onResult: (Boolean, String?) -> Unit) {
        db.collection("materials").add(material)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getMaterialsForUnit(unitCode: String, onResult: (List<CourseMaterial>?, String?) -> Unit) {
        db.collection("materials").whereEqualTo("unitCode", unitCode).get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<CourseMaterial>()
                for (doc in result) {
                    val mat = doc.toObject(CourseMaterial::class.java)
                    if (mat != null) list.add(mat)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    fun getAllMaterials(onResult: (List<CourseMaterial>?, String?) -> Unit) {
        db.collection("materials").get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<CourseMaterial>()
                for (doc in result) {
                    val mat = doc.toObject(CourseMaterial::class.java)
                    if (mat != null) list.add(mat)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }
}