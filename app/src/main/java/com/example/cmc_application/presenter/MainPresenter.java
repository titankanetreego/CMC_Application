package com.example.cmc_application.presenter;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.cmc_application.R;
import com.example.cmc_application.S;
import com.example.cmc_application.comm.IMessage;
import com.example.cmc_application.model.SettingsModel;
import com.example.cmc_application.util.ByteUtil;
import com.example.cmc_application.util.Util;

import java.util.List;

public class MainPresenter {
    private View view;
    private AppCompatActivity parent;

    private String[] session_1 = {"32", "33", "34x", "35x", "36x", "37", "38", "39"};
    private String[] session_2 = {"40", "41", "42", "43x", "44", "45", "46", "47", "48", "49x"};
    private String[] session_3 = {"7D", "7C", "7E"};

    public MainPresenter(AppCompatActivity parent, View view) {
        this.view = view;
        this.parent = parent;
    }

    public void updateStatus(int command){
        switch (command) {
            case 10:
                view.updateStatus(R.mipmap.ic_u2_hmi_1);
                break;
            case 20:
                view.updateStatus(R.mipmap.ic_u2_hmi_2);
                break;
            case 30:
                view.updateStatus(R.mipmap.ic_u2_hmi_3);
                break;
            case 40:
                view.updateStatus(R.mipmap.ic_u2_hmi_4);
                break;
            case 50:
                view.updateStatus(R.mipmap.ic_u2_hmi_5);
                break;
            case 60:
                view.updateStatus(R.mipmap.ic_u2_hmi_6);
                break;
            case 70:
                view.updateStatus(R.mipmap.ic_u2_hmi_7);
                break;
            case 80:
                view.updateStatus(R.mipmap.ic_u2_hmi_8);
                break;
            case 90:
                view.updateStatus(R.mipmap.ic_u2_hmi_9);
                break;
        }
    }

    public void updateValue(String command){
        switch (command.length()){
            case 42:
                String value_1 = command.substring(6, 38);
                String evccL = String.valueOf(ByteUtil.hexStr2decimal(value_1.substring(0, 4)));
                view.updateValue(S.EVCC_L, evccL);
                String evccH = String.valueOf(ByteUtil.hexStr2decimal(value_1.substring(4, 8)));
                view.updateValue(S.EVCC_H, evccH);
                String kiosk = String.valueOf(ByteUtil.hexStr2decimal(value_1.substring(20, 24)));
                view.updateValue(S.KIOSK, kiosk);
                String soc = String.valueOf(ByteUtil.hexStr2decimal(value_1.substring(24, 28)));
                view.updateValue(S.SOC, soc);
                String status = String.valueOf(ByteUtil.hexStr2decimal(value_1.substring(28, 32)));
                view.updateValue(S.STATUS, status);
                break;
            case 50:
                String value_2 = command.substring(6, 46);
                String seccVer = String.valueOf(ByteUtil.hexStr2decimal(value_2.substring(0, 4)));
                view.updateValue(S.SECC_VER, seccVer);
                String inDetP = String.valueOf(ByteUtil.hexStr2decimal(value_2.substring(4, 8)));
                view.updateValue(S.IN_DET_N, inDetP);
                String inDetN = String.valueOf(ByteUtil.hexStr2decimal(value_2.substring(8, 12)));
                view.updateValue(S.IN_DET_P, inDetN);
                String voltageFirst = String.valueOf(ByteUtil.hexStr2decimal(value_2.substring(16, 20)));
                view.updateValue(S.VOL_FIRST, voltageFirst);
                String rechargingCurrent = String.valueOf(ByteUtil.hexStr2decimal(value_2.substring(20, 24)));
                view.updateValue(S.REC_CURRENT, rechargingCurrent);
                String rechargingTime = String.valueOf(ByteUtil.hexStr2decimal(value_2.substring(24, 28)));
                view.updateValue(S.REC_TIME, rechargingTime);
                String seccErrorCode = String.valueOf(ByteUtil.hexStr2decimal(value_2.substring(28, 32)));
                view.updateValue(S.SECC_ERRCODE, seccErrorCode);
                String voltageSecond = String.valueOf(ByteUtil.hexStr2decimal(value_2.substring(32, 36)));
                view.updateValue(S.VOL_SECOND, voltageSecond);
                break;
            case 22:
                String value_3 = command.substring(6, 18);
                String moduleErrorCode = String.valueOf(ByteUtil.hexStr2decimal(value_3.substring(0, 4)));
                view.updateValue(S.MOD_ERRCODE, moduleErrorCode);
                String moduleCodeL = String.valueOf(ByteUtil.hexStr2decimal(value_3.substring(4, 8)));
                view.updateValue(S.MOD_CODE_L, moduleCodeL);
                String moduleCodeH = String.valueOf(ByteUtil.hexStr2decimal(value_3.substring(8, 12)));
                view.updateValue(S.MOD_CODE_H, moduleCodeH);
                break;
        }
    }

    public void setButtonEnable(List<Button> buttons, boolean isEnable){
        for (Button b : buttons){
            b.setEnabled(isEnable);
        }
    }

    public SettingsModel getSettingData(){
        SettingsModel data = S.dbSetting.fetchFirstByKey(S.SETTINGS_DB_KEY);
        if (data == null)
            data = new SettingsModel();
        return data;
    }

    public void setSettingData(SettingsModel data){
        data.setKey(S.SETTINGS_DB_KEY);
        if (S.dbSetting.fetchFirstByKey(S.SETTINGS_DB_KEY) == null)
            S.dbSetting.insert(data);
        else
            S.dbSetting.update(data);
    }

    public interface View{
        void updateStatus(int bgId);
        void updateValue(String title, String value);
    }
}
