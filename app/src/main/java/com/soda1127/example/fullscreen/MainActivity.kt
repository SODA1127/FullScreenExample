package com.soda1127.example.fullscreen

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Configuration.*
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.WindowInsetsController.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.*
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val cutout = window.decorView.rootWindowInsets.displayCutout
            if (cutout != null && cutout.boundingRects.isNotEmpty()) {
                binding.fullScreenSwitch.isVisible = true
                binding.fullScreenSwitch.setOnCheckedChangeListener { _, isChecked ->
                    window.handleCutoutInsetInSafeArea(isChecked)
                }
            } else {
                binding.fullScreenSwitch.isVisible = false
                binding.fullScreenSwitch.setOnCheckedChangeListener(null)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun Window.handleCutoutInsetInSafeArea(isFullMode: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(this, isFullMode.not())
        // ?????? ??????????????? Cutout?????? ????????? ????????? Inset??? ???????????? ????????? ??? ??????
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val displayCutout = insets.displayCutout
            if (displayCutout != null && displayCutout.boundingRects.size > 0) {
                when (resources.configuration.orientation) {
                    ORIENTATION_PORTRAIT -> {
                        /*v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            topMargin = if (isFullMode) {
                                0
                            } else {
                                displayCutout.safeInsetTop
                            }
                            bottomMargin = if (isFullMode) {
                                0
                            } else {
                                displayCutout.safeInsetBottom
                            }
                        }*/
                    }
                    ORIENTATION_LANDSCAPE -> {
                        /*v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            marginStart = if (isFullMode) {
                                0
                            } else {
                                displayCutout.safeInsetLeft
                            }
                            marginEnd = if (isFullMode) {
                                0
                            } else {
                                displayCutout.safeInsetRight
                            }
                        }*/
                    }
                    ORIENTATION_UNDEFINED -> Unit
                    ORIENTATION_SQUARE -> Unit
                }
            }
            ViewCompat.setOnApplyWindowInsetsListener(binding.root, null)
            insets
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
                        0, // clear value
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
                        APPEARANCE_LIGHT_NAVIGATION_BARS, // value
                        APPEARANCE_LIGHT_NAVIGATION_BARS // mask
                    )
                } else {
                    it.setSystemBarsAppearance(
                        0, // clear value
                        APPEARANCE_LIGHT_NAVIGATION_BARS // mask
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
                    it.hide(WindowInsets.Type.systemBars()) // ?????? ????????? ?????? Behavior ?????? ??????????????? ?????? ???????????? ?????? ????????? ??????.
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
        binding.fullScreenSwitch.isChecked = false
        window.decorView.systemUiVisibility = when (checkedId) {
            R.id.leanbackButton -> {
                supportActionBar?.hide()
                (View.SYSTEM_UI_FLAG_FULLSCREEN // ??? ????????? ??????
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // ?????? ??????????????? ??? ????????? ?????????
            }
            R.id.immersiveButton -> {
                supportActionBar?.hide()
                (View.SYSTEM_UI_FLAG_IMMERSIVE // ????????? ?????? SYSTEM UI ??????
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // ??? ????????? ??????
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // ?????? ??????????????? ??? ????????? ?????????
            }
            R.id.stickyImmersiveButton -> {
                supportActionBar?.hide()
                (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // ????????? ?????? SYSTEM UI ??????
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // ??? ????????? ??????
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // ?????? ??????????????? ??? ????????? ?????????
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
