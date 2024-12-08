package io.github.madkarmaa.url2qr

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Patterns
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.setPadding
import qrcode.QRCode
import qrcode.color.Colors

const val QR_CODE_SIZE: Int = 40

class MainActivity : ComponentActivity() {
    private var sharedUrl: String = ""

    private fun showToastAndExit(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
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
            contentDescription = "QR Code for $sharedUrl"
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

fun isWebUrl(text: String): Boolean = Patterns.WEB_URL.matcher(text).matches()

fun stringToQRCodeImage(text: String): ByteArray =
    QRCode
        .ofSquares()
        .withSize(QR_CODE_SIZE)
        .withBackgroundColor(Colors.TRANSPARENT)
        .build(text)
        .renderToBytes()