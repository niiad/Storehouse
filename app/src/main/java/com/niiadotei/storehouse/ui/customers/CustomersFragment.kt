package com.niiadotei.storehouse.ui.customers

import android.app.AlertDialog
import android.app.Dialog

import android.content.DialogInterface

import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.os.Bundle

import android.text.TextUtils

import android.view.*

import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast

import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.niiadotei.storehouse.R
import com.niiadotei.storehouse.data.DatabaseHelper
import com.niiadotei.storehouse.databinding.FragmentCustomersBinding

import java.text.SimpleDateFormat
import java.util.*

class CustomersFragment : Fragment() {
    private lateinit var customersViewModel: CustomersViewModel
    private lateinit var binding: FragmentCustomersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCustomersBinding.inflate(inflater, container, false)

        val addCustomerFAB: FloatingActionButton = binding.addCustomerFAB

        val databaseHelper: DatabaseHelper = DatabaseHelper(this.activity)

        val recyclerView: RecyclerView = binding.customerRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)

        customersViewModel = CustomersViewModel(this, databaseHelper.customerArray)
        recyclerView.adapter = customersViewModel

        addCustomerFAB.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_customer)
            dialog.show()

            val customerName = dialog.findViewById<EditText>(R.id.customerNameEditText)
            val customerPhone = dialog.findViewById<EditText>(R.id.phoneNumberEditText)
            val customerLocation = dialog.findViewById<EditText>(R.id.locationEditText)
            val addCustomer = dialog.findViewById<Button>(R.id.addCustomerButton)

            addCustomer.setOnClickListener Dialog@{
                val name = customerName.text.toString().trim { it <= ' ' }
                val phone = customerPhone.text.toString().trim { it <= ' ' }
                val location = customerLocation.text.toString().trim { it <= ' ' }
                val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(context, "The name of the customer cannot be empty", Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@Dialog
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(context, "The customer should provide a phone number", Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@Dialog
                }
                if (TextUtils.isEmpty(location)) {
                    Toast.makeText(context, "The location of the customer should be provided", Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    return@Dialog
                }

                databaseHelper.insertCustomer(name, phone, location, date)
                customersViewModel.updateArray(databaseHelper.customerArray)

                dialog.dismiss()
            }
        }

        addCustomerFAB.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm")
            builder.setMessage("Are you sure you want to delete all customers?")

            builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                databaseHelper.truncateCustomerTable()
                customersViewModel.updateArray(databaseHelper.customerArray)
                recyclerView.adapter = customersViewModel
            }
            builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            builder.show()

            true
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.customer_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val search = menu.findItem(R.id.search_customer)
        val searchView = search.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                customersViewModel.filter.filter(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                customersViewModel.filter.filter(s)
                return false
            }
        })
    }
}