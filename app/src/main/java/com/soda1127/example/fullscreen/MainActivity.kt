package com.soda1127.example.fullscreen

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowInsetsController.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.soda1127.example.fullscreen.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding) {
        fullScreenRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setFullScreenAbove30(checkedId)
            } else {
                if (checkedId != View.NO_ID) {
                    setFullScreen(checkedId)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun Activity.setFullScreenAbove30(checkedId: Int) {
        when (checkedId) {
            R.id.leanbackButton -> {
                supportActionBar?.hide()
                window.insetsController?.let {
                    it.hide(WindowInsets.Type.systemBars() or WindowInsets.Type.navigationBars())
                    it.systemBarsBehavior = BEHAVIOR_SHOW_BARS_BY_TOUCH
                    window.navigationBarColor = getColor(R.color.black)
                }
            }
            R.id.immersiveButton,
            R.id.stickyImmersiveButton -> {
                supportActionBar?.hide()
                window.insetsController?.let {
                    it.hide(WindowInsets.Type.systemBars() or WindowInsets.Type.navigationBars())
                    it.systemBarsBehavior = when (checkedId) {
                        R.id.immersiveButton -> BEHAVIOR_SHOW_BARS_BY_SWIPE
                        R.id.stickyImmersiveButton -> BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        else -> -1
                    }
                    window.navigationBarColor = getColor(R.color.black)
                }
            }
            R.id.showSystemUIButton -> {
                window.insetsController?.let {
                    supportActionBar?.show()
                    it.show(WindowInsets.Type.systemBars() or WindowInsets.Type.navigationBars())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    @Suppress("DEPRECATION")
    private fun setFullScreen(checkedId: Int) {
        window.decorView.systemUiVisibility = when (checkedId) {
            R.id.leanbackButton -> {
                supportActionBar?.hide()
                (View.SYSTEM_UI_FLAG_FULLSCREEN // 풀 스크린 모드
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // 하단 네비게이션 바 숨기기 플래그
            }
            R.id.immersiveButton -> {
                supportActionBar?.hide()
                (View.SYSTEM_UI_FLAG_IMMERSIVE // 제스쳐 이후 SYSTEM UI 보임
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // 풀 스크린 모드
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // 하단 네비게이션 바 숨기기 플래그
            }
            R.id.stickyImmersiveButton -> {
                supportActionBar?.hide()
                (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // 제스쳐 이후 SYSTEM UI 가림
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // 풀 스크린 모드
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // 하단 네비게이션 바 숨기기 플래그
            }
            R.id.showSystemUIButton -> {
                supportActionBar?.show()
                0
            }
            else -> 0
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val checkedId = binding.fullScreenRadioGroup.checkedRadioButtonId
        setFullScreen(checkedId)
    }

}
