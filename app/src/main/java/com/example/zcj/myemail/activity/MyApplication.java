package com.example.zcj.myemail.activity;

import android.app.Application;


import com.example.zcj.myemail.bean.MailInfo;

import java.io.InputStream;
import java.util.ArrayList;

import javax.mail.Session;
import javax.mail.Store;

public class MyApplication extends Application {
    public static Session session = null;
    private static Store store;

    public static Store getStore() {
        return store;
    }

    public static void setStore(Store store) {
        MyApplication.store = store;
    }

    public static MailInfo info = new MailInfo();

    private ArrayList<InputStream> attachmentsInputStreams;

    public ArrayList<InputStream> getAttachmentsInputStreams() {
        return attachmentsInputStreams;
    }

    public void setAttachmentsInputStreams(ArrayList<InputStream> attachmentsInputStreams) {
        this.attachmentsInputStreams = attachmentsInputStreams;
    }

}
