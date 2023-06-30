package dev.rranndt.storay.util

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.GradientDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dev.rranndt.storay.R
import dev.rranndt.storay.util.Constant.BEARER
import dev.rranndt.storay.util.Constant.DAY
import dev.rranndt.storay.util.Constant.DEFAULT_TIME
import dev.rranndt.storay.util.Constant.HOUR
import dev.rranndt.storay.util.Constant.JPG
import dev.rranndt.storay.util.Constant.MESSAGE
import dev.rranndt.storay.util.Constant.MINUTE
import dev.rranndt.storay.util.Constant.MONTH
import dev.rranndt.storay.util.Constant.PARSE_DEFAULT_TIME
import dev.rranndt.storay.util.Constant.TIME_ZONE
import dev.rranndt.storay.util.Constant.WEEK
import dev.rranndt.storay.util.Constant.YEAR
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

object Helper {

    // Error Exception
    fun HttpException.getErrorMessage(): String {
        val message = response()?.errorBody()?.string().toString()
        return JSONObject(message).getString(MESSAGE)
    }

    // SnackBar
    fun View.showShortSnackBar(message: String?) {
        Snackbar.make(this, message.toString(), Snackbar.LENGTH_SHORT).show()
    }

    // Hide Keyboard
    fun Context.hideKeyboard(view: View) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    // Alert Dialog
    fun Context.alert(
        @StyleRes style: Int = 0,
        dialogBuilder: MaterialAlertDialogBuilder.() -> Unit,
    ) {
        MaterialAlertDialogBuilder(this, style).apply {
            setCancelable(false)
            dialogBuilder()
            create()
            show()
        }
    }

    fun MaterialAlertDialogBuilder.positiveButton(
        text: String = context.getString(R.string.alert_dialog_text_yes),
        handleClick: (dialogInterface: DialogInterface) -> Unit = { it.dismiss() },
    ) {
        this.setPositiveButton(text) { dialogInterface, _ -> handleClick(dialogInterface) }
    }

    fun MaterialAlertDialogBuilder.negativeButton(
        text: String = context.getString(R.string.alert_dialog_text_no),
        handleClick: (dialogInterface: DialogInterface) -> Unit = { it.dismiss() },
    ) {
        this.setNegativeButton(text) { dialogInterface, _ -> handleClick(dialogInterface) }
    }

    // String Generate Token
    fun String.generateToken() = "$BEARER $this"

    // Parse Time
    private fun currentDate(): Long = Calendar.getInstance().timeInMillis

    fun String.parseToTimeAgo(context: Context): String {
        val format = SimpleDateFormat(DEFAULT_TIME, Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone(TIME_ZONE)
        var date: String
        try {
            val uploadAt = format.parse(this)?.time
            val result = currentDate() - (uploadAt ?: 0)

            date = when {
                result < MINUTE -> context.getString(R.string.just_now)
                result < 2 * MINUTE -> context.getString(R.string.minute_ago)
                result < 60 * MINUTE -> "${result / MINUTE}${
                    context.getString(R.string.m)
                }"

                result < 2 * HOUR -> context.getString(R.string.hour_ago)
                result < 24 * HOUR -> "${result / HOUR}${context.getString(R.string.h)}"
                result < 2 * DAY -> context.getString(R.string.yesterday)
                result < 30 * DAY -> "${result / DAY}${context.getString(R.string.d)}"
                result < 2 * WEEK -> context.getString(R.string.week_ago)
                result < 4 * WEEK -> "${result / WEEK}${context.getString(R.string.w)}"
                result < 2 * MONTH -> context.getString(R.string.month_ago)
                result < 12 * MONTH -> "${result / MONTH}${
                    context.getString(R.string.mo)
                }"

                result < 2 * YEAR -> context.getString(R.string.year_ago)
                else -> "${result / YEAR}${context.getString(R.string.years_ago)}"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            date = ""
        }

        return date
    }

    // Parse Location
    fun parseToAddress(
        context: Context,
        lat: Double,
        lon: Double,
    ): String {
        var addressText = ""
        val geocoder = Geocoder(context)

        // Just ignore the warning, this is the way to go on lower SDKs. Piss
        try {
            val geoLocation = geocoder.getFromLocation(lat, lon, 1)
            if (geoLocation != null) {
                addressText = if (geoLocation.isNotEmpty()) {
                    val location = geoLocation[0]
                    location.getAddressLine(0)
                } else {
                    context.getString(R.string.no_location_included)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return addressText
    }

    // Random Color
    fun View.colorBackground() {
        this.background = GradientDrawable().apply {
            setColor(
                Color.argb(
                    255,
                    Random.nextInt(255),
                    Random.nextInt(255),
                    Random.nextInt(255)
                )
            )
            shape = GradientDrawable.OVAL
        }
    }

    // Substring
    fun String.showFirstLetter() = this.substring(0, 1).uppercase(Locale.getDefault())

    // Get image from gallery
    fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, JPG, storageDir)
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private val timeStamp: String = SimpleDateFormat(
        PARSE_DEFAULT_TIME,
        Locale.US
    ).format(System.currentTimeMillis())

    // Rotate file
    fun rotateFile(file: File, isBackCamera: Boolean = false) {
        val matrix = Matrix()
        val bitmap = BitmapFactory.decodeFile(file.path)
        val rotation = if (isBackCamera) 90f else -90f
        matrix.postRotate(rotation)
        if (!isBackCamera) {
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }

        val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
    }

    fun Context.res(@StringRes stringResId: Int) = this.getString(stringResId)

    // Animation
    fun View.doAnimation() = ObjectAnimator.ofFloat(this, View.ALPHA, 1f).setDuration(500)
}