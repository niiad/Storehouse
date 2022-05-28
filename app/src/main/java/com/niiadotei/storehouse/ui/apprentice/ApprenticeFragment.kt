package com.niiadotei.storehouse.ui.apprentice

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.DatePicker
import android.widget.TextView

import androidx.fragment.app.Fragment

import com.niiadotei.storehouse.data.DatabaseHelper
import com.niiadotei.storehouse.databinding.FragmentApprenticeBinding

import java.text.DecimalFormat
import java.text.SimpleDateFormat

import java.util.*

class ApprenticeFragment : Fragment() {
    private lateinit var binding: FragmentApprenticeBinding

    var databaseHelper: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentApprenticeBinding.inflate(inflater, container, false)

        val resources = this.resources
        val locale = resources.configuration.locale
        val currencyInstance = DecimalFormat.getCurrencyInstance(locale)

        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        val pastSalesMadeTextView: TextView = binding.pastSalesMadeTextView
        val pastSalesQuantityTextView: TextView = binding.pastSalesQuantityTextView
        val pastSalesNumberTextView: TextView = binding.pastSalesNumberTextView
        val pastCustomerNumberTextView: TextView = binding.pastCustomerNumberTextView
        val dateOfPastSalesTextView: TextView = binding.dateOfPastSales
        val salesPerformanceValueTextView: TextView = binding.salesPerformanceValue
        val salesPerformancePercentTextView: TextView = binding.salesPerformancePercent
        val differenceInQuantityTextView: TextView = binding.differenceInQuantitySold

        databaseHelper = DatabaseHelper(this.activity)

        val apprenticeRetriever = ApprenticeRetriever(date, requireContext())

        val salesDateButton = binding.salesDateButton
        val totalSalesMadeTextView: TextView = binding.salesMadeTextView

        val totalSalesMade = apprenticeRetriever.getTotalSalesMadeToday()
        totalSalesMadeTextView.text = currencyInstance.format(totalSalesMade)

        val salesQuantityTextView: TextView = binding.salesQuantityTextView
        salesQuantityTextView.text = apprenticeRetriever.getTotalQuantitySoldToday().toString()

        val salesNumberTextView: TextView = binding.salesNumberTextView
        salesNumberTextView.text = apprenticeRetriever.getTotalNumberOfSalesToday().toString()

        val customerNumberTextView: TextView = binding.customerNumberTextView
        customerNumberTextView.text = apprenticeRetriever.getNumberOfNewCustomersToday().toString()

        val dateSetListener = OnDateSetListener {
                _: DatePicker?, year: Int, month: Int, day: Int ->

                val calendar = Calendar.getInstance()
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = day

                val pastDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
                val apprenticeRetrieverPast = ApprenticeRetriever(pastDate, requireContext())

                val dateOfPastSales = "Date of Sales: $pastDate"
                dateOfPastSalesTextView.text = dateOfPastSales

                val pastTotalSalesMade = apprenticeRetrieverPast.getTotalSalesMadeToday()
                pastSalesMadeTextView.text = currencyInstance.format(pastTotalSalesMade)

                pastSalesQuantityTextView.text = apprenticeRetrieverPast.getTotalQuantitySoldToday().toString()

                pastSalesNumberTextView.text = apprenticeRetrieverPast.getTotalNumberOfSalesToday().toString()

                pastCustomerNumberTextView.text = apprenticeRetrieverPast.getNumberOfNewCustomersToday().toString()

                val salesPerformanceValue = totalSalesMade - pastTotalSalesMade
                salesPerformanceValueTextView.text = currencyInstance.format(salesPerformanceValue)

                val decimalFormat = DecimalFormat("#.##")

                var salesPerformancePercent = 0.0
                if (totalSalesMade != 0.0) {
                    salesPerformancePercent =
                        decimalFormat.format((totalSalesMade - pastTotalSalesMade) / totalSalesMade * 100).toDouble()
                }

                if (java.lang.Double.isNaN(salesPerformancePercent)) {
                    salesPerformancePercent = 0.0
                }

                val salesPerformancePercentString = String.format(Locale.US, "%.0f%%", salesPerformancePercent)
                salesPerformancePercentTextView.text = salesPerformancePercentString

                val differenceInQuantity = apprenticeRetriever.getTotalQuantitySoldToday() - apprenticeRetrieverPast.getTotalQuantitySoldToday()
                differenceInQuantityTextView.text = differenceInQuantity.toString()
        }

        salesDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(requireActivity(), dateSetListener, year, month, day)
            datePickerDialog.show()
        }

        return binding.root
    }

}