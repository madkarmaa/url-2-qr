package io.github.madkarmaa.url2qr

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import java.io.FileNotFoundException

// https://stackoverflow.com/a/66703893
fun Modifier.clickableWithoutEffect(
    onClick: () -> Unit
) = composed(
    factory = {
        then(
            Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick() }
            )
        )
    }
)

class MainActivity : ComponentActivity() {
    private var sharedUrl: String = ""

    private fun showToastAndExit(text: String) {
        showToast(applicationContext, text)
        finishAndRemoveTask()
    }

    private fun setupStatusBar() {
        // transparent status bar
        enableEdgeToEdge()

        // dark status bar icons
        WindowCompat
            .getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupStatusBar()

        if (intent?.action != Intent.ACTION_SEND) {
            showToastAndExit("Share a URL to me!")
            return
        }

        intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedUrl = it }
        sharedUrl = sharedUrl.trim()

        if (sharedUrl.isEmpty() || !isWebUrl(sharedUrl)) {
            showToastAndExit("Please provide a valid URL!")
            return
        }

        val qrImageBlob = stringToQRCodeImage(sharedUrl)

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                QRCodeBox(applicationContext, qrImageBlob)
            }
        }
    }

    // when the app is exited
    override fun onDestroy() {
        super.onDestroy()
        finishAndRemoveTask()
    }

    // when the app is no longer in the foreground
    override fun onStop() {
        super.onStop()
        finishAndRemoveTask()
    }
}

@Composable
fun QRCodeBox(context: Context, qrImageBlob: ByteArray) {
    val bitmap = BitmapFactory.decodeByteArray(qrImageBlob, 0, qrImageBlob.size)
    val filename = "${context.packageName}-${getCurrentDateString()}.png"

    fun save() {
        try {
            savePNGImage(context, bitmap, filename)
            showToast(context, "Saved!")
        } catch (e: FileNotFoundException) {
            showToast(context, "Failed to save!")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithoutEffect {
                save()
            }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR code",
            modifier = Modifier.fillMaxWidth()
        )
    }
}