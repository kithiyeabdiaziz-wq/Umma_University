package com.example.umma_university

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class AddMaterialActivity : AppCompatActivity() {

    private lateinit var repo: MaterialRepository
    private lateinit var recycler: RecyclerView
    private var unitCode: String = ""
    private var selectedFileUri: Uri? = null
    private lateinit var tvFileName: TextView

    // File Picker launcher to select any file
    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            val fileName = getFileName(uri)
            tvFileName.text = getString(R.string.file_selected, fileName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_material)

        // Initialize Cloudinary
        val config = HashMap<String, String>()
        config["cloud_name"] = "scqsjzgy"
        try {
            MediaManager.init(this, config)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        unitCode = intent.getStringExtra("UNIT_CODE") ?: ""

        val tvUnit = findViewById<TextView>(R.id.tvMatUnit)
        val etTitle = findViewById<EditText>(R.id.etMatTitle)
        val btnSelectFile = findViewById<Button>(R.id.btnSelectFile)
        tvFileName = findViewById(R.id.tvFileName)
        val btnUpload = findViewById<Button>(R.id.btnPostMaterial)

        recycler = findViewById(R.id.recyclerMaterials)
        recycler.layoutManager = LinearLayoutManager(this)

        tvUnit.text = getString(R.string.materials_for_unit, unitCode)
        repo = MaterialRepository()

        loadMaterials()

        btnSelectFile.setOnClickListener {
            // Allow the lecturer to pick any file format (PDF, PPT, Word, Video, Image)
            pickFileLauncher.launch("*/*")
        }

        btnUpload.setOnClickListener {
            val title = etTitle.text.toString().trim()

            if (title.isEmpty() || selectedFileUri == null) {
                Toast.makeText(this, R.string.please_fill_all, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Uploading file, please wait...", Toast.LENGTH_LONG).show()

            val mimeType = contentResolver.getType(selectedFileUri!!) ?: "document"

            // Determine the correct file type label for the UI
            val type = when {
                mimeType.contains("pdf") -> "PDF"
                mimeType.contains("image") -> "Image"
                mimeType.contains("video") -> "Video"
                mimeType.contains("presentation") || mimeType.contains("powerpoint") -> "PPT"
                mimeType.contains("word") -> "Word Document"
                else -> "Document"
            }

            // Upload to Cloudinary
            MediaManager.get().upload(selectedFileUri)
                .unsigned("umma_preset")
                .option("resource_type", "image") // CRITICAL FIX: "image" allows PDFs to be viewed, "raw" causes downloads
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val secureUrl = resultData?.get("secure_url").toString()

                        val mat = CourseMaterial(unitCode = unitCode, title = title, fileUrl = secureUrl, fileType = type)

                        repo.addMaterial(mat) { success, _ ->
                            runOnUiThread {
                                if (success) {
                                    Toast.makeText(this@AddMaterialActivity, "Uploaded Successfully!", Toast.LENGTH_SHORT).show()
                                    etTitle.text.clear()
                                    selectedFileUri = null
                                    tvFileName.text = getString(R.string.no_file_selected)
                                    loadMaterials()
                                }
                            }
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        runOnUiThread {
                            Toast.makeText(this@AddMaterialActivity, "Upload Error: ${error?.description}", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
        }
    }

    private fun loadMaterials() {
        repo.getMaterialsForUnit(unitCode) { list, _ ->
            runOnUiThread {
                if (list != null) {
                    recycler.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_material, parent, false)
                            return object : RecyclerView.ViewHolder(view) {}
                        }
                        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                            val m = list[position]
                            holder.itemView.findViewById<TextView>(R.id.tvMatTitle).text = m.title
                            holder.itemView.findViewById<TextView>(R.id.tvMatType).text = getString(R.string.file_type_format, m.fileType)
                        }
                        override fun getItemCount() = list.size
                    }
                }
            }
        }
    }

    // Helper function to get the real file name from the phone's storage
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "Unknown File"
    }
}