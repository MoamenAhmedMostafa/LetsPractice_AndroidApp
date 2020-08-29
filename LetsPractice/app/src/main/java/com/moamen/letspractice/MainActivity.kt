package com.moamen.letspractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.moamen.icpcassuit.Connection_Network
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var myAuth= FirebaseAuth.getInstance()
    lateinit var email:String;
    lateinit var pass:String;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(myAuth.currentUser!=null){
            var i = Intent(this, MainPage::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)
            Toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show()
            finish()
        }
        guest.setOnClickListener{
            var i = Intent(this, MainPage::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)
            Toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show()
            finish()
        }
        forget_btn.setOnClickListener{
            var intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)

        }
        Register_newAccount.setOnClickListener{
            var intent = Intent(this, Register::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)

        }
        Loginbtn.setOnClickListener {
            email = Input_Email.text.trim().toString();
            pass = Input_Password.text.trim().toString();
            if (email.isEmpty()) Input_Email.setError("Not allow empty")
            if (email.isEmpty()) Input_Password.setError("Not allow empty")
            val e = Connection_Network(this)
            if (e.Isconnected()) {
                if (!email.isEmpty() && !pass.isEmpty()) {
                    login_wait.visibility = View.VISIBLE
                    myAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, { task ->
                            if (task.isSuccessful) {
                                login_wait.visibility = View.GONE
                                var i = Intent(this, MainPage::class.java)
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(i)
                                Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                login_wait.visibility = View.GONE
                                Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                }
            }
            else {
                Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
