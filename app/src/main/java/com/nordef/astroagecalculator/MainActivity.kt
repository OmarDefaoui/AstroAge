package com.nordef.astroagecalculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.ads.consent.*
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.nordef.astroagecalculator.CustomDialog.DatePicker
import com.nordef.astroagecalculator.CustomDialog.TimePicker
import com.nordef.astroagecalculator.calculation.RealAgeCalculation
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var iv_terre: ImageView
    lateinit var tv_birth_day: TextView
    lateinit var tv_birth_hour: TextView
    lateinit var btn_submit: Button
    lateinit var btn_delete: Button

    lateinit var tv_days: TextView
    lateinit var tv_months: TextView
    lateinit var tv_years: TextView
    lateinit var tv_hours: TextView
    lateinit var tv_minutes: TextView

    var isTimeChoosed: Boolean = false
    var shouldAddDay: Boolean = false

    var elapsedHours: Long = 0
    var elapsedMinutes: Long = 0
    var elapsedDays: Long = 0

    //for interstitial
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var ADS_TYPE: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        setViewsClickListener()
        animateMainImage()

        Thread(Runnable {
            runOnUiThread {
                //update UI
                requestGDPRConsent()
            }
        }).start()
    }

    //initialisation of all views in this activity
    private fun initView() {
        iv_terre = findViewById(R.id.iv_terre)
        tv_birth_day = findViewById(R.id.tv_birth_day)
        tv_birth_hour = findViewById(R.id.tv_birth_hour)
        btn_submit = findViewById(R.id.btn_submit)
        btn_delete = findViewById(R.id.btn_delete)

        tv_days = findViewById(R.id.tv_days)
        tv_months = findViewById(R.id.tv_months)
        tv_years = findViewById(R.id.tv_years)
        tv_hours = findViewById(R.id.tv_hours)
        tv_minutes = findViewById(R.id.tv_minutes)
    }

    //set event listener of all component that need it
    private fun setViewsClickListener() {
        val fragmentManager: FragmentManager = supportFragmentManager

        //show popup to select time
        tv_birth_hour.setOnClickListener {
            val timePicker = TimePicker()
            timePicker.show(fragmentManager, null)
        }
        //show popup to select date
        tv_birth_day.setOnClickListener {
            if (!isTimeChoosed) {
                Toast.makeText(
                    this, getString(R.string.choose_time_first),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val datePicker = DatePicker()
                datePicker.show(fragmentManager, null)
            }
        }

        btn_submit.setOnClickListener {
            if ((elapsedDays > 0) || (elapsedDays == 0L && (elapsedMinutes > 0 || elapsedHours > 0))) {
                val intent = Intent(this, AgeInWorld::class.java)
                intent.putExtra("ageInDays", elapsedDays)
                intent.putExtra("ADS_TYPE", ADS_TYPE)
                startActivity(intent)
            } else
                Toast.makeText(this, getString(R.string.choose_date_first), Toast.LENGTH_SHORT).show()
        }
        btn_delete.setOnClickListener {
            isTimeChoosed = false
            shouldAddDay = false

            tv_birth_day.text = ""
            tv_birth_hour.text = ""

            elapsedDays = 0
            elapsedMinutes = 0
            elapsedHours = 0

            tv_days.text = ""
            tv_months.text = ""
            tv_years.text = ""
            tv_hours.text = ""
            tv_minutes.text = ""
        }
    }

    //set animation to the top image ine the view
    private fun animateMainImage() {
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.resize)
        iv_terre.startAnimation(animation)
    }

    //this function is called when user choose the time of birth
    fun setTime(millis: Long) {
        isTimeChoosed = true

        val dateOfBirth = Date()
        dateOfBirth.time = millis
        val formattedDate = SimpleDateFormat("HH:mm").format(dateOfBirth)
        tv_birth_hour.text = formattedDate.toString()

        val currentDate = Date()
        currentDate.time = Calendar.getInstance().timeInMillis

        if (dateOfBirth.before(currentDate))
            printDifference(dateOfBirth, currentDate)
        else {
            printDifference(dateOfBirth, currentDate)
            elapsedHours += 23
            elapsedMinutes += 60
            shouldAddDay = true
        }
    }

    //this function is called when user choose the day of birth
    //to work, it needs that the time be chosen first to calculate the whole date in millis
    // and display result
    fun setDate(millis: Long, day: Int, month: Int, year: Int) {
        tv_birth_day.text = "$day/$month/$year"

        //return elapsedDays: age in days count
        val dateOfBirth = Date()
        dateOfBirth.time = millis
        getAgeInDaysCount(dateOfBirth)

        //return full age in years, months and days
        val c = Calendar.getInstance()
        c.timeInMillis = millis
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH) + 1
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val maxDayOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH)

        val age = RealAgeCalculation()
        age.getCurrentDate()
        age.setDateOfBirth(mYear, mMonth, mDay)
        var calculatedYear = age.calcualteYear()
        var calculatedMonth = age.calcualteMonth()
        var calculatedDate = age.calcualteDay()

        if (shouldAddDay) {
            shouldAddDay = false
            calculatedDate += 1
            if (calculatedDate > maxDayOfMonth) {
                calculatedDate = 1
                calculatedMonth += 1
            }
            if (calculatedMonth > 12) {
                calculatedMonth = 1
                calculatedYear += 1
            }
        }

        setDataToUI(calculatedDate, calculatedMonth, calculatedYear)
    }

    //display data on the screen
    private fun setDataToUI(days: Int, months: Int, years: Int) {
        tv_days.text = days.toString()
        tv_months.text = months.toString()
        tv_years.text = years.toString()

        tv_hours.text = elapsedHours.toString()
        tv_minutes.text = elapsedMinutes.toString()
    }

    //calculate and return elapsed hours and minutes
    private fun printDifference(startDate: Date, endDate: Date) {
        //milliseconds
        var different = endDate.time - startDate.time

        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        //elapsedDays = different / daysInMilli
        different = different % daysInMilli

        elapsedHours = different / hoursInMilli
        different = different % hoursInMilli

        elapsedMinutes = different / minutesInMilli
    }

    //get the full age in only days to be used to calculate age in other planets
    private fun getAgeInDaysCount(birthDate: Date) {
        val currentDate = Date()
        currentDate.time = Calendar.getInstance().timeInMillis

        //milliseconds
        val different = currentDate.time - birthDate.time

        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        elapsedDays = different / daysInMilli
    }

    //display GDPR consent and display ads depending on user choice
    private fun requestGDPRConsent() {
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getString(R.string.admob_app_id))

        val gdprOk = getSharedPreferences("info", Context.MODE_PRIVATE)
            .getString("ads_type", "no")

        //if true GDPR is not showed yet
        if (gdprOk.equals("no"))
            requestValidationForGoogleConsentSDKForGDPR()
        else
            loadAds()
    }

    //ads code part

    private fun loadAds() {
        ADS_TYPE = getSharedPreferences("info", Context.MODE_PRIVATE)
            .getString("ads_type", "NON_PERSONALIZED")

        showInterstitialAd()
    }

    private fun showInterstitialAd() {
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.admob_interstitial_ad)
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


    //gdpr code part

    private var form: ConsentForm? = null

    private fun requestValidationForGoogleConsentSDKForGDPR() {
        val consentInformation: ConsentInformation = ConsentInformation.getInstance(this)
        val publisherIds = arrayOf(getString(R.string.admob_publisher_id))

        consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                if (ConsentInformation.getInstance(applicationContext).isRequestLocationInEeaOrUnknown) {
                    when (consentStatus) {
                        ConsentStatus.UNKNOWN -> form!!.load()
                        ConsentStatus.PERSONALIZED -> SetAdsType("PERSONALIZED")
                        ConsentStatus.NON_PERSONALIZED -> SetAdsType("NON_PERSONALIZED")
                    }
                } else
                //user are not in countries that require GDPR consent
                    SetAdsType("PERSONALIZED")
            }

            override fun onFailedToUpdateConsentInfo(errorDescription: String) {

            }
        })


        var privacyUrl: URL? = null
        try {
            privacyUrl = URL(getString(R.string.admob_GDPR_privacy_policy_link))
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            // Handle error.
        }

        form = ConsentForm.Builder(this, privacyUrl)
            .withListener(object : ConsentFormListener() {
                override fun onConsentFormLoaded() {
                    // Consent form loaded successfully.
                    form!!.show()
                }

                override fun onConsentFormClosed(
                    consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?
                ) {
                    // Consent form was closed.
                    when (consentStatus) {
                        ConsentStatus.PERSONALIZED -> {
                            ConsentInformation.getInstance(applicationContext).consentStatus =
                                ConsentStatus.PERSONALIZED
                            SetAdsType("PERSONALIZED")
                        }
                        ConsentStatus.NON_PERSONALIZED -> {
                            ConsentInformation.getInstance(applicationContext).consentStatus =
                                ConsentStatus.NON_PERSONALIZED
                            SetAdsType("NON_PERSONALIZED")
                        }
                    }
                }
            })
            .withPersonalizedAdsOption()
            .withNonPersonalizedAdsOption()
            .build()

    }

    private fun SetAdsType(type: String) {
        val editor = getSharedPreferences("info", Context.MODE_PRIVATE).edit()
        editor.putString("ads_type", type)
        editor.commit()

        loadAds()
    }

}
