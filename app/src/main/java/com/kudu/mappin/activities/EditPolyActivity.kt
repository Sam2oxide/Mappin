package com.kudu.mappin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kudu.mappin.dao.Db
import com.kudu.mappin.databinding.ActivityEditPolyBinding
import com.kudu.mappin.model.Polygon


class EditPolyActivity : BaseActivity() {

    private lateinit var binding: ActivityEditPolyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPolyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Db()
//        val db = Database()

        //button add
        binding.btnSavePolyFeatures.setOnClickListener {
            //TODO: save the feature

            validatePolyFeatures()
//            showProgressDialog(resources.getString(R.string.please_wait))
            Toast.makeText(this, "Add Poly Features clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }
        //button delete
        binding.btnDeletePolyFeatures.setOnClickListener {
            //TODO: delete the feature

//            showProgressDialog(resources.getString(R.string.please_wait))
            Toast.makeText(this, "Remove Poly Features clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }


    }

    private fun validatePolyFeatures() {
        val polygon = Polygon(
            binding.etPolyId.text.toString().trim { it <= ' ' },
            //TODO: save geometry JSON
            binding.etNombre.text.toString().trim { it <= ' ' },
            binding.etComentario.text.toString().trim { it <= ' ' }
        )

       /* val polygonObject = net.postgis.jdbc.geometry.Polygon(

        )*/

    }

}