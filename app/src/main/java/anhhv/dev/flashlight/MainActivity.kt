package anhhv.dev.flashlight

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import anhhv.dev.flashlight.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraManager: CameraManager
    private var cameraId = "0"
    private var torchState = false
    private var sosState = false

    companion object {
        const val CAMERA_REQUEST = 123
    }

    var isBlink = false
    var cycleCount = 0
    val blinkDuration = 500 // milliseconds (adjust as needed)
    val blinkCycles = 10 // number of blinking cycles (adjust as needed)
    private val blinkRunnable = object : Runnable {
        override fun run() {
            binding.apply {
                try {
                    // Toggle torch mode
                    isBlink = !isBlink
                    cameraManager.setTorchMode(cameraId, isBlink)
                    // Toggle visibility of the light view
                    if (isBlink) {
                        ivLight.visibility = View.VISIBLE
                    } else {
                        ivLight.visibility = View.GONE
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }

                cycleCount++
            }
            // Check if the desired number of cycles is reached
            if (cycleCount < blinkCycles) {
                // Schedule the next blink
                binding.ivSos.postDelayed(this, blinkDuration.toLong())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(
            this@MainActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST
        )

        val hasCameraFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        if (hasCameraFlash) {
            cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
            cameraId = cameraManager.cameraIdList[0]
            binding.apply {
                ivTurnFlash.setOnClickListener {
                    stopAll()
                    sosState = false
                    torchState = !torchState
                    if (torchState) {
                        cameraManager.setTorchMode(cameraId, true)
                        ivLight.visibility = View.VISIBLE
                        ivTurnFlash.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity, R.drawable.ic_power_on
                            )
                        )
                    }
                }

                ivSos.setOnClickListener {
                    stopAll()
                    torchState = false
                    sosState = !sosState
                    if (sosState) {
                        cycleCount = 0
                        ivSos.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity, R.drawable.ic_sos_on
                            )
                        )
                        ivSos.postDelayed(blinkRunnable, blinkDuration.toLong())
                    }
                }
            }
        } else {
            Toast.makeText(
                this@MainActivity, R.string.no_flash_available, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun stopAll() {
        binding.apply {
            isBlink = false
            ivSos.removeCallbacks(blinkRunnable)
            cameraManager.setTorchMode(cameraId, false)
            ivLight.visibility = View.GONE
            ivTurnFlash.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.ic_power_off
                )
            )
            ivSos.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.ic_sos_off
                )
            )
        }
    }
}

