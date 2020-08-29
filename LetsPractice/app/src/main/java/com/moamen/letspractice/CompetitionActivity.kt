package com.moamen.letspractice

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_competition.*

class CompetitionActivity : AppCompatActivity() {

    lateinit var mAdView : AdView
    lateinit var time:CountDownTimer
    var ok1:Boolean=false
    var ok2:Boolean=false
    var myAuth= FirebaseAuth.getInstance()
    lateinit var progressionDialog:ProgressDialog
    lateinit var i:Intent
    var rem_time:Long = 0
    var dp:DataPractice?=null
    var user:User?=null
    val userDataBase= FirebaseFirestore.getInstance().collection("Users");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competition)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if(myAuth.currentUser!=null) {
            FirebaseFirestore.getInstance().collection("Register")
                .whereEqualTo("id", myAuth.uid.toString())
                .limit(1).get().addOnCompleteListener {
                            ok1 =  (!it.getResult()!!.isEmpty)
                }
            FirebaseFirestore.getInstance().collection("CompetitionUser").whereEqualTo("id",myAuth.uid.toString())
                .limit(1).get().addOnCompleteListener{
                            ok2 =  (it.getResult()!!.isEmpty)
                }
        }
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object: AdListener() {
            override fun onAdClosed() {
                mAdView.loadAd(adRequest)
            }
            override fun onAdClicked() {
                mAdView.loadAd(adRequest)
            }
            override fun onAdOpened() {
                mAdView.loadAd(adRequest)
            }
        }
        if(myAuth.currentUser!=null) {
            userDataBase.document(myAuth.currentUser!!.uid.toString()).get().addOnSuccessListener {
                user = it.toObject(User::class.java)!!
            }
        }
        i = Intent(this, StartPractice::class.java)
        rem_time=-1

        val pd = ProgressDialog(this)
        pd.setMessage("Loading.....")
        pd.setCancelable(false)
        pd.show()
        Handler().postDelayed({
            pd.dismiss()
            load_page()
            start_comp()
        },1000)

         progressionDialog = ProgressDialog(this)
        progressionDialog.setMessage("Loading.....")
        progressionDialog.setCancelable(false)

        comp_refresh.setOnRefreshListener {
            load_page()
            Handler().postDelayed(object:Runnable {
                override fun run() {
                    comp_refresh.isRefreshing=false
                }
            },900)
        }
        val i2=Intent(this,Standing::class.java)
        if(comp_register.isVisible){
            if(ok1){
                comp_register.text = "registered"
            }
        }
        comp_register.setOnClickListener {
            if (myAuth.uid == null) {
                Toast.makeText(this,"You're a guest, Login please...",Toast.LENGTH_SHORT).show()
            }
            else {
                val to=Toast.makeText(this,"You can't enter",Toast.LENGTH_SHORT)
                val chick= object : CountDownTimer(500,1000) {
                    override fun onFinish() {
                        if (comp_register.text.toString() == "registered") {
                            unRegisterFun()
                            comp_register.text = "register"
                            ok1=false
                        } else if(comp_register.text.toString() == "register") {
                            comp_register.text = "registered"
                            RegisterFun()
                            ok1=true
                        }
                        else if(comp_register.text.toString() == "standing"){
                            i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(i2)
                        }
                        else {

                            if(ok1 && ok2 &&Timestamp.now().seconds*1000-rem_time<=10*60*1000-5){
                                competition_load()
                                progressionDialog.show()
                                Handler().postDelayed({
                                    progressionDialog.dismiss()
                                   },5000)
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.putExtra("name", "comp")
                                i.putExtra("time",(10*60*1000-(Timestamp.now().seconds*1000-rem_time-5)).toString())
                                startActivity(i)
                                //finish()

                            }
                            else {
                                to.show()
                            }
                        }
                    }
                    override fun onTick(millisUntilFinished: Long) {
                    }
                }
                chick.start()
            }
        }
    }
    fun load_page(){
        FirebaseFirestore.getInstance().collection("Practice").document("competitions")
            .collection("hard").addSnapshotListener { q, fire_x ->
                q!!.documents.forEach { P ->
                    dp=P.toObject(DataPractice::class.java)!!
                }
                comp_not_avaliable.visibility=View.VISIBLE
                if(dp!=null){
                    comp_register_number.text=dp!!.number_of_people.toString()
                    comp_timer.visibility=View.VISIBLE
                    comp_register_number.visibility=View.VISIBLE
                    imageView2.visibility=View.VISIBLE
                    comp_register.visibility=View.VISIBLE
                    comp_not_avaliable.visibility=View.INVISIBLE
                   if(ok1) comp_register.text = "registered"
                }
            }

    }
    fun start_comp(){
        FirebaseFirestore.getInstance().collection("Competitions")
            .document("time").get().addOnSuccessListener {
                val time_comp = it.toObject(CompetitionTime::class.java)
                if(time_comp!=null){
                    rem_time=time_comp.time.seconds*1000;
                }
            }
        val start_time_after_one= object : CountDownTimer(1000,1000){
            override fun onFinish() {
                if (dp != null) {
                    if (rem_time - Timestamp.now().seconds * 1000 > 0) {
                        time = object :
                            CountDownTimer(rem_time - Timestamp.now().seconds * 1000, 1000) {
                            override fun onFinish() {
                                if (comp_timer.isVisible) comp_register.text = "open"
                                competition_load()
                                progressionDialog.show()
                                Handler().postDelayed({
                                    progressionDialog.dismiss()
                                }, 3000)
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.putExtra("name", "comp")
                                i.putExtra("time", (10 * 60 * 1000).toString())
                                startActivity(i)
                            }

                            override fun onTick(millisUntilFinished: Long) {
                                var t = millisUntilFinished / 1000;
                                var hurs: String = (t / (60 * 60)).toString()
                                t %= (60 * 60)
                                var mints: String = (t / 60).toString()
                                t %= 60
                                var sec: String = (t).toString()
                                if (hurs.length < 2) hurs = "0" + hurs
                                if (mints.length < 2) mints = "0" + mints
                                if (sec.length < 2) sec = "0" + sec
                                if (comp_timer.isVisible) comp_timer.text =
                                    hurs + ":" + mints + ":" + sec
                            }
                        }
                        time.start()
                    } else if ((rem_time - Timestamp.now().seconds * 1000) * -1 <= 10 * 60 * 1000) {
                        if (comp_timer.isVisible) {
                            comp_register.text = "open"
                            comp_timer.text = "Running"
                        }
                    } else {
                        if (comp_timer.isVisible) {
                            comp_timer.text = "Finished"
                            comp_register.text = "standing"
                        }
                    }
                }
            }
            override fun onTick(millisUntilFinished: Long) {
            }
        }
        start_time_after_one.start()
    }
    private fun RegisterFun() {
        if(dp!=null) {
            dp!!.number_of_people++;
            comp_register_number.text=dp!!.number_of_people.toString()
            val new_prac = mutableMapOf<String, Any>()
            new_prac["number_of_people"] = dp!!.number_of_people
            FirebaseFirestore.getInstance().collection("Practice").document("competitions")
                .collection("hard").document(dp!!.id.toString()).update(new_prac)
            FirebaseFirestore.getInstance().collection("Register").document(myAuth.uid.toString())
                .set(user!!)
        }
    }
    private fun unRegisterFun() {
        if(dp!=null) {
            dp!!.number_of_people--;
            comp_register_number.text=dp!!.number_of_people.toString()
            val new_prac = mutableMapOf<String, Any>()
            new_prac["number_of_people"] = dp!!.number_of_people
            FirebaseFirestore.getInstance().collection("Practice").document("competitions")
                .collection("hard").document(dp!!.id.toString()).update(new_prac)
            FirebaseFirestore.getInstance().collection("Register").document(myAuth.uid.toString()).delete()

        }
    }

    fun competition_load(){
        FirebaseFirestore.getInstance().collection(dp!!.path_of_questions)
            .addSnapshotListener { q, fire_x ->
                var id = 0
                q!!.documents.forEach { P ->
                    if (P != null) {
                        val p = P.toObject(DataQuestion::class.java)
                        load(id, p!!)
                        id++
                    }
                }
            }
    }
    fun load(id:Int,data:DataQuestion){
        StartPractice.dataQuestions[id]= data
    }

    override fun onDestroy() {
        if(comp_register.isVisible) {
            if (comp_register.text == "register" || comp_register.text == "registered")
                time.cancel()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if(comp_register.isVisible){
            if(comp_register.text=="register"||comp_register.text=="registered")
                time.cancel()
        }

        finish()
        overridePendingTransition(R.anim.slid_in_left,R.anim.slid_out_right)
        super.onBackPressed()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                if(comp_register.text=="register"||comp_register.text=="registered")
                    time.cancel()
                finish()
                overridePendingTransition(R.anim.slid_in_left,R.anim.slid_out_right)
                return  true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
