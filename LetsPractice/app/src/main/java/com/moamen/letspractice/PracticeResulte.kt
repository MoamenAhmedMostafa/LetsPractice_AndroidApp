package com.moamen.letspractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_practice_resulte.*

class PracticeResulte : AppCompatActivity() {
    var id:Int=0
    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_resulte)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
        resutle_score.text="Score= "+StartPractice.score.toString()
        resulte_solve.text="Correct: " + StartPractice.correct.toString() + " of 10"
        getAns()
        resulte_finish.setOnClickListener{
            var i = Intent(this,MainPage::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)
            finish()
        }
        resulte_next.setOnClickListener{
            if(id+1<10){
                id++
                getAns()
            }
        }
        resulte_previos.setOnClickListener{
            if(id-1>=0){
                id--
                getAns()
            }
        }
    }
    fun getAns(){
        ans1.visibility= View.VISIBLE
        ans2.visibility= View.VISIBLE
        ans3.visibility= View.VISIBLE
        ans4.visibility= View.VISIBLE
        resulte_number_q.text=(id+1).toString()+"/10"
        resulte_q.text=StartPractice.dataQuestions[id].Q
        ans1.text=StartPractice.dataQuestions[id].ans1
        ans2.text=StartPractice.dataQuestions[id].ans2
        ans3.text=StartPractice.dataQuestions[id].ans3
        ans4.text=StartPractice.dataQuestions[id].ans4
        ans1.setBackgroundResource(R.drawable.border_view)
        ans2.setBackgroundResource(R.drawable.border_view)
        ans3.setBackgroundResource(R.drawable.border_view)
        ans4.setBackgroundResource(R.drawable.border_view)
        if(ans1.text=="")
            ans1.visibility= View.INVISIBLE
        if(ans2.text=="")
            ans2.visibility= View.INVISIBLE
        if(ans3.text=="")
            ans3.visibility= View.INVISIBLE
        if(ans4.text=="")
            ans4.visibility= View.INVISIBLE
        if(ans1.text==StartPractice.user_answers[id])
            ans1.setBackgroundResource(R.drawable.wrong_ans)
        else if(ans2.text==StartPractice.user_answers[id])
            ans2.setBackgroundResource(R.drawable.wrong_ans)
        else if(ans3.text==StartPractice.user_answers[id])
            ans3.setBackgroundResource(R.drawable.wrong_ans)
        else if(ans4.text==StartPractice.user_answers[id])
            ans4.setBackgroundResource(R.drawable.wrong_ans)

        if(ans1.text==StartPractice.dataQuestions[id].Correct_ans)
            ans1.setBackgroundResource(R.drawable.correct_ans)
        else if(ans2.text==StartPractice.dataQuestions[id].Correct_ans)
            ans2.setBackgroundResource(R.drawable.correct_ans)
        else if(ans3.text==StartPractice.dataQuestions[id].Correct_ans)
            ans3.setBackgroundResource(R.drawable.correct_ans)
        else if(ans4.text==StartPractice.dataQuestions[id].Correct_ans)
            ans4.setBackgroundResource(R.drawable.correct_ans)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                var i = Intent(this,MainPage::class.java)
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
                overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)
                finish()
                return  true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        var i = Intent(this,MainPage::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)
        finish()
    }
}
