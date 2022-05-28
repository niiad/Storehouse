package com.niiadotei.storehouse.ui.customers.purchases

import androidx.appcompat.app.AppCompatActivity
import com.niiadotei.storehouse.data.DatabaseHelper
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.niiadotei.storehouse.databinding.ActivityCustomerPurchasesBinding

class CustomerPurchasesActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCustomerPurchasesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        title = "Customer Purchases"

        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        val recyclerView = binding.customerPurchasesRecyclerView
        val databaseHelper = DatabaseHelper(applicationContext)

        val intent = intent
        val customer = intent.getStringExtra("customer")

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        val customerPurchasesViewModel = CustomerPurchasesViewModel(databaseHelper.getPurchasesArray(customer!!))
        recyclerView.adapter = customerPurchasesViewModel
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}