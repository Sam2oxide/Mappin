package com.kudu.mappin.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Toast
import com.kudu.mappin.R
import com.kudu.mappin.databinding.ActivityEditPointBinding
import com.kudu.mappin.model.Point
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
            validatePointFeatures()
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

    private fun validatePointFeatures() {
        val point = Point(
            binding.etPointId.text.toString().trim { it <= ' ' },
            //TODO: save geometry JSON
            binding.etOrden.text.toString().trim { it <= ' ' },
            binding.etCodProd.text.toString().trim { it <= ' ' },
            binding.etProv.text.toString().trim { it <= ' ' },
            binding.etDis.text.toString().trim { it <= ' ' },
            binding.etCrg.text.toString().trim { it <= ' ' },
            binding.etPuntoRef.text.toString().trim { it <= ' ' },
            binding.dropdownTipo.text.toString().trim { it <= ' ' },//tipo
            binding.etEmpresa.text.toString().trim { it <= ' ' },
            binding.etProductor.text.toString().trim { it <= ' ' },
            binding.etOtroProd.text.toString().trim { it <= ' ' },
            binding.etClip.text.toString().trim { it <= ' ' },
            binding.etRuc.text.toString().trim { it <= ' ' },
            binding.etContacto.text.toString().trim { it <= ' ' },
            binding.etTelFijo.text.toString().trim { it <= ' ' },
            binding.etCel.text.toString().trim { it <= ' ' },
            binding.etAreaSem.text.toString().trim { it <= ' ' },
            binding.etAreaCos.text.toString().trim { it <= ' ' },
            binding.etFeSiem.text.toString().trim { it <= ' ' }, //fe_siem
            binding.etFeCos.text.toString().trim { it <= ' ' },//fe_cos
            binding.etProduccion.text.toString().trim { it <= ' ' },
            binding.etRedim.text.toString().trim { it <= ' ' },
            binding.dropdownTpo.text.toString().trim { it <= ' ' },//tpo
            binding.etCoa.text.toString().trim { it <= ' ' },
            binding.etCodReg.text.toString().trim { it <= ' ' },
            binding.etZonaReg.text.toString().trim { it <= ' ' },
            binding.etRuta.text.toString().trim { it <= ' ' },
            binding.etRubro.text.toString().trim { it <= ' ' },
            binding.dropdownEstado.text.toString().trim { it <= ' ' },//estado
            binding.etComent.text.toString().trim { it <= ' ' },
            binding.etObserva.text.toString().trim { it <= ' ' },
            binding.dropdownResponsabl.text.toString().trim { it <= ' ' },//responsabl
            binding.etFeAc.text.toString().trim { it <= ' ' },//fe_ac
            binding.dropdownVariedad.text.toString().trim { it <= ' ' },//variedad
        )
    }

    private fun datePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            }

        DatePickerDialog(this@EditPointActivity,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
    }


}