package com.niiadotei.storehouse.ui.purchases

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.niiadotei.storehouse.data.DatabaseHelper
import com.niiadotei.storehouse.databinding.ActivityPurchasesBinding

class PurchasesActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPurchasesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Purchase History"

        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        val recyclerView = binding.purchasesRecyclerView

        val databaseHelper = DatabaseHelper(applicationContext)

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        val purchasesViewModel = PurchasesViewModel(databaseHelper.purchasesArray)

        recyclerView.adapter = purchasesViewModel
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}