package com.example.productshop

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.productshop.databinding.ActivityShopBinding
import com.example.productshop.databinding.ListItemBinding
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class ShopActivity : AppCompatActivity() {
    private val products = mutableListOf<Product>()
    private lateinit var binding: ActivityShopBinding
    private var bitmap: Uri? = null
    private lateinit var adapter: ArrayAdapter<Product>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = object : ArrayAdapter<Product> (this, R.layout.list_item, products) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var view = convertView
                if (view == null) {
                    val binding = ListItemBinding.inflate(LayoutInflater.from(context), parent, false)
                    view = binding.root
                    view.tag = binding
                }
                val binding = view.tag as ListItemBinding
                binding.apply {
                    val product = getItem(position)
                    product?.let {
                        avatarIV.setImageURI(Uri.parse(product.image))
                        nameTV.text = product.name
                        priceTV.text = product.price.toString()
                    }
                }
                return view
            }
        }
        binding.apply {
            setSupportActionBar(toolbar)
            listLV.adapter = adapter
            listLV.setOnItemClickListener { adapterView, view, position, id ->
                val intent = Intent(this@ShopActivity, DetailsActivity::class.java).apply {
                    putExtra(DetailsActivity.KEY_PRODUCT, adapterView.adapter.getItem(position) as Product)
                }
                startActivity(intent)
            }
            avatarIV.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), REQUEST_CODE_GALLERY)
            }
            addBTN.setOnClickListener {
                try {
                    if (nameET.text.isBlank()) {
                        makeToast(R.string.name_is_required)
                        return@setOnClickListener
                    }
                    if (descET.text.isBlank()) {
                        makeToast(R.string.description_is_required)
                        return@setOnClickListener
                    }

                    val product = Product(
                        nameET.text.toString(),
                        priceET.text.toString().toInt(),
                        descET.text.toString(),
                        bitmap!!.toString()
                    )
                    products.add(product)
                    adapter.notifyDataSetChanged()
                    clear()
                } catch (e: NullPointerException) {
                    makeToast(R.string.image_is_required)
                } catch (e: NumberFormatException) {
                    makeToast(R.string.enter_valid_price)
                }
            }
        }
    }

    fun clear() {
        binding.apply {
            avatarIV.setImageResource(R.drawable.avatar)
            bitmap = null
            nameET.text.clear()
            priceET.text.clear()
            descET.text.clear()
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
        const val REQUEST_CODE_GALLERY = 31 * 17
    }
}

