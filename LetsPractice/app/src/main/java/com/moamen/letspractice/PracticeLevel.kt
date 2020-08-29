package com.moamen.letspractice

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_practice_level.*

class PracticeLevel : AppCompatActivity() {
    lateinit var mInterstitialAd: InterstitialAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_level)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        init_score()
        val type=intent.getStringExtra("type")
        var i = Intent(this,PracticeList::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.putExtra("type",type)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-9403143534907409/3398150657"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        val progressionDialog= ProgressDialog(this)
        progressionDialog.setMessage("Loading.....")
        progressionDialog.setCancelable(false)
        progressionDialog.show()
        Handler().postDelayed({progressionDialog.dismiss()},2000)
        btn_easy.setOnClickListener{
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
                mInterstitialAd.adListener = object: AdListener() {
                    override fun onAdClosed() {
                        mInterstitialAd.loadAd(AdRequest.Builder().build())
                        i.putExtra("level","easy")
                        startActivity(i)
                        overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)

                    }
                }
            }
            else {
                i.putExtra("level","easy")
                startActivity(i)
                overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)
            }
        }
        btn_medium.setOnClickListener{
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
                mInterstitialAd.adListener = object: AdListener() {
                    override fun onAdClosed() {
                        mInterstitialAd.loadAd(AdRequest.Builder().build())
                        i.putExtra("level","medium")
                        startActivity(i)
                        overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)

                    }
                }
            }
            else {
                i.putExtra("level", "medium")
                startActivity(i)
                overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)

            }
        }
        btn_hard.setOnClickListener{
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
                mInterstitialAd.adListener = object: AdListener() {
                    override fun onAdClosed() {
                        mInterstitialAd.loadAd(AdRequest.Builder().build())
                        i.putExtra("level", "hard")
                        startActivity(i)
                        overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)
                    }
                }
            }
            else {
                i.putExtra("level", "hard")
                startActivity(i)
                overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)

            }
        }
    }

    private fun init_score() {
        StartPractice.score=0;
        StartPractice.user_answers= arrayOf("","","","","","","","","","")
        StartPractice.correct=0
        StartPractice.number_question=0
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
