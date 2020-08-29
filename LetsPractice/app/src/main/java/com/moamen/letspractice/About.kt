package com.moamen.letspractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

class About : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                overridePendingTransition(R.anim.slid_in_left,R.anim.slid_out_right)
                return  true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
