package com.example.jsonappcurrancy

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var spinner: Spinner
    private lateinit var amount: EditText
    private lateinit var convert: Button
    private lateinit var replace: ImageButton
    private lateinit var result: TextView
    private lateinit var from: TextView
    private lateinit var to: TextView
    private lateinit var date: TextView
    var replaced = false
    private var finalAmount: Double = 0.0
    private lateinit var selectedCurrency: String
    private var apiInterface: APIInterface? = null

    private fun setInitViewsValue() {
        // first we get our API Client
        apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

        spinner = findViewById(R.id.spinner_to)
        amount = findViewById(R.id.et_amount)
        convert = findViewById(R.id.btn_convert)
        result = findViewById(R.id.tv_result)
        from = findViewById(R.id.tv_from)
        to = findViewById(R.id.tv_to)
        date = findViewById(R.id.tv_date)
        replace = findViewById(R.id.img_btn_replace)
    }

    private fun setSpinnerValues() {
        spinner.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setInitViewsValue()
        setSpinnerValues()

        apiInterface?.getCurrency()?.enqueue(object : Callback<CuConvert> {
            override fun onResponse(call: Call<CuConvert>, response: Response<CuConvert>) {
                // we use a try block to make sure that our app doesn't crash if the data is incomplete
                try {
                    date.text = response.body()!!.date

                } catch (e: Exception) {
                    Log.e("MAIN", "ISSUE: $e")
                }
            }

            override fun onFailure(call: Call<CuConvert>, t: Throwable) {}
        })

        // BUTTONS handle
        replaceBtnHandle()
        convertBtnHandle()
    }

    @SuppressLint("SetTextI18n")
    private fun replaceBtnHandle() {
        replace.setOnClickListener {
            replaced = !replaced
            if (replaced) {
                from.text = "To"
                to.text = "From"
            } else {
                from.text = "From"
                to.text = "To"
            }
        }
    }

    private fun convertBtnHandle() {
        convert.setOnClickListener {
            if (amount.text.toString() != "") {
                finalAmount = amount.text.toString().toDouble()
            }
            if (replaced)
                fromCurrencyToEur(selectedCurrency = spinner.selectedItem.toString())
            else
                fromEurToCurrency(selectedCurrency = spinner.selectedItem.toString())

        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedCurrency = parent!!.getItemAtPosition(position).toString()

        if (amount.text.toString() != "") {
            finalAmount = amount.text.toString().toDouble()
        }
        if (replaced)
            fromCurrencyToEur(selectedCurrency = selectedCurrency)
        else
            fromEurToCurrency(selectedCurrency = selectedCurrency)
    }
    private fun fromEurToCurrency(selectedCurrency :String){
        // here we use the enqueue callback to make sure that we get the data
        // enqueue gives us async functionality like coroutines, later we will replace this with coroutines
        apiInterface?.getCurrency()?.enqueue(object : Callback<CuConvert> {
            override fun onResponse(call: Call<CuConvert>, response: Response<CuConvert>) {
                // we use a try block to make sure that our app doesn't crash if the data is incomplete
                try {
                    val data = response.body()!!.eur
                    when (selectedCurrency) {
                        "INR" -> {
                            result.text = "${finalAmount * data!!.inr!!}"
                        }
                        "USD" -> {
                            result.text = "${finalAmount * data!!.usd!!}"
                        }
                        "AUD" -> {
                            result.text = "${finalAmount * data!!.aud!!}"
                        }
                        "SAR" -> {
                            result.text = "${finalAmount * data!!.sar!!}"
                        }
                        "CNY" -> {
                            result.text = "${finalAmount * data!!.cny!!}"
                        }
                        "JPY" -> {
                            result.text = "${finalAmount * data!!.jpy!!}"
                        }
                        else -> {
                            result.text = "${finalAmount * 1}"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MAIN", "ISSUE: $e")
                }
            }
            override fun onFailure(call: Call<CuConvert>, t: Throwable) {
                Log.e("MAIN", "Unable to get data")
            }

        })
    }

    private fun fromCurrencyToEur(selectedCurrency :String){
        // here we use the enqueue callback to make sure that we get the data
        // enqueue gives us async functionality like coroutines, later we will replace this with coroutines
        apiInterface?.getCurrency()?.enqueue(object : Callback<CuConvert> {
            override fun onResponse(call: Call<CuConvert>, response: Response<CuConvert>) {
                // we use a try block to make sure that our app doesn't crash if the data is incomplete
                try {
                    val data = response.body()!!.eur
                    when (selectedCurrency) {
                        "INR" -> {
                            result.text = "${finalAmount / data!!.inr!!}"
                        }
                        "USD" -> {
                            result.text = "${finalAmount / data!!.usd!!}"
                        }
                        "AUD" -> {
                            result.text = "${finalAmount / data!!.aud!!}"
                        }
                        "SAR" -> {
                            result.text = "${finalAmount / data!!.sar!!}"
                        }
                        "CNY" -> {
                            result.text = "${finalAmount / data!!.cny!!}"
                        }
                        "JPY" -> {
                            result.text = "${finalAmount / data!!.jpy!!}"
                        }
                        else -> {
                            result.text = "${finalAmount / 1}"
                        }
                    }

                } catch (e: Exception) {
                    Log.e("MAIN", "ISSUE: $e")
                }
            }

            override fun onFailure(call: Call<CuConvert>, t: Throwable) {
                Log.e("MAIN", "Unable to get data")
            }

        })
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Another interface callback
    }
}