package com.lzp.zedittex.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.lzp.zedittex.ZEditText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        edit_1.setOnClickListener {
//            Toast.makeText(this, "edit_1 click", Toast.LENGTH_SHORT).show()
//        }
//        edit_2.setOnClickListener {
//            Toast.makeText(this, "edit_2 click", Toast.LENGTH_SHORT).show()
//        }
//        edit_3.setOnClickListener {
//            Toast.makeText(this, "edit_3 click", Toast.LENGTH_SHORT).show()
//        }

        edit_1.setOnEditCompleteListener(object : ZEditText.OnEditCompleteListener{
            override fun onEditComplete(text: String) {
                Toast.makeText(this@MainActivity, "edit_1 text: $text", Toast.LENGTH_SHORT).show()
            }

        })

        edit_2.setOnEditCompleteListener(object : ZEditText.OnEditCompleteListener{
            override fun onEditComplete(text: String) {
                Toast.makeText(this@MainActivity, "edit_2 text: $text", Toast.LENGTH_SHORT).show()
            }

        })

        edit_3.setOnEditCompleteListener(object : ZEditText.OnEditCompleteListener{
            override fun onEditComplete(text: String) {
                Toast.makeText(this@MainActivity, "edit_3 text: $text", Toast.LENGTH_SHORT).show()
            }

        })

    }
}