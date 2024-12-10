package io.github.madkarmaa.url2qr

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.setPadding

class MainActivity : ComponentActivity() {
    private var sharedUrl: String = ""

    private fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun showToastAndExit(text: String) {
        this.showToast(text)
        this.finishAndRemoveTask()
    }

    private fun setupStatusBar() {
        // transparent status bar
        this.enableEdgeToEdge()

        // dark status bar icons
        WindowCompat
            .getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setupStatusBar()

        if (intent?.action != Intent.ACTION_SEND) {
            this.showToastAndExit("Share a URL to me!")
            return
        }

        intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedUrl = it }
        sharedUrl = sharedUrl.trim()

        if (sharedUrl.isEmpty() || !isWebUrl(sharedUrl)) {
            this.showToastAndExit("Please provide a valid URL!")
            return
        }

        val qrImageBlob = stringToQRCodeImage(sharedUrl)
        val bitmap = BitmapFactory.decodeByteArray(qrImageBlob, 0, qrImageBlob.size)

        val imageView = ImageView(this).apply {
            setImageBitmap(bitmap)
            contentDescription = "QR code for $sharedUrl"
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        val frameLayout = FrameLayout(this).apply {
            val paddingInDp = 30
            val paddingInPx = (paddingInDp * resources.displayMetrics.density).toInt()

            setPadding(paddingInPx)
            addView(imageView)
        }

        setContentView(frameLayout)
    }

    // when the app is exited
    override fun onDestroy() {
        super.onDestroy()
        this.finishAndRemoveTask()
    }

    // when the app is no longer in the foreground
    override fun onStop() {
        super.onStop()
        this.finishAndRemoveTask()
    }
}