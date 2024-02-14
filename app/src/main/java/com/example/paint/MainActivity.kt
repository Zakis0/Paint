package com.example.paint

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.paint.databinding.ActivityMainBinding
// TODO Bad
lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.changeModeBtn.text = binding.drawField.mode.name

        binding.changeModeBtn.setOnClickListener {
            nextMode()
            binding.changeModeBtn.text = binding.drawField.mode.name
        }
    }
    fun nextMode() {
        binding.drawField.mode = when (binding.drawField.mode) {
            Modes.DRAW -> Modes.MOVE
//            Modes.MOVE -> Modes.DRAW
            Modes.MOVE -> Modes.SELECT
            Modes.SELECT -> Modes.DRAW
            else -> Modes.DRAW
        }
    }
}