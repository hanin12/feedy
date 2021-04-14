package com.example.feedy1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Browser
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.activity_web_view.*
import java.util.*


class WebViewActivity : AppCompatActivity() {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val url = "https://feedy.ly/"
    private var isAlreadyCreated = false
    private var lastBackPressTime: Long = 0
    private val mContext: Context? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "1"
        val channel2 = "2"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                "Channel 1", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "This is feedy"
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(notificationChannel)
            val notificationChannel2 = NotificationChannel(
                channel2,
                "Channel 2", NotificationManager.IMPORTANCE_MIN
            )
            notificationChannel.description = "This is Feedy"
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(notificationChannel2)
        }
        val settings: WebSettings = webView.getSettings()

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH)

        webView.getSettings().setAppCacheEnabled(true)
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        settings.domStorageEnabled = true

        settings.allowFileAccess = true



        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL


        settings.useWideViewPort = true
        settings.savePassword = true

        settings.saveFormData = true




        swipeRefreshLayout = findViewById(R.id.swipe)

        swipeRefreshLayout.setOnRefreshListener {

            Handler().postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false
                webView.reload()
            }, 4000)
        }

        val connectionManager =
            this@WebViewActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected || !networkInfo.isAvailable) {
            webView.loadUrl("file:///android_asset/error.html")

        } else {
            webView.loadUrl(url)
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(false)
        webView.settings.domStorageEnabled = true
        webView.settings.allowContentAccess = true
        webView.settings.setAppCacheEnabled(true)
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        // life saver, do not remove

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {


                if (url.startsWith("tel:")) {
                    webView.stopLoading()
                    this@WebViewActivity.startActivity(
                        Intent(
                            "android.intent.action.DIAL",
                            Uri.parse(url)

                        )

                    )

                    true
                } else if (!url.startsWith("mailto:")) {
                    false

                } else {

                    try {
                        webView.stopLoading()
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        intent.scheme

                        intent.putExtra(Browser.EXTRA_CREATE_NEW_TAB, true);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, mContext?.packageName);

                        startActivity(intent)

                    } catch (e: Exception) {
                        e.printStackTrace();

                    }

                    true

                }


                super.onPageStarted(view, url, favicon)
            }


            override fun onPageFinished(view: WebView?, url: String?) {


            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {

                webView.goBack()
                super.onReceivedError(view, request, error)

                val connectionManager =
                    this@WebViewActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectionManager.activeNetworkInfo
                if (networkInfo == null || !networkInfo.isConnected || !networkInfo.isAvailable) {
                    webView.loadUrl("file:///android_asset/error.html")

                } else {

                }

            }
        }
        webView.loadUrl(url)

    }

    override fun onPause() {
        super.onPause()
        webView.onPause()


    }

    override fun onBackPressed() {
        if (this.webView.canGoBack()) {
            val webView: WebView = this.webView
            if (webView != null) {
                webView.goBack()
                Log.e("wv", "inside go back pressed")
            }
        } else if (lastBackPressTime < System.currentTimeMillis() - 4000) {

            lastBackPressTime = System.currentTimeMillis()
        } else {
            Log.e("wv", "inside else sec")
            super.onBackPressed()
        }
    }

    override fun onResume() {

        super.onResume()


        this.webView.onResume();

        if (isAlreadyCreated && !isNetworkAvailable()) {
            isAlreadyCreated = false

        }
    }


    private fun isNetworkAvailable(): Boolean {
        val connectionManager =
            this@WebViewActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {

        return true
    }


}
