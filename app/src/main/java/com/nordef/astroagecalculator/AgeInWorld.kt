package com.nordef.astroagecalculator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.nordef.astroagecalculator.adapter.RecyclerViewAdapter
import com.nordef.astroagecalculator.model.ListItemPlanetsData

class AgeInWorld : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerViewAdapter

    var planetsData: ArrayList<ListItemPlanetsData> = ArrayList()
    var ageInDays: Int = 0

    //for interstitial
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var ADS_TYPE: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age_in_world)

        ageInDays = intent?.extras?.get("ageInDays").toString().toInt()
        ADS_TYPE = intent?.extras?.get("ADS_TYPE").toString()

        initRecyclerView()
        loadListOfPlanets()
        displayResult()

        showInterstitialAd()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    private fun loadListOfPlanets() {
        planetsData.add(ListItemPlanetsData(getString(R.string.Mercury), R.drawable.ic_001_mercury,
                58.646f, 87.97f))
        planetsData.add(ListItemPlanetsData(getString(R.string.Venus), R.drawable.ic_002_venus,
                243.018f, 224.70f))
        planetsData.add(ListItemPlanetsData(getString(R.string.Earth), R.drawable.ic_003_earth,
                0.99726968f, 365.26f))
        planetsData.add(ListItemPlanetsData(getString(R.string.Mars), R.drawable.ic_004_mars,
                1.026f, (1.8808476 * 365.26).toFloat()))
        planetsData.add(ListItemPlanetsData(getString(R.string.Jupiter), R.drawable.ic_005_jupiter,
                0.41354f, (11.862615 * 365.26).toFloat()))
        planetsData.add(ListItemPlanetsData(getString(R.string.Saturn), R.drawable.ic_006_saturn,
                0.444f, (29.447498 * 365.26).toFloat()))
        planetsData.add(ListItemPlanetsData(getString(R.string.Uranus), R.drawable.ic_007_uranus,
                0.718f, (84.016846 * 365.26).toFloat()))
        planetsData.add(ListItemPlanetsData(getString(R.string.Neptune), R.drawable.ic_008_neptune,
                0.671f, (164.79132 * 365.26).toFloat()))
        planetsData.add(ListItemPlanetsData(getString(R.string.Pluto), R.drawable.ic_009_pluto,
                6.387f, (247.92065 * 365.26).toFloat()))
    }

    private fun displayResult() {
        for (i in planetsData) {
            i.rotation_period = ageInDays / i.rotation_period
            i.orbit_period = ageInDays / i.orbit_period
        }
        adapter = RecyclerViewAdapter(this, planetsData)
        recyclerView.adapter = adapter
    }

    //ads code part

    private fun showInterstitialAd() {
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.admob_interstitial_ad_2)
        mInterstitialAd.loadAd(getRequestDependingOnGDPR())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show()
            }
        }
    }

    private fun getRequestDependingOnGDPR(): AdRequest? {
        var request: AdRequest? = null

        when (ADS_TYPE) {
            "PERSONALIZED" -> request = AdRequest.Builder().build()

            "NON_PERSONALIZED" -> {
                val extras = Bundle()
                extras.putString("npa", "1")

                request = AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                        .build()
            }
        }

        return request
    }

}
