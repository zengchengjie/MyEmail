package com.example.zcj.myemail.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.zcj.myemail.R;
import com.sun.mail.imap.IMAPFolder;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Created by zcj on 2017/2/13.
 */
public class ContactActivity extends ActivityBase implements View.OnClickListener {
    private static final String TAG = "folderTest";
    private Button btnContactList;
    private Button btnNewContact;
    private TextView tvContactText;
    private Store store  = null;
    private String s ="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);
            initView();

    }

    private void initView() {
        btnContactList = (Button) findViewById(R.id.MyContactList);
        btnNewContact = (Button) findViewById(R.id.newContact);
        tvContactText = (TextView) findViewById(R.id.contactText);
        btnContactList.setOnClickListener(this);
        btnNewContact.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.MyContactList:
                //测试收件箱
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TestSession();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
            case R.id.newContact:

                break;
        }
    }

    private void TestSession() throws MessagingException {

        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", "imap.qq.com");
//        props.setProperty("mail.imap.host", "imap.163.com");
        props.setProperty("mail.imap.port", "143");

        /**  QQ邮箱需要建立ssl连接 */
        props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.starttls.enable", "true");
        props.setProperty("mail.imap.socketFactory.port", "993");

        // 创建Session实例对象
        Session session = Session.getInstance(props);

        // 创建IMAP协议的Store对象
        store = session.getStore("imap");
        MyApplication.session = session;
        // 连接邮件服务器
        store.connect(MyApplication.info.getUserName(), MyApplication.info.getPassword());
//        Folder folder = store.getFolder("INBOX");
        IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();
//        for (int i = 0; i < messages.length; i++) {
//            s += messages[i].getFrom() + "    " + messages[i].getReceivedDate() + "  " + messages[i].getSubject()+"\n\n";
//        }
        Log.d(TAG, "收件箱文件数目   ： "+folder.getMessageCount()+"\n\n"+s);
    }
}
