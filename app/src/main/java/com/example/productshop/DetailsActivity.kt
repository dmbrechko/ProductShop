package com.example.productshop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.productshop.ShopActivity.Companion.REQUEST_CODE_GALLERY
import com.example.productshop.databinding.ActivityDetailsBinding
import java.io.IOException
import java.io.InputStream

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var bitmap: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val product = intent.getParcelableCompat<Product>(KEY_PRODUCT) ?: throw IllegalArgumentException("No product supplied")
        val position = intent.getIntExtra(KEY_POSITION, 0)
        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            avatarIV.setImageURI(Uri.parse(product.image))
            bitmap = Uri.parse(product.image)
            nameET.setText(product.name)
            priceET.setText(product.price.toString())
            descET.setText(product.desc)
            avatarIV.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), REQUEST_CODE_GALLERY)
            }
            saveBTN.setOnClickListener {
                if (nameET.text.isBlank() || priceET.text.isBlank() || descET.text.isBlank()) {
                    makeToast(R.string.fill_all_fields)
                    return@setOnClickListener
                }
                try {
                    val intent = Intent().apply {
                        putExtra(ShopActivity.KEY_SAVED_PRODUCT,
                            Product(
                            nameET.text.toString(),
                                priceET.text.toString().toInt(),
                                descET.text.toString(),
                                bitmap?.toString()!!
                        ))
                        putExtra(ShopActivity.KEY_SAVED_POSITION, position)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                } catch (e: NullPointerException) {
                    makeToast(R.string.image_is_required)
                } catch (e: NumberFormatException) {
                    makeToast(R.string.enter_valid_price)
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_shop_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_exit -> {
                moveTaskToBack(true)
                finish()
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_GALLERY -> if (resultCode == RESULT_OK) {
                val selectedImage: Uri? = data?.data
                var checkImageStream: InputStream? = null
                try {
                    checkImageStream = contentResolver.openInputStream(selectedImage ?: throw IOException()) ?: throw IOException()
                    bitmap = selectedImage
                    binding.avatarIV.setImageURI(bitmap)
                } catch (e: IOException) {
                    makeToast(R.string.error_loading_image)
                } finally {
                    checkImageStream?.close()
                }
            }
        }
    }

    companion object {
        const val KEY_PRODUCT = "key product"
        const val KEY_POSITION = "key position"
    }
}