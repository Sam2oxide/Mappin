package com.kudu.mappin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kudu.mappin.databinding.ActivitySettingsBinding
import java.io.File


@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private var file: File? = null
    private var cacheFile: File? = null
    private var isEditing = false
    private var errorMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
// val filePath = intent.extras!!.getString("filepath")
// val mimeType = intent.extras!!.getString("filetype")
// (intent
// .extras!!
// .getString("fileediting")
// .compareTo("true") == 0).also { isEditing = it }
// Log.d(TAG,
// "Received filepath: $filePath and mimeType: $mimeType")
//
// file = File(filePath!!)
// cacheFile = File(cacheDir, file!!.name)
//
// // copy file to a temporary file
//
// // copy file to a temporary file
// try {
// copyFile(file!!, cacheFile!!)
// } catch (e: IOException) {
// Log.d(TAG, e.message!!)
// finish()
// }
//
// val contentUri: Uri =
// if (Build.VERSION.SDK_INT < 24) Uri.fromFile(file) else FileProvider.getUriForFile(
// this, BuildConfig.APPLICATION_ID + ".fileprovider",
// cacheFile!!)
//
// Log.d(TAG, "content URI: $contentUri")
// Log.d(TAG, if (isEditing) "call ACTION_EDIT intent" else "call ACTION_VIEW intent")
// val intent = Intent(if (isEditing) Intent.ACTION_EDIT else Intent.ACTION_VIEW)
// if (isEditing) {
// intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or
// Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
// if (mimeType!!.contains("image/")) {
// intent.setDataAndType(contentUri, "image/*")
// } else {
// intent.setDataAndType(contentUri, mimeType)
// }
// intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
// } else {
// intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
// intent.setDataAndType(contentUri, mimeType)
// }
// try {
// startActivityForResult(intent, 102)
// } catch (e: IllegalArgumentException) {
// Log.d(TAG, e.message!!)
// errorMessage = e.message
// } catch (e: Exception) {
// Log.d(TAG, e.message!!)
// errorMessage = e.message
// }
// }
//
// override fun onActivityResult(
// requestCode: Int, resultCode: Int,
// data: Intent,
// ) {
// super.onActivityResult(requestCode, resultCode, data)
// // on ACTION_VIEW back key pressed it returns RESULT_CANCEL - on error
// // as well
// if (resultCode == RESULT_OK) {
// try {
// if (isEditing) {
// Log.d(TAG, "Copy file back from uri " +
// data.dataString +
// " to file: " + file!!.absolutePath)
// val `in`: InputStream? = contentResolver.openInputStream(data.data!!)
// val out: OutputStream = FileOutputStream(file)
// // Transfer bytes from in to out
// val buf = ByteArray(1024)
// var len: Int
// while (`in`!!.read(buf).also { len = it } > 0) {
// out.write(buf, 0, len)
// }
// out.close()
// }
// val intent = this.intent
// setResult(RESULT_OK, intent)
// } catch (e: SecurityException) {
// val intent = this.intent
// intent.putExtra("ERROR_MESSAGE", e.message)
// setResult(RESULT_CANCELED, intent)
// } catch (e: IOException) {
// val intent = this.intent
// intent.putExtra("ERROR_MESSAGE", e.message)
// setResult(RESULT_CANCELED, intent)
// }
// } else {
// val intent = this.intent
// intent.putExtra("ERROR_MESSAGE", errorMessage)
// setResult(RESULT_CANCELED, intent)
// }
// finish()
// }
//
// @Throws(IOException::class)
// private fun copyFile(src: File, dst: File) {
// Log.d(TAG, "Copy file: " + src.absolutePath +
// " to file: " + dst.absolutePath)
// FileInputStream(src).use { `in` ->
// FileOutputStream(dst).use { out ->
// // Transfer bytes from in to out
// val buf = ByteArray(1024)
// var len: Int
// while (`in`.read(buf).also { len = it } > 0) {
// out.write(buf, 0, len)
// }
// out.close()
// }
// }
// }
// }