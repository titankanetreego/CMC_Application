package com.example.cmc_application;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cmc_application.comm.IMessage;
import com.example.cmc_application.dialog.BaseDialog;
import com.example.cmc_application.dialog.ErrorCodeDialog;
import com.example.cmc_application.model.SettingsModel;
import com.example.cmc_application.presenter.MainPresenter;
import com.example.cmc_application.util.ByteUtil;
import com.example.cmc_application.util.Util;

import java.util.List;

import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class MainActivity extends BaseActivity implements MainPresenter.View{
    @BindViews({R.id.set_recharging_time_button, R.id.set_max_voltage_button, R.id.set_max_current_button, R.id.set_years_button, R.id.set_months_button, R.id.set_serial_button})
    List<Button> buttons;

    private MainPresenter presenter;
    private SettingsModel settingsData;
    private TextView volFirst_tv, volSecond_tv, recCurrent_tv, kiosk_tv, soc_tv,
            inDetP_tv, inDetN_tv, status_tv, seccErrCode_tv,
            modCodeL_tv, modCodeH_tv, evccL_tv, evccH_tv, seccVer_tv;
    private EditText rechargingTime_edit, maxVoltage_edit, maxCurrent_edit, year_edit, month_edit, serial_edit;
    private RelativeLayout bgView;
    private LinearLayout enView;
    private int index = 0;
    private int doorIndex = 0;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MainPresenter(mParent(), this);
        settingsData = presenter.getSettingData();
        mediaPlayer = MediaPlayer.create(mParent().getApplicationContext(), R.raw.do_big_things);
        mediaPlayer.start();
        createAndShowGUI();
    }

    @Override
    protected void createAndShowGUI() {
        bgView = (RelativeLayout) findViewById(R.id.background_view);
        enView = (LinearLayout) findViewById(R.id.engineering_mode_view);
        volFirst_tv = (TextView) findViewById(R.id.voltage_first_tv);
        volSecond_tv = (TextView) findViewById(R.id.voltage_second_tv);
        recCurrent_tv = (TextView) findViewById(R.id.recharging_current_tv);
        kiosk_tv = (TextView) findViewById(R.id.kiosk_tv);
        soc_tv = (TextView) findViewById(R.id.soc_tv);
        inDetP_tv = (TextView) findViewById(R.id.insulation_detection_p_tv);
        inDetN_tv = (TextView) findViewById(R.id.insulation_detection_n_tv);
        status_tv = (TextView) findViewById(R.id.status_tv);
        seccErrCode_tv = (TextView) findViewById(R.id.secc_error_code_tv);
        modCodeL_tv = (TextView) findViewById(R.id.module_code_l_tv);
        modCodeH_tv = (TextView) findViewById(R.id.module_code_h_tv);
        evccL_tv = (TextView) findViewById(R.id.evcc_l_tv);
        evccH_tv = (TextView) findViewById(R.id.evcc_h_tv);
        seccVer_tv = (TextView) findViewById(R.id.secc_ver_tv);

        rechargingTime_edit = (EditText) findViewById(R.id.set_recharging_time_edit);
        maxVoltage_edit = (EditText) findViewById(R.id.set_max_voltage_edit);
        maxCurrent_edit = (EditText) findViewById(R.id.set_max_current_edit);
        year_edit = (EditText) findViewById(R.id.set_years_edit);
        month_edit = (EditText) findViewById(R.id.set_months_edit);
        serial_edit = (EditText) findViewById(R.id.set_serial_edit);

        rechargingTime_edit.setText(settingsData.getTime());
        maxVoltage_edit.setText(settingsData.getVoltage());
        maxCurrent_edit.setText(settingsData.getCurrent());
        year_edit.setText(settingsData.getYear());
        month_edit.setText(settingsData.getMonth());
        serial_edit.setText(settingsData.getSerial());

        ImageView img = (ImageView) findViewById(R.id.backdoor_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorIndex++;
                if (doorIndex == 5){
                    bgView.setVisibility(View.GONE);
                    enView.setVisibility(View.VISIBLE);
                    doorIndex = 0;
                }
            }
        });

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                index = index + 10;
                presenter.updateStatus(index);
                if (index % 9 == 0)
                    index = 0;
                handler.postDelayed(this,2000);
            }
        };
        handler.postDelayed(runnable, 0);

        mReadOpen();
        mSendOpen();
    }

    @Override
    public void refresh() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void onMessageEvent(IMessage message) {
        super.onMessageEvent(message);
        if (message.getAction().equals(S.ACTION_RECEIVED)) {
            Log.d("test",message.getCommand());
            if (message.getCommand().substring(0, 4).equals(S.ACTION_SETTING)) {
                mSendOpen();
                presenter.setSettingData(settingsData);
                presenter.setButtonEnable(buttons, true);
            }
            else
                presenter.updateValue(message.getCommand());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void updateStatus(int bgId) {
        bgView.setBackground(getResDrawable(bgId));
    }

    @Override
    public void updateValue(String title, String value) {
        switch (title){
            case S.EVCC_L:
                evccL_tv.setText(value);
                break;
            case S.EVCC_H:
                evccH_tv.setText(value);
                break;
            case S.KIOSK:
                kiosk_tv.setText(value);
                break;
            case S.SOC:
                soc_tv.setText(value);
                break;
            case S.STATUS:
                switch (value){
                    case "0":
                        status_tv.setText(getResString(R.string.status_0));
                        break;
                    case "1":
                        status_tv.setText(getResString(R.string.status_1));
                        break;
                    case "2":
                        status_tv.setText(getResString(R.string.status_2));
                        break;
                    case "3":
                        status_tv.setText(getResString(R.string.status_3));
                        break;
                    case "4":
                        status_tv.setText(getResString(R.string.status_4));
                        break;
                    case "5":
                        status_tv.setText(getResString(R.string.status_5));
                        break;
                    case "6":
                        status_tv.setText(getResString(R.string.status_6));
                        break;
                    case "7":
                        status_tv.setText(getResString(R.string.status_8));
                        break;
                    case "9":
                        status_tv.setText(getResString(R.string.status_10));
                        break;
                    case "11":
                        status_tv.setText(getResString(R.string.status_11));
                        break;
                    case "12":
                        status_tv.setText(getResString(R.string.status_12));
                        break;
                    case "13":
                        status_tv.setText(getResString(R.string.status_13));
                        break;
                    case "14":
                        status_tv.setText(getResString(R.string.status_14));
                        break;
                    case "238":
                        status_tv.setText(getResString(R.string.status_42));
                        break;
                }
                break;
            case S.SECC_VER:
                seccVer_tv.setText(value);
                break;
            case S.IN_DET_N:
                inDetN_tv.setText(value);
                break;
            case S.IN_DET_P:
                inDetP_tv.setText(value);
                break;
            case S.VOL_FIRST:
                volFirst_tv.setText(Util.decimalPointFirst(Integer.parseInt(value), 10));
                break;
            case S.REC_CURRENT:
                recCurrent_tv.setText(value);
                break;
            case S.REC_TIME:

                break;
            case S.SECC_ERRCODE:
                seccErrCode_tv.setText(String.valueOf(ByteUtil.hexStr2decimal(value)));
                break;
            case S.VOL_SECOND:
                volSecond_tv.setText(Util.decimalPointFirst(Integer.parseInt(value), 10));
                break;
            case S.MOD_ERRCODE:

                break;
            case S.MOD_CODE_L:
                modCodeL_tv.setText(value);
                break;
            case S.MOD_CODE_H:
                modCodeH_tv.setText(value);
                break;
        }
    }
    @OnClick({ R.id.callback_button, R.id.set_recharging_time_button, R.id.set_max_voltage_button,
            R.id.set_max_current_button, R.id.set_years_button, R.id.set_months_button, R.id.set_serial_button,
            R.id.status_button, R.id.secc_error_code_button, R.id.set_module_error_code_button, R.id.set_car_status_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.callback_button:
                bgView.setVisibility(View.VISIBLE);
                enView.setVisibility(View.GONE);
                break;
            case R.id.set_recharging_time_button:
                String valueTime = rechargingTime_edit.getText().toString();
                settingsData.setTime(valueTime);
                if (!valueTime.isEmpty())
                    settingSendCommand("20060054", valueTime);
                break;
            case R.id.set_max_voltage_button:
                String valueVoltage = maxVoltage_edit.getText().toString();
                settingsData.setVoltage(valueVoltage);
                if (!valueVoltage.isEmpty())
                    settingSendCommand("20060050", String.valueOf(Integer.parseInt(valueVoltage) * 10));
                break;
            case R.id.set_max_current_button:
                String valueCurrent = maxCurrent_edit.getText().toString();
                settingsData.setCurrent(valueCurrent);
                if (!valueCurrent.isEmpty())
                    settingSendCommand("20060051", String.valueOf(Integer.parseInt(valueCurrent) * 10));
                break;
            case R.id.set_years_button:
                String valueYears = year_edit.getText().toString();
                settingsData.setYear(valueYears);
                if (!valueYears.isEmpty())
                    settingSendCommand("20060056", valueYears);
                break;
            case R.id.set_months_button:
                String valueMonths = month_edit.getText().toString();
                settingsData.setMonth(valueMonths);
                if (!valueMonths.isEmpty())
                    settingSendCommand("20060057", valueMonths);
                break;
            case R.id.set_serial_button:
                String valueSerial = serial_edit.getText().toString();
                settingsData.setSerial(valueSerial);
                if (!valueSerial.isEmpty())
                    settingSendCommand("20060058", valueSerial);
                break;
            case R.id.status_button:

                break;
            case R.id.secc_error_code_button:
                ErrorCodeDialog completeDialog = new ErrorCodeDialog(mParent(), mParent().getApplicationContext(), R.string.set_car_status,
                        new BaseDialog.OnCompletionListener() {
                            @Override
                            public void onCompletion(Object result) {
                                if (result != null) {
                                }
                            }
                        });
                completeDialog.show();
                break;
            case R.id.set_module_error_code_button:

                break;
            case R.id.set_car_status_button:

                break;
        }
    }
    @OnEditorAction({R.id.set_recharging_time_edit, R.id.set_max_voltage_edit, R.id.set_max_current_edit, R.id.set_years_edit, R.id.set_months_edit, R.id.set_serial_edit})
    boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId== EditorInfo.IME_ACTION_DONE){
            hideKeyboard();
        }
        return true;
    }

    private void settingSendCommand(final String address, final String value){
        presenter.setButtonEnable(buttons, false);
        mSendClose();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String command = address + ByteUtil.decimal2fitHex(Integer.parseInt(value), 4);
                command = command + ByteUtil.getCrcCheckStr(command);
                sendCommand(command);
            }
        }, 500);
    }
}
