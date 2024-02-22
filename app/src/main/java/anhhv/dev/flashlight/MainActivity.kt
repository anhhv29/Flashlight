package anhhv.dev.flashlight

import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import anhhv.dev.flashlight.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraManager: CameraManager
    private var cameraId = "0"
    private var torchState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        binding.apply {
            ivSos.setOnClickListener {
                ivSos.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity, R.drawable.ic_sos_on
                    )
                )
                Toast.makeText(this@MainActivity, "sos", Toast.LENGTH_SHORT).show()
            }

            ivPower.setOnClickListener {
                torchState = !torchState
                turnOnFlashLight()
            }

            ivBlink.setOnClickListener {
                vSeekBar.visibility = View.VISIBLE
                ivBlink.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity, R.drawable.ic_blink_on
                    )
                )
                Toast.makeText(this@MainActivity, "blink", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun turnOnFlashLight() {
        binding.apply {
            when (torchState) {
                false -> {
                    cameraManager.setTorchMode(cameraId, false)
                    ivLight.visibility = View.GONE
                    ivPower.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity, R.drawable.ic_power_off
                        )
                    )
                }

                true -> {
                    cameraManager.setTorchMode(cameraId, true)
                    ivLight.visibility = View.VISIBLE
                    ivPower.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity, R.drawable.ic_power_on
                        )
                    )
                }
            }
        }
    }
}