package com.kudu.mappin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kudu.mappin.databinding.ActivityEditPolyBinding

class EditPolyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPolyBinding

//    private var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPolyBinding.inflate(layoutInflater)
        setContentView(binding.root)


       /* val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        binding.btnDatepicker.setOnClickListener {
            DatePickerDialog(this@EditPolyActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }*/

    }

   /* private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDatepicker.text = Editable.Factory.getInstance().newEditable(sdf.format(cal.time))
    }*/
}