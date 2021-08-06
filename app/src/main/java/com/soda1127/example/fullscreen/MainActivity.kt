package com.soda1127.example.fullscreen

import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowInsetsController.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
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
        lightStatusBarButton.setOnClickListener {
            setStatusBarMode(isLight = true)
        }
        lightNavigationBarButton.setOnClickListener {
            setNavigationBarMode(isLight = true)
        }
        darkStatusBarButton.setOnClickListener {
            setStatusBarMode(isLight = false)
        }
        darkNavigationBarButton.setOnClickListener {
            setNavigationBarMode(isLight = false)
        }
    }

    private fun setStatusBarMode(isLight: Boolean) {
        window.statusBarColor = if (isLight) {
            Color.WHITE
        } else {
            ContextCompat.getColor(this, R.color.design_default_color_primary_dark)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                if (isLight) {
                    it.setSystemBarsAppearance(
                        APPEARANCE_LIGHT_STATUS_BARS, // value
                        APPEARANCE_LIGHT_STATUS_BARS // mask
                    )
                } else {
                    it.setSystemBarsAppearance(
                        0, // value
                        APPEARANCE_LIGHT_STATUS_BARS // mask
                    )
                }
            }
        } else {
            val lFlags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility =
                if (isLight.not()) lFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                else lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setNavigationBarMode(isLight: Boolean) {
        window.navigationBarColor = if (isLight) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                if (isLight) {
                    it.setSystemBarsAppearance(
                        APPEARANCE_LIGHT_NAVIGATION_BARS,
                        APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                } else {
                    it.setSystemBarsAppearance(
                        0,
                        APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val lFlags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility =
                if (isLight.not()) lFlags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                else lFlags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun Activity.setFullScreenAbove30(checkedId: Int) {
        when (checkedId) {
            R.id.leanbackButton -> {
                supportActionBar?.hide()
                window.insetsController?.let {
                    it.hide(WindowInsets.Type.systemBars()) // 린백 모드의 경우 Behavior 이후 스테이터스 바가 돌아오지 않는 이슈가 있음.
                    it.systemBarsBehavior = BEHAVIOR_SHOW_BARS_BY_TOUCH
                }
            }
            R.id.immersiveButton,
            R.id.stickyImmersiveButton -> {
                supportActionBar?.hide()
                window.insetsController?.let {
                    it.hide(WindowInsets.Type.systemBars())
                    it.systemBarsBehavior = when (checkedId) {
                        R.id.immersiveButton -> BEHAVIOR_SHOW_BARS_BY_SWIPE
                        R.id.stickyImmersiveButton -> BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        else -> -1
                    }
                }
            }
            R.id.showSystemUIButton -> {
                window.insetsController?.let {
                    supportActionBar?.show()
                    it.show(WindowInsets.Type.systemBars())
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
