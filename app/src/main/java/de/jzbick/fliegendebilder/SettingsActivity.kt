package de.jzbick.fliegendebilder

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlin.math.roundToInt

class SettingsActivity : AppCompatActivity() {

    val settings = Settings.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        radiusSeekBar.progress = settings.radius.roundToInt()
        labelForRadius.text = "Radius: ${radiusSeekBar.progress.toDouble()} km"

        imageSizeSeekBar.progress = settings.imageSize
        labelForImageSize.text = "Image Size: ${imageSizeSeekBar.progress} dp"

        latLongLabel.text = "Lat: ${settings.fixedLocation.latitude}\nLong: ${settings.fixedLocation.longitude}"

        fab.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        radiusSeekBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                val progressDouble = progress.toDouble()
                labelForRadius.text = "Radius: $progressDouble km"
                settings.radius = progressDouble
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        imageSizeSeekBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                labelForImageSize.text = "Image Size: $progress dp"
                settings.imageSize = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        editTextLat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(chars: CharSequence?, p1: Int, p2: Int, p3: Int) {
                settings.fixedLocation.latitude = chars.toString().toDouble()
                latLongLabel.text = "Lat: ${settings.fixedLocation.latitude}\nLong: ${settings.fixedLocation.longitude}"
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        editTextLong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(chars: CharSequence?, p1: Int, p2: Int, p3: Int) {
                settings.fixedLocation.longitude = chars.toString().toDouble()
                latLongLabel.text = "Lat: ${settings.fixedLocation.latitude}\nLong: ${settings.fixedLocation.longitude}"
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        gpsToggle.setOnCheckedChangeListener { _, isChecked -> settings.useFixedLocation = isChecked }
    }
}
