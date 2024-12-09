package io.github.madkarmaa.url2qr

import android.util.Patterns
import qrcode.QRCode
import qrcode.color.Colors

fun isWebUrl(text: String): Boolean = Patterns.WEB_URL.matcher(text).matches()

fun stringToQRCodeImage(text: String, qrSize: Int = 40): ByteArray =
    QRCode
        .ofSquares()
        .withSize(qrSize)
        .withBackgroundColor(Colors.TRANSPARENT)
        .build(text)
        .renderToBytes()