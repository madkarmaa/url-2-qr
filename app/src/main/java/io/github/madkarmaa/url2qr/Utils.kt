package io.github.madkarmaa.url2qr

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import qrcode.QRCode
import qrcode.color.Colors
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

fun showToast(context: Context, text: String) =
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()

fun isWebUrl(text: String): Boolean = Patterns.WEB_URL.matcher(text).matches()

fun stringToQRCodeImage(text: String, qrSize: Int = 40): ByteArray =
    QRCode
        .ofSquares()
        .withSize(qrSize)
        .withBackgroundColor(Colors.WHITE)
        .withColor(Colors.BLACK)
        .build(text)
        .renderToBytes()

fun getCurrentDateString(): String {
    val now = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val randomSuffix = Random.nextInt(0, 1000000).toString().padStart(6, '0')
    return "${now.format(dateFormatter)}-$randomSuffix"
}

@Throws(FileNotFoundException::class)
fun savePNGImage(context: Context, bitmap: Bitmap, fileName: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val contentResolver = context.contentResolver
    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        contentResolver.update(uri, contentValues, null, null)
    }
}