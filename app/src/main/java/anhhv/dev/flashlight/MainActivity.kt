package anhhv.dev.flashlight

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
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
                    torchState = !torchState
                    flashLightControl()
                }

                ivSos.setOnClickListener {
                    sosState = !sosState
                    sosControl()
                }
            }
        } else {
            Toast.makeText(
                this@MainActivity, R.string.no_flash_available, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun flashLightControl() {
        binding.apply {
            sosState = false
            ivSos.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.ic_sos_off
                )
            )
            when (torchState) {
                true -> {
                    cameraManager.setTorchMode(cameraId, true)
                    ivLight.visibility = View.VISIBLE
                    ivTurnFlash.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity, R.drawable.ic_power_on
                        )
                    )
                }

                false -> {
                    cameraManager.setTorchMode(cameraId, false)
                    ivLight.visibility = View.GONE
                    ivTurnFlash.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity, R.drawable.ic_power_off
                        )
                    )
                }
            }
        }
    }

    private fun sosControl() {
        binding.apply {
            cameraManager.setTorchMode(cameraId, false)
            ivLight.visibility = View.GONE
            ivTurnFlash.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.ic_power_off
                )
            )
            when (sosState) {
                true -> {
                    ivSos.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity, R.drawable.ic_sos_on
                        )
                    )
                    blinkFlash()
                    Log.d("123123", "blinkOn")
                }

                false -> {
                    cameraManager.setTorchMode(cameraId, false)
                    ivLight.visibility = View.GONE
                    ivSos.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity, R.drawable.ic_sos_off
                        )
                    )
                    Log.d("123123", "blinkOff")
                }
            }
        }
    }

    private fun blinkFlash() {
        binding.apply {
            val blinkDuration = 500 // milliseconds (adjust as needed)
            val blinkCycles = 10 // number of blinking cycles (adjust as needed)

            val blinkRunnable = object : Runnable {
                var isBlink = false
                var cycleCount = 0

                override fun run() {
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

                    // Check if the desired number of cycles is reached
                    if (cycleCount < blinkCycles) {
                        // Schedule the next blink
                        ivSos.postDelayed(this, blinkDuration.toLong())
                    }
                }
            }

            //Start the blinking
            ivSos.post(blinkRunnable)
        }
    }
}

