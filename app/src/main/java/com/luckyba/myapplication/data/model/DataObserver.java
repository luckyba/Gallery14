package com.luckyba.myapplication.data.model;

import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DataObserver extends ContentObserver {
    public static final int SCAN_DATA_CALLBACK = 1;

    private Handler mHandler;
    public DataObserver(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Message message = Message.obtain();
        message.what = SCAN_DATA_CALLBACK;
        message.obj = "Data has changed";
        mHandler.sendMessage(message);
        Log.d("DataObserver", "MyContentObserver.onChange("+selfChange+")");
    }

}
