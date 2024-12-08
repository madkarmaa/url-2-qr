package io.github.madkarmaa.url2qr

import android.util.Patterns
import qrcode.QRCode
import qrcode.color.Colors

fun isWebUrl(text: String): Boolean = Patterns.WEB_URL.matcher(text).matches()

fun stringToQRCodeImage(text: String): ByteArray =
    QRCode
        .ofSquares()
        .withSize(QR_CODE_SIZE)
        .withBackgroundColor(Colors.TRANSPARENT)
        .build(text)
        .renderToBytes()