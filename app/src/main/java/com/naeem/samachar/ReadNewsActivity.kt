package com.naeem.samachar

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.naeem.samachar.architecture.NewsViewModel
import com.naeem.samachar.utils.Constants.NEWS_DESCRIPTION
import com.naeem.samachar.utils.Constants.NEWS_IMAGE_URL
import com.naeem.samachar.utils.Constants.NEWS_PUBLICATION_TIME
import com.naeem.samachar.utils.Constants.NEWS_SOURCE
import com.naeem.samachar.utils.Constants.NEWS_TITLE
import com.naeem.samachar.utils.Constants.NEWS_URL
import java.util.*


class ReadNewsActivity : AppCompatActivity() {

    private lateinit var newsWebView: WebView
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsData: ArrayList<NewsModel>

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_news)

        newsWebView = findViewById(R.id.news_webview)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        //loading data into list
        newsData = ArrayList(1)
        val newsUrl = intent.getStringExtra(NEWS_URL)
        newsData.add(
            NewsModel(
                intent.getStringExtra(NEWS_TITLE)!!,
                intent.getStringExtra(NEWS_IMAGE_URL),
                intent.getStringExtra(NEWS_DESCRIPTION),
                newsUrl,
                intent.getStringExtra(NEWS_SOURCE),
                intent.getStringExtra(NEWS_PUBLICATION_TIME)
            )
        )

        // Webview
        newsWebView.apply {
            settings.apply {
                domStorageEnabled = true
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                javaScriptEnabled = true
            }
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
        }


        if (newsUrl != null) {
            newsWebView.loadUrl(newsUrl)
        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item_readnewsactivity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.share_news -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, checkout this news : " + newsData[0].url)
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share with :"))
                return true
            }

            R.id.save_news -> {
                this.let { viewModel.insertNews(this@ReadNewsActivity, newsData[0]) }
                Toast.makeText(this, "News saved!", Toast.LENGTH_SHORT)
                    .show()
            }

            R.id.browse_news -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsData[0].url))
                startActivity(intent)
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

}