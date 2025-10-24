package com.example.findmypath

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.findmypath.db.RouteDatabase
import com.example.findmypath.repo.UploadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoutesListActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var uploadButton: Button
    private lateinit var progressBar: ProgressBar
    private var ids: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routes_list)
        listView = findViewById(R.id.routesList)
        uploadButton = findViewById(R.id.btnUpload)
        progressBar = findViewById(R.id.uploadProgress)

        loadRouteIds()

        uploadButton.setOnClickListener {
            lifecycleScope.launch {
                progressBar.visibility = View.VISIBLE
                val apiBase = getApiBase()
                val apiKey = getApiKey()
                val manager = UploadManager(applicationContext, apiBase, apiKey)
                manager.retryAllFailed()
                progressBar.visibility = View.GONE
                Toast.makeText(this@RoutesListActivity, "Retry attempted for failed uploads", Toast.LENGTH_SHORT).show()
                // refresh list
                loadRouteIds()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val routeId = ids[position]
            val intent = Intent(this@RoutesListActivity, RouteViewActivity::class.java)
            intent.putExtra("routeId", routeId)
            startActivity(intent)
        }
    }

    private fun loadRouteIds() {
        lifecycleScope.launch {
            ids = withContext(Dispatchers.IO) {
                val db = RouteDatabase.getInstance(applicationContext)
                db.routeDao().getAllRouteIds()
            }
            val adapter = ArrayAdapter(this@RoutesListActivity, android.R.layout.simple_list_item_1, ids)
            listView.adapter = adapter
        }
    }

    private fun getApiBase(): String {
        val env = System.getenv("API_BASE_URL")
        return env ?: "https://api.example.com/"
    }
    private fun getApiKey(): String? {
        val env = System.getenv("API_KEY")
        return env ?: null
    }
}
