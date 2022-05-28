package com.niiadotei.storehouse.ui.suppliers

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.niiadotei.storehouse.data.DatabaseHelper
import com.niiadotei.storehouse.databinding.ActivitySuppliersBinding

class SuppliersActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySuppliersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Suppliers"

        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        val recyclerView = binding.suppliersRecyclerView

        val databaseHelper = DatabaseHelper(applicationContext)

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        val suppliersViewModel = SuppliersViewModel(databaseHelper.suppliersArray)
        recyclerView.adapter = suppliersViewModel
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}