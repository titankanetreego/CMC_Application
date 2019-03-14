package com.example.cmc_application;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.serialport.SerialPortFinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.cmc_application.comm.Device;
import com.example.cmc_application.comm.IMessage;
import com.example.cmc_application.comm.SerialPortManager;
import com.example.cmc_application.db.SettingsDataModule;
import com.example.cmc_application.model.SettingsModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    private final static int DB_VERSION = 3;
    private final static String DB_SETTINGS_NAME = "settings_info.db";

    protected boolean mWasPushedIn; // set to true if the activity was pushed in
    protected BaseActivity self = this;
    protected ProgressDialog progressDialog = null;

    private SerialPortManager sManager = SerialPortManager.instance();

    private Device mDevice;

    private int mDeviceIndex;
    private int mBaudrateIndex;

    private String[] mDevices;
    private String[] mBaudrates;

    private boolean mOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        S.dbSetting = new SettingsDataModule(getApplicationContext(), DB_SETTINGS_NAME, DB_VERSION);
        ButterKnife.bind(this);
        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey("isPushedIn")) {
            mWasPushedIn = true;
        }
        //畫面旋轉180
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        registerSystemUiVisibility();

        initDevice();
    }

    abstract protected void createAndShowGUI();

    public abstract void refresh();

    protected abstract int getLayoutId();

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void pushIntent(Intent i) {
        i.putExtra("isPushedIn", true);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.stationary);
        finish(); // close this activity
    }

    public void popActivity() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stationary, R.anim.slide_out_to_right);
    }

    @Override
    public void onBackPressed() {
        if (mWasPushedIn)
            popActivity();
        else
            super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            hideSystemUI();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IMessage message) {

    }

    public void mReadOpen(){
       sManager.mReadOpen();
    }

    public void mReadClose(){
        sManager.mReadClose();
    }

    public void mSendOpen(){
        sManager.mSendOpen();
    }

    public void mSendClose(){
        sManager.mSendClose();
    }

    public void sendCommand(String command){
        sManager.sendCommand(command);
    }

    protected AppCompatActivity mParent() {
        return this;
    }

    protected String getResString(int resId, int size) {
        return this.getResources().getString(resId, size);
    }

    protected String getResString(int resId) {
        return this.getResources().getString(resId);
    }

    protected String[] getResStringArray(int resId) {
        return this.getResources().getStringArray(resId);
    }

    protected int getResColor(int resId) {
        return getResources().getColor(resId);
    }

    protected Drawable getResDrawable(int resId) {
        return this.getResources().getDrawable(resId);
    }

    public Context mContext() {
        return this.getApplicationContext();
    }

    public void showToast(String msg) {
        Toast.makeText(self, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int msgId) {
        showToast(getString(msgId));
    }

    public ProgressDialog showProgress(int msgId) {
        if (progressDialog != null)
            return progressDialog;
        progressDialog = ProgressDialog.show(self, "", getString(msgId), true);
        return progressDialog;
    }

    public void dismissProgress() {
        if (progressDialog == null)
            return;

        progressDialog.dismiss();
        progressDialog = null;
    }

    private void initDevice() {
        SerialPortFinder serialPortFinder = new SerialPortFinder();

        // 设备
        mDevices = serialPortFinder.getAllDevicesPath();
        if (mDevices.length == 0) {
            mDevices = new String[] {
                    mParent().getString(R.string.no_serial_device)
            };
        }
        // 波特率
        mBaudrates = getResStringArray(R.array.baudrates);

        for (int i = 0; i < mDevices.length; i++){
            if (mDevices[i].equals("/dev/ttyS3")) {
                mDeviceIndex = i;
                break;
            }
        }
        for (int j = 0; j < mBaudrates.length; j++){
            if (mBaudrates[j].equals("19200")) {
                mBaudrateIndex = j;
                break;
            }
        }

        mDevice = new Device(mDevices[mDeviceIndex], mBaudrates[mBaudrateIndex]);
        mOpened = sManager.init(mDevice) != null;
        if (mOpened) {
            Log.d("SerialPort-Open", "true");
        } else {
            Log.d("SerialPort-Open", "false");
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void registerSystemUiVisibility() {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    hideSystemUI();
                }
            }
        });
    }

}
