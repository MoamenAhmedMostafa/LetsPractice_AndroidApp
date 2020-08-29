package com.moamen.letspractice

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_start_practice.*
import kotlinx.android.synthetic.main.practice_question.view.*

class StartPractice : AppCompatActivity() {
    lateinit var mAdView : AdView
    var clicked:Boolean=true

    companion object{
        var user_answers = arrayOf("","","","","","","","","","")
        var dataQuestions = arrayOf(DataQuestion(),DataQuestion(),DataQuestion(),DataQuestion(),DataQuestion()
            ,DataQuestion(),DataQuestion(),DataQuestion(),DataQuestion(),DataQuestion())
        var number_question:Int=1
        var correct:Int=0
        var score:Int=0
    }
     var practice_name:String=""
    var myAuth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_practice)
        practice_refresh.setOnRefreshListener {
            if(number_question<=10){
                getNext();
            }
            Handler().postDelayed(object:Runnable {
                override fun run() {
                    practice_refresh.isRefreshing=false
                }
            },900)
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
        practice_name=intent.getStringExtra("name")
        var duration:Long=intent.getStringExtra("time").toLong()
        number_question=1
        getNext()

        var mediaPlayer:MediaPlayer?=MediaPlayer.create(this,R.raw.tic)
        val time= object :CountDownTimer(duration,1000){
            override fun onFinish() {
                mediaPlayer?.stop()
                clicked=false
                finished()
            }
            override fun onTick(millisUntilFinished: Long) {
                var mints:String=(millisUntilFinished/1000/60).toString()
                var sec:String=((millisUntilFinished/1000)%60).toString()
                if(mints.length<2)mints="0"+mints
                if(sec.length<2)sec="0"+sec
                if(mints=="00"&&sec=="30"){
                    TimerView.setBackgroundResource(R.color.red)
                }
                if(millisUntilFinished/1000 == 11.toLong()){
                    mediaPlayer?.start()
                }
                TimerView.text=mints+":"+sec
            }
        }
        time.start()
        practice_next.setOnClickListener{
            if( practice_wait.visibility!=View.VISIBLE) {
                if (number_question < 10) {
                    number_question++
                    getNext()
                    if(number_question==10){
                        practice_next.text="Result"
                    }
                }
                else {
                    if (clicked) {
                        clicked = false
                        time.cancel()
                        finished()
                    }
                }
            }
            practice_number_q.text=number_question.toString() + "/10"
        }
        practice_finish.setOnClickListener{
            if(clicked) {
                clicked=false
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure?")
                builder.setPositiveButton("YES") { dialog, which ->
                    clicked=false
                    time.cancel()
                    finished()
                }
                builder.setNegativeButton("NO") { dialog, which ->
                    clicked=true
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
        practice_ans1.setOnClickListener{
            if( practice_wait.visibility!=View.VISIBLE) {
                practice_ans1.setBackgroundResource(R.drawable.chois_ans)
                practice_ans2.setBackgroundResource(R.drawable.border_view)
                practice_ans3.setBackgroundResource(R.drawable.border_view)
                practice_ans4.setBackgroundResource(R.drawable.border_view)
                user_answers[number_question - 1] = practice_ans1.text.toString()
            }
        }
        practice_ans2.setOnClickListener{
            if( practice_wait.visibility!=View.VISIBLE) {
                practice_ans2.setBackgroundResource(R.drawable.chois_ans)
                practice_ans1.setBackgroundResource(R.drawable.border_view)
                practice_ans3.setBackgroundResource(R.drawable.border_view)
                practice_ans4.setBackgroundResource(R.drawable.border_view)
                user_answers[number_question - 1] = practice_ans2.text.toString()
            }
        }
        practice_ans3.setOnClickListener{
            if( practice_wait.visibility!=View.VISIBLE) {
                practice_ans3.setBackgroundResource(R.drawable.chois_ans)
                practice_ans1.setBackgroundResource(R.drawable.border_view)
                practice_ans2.setBackgroundResource(R.drawable.border_view)
                practice_ans4.setBackgroundResource(R.drawable.border_view)
                user_answers[number_question - 1] = practice_ans3.text.toString()
            }
        }
        practice_ans4.setOnClickListener{
            if( practice_wait.visibility!=View.VISIBLE) {
                practice_ans4.setBackgroundResource(R.drawable.chois_ans)
                practice_ans1.setBackgroundResource(R.drawable.border_view)
                practice_ans2.setBackgroundResource(R.drawable.border_view)
                practice_ans3.setBackgroundResource(R.drawable.border_view)
                user_answers[number_question - 1] = practice_ans4.text.toString()
            }
        }
    }

    fun getNext(){
        practice_ans1.visibility=View.VISIBLE
        practice_ans2.visibility=View.VISIBLE
        practice_ans3.visibility=View.VISIBLE
        practice_ans4.visibility=View.VISIBLE
        practice_ans1.setBackgroundResource(R.drawable.border_view)
        practice_ans2.setBackgroundResource(R.drawable.border_view)
        practice_ans3.setBackgroundResource(R.drawable.border_view)
        practice_ans4.setBackgroundResource(R.drawable.border_view)
        practice_q.text=dataQuestions[number_question-1].Q
        if(dataQuestions[number_question-1].ans1=="") practice_ans1.visibility=View.INVISIBLE
        if(dataQuestions[number_question-1].ans2=="") practice_ans2.visibility=View.INVISIBLE
        if(dataQuestions[number_question-1].ans3=="") practice_ans3.visibility=View.INVISIBLE
        if(dataQuestions[number_question-1].ans4=="") practice_ans4.visibility=View.INVISIBLE

        practice_ans1.text=dataQuestions[number_question-1].ans1
        practice_ans2.text=dataQuestions[number_question-1].ans2
        practice_ans3.text=dataQuestions[number_question-1].ans3
        practice_ans4.text=dataQuestions[number_question-1].ans4
    }

    fun finished(){
        practice_wait.visibility=View.VISIBLE
        var idx:Int=0
        while(idx<10){
            if(user_answers[idx]== dataQuestions[idx].Correct_ans){
                correct++
                score+= dataQuestions[idx].score
            }
            idx++
        }
        if(practice_name!="comp" && myAuth.uid!=null)
          Check()
        var user_score:Long=0
        var user_numbers:Int=0
        var U_comp:User=User()
        if((myAuth.uid!=null)) {
            FirebaseFirestore.getInstance().collection("Users").document(myAuth.uid.toString())
                .get()
                .addOnSuccessListener {
                    val u = it.toObject(User::class.java)
                    if (u != null) {
                        U_comp=u;
                        user_score = u?.score
                        user_numbers = u?.n_of_quiz
                    }
                }
        }
        var i = Intent(this,PracticeResulte::class.java)
        var i2=Intent(this,MainPage::class.java)

        val time1= object :CountDownTimer(3000,3000){
            override fun onFinish() {
                practice_wait.visibility=View.GONE
                if(practice_name!="" && practice_name!="comp" &&myAuth.uid!=null){
                    FirebaseFirestore.getInstance().collection("UsersPractices").document(myAuth.uid.toString())
                        .collection("Finished").add(DataPracticeFinished(practice_name))
                    val upd_score = mutableMapOf<String, Any>()
                    upd_score["score"]=user_score+score
                    upd_score["n_of_quiz"]=user_numbers+1
                    FirebaseFirestore.getInstance().collection("Users")
                        .document(myAuth.currentUser!!.uid.toString()).update(upd_score)
                }
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                if(practice_name=="comp") {
                    // update user rank
                    //U_comp
                    U_comp.score= score.toLong()
                    FirebaseFirestore.getInstance().collection("CompetitionUser").document(U_comp.id).set(U_comp).addOnCompleteListener{
                    }
                    startActivity(i2)
                }
                else
                    startActivity(i)
                finish()
                overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)

            }
            override fun onTick(millisUntilFinished: Long) {
            }
        }
        time1.start()
    }
    fun Check(){
        if((myAuth.uid!=null)) {
            FirebaseFirestore.getInstance().collection("UsersPractices")
                .document(myAuth.uid.toString())
                .collection("Finished")
                .addSnapshotListener { q, fire_x ->
                    q!!.documents.forEach { P ->
                        if (P != null) {
                            val p = P.toObject(DataPracticeFinished::class.java)
                            if (p?.name == practice_name) {
                                practice_name = ""

                            }
                        }
                    }
                }
        }
        else {
            practice_name = ""
        }
    }
    override fun onBackPressed() {
    }
    override fun onDestroy() {
        if(clicked){
            finished()
        }
        super.onDestroy()
    }
}
