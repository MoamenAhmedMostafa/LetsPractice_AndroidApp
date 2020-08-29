package com.moamen.letspractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.moamen.icpcassuit.Connection_Network
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        forgot_send.setOnClickListener{
            val e= Connection_Network(this)
            if(e.Isconnected()) {
                forget_wait.visibility = View.VISIBLE
                FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(forgot_Email.text.trim().toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Sent... check your gmail", Toast.LENGTH_LONG)
                                .show()
                            forgot_Email.setText("")
                            forget_wait.visibility = View.GONE
                        } else {
                            forget_wait.visibility = View.GONE
                            Toast.makeText(this, it.exception!!.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
            else {
                Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show()
            }
        }
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
