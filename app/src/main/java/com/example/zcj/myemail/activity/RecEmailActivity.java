package com.example.zcj.myemail.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zcj.myemail.R;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;

/**
 * Created by zcj on 2017/2/9.
 */
public class RecEmailActivity extends ActivityBase implements View.OnClickListener, FolderListener {
    private static final String TAG = "recText";
    private TextView tv_RecEmail;
    private static final int SUCCESS = 1;
    private static final int FAILED = -1;
    private String RecMailText = "";
    private static Folder folder;
    private Button btn_createFolder;
    private Button btn_deleteFolder;
    private Button btn_renameFolder;
    private Button btn_moveFolder;
    private Properties props;
    private static Store store;
    private Session session;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_email);


        tv_RecEmail = (TextView) findViewById(R.id.text_rec);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
//                    android.os.Message message = new android.os.Message();
//                    if (IMAPRecMail()) {
//                        Log.d(TAG, "新建成功");
//                    }
//                    if (IMAPRecMail()){
//                    message.what = SUCCESS;
//                    handler.sendMessage(message);
//                    }else {
//                        message.what = FAILED;
//                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        btn_createFolder = (Button) findViewById(R.id.createFolder);
        btn_deleteFolder = (Button) findViewById(R.id.deleteFolder);
        btn_renameFolder = (Button) findViewById(R.id.renameFolder);
        btn_moveFolder = (Button) findViewById(R.id.moveMail);
        btn_createFolder.setOnClickListener(this);
        btn_deleteFolder.setOnClickListener(this);
        btn_renameFolder.setOnClickListener(this);
        btn_moveFolder.setOnClickListener(this);
    }

    private void init() throws MessagingException {
        // 准备连接服务器的会话信息
        props = new Properties();
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
        session = Session.getInstance(props);

        // 创建IMAP协议的Store对象
        store = session.getStore("imap");
        MyApplication.session = session;
        Log.d(TAG, "address   : " + MyApplication.info.getUserName() + "    pws   " + MyApplication.info.getPassword());
        // 连接邮件服务器
        store.connect(MyApplication.info.getUserName(), MyApplication.info.getPassword());
    }

    public boolean IMAPRecMail() throws MessagingException {
//        Folder [] folders =store.getDefaultFolder().list();
//        Log.d(TAG, "执行   folders: "+folders);
        //遍历文件夹
//        for (Folder folder:folders){
//            RecMailText +="folder.getURLName()    "+folder.getURLName()+"\n"+"folder.getName():     "+folder.getName()+"\n";
//        }
//        Log.d(TAG, "RecMailText: \n"+RecMailText);

        // 获得收件箱
//        folder = store.getFolder("INBOX.create");
        folder = store.getDefaultFolder();

        return createFolder(folder, "测试新建");


        // 以读写模式打开收件箱
//        folder.open(Folder.READ_WRITE);
//        folder.open(Folder.READ_ONLY);
//        folder.create(Folder.HOLDS_MESSAGES);

        // 获得收件箱的邮件列表
//        Message[] messages = folder.getMessages();
       /* RecMailText =
                "收件箱中共" + messages.length + "封邮件!\n" +
                        "收件箱中共" + folder.getMessageCount() + "封邮件!\n"
                        + "收件箱中共" + folder.getUnreadMessageCount() + "封未读邮件!\n"
                        + "收件箱中共" + folder.getNewMessageCount() + "封新邮件!\n\n"
                        + "收件箱中共" + folder.getDeletedMessageCount() + "封已删除邮件!\n"
                        + "folder.getFullName():    " + folder.getFullName() + "\n"
                        + "folder.getName():    " + folder.getName() + "\n"
                        + "folder.getParent():   " + folder.getParent() + "\n"
                        + "folder.getType():     " + folder.getType() + "\n"
                        + "folder.getMode():    " + folder.getMode() + "\n"
                        + "folder.getStore():    " + folder.getStore() + "\n";


        for (int i = 0; i < messages.length; i++) {
            RecMailText += messages[i].getFrom() + "    " + messages[i].getReceivedDate() + "  " + messages[i].getSubject() + "\n\n";
//            messages[i].
//            folder.get
        }*/
   /*     Log.d(TAG, "收件箱中共" + messages.length + "封邮件!\n\n"
                + "收件箱中共" + folder.getUnreadMessageCount() + "封未读邮件!\n\n"
                + "收件箱中共" + folder.getNewMessageCount() + "封新邮件!\n\n"
                + "收件箱中共" + folder.getDeletedMessageCount() + "封已删除邮件!");*/

        /*
        // 打印不同状态的邮件数量
        System.out.println("收件箱中共" + messages.length + "封邮件!");
        System.out.println("收件箱中共" + folder.getUnreadMessageCount() + "封未读邮件!");
        System.out.println("收件箱中共" + folder.getNewMessageCount() + "封新邮件!");
        System.out.println("收件箱中共" + folder.getDeletedMessageCount() + "封已删除邮件!");

        System.out.println("------------------------开始解析邮件----------------------------------");*/

       /* // 解析邮件
        for (Message message : messages) {
            IMAPMessage msg = (IMAPMessage) message;
            String subject = MimeUtility.decodeText(msg.getSubject());
            System.out.println("[" + subject + "]未读，是否需要阅读此邮件（yes/no）？");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String answer = reader.readLine();   www.2cto.com
            if ("yes".equalsIgnoreCase(answer)) {
                POP3ReceiveMailTest.parseMessage(msg);  // 解析邮件
                // 第二个参数如果设置为true，则将修改反馈给服务器。false则不反馈给服务器
                msg.setFlag(Flags.Flag.SEEN, true);   //设置已读标志
            }
        }*/

        // 关闭资源
//        folder.close(false);
//        store.close();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    tv_RecEmail.setText(RecMailText);
                    break;
                /*case FAILED:
                    Toast.makeText(RecEmailActivity.this, "查询收件箱失败", Toast.LENGTH_SHORT).show();
                    break;*/
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createFolder:

                break;
            case R.id.deleteFolder:

                break;
            //垃圾箱
            case R.id.renameFolder:
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            deleteMessage();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.moveMail:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            moveMail();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
        }
    }

    private void deleteMessage() throws MessagingException {
        folder = store.getFolder("Junk");
        folder.open(Folder.READ_ONLY);
        RecMailText =
                "垃圾箱中共" + folder.getMessageCount() + "封邮件!\n"
                        + "垃圾箱中共" + folder.getUnreadMessageCount() + "封未读邮件!\n"
                        + "垃圾箱中共" + folder.getNewMessageCount() + "封新邮件!\n\n"
                        + "垃圾箱中共" + folder.getDeletedMessageCount() + "封已删除邮件!\n"
                        + "folder.getFullName():    " + folder.getFullName() + "\n"
                        + "folder.getName():    " + folder.getName() + "\n"
                        + "folder.getParent():   " + folder.getParent() + "\n"
                        + "folder.getType():     " + folder.getType() + "\n"
                        + "folder.getMode():    " + folder.getMode() + "\n"
                        + "folder.getStore():    " + folder.getStore() + "\n";
        Log.d(TAG, "垃圾箱     : " + RecMailText);
        folder.close(true);
        store.close();
    }

    private void moveMail() throws MessagingException {
        Folder[] folders = store.getDefaultFolder().list();
        //遍历文件夹
        for (Folder f : folders) {
            RecMailText += "folder.getURLName()    " + f.getURLName() + "\n" + "folder.getName():     " + f.getName() + "\n";
        }
        Log.d(TAG, "RecMailText: \n" + RecMailText);

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        Folder dfolder = store.getFolder("测试新建");
        dfolder.open(Folder.READ_WRITE);
        javax.mail.Message[] msgs = folder.getMessages(1, 2);
        if (msgs.length != 0) {
            folder.copyMessages(msgs, dfolder);//复制到新文件夹
            folder.setFlags(msgs, new Flags(Flags.Flag.DELETED), true);//删除源文件夹下的邮件
        }
        folder.close(true);
        store.close();
    }

    @Override
    public void folderCreated(FolderEvent folderEvent) {
        Log.d(TAG, "文件夹创建成功！");
        Toast.makeText(RecEmailActivity.this, "文件夹创建成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void folderDeleted(FolderEvent folderEvent) {
        Log.d(TAG, "文件夹删除成功！");
    }

    @Override
    public void folderRenamed(FolderEvent folderEvent) {
        Log.d(TAG, "文件夹重命名成功！");
    }


    private boolean createFolder(Folder parent, String folderName) {
        boolean isCreated = true;

        try {
            Folder newFolder = parent.getFolder(folderName);
            isCreated = newFolder.create(Folder.HOLDS_MESSAGES);
            Log.d(TAG, "created: " + isCreated);
//            System.out.println("created: " + isCreated);

        } catch (Exception e) {
            Log.d(TAG, "Error creating folder: " + e.getMessage());
//            System.out.println("Error creating folder: " + e.getMessage());
            e.printStackTrace();
            isCreated = false;
        }
        return isCreated;
    }
}
