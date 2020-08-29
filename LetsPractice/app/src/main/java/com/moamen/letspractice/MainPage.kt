package com.moamen.letspractice

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.moamen.icpcassuit.Connection_Network

class MainPage : AppCompatActivity() {
    lateinit var mInterstitialAd: InterstitialAd
    var myAuth= FirebaseAuth.getInstance()
    lateinit var mAdView : AdView
    companion object{
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-9403143534907409/3398150657"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
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
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_fram, Home())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
        val btn: BottomNavigationView =findViewById(R.id.mainBottomNavigationView)
        btn.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_fram, Home())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.menu_top -> {
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                        mInterstitialAd.adListener = object: AdListener() {
                            override fun onAdClosed() {
                                mInterstitialAd.loadAd(AdRequest.Builder().build())
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.home_fram, AllUsers())
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit()
                            }
                        }
                    }
                    else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_fram, AllUsers())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    }
                }
                R.id.menu_profile -> {
                    if (myAuth.currentUser != null) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_fram, Profile())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    } else {
                        Toast.makeText(this, "You're a guest", Toast.LENGTH_SHORT).show()
                    }
                }

                R.id.chat -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_fram, Chat())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()

                }

            }
            true
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.competition->{
                val e= Connection_Network(this)
                if(e.Isconnected()) {
                    var i = Intent(this, CompetitionActivity::class.java)
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                    overridePendingTransition(R.anim.slid_in_right, R.anim.slid_out_left)
                }
                else {
                    Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show()
                }
            }
            R.id.main_logout ->{
                val e= Connection_Network(this)
                if(e.Isconnected()) {
                        myAuth.signOut()
                        var i = Intent(this, MainActivity::class.java)
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                        overridePendingTransition(R.anim.slid_in_right, R.anim.slid_out_left)
                }
                else {
                    Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show()
                }
            }
            R.id.main_rate->{
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data= Uri.parse("https://play.google.com/store/apps/details?id=com.moamen.letspractice")
                startActivity(openURL)
            }
            R.id.main_about->{
                var i = Intent(this,About::class.java)
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
                overridePendingTransition(R.anim.slid_in_right,R.anim.slid_out_left)
            }
            R.id.main_share->{
                val sent=Intent()
                sent.action=Intent.ACTION_SEND
                sent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.moamen.letspractice")
                sent.type="text/plain"
                startActivity(Intent.createChooser(sent,""))
            }
        }
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val my_inflate=menuInflater
        my_inflate.inflate(R.menu.home_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
