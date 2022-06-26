package com.kudu.mappin.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kudu.mappin.R
import com.kudu.mappin.databinding.ActivityImportBinding
import com.kudu.mappin.util.Constants
import java.io.File


@Suppress("DEPRECATION")
class ImportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImportBinding

    private val filePath = "MyFileStorage"
    internal var myExternalFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        binding.btnInternalStorage.setOnClickListener {
            askPermissionForStorage()
        }
    }

    private fun askPermissionForStorage() {
        if (checkPermission()) {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"),
                Constants.READ_STORAGE_PERMISSION_CODE)
        } else {
            requestPermission() // Code for permission
        }


    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            Toast.makeText(this,
                "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.",
                Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE)
        }
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.READ_STORAGE_PERMISSION_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .")
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .")
            }
        }
    }*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //if permission granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .")
            } else {
                //displaying another toast if permission not granted
                Toast.makeText(this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG).show()
                Log.e("value", "Permission Denied, You cannot use local drive .")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
        }
    }


    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarImportActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
        }
        binding.toolbarImportActivity.setNavigationOnClickListener { onBackPressed() }
    }

    //menu
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.import_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //menu items
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_import_from_folder -> {
                Toast.makeText(this, "Folder Import", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_import_from_zip -> {
                Toast.makeText(this, "Zip Import", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}