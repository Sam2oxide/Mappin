package com.kudu.mappin.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Toast
import com.kudu.mappin.R
import com.kudu.mappin.databinding.ActivityEditPointBinding
import java.text.SimpleDateFormat
import java.util.*

class EditPointActivity : BaseActivity() {

    private lateinit var binding: ActivityEditPointBinding

    private var cal: Calendar = Calendar.getInstance()
    private val myFormat = "dd/MM/yyyy" // mention the format you need
    private val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPointBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //button save
        binding.btnSavePointFeatures.setOnClickListener {
            //TODO: add and save the feature

//            showProgressDialog(resources.getString(R.string.please_wait))
            Toast.makeText(this, "Add Point Features clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }
        //button delete
        binding.btnDeletePointFeatures.setOnClickListener {

            //TODO: delete the feature

//            showProgressDialog(resources.getString(R.string.please_wait))
            Toast.makeText(this, "Remove Point Features clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }

        //dropdown tipo
        //add these to onResume()
        val tipoItems = resources.getStringArray(R.array.tipo_menu)
        val tipoAdapter = ArrayAdapter(this, R.layout.item_dropdown, tipoItems)
        binding.dropdownTipo.setAdapter(tipoAdapter)

        //dropdown tpo
        val tpoItems = resources.getStringArray(R.array.tpo_menu)
        val tpoAdapter = ArrayAdapter(this, R.layout.item_dropdown, tpoItems)
        binding.dropdownTpo.setAdapter(tpoAdapter)

        //dropdown estado
        val estadoItems = resources.getStringArray(R.array.estado_menu)
        val estadoAdapter = ArrayAdapter(this, R.layout.item_dropdown, estadoItems)
        binding.dropdownEstado.setAdapter(estadoAdapter)

        //dropdown responsabl
        val responsablItems = resources.getStringArray(R.array.responsabl_menu)
        val responsablAdapter = ArrayAdapter(this, R.layout.item_dropdown, responsablItems)
        binding.dropdownResponsabl.setAdapter(responsablAdapter)

        //dropdown variedad
        val variedadItems = resources.getStringArray(R.array.variedad_menu)
        val variedadAdapter = ArrayAdapter(this, R.layout.item_dropdown, variedadItems)
        binding.dropdownVariedad.setAdapter(variedadAdapter)

        //date picker fe_siem
        binding.btnDatepickerFeSiem.setOnClickListener {
            datePicker()
            binding.etFeSiem.text = Editable.Factory.getInstance().newEditable(sdf.format(cal.time))
        }

        //date picker fe_cos
        binding.btnDatepickerFeCos.setOnClickListener {
            datePicker()
            binding.etFeCos.text = Editable.Factory.getInstance().newEditable(sdf.format(cal.time))
        }

        //date picker fe_ac
        binding.btnDatepickerFeAc.setOnClickListener {
            datePicker()
            binding.etFeAc.text = Editable.Factory.getInstance().newEditable(sdf.format(cal.time))
        }
    }

    private fun datePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                /*val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())*/
            }

        DatePickerDialog(this@EditPointActivity,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
    }


}