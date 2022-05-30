package com.niiadotei.storehouse.ui.supplies

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.niiadotei.storehouse.data.DatabaseHelper
import com.niiadotei.storehouse.databinding.ActivitySuppliesBinding

class SuppliesActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySuppliesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Supplies History"

        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        val recyclerView = binding.suppliesRecyclerView

        databaseHelper = DatabaseHelper(applicationContext)

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        val suppliesViewModel = SuppliesViewModel(databaseHelper.supplyChainArray)
        recyclerView.adapter = suppliesViewModel
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}