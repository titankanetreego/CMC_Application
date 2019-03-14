package com.example.cmc_application.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public abstract class BaseDialog {

    private AppCompatActivity activity;
    private Context context;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private View view;
    private OnCompletionListener listener = null;

    public interface OnCompletionListener<T>{
        void onCompletion(T result);
    }

    public BaseDialog(AppCompatActivity activity, Context context, OnCompletionListener listener) {
        builder = new AlertDialog.Builder(activity);
        this.activity = activity;
        this.context = context;
        this.listener = listener;
    }

    public AlertDialog show() {
        if (builder != null) {
            dialog = builder.show();
        }
        //hideSystemUI();
        return dialog;
    }

    public void setTitle(int titleId) {
        builder.setTitle(titleId);
    }

    public void setTitle(String titleStr) {
        builder.setTitle(titleStr);
    }

    public void setTitle(TextView titleText) {
        builder.setCustomTitle(titleText);
    }

    public void setMessage(int msgId) {
        builder.setMessage(msgId);
    }

    public void setMessage(String msgStr) {
        builder.setMessage(msgStr);
    }

    public void setView(int layout) {
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(layout, null);
        builder.setView(view);
    }

    public void setDialogButton(int confirmId, int cancelId, DialogInterface.OnClickListener listener) {
        builder.setCancelable(false);
        if (cancelId != 0)
            builder.setPositiveButton(confirmId, listener);
        if (cancelId != 0)
            builder.setNegativeButton(cancelId, listener);
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public View getDialogView() {
        return view;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public AlertDialog.Builder getBuilder() {
        return builder;
    }

    public OnCompletionListener getListener() {
        return listener;
    }

    public Context getContext() {
        return context;
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public TextView getDialogTitle() {
        TextView title = (TextView) dialog.findViewById(android.R.id.title);
        return title;
    }

    public TextView getDialogMsg() {
        TextView msg = (TextView) dialog.findViewById(android.R.id.message);
        return msg;
    }

    private void hideSystemUI() {
        View decorView = dialog.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT < 16) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

}
