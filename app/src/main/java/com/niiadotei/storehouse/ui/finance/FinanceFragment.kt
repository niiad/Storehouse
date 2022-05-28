package com.niiadotei.storehouse.ui.finance

import android.os.Bundle

import android.view.*

import android.widget.TextView

import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager

import com.niiadotei.storehouse.R
import com.niiadotei.storehouse.data.DatabaseHelper
import com.niiadotei.storehouse.databinding.FragmentFinanceBinding

import java.text.DecimalFormat

class FinanceFragment : Fragment() {
    private lateinit var financeViewModel: FinanceViewModel
    private lateinit var binding: FragmentFinanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFinanceBinding.inflate(inflater, container, false)

        val databaseHelper = DatabaseHelper(this.activity)

        val accountant = Accountant(requireContext())

        val resources = requireContext().resources
        val locale = resources.configuration.locale
        val currencyInstance = DecimalFormat.getCurrencyInstance(locale)

        val recyclerView = binding.financeProductRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)

        financeViewModel = FinanceViewModel(this, databaseHelper.productArray)
        recyclerView.adapter = financeViewModel

        val currentStockValueTextView: TextView = binding.currentStockValueTextView
        currentStockValueTextView.text = currencyInstance.format(accountant.getCurrentStockValue())

        val mostPricedProductTextView: TextView = binding.mostPricedProductTextView
        mostPricedProductTextView.text = accountant.getMostPricedProductName()

        val leastPricedProductTextView: TextView = binding.leastPricedProductTextView
        leastPricedProductTextView.text = accountant.getLeastPricedProductName()

        val mostProfitableProductTextView: TextView = binding.mostProfitableProductTextView
        mostProfitableProductTextView.text = accountant.getMostProfitableProductName()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.finance_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}