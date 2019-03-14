package com.example.cmc_application.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.cmc_application.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorCodeDialog extends BaseDialog{
    private int title;
    public String[][] data = {
            {"42","充電中Power異常停止"},
            {"43","終端機過熱停止"},
            {"50","後台緊急停止"},
            {"60","充電中PPCP OFF"},
            {"61","輸出電流大於柱端Limit_I"},
            {"62","車輛異常停止"},
            {"63","電壓超過Limit_Volt"},
            {"65","初始狀態PPCP On或車輛準備完成"},
            {"66","柱端充電中異常停止"},
            {"67","非充電中PPCP Off"},
            {"68","Can斷訊保護"},
            {"69","電子鎖鎖附失效"},
            {"6A","充電系統異常"},
            {"70","車端電流命令大於Limit_I"},
            {"91","驗證錯誤停止"},
            {"93","正常停止命令"},
            {"94","充電中時間到期正常停止"},
            {"95","充電中正常停止"},
            {"96","車端充電前正常停止"},
            {"97","柱端充電前正常停止"},
            {"98","一時停止中 時間到期"},
            {"9A","正常停止"},
            {"9B","車輛充電姿勢不對 正常停止"},
            {"9C","SOC滿電正常停止"},
            {"9D","Canbus後台SOC到達 正常停止"},
            {"9F","KIOSK正常停止"},
            {"0A00","錯誤異常"},
            {"0C00","緊急停止異常"},
            {"006E","緊急停止按鈕"},
            {"1000","電池與柱端能力不匹配"},
            {"2000","車端充電極值與電池能力不匹配"},
            {"3000","電子鎖無法上鎖"},
            {"4000","絕緣阻抗過低"},
            {"5000","電源追隨錯誤"},
            {"6000","絕緣短路測試失敗"},
            {"7000","絕緣偵測前車端繼電器未斷開"},
            {"10000","時間設定階段PPCP On逾時"},
            {"20000","10秒鎖附逾時"},
            {"30000","充電電流設定階段電流命令發送逾時"},
            {"40000","車端PPCP停止命令逾時"},
            {"50000","車端PPCP停止命令逾時"},
            {"60000","車端段開階段車端繼電器動作逾時"},
            {"70000","停止通訊階段Can中斷逾時"},
            {"80000","停止充電解鎖逾時"},
            {"B1","無耦合錯誤時產生"},
            {"B2","無耦合錯誤時產生"},
            {"B3","無耦合錯誤時產生"},
            {"B4","無耦合錯誤時產生"},
            {"B5","無耦合錯誤時產生"},
            {"B6","無耦合錯誤時產生"},
            {"B7","無耦合錯誤時產生"},
            {"A1","無耦合錯誤時產生"},
            {"A2","無耦合錯誤時產生"},
            {"A3","無耦合錯誤時產生"},
            {"A4","無耦合錯誤時產生"},
            {"A5","無耦合錯誤時產生"},
            {"A6","無耦合錯誤時產生"},
            {"A7","無耦合錯誤時產生"},
            {"A8","無耦合錯誤時產生"}
    };
    public ErrorCodeDialog(AppCompatActivity activity, Context context, int title, OnCompletionListener listener) {
        super(activity, context, listener);
        this.title = title;
    }
    @Override
    public AlertDialog show() {
        setView(R.layout.error_code_dialog);
        View view = getDialogView();
        TextView titleText = (TextView)view.findViewById(R.id.title_text);
        titleText.setText(getActivity().getString(title));
        ListView listView = (ListView)view.findViewById(R.id.anchor_table_list);
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for (int i=0;i < data.length;i++){
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("code", data[i][0]);
            item.put("title", data[i][1]);
            items.add(item);
        }

        //帶入對應資料
        SimpleAdapter adapter = new SimpleAdapter(
                getContext(),
                items,
                R.layout.list_adapter_error_code,
                new String[]{"code", "title"},
                new int[]{R.id.item_code, R.id.item_title}
        );
        listView.setAdapter(adapter);
        return super.show();
    }
}