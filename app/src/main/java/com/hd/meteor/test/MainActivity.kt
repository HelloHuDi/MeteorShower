package com.hd.meteor.test

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ViewSwitcher
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hd.meteor.MeteorBean
import com.hd.meteor.MeteorConfig
import com.hd.meteor.MeteorCreateCallback
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.concurrent.timerTask

class MainActivity : BaseActivity(), MeteorCreateCallback, EasyPermissions.PermissionCallbacks {

    private val RESULT_CODE = 100

    private val playing = AtomicBoolean(false)

    private var timer: Timer? = null

    private val musicUtil = MusicUtil(this)

    private var musicList = mutableListOf<Music>()

    private var musicPath: String? = null

    private var imagePath: String? = null

    private var rvMusics: RecyclerView? = null

    private var dialog: BottomSheetDialog? = null

    private var createMeteorCount = 0

    private var rotateAnimation: RotateAnimation? = null

    private val options = RequestOptions().circleCrop()

    private val vib by lazy { getSystemService(Service.VIBRATOR_SERVICE) as Vibrator }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Glide.with(this).asBitmap().load(R.drawable.run).apply(options).into(image)
        showMeteor()
        setTextSwitcher()
        updateCount()
        addAdapter()
        checkPermission()
    }

    private fun setTextSwitcher() {
        create_count.inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        create_count.outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        create_count.setFactory(textViewFactory)
        create_count.setCurrentText(resources.getString(R.string.create_meteor_count) + " :\n" + createMeteorCount)
    }

    private fun showMeteor() {
        val meteorConfig = MeteorConfig(this)
        meteorConfig.setMeteorBean(MeteorBean(this))
        meteorConfig.nightSkyBackgroundDrawable = ContextCompat.getDrawable(this, R.drawable.meteor_background1)
        meteorConfig.createCallback = this
        meteor.addConfig(meteorConfig)
    }

    private var contentView: RelativeLayout? = null

    private fun addAdapter() {
        contentView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_music_list, null) as RelativeLayout
        rvMusics = contentView!!.findViewById<RecyclerView>(R.id.rvMusics)
        rvMusics!!.layoutManager = LinearLayoutManager(this)
        rvMusics!!.itemAnimator = DefaultItemAnimator()
        rvMusics!!.adapter = commonAdapter()
    }

    private fun showBottomSheet() {
        if (dialog != null) {
            dialog!!.show()
            return
        }
        dialog = BottomSheetDialog(this)
        dialog!!.setContentView(contentView)
        dialog!!.show()
    }

    private fun hideBottomSheet() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    private fun commonAdapter(): CommonAdapter<Music> {
        val commonAdapter = object : CommonAdapter<Music>(this, R.layout.item_music, musicList) {
            @SuppressLint("SetTextI18n")
            override fun convert(holder: ViewHolder?, music: Music?, position: Int) {
                if (holder != null && music != null && musicList.size > 0) {
                    holder.getView<TextView>(R.id.tv_details).text = music.fileName
                }
            }
        }
        commonAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemLongClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int): Boolean {
                return false
            }

            override fun onItemClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int) {
                musicUtil.stopPlayMusic()
                musicPath = musicList[position].path
                imagePath = musicList[position].album_art
                Glide.with(this@MainActivity).asBitmap().load(imagePath).apply(options).into(image)
                hideBottomSheet()
            }
        })
        return commonAdapter
    }

    private fun checkPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            queryAllMusic()
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.query_music),
                    RESULT_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        queryAllMusic()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkPermission()
        }
    }

    private fun queryAllMusic() {
        thread {
            musicList.addAll(musicUtil.scanMusic())
            runOnUiThread { rvMusics?.adapter?.notifyDataSetChanged() }
            Log.d("tag", "all :${musicList.size}+++$musicList")
        }
    }

    override fun onResume() {
        super.onResume()
        createAlbumImageAnimator()
        setTimer()
    }

    private fun createAlbumImageAnimator() {
        rotateAnimation?.cancel()
        rotateAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, //
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation!!.duration = 15000
        rotateAnimation!!.repeatCount = Animation.INFINITE
        rotateAnimation!!.interpolator = LinearInterpolator()
        image.animation = rotateAnimation
        rotateAnimation!!.start()
    }

    private fun setTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        timer = Timer()
        timer!!.schedule(timerTask {
            playing.set(false)
            SystemClock.sleep(1000)
            if (!playing.get()) {
                musicUtil.stopPlayMusic()
            }
        }, 2000, 1000)
    }

    override fun onStop() {
        super.onStop()
        rotateAnimation?.cancel()
        timer?.cancel()
        musicUtil.stopPlayMusic()
    }

    override fun create() {
        playing.set(true)
        createMeteorCount++
        if (!musicUtil.isPlaying) {
            if (musicPath.isNullOrEmpty())
                musicUtil.playDefaultMusic()
            else
                musicUtil.playMusic(musicPath)
        }
        runOnUiThread { updateCount() }
//        vibrate()
    }

    private var isVibrating = false

    private fun vibrate() {
        if (isVibrating) {
            vib.cancel()
            isVibrating = false
        }
        vib.vibrate(30)
        isVibrating = true
    }

    @SuppressLint("SetTextI18n")
    private fun updateCount() {
        create_count.setText(resources.getString(R.string.create_meteor_count) + " :\n" + createMeteorCount)
    }

    fun selectMusic(view: View?) {
        if (musicList.size > 0) {
            showBottomSheet()
        } else {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showBottomSheet()
            } else {
                checkPermission()
            }
        }
    }

    private val textViewFactory = ViewSwitcher.ViewFactory {
        // Create a new TextView
        val t = TextView(this@MainActivity)
        t.gravity = Gravity.START
        t.setTextColor(Color.WHITE)
        t.setLineSpacing(10f, 1f)
        t
    }
}
