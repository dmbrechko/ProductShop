package com.example.productshop

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.productshop.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
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
        binding.apply {
            setSupportActionBar(toolbar)
            avatarIV.setImageURI(Uri.parse(product.image))
            nameET.setText(product.name)
            priceET.setText(product.price.toString())
            descET.setText(product.desc)
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

    companion object {
        const val KEY_PRODUCT = "key product"
    }
}