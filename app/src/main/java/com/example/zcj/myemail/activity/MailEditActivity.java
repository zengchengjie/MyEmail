package com.example.zcj.myemail.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.example.zcj.myemail.R;
import com.example.zcj.myemail.Utils.Attachment;
import com.example.zcj.myemail.Utils.HttpUtil;
import com.example.zcj.myemail.Utils.MailReciver;
import com.example.zcj.myemail.Utils.SpUtil;
import com.example.zcj.myemail.Utils.UriUtil;
import com.example.zcj.myemail.adapter.GridViewAdapter;
import com.example.zcj.myemail.bean.MailReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

public class MailEditActivity extends Activity implements OnClickListener {
    private static final String TAG = "sessionTest-";
    private EditText mail_to;
    private EditText mail_from;
    private EditText mail_topic;
    private EditText mail_content;

    private Button send;
    private ImageButton add_lianxiren;
    private ImageButton attachment;
    private GridView gridView;
    private GridViewAdapter<Attachment> adapter = null;
    private int mailid = -1;
    private Button turnToRec;
    private static String newFolderName = "";

    private static final int SUCCESS = 1;
    private static final int FAILED = -1;
    private ProgressDialog dialog;
    HttpUtil util = new HttpUtil();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    dialog.cancel();
                    if (mailid > 0) {
                        Uri uri = Uri.parse("content://com.caogaoxiangprovider");
                        getContentResolver().delete(uri, "id=?", new String[]{mailid + ""});
                        uri = Uri.parse("content://com.attachmentprovider");
                        getContentResolver().delete(uri, "mailid=?", new String[]{mailid + ""});
                        //返回草稿箱
                        Toast.makeText(getApplicationContext(), "邮件发送成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "邮件发送成功", Toast.LENGTH_SHORT).show();
                        //清空之前填写的数据
                        mail_from.getText().clear();
                        mail_to.getText().clear();
                        mail_topic.getText().clear();
                        mail_content.getText().clear();
                        adapter = new GridViewAdapter<Attachment>(MailEditActivity.this);
                    }

                    break;
                case FAILED:
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "邮件发送失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_writer);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mail_to = (EditText) findViewById(R.id.mail_to);
        mail_from = (EditText) findViewById(R.id.mail_from);
        mail_topic = (EditText) findViewById(R.id.mail_topic);
        mail_content = (EditText) findViewById(R.id.content);
        send = (Button) findViewById(R.id.send);
        attachment = (ImageButton) findViewById(R.id.add_att);
        add_lianxiren = (ImageButton) findViewById(R.id.add_lianxiren);
        gridView = (GridView) findViewById(R.id.pre_view);
        turnToRec = (Button) findViewById(R.id.turnToRec);

        mail_from.setText(MyApplication.info.getUserName());
        send.setOnClickListener(this);
        attachment.setOnClickListener(this);
        add_lianxiren.setOnClickListener(this);
        turnToRec.setOnClickListener(this);

        adapter = new GridViewAdapter<Attachment>(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new MyOnItemClickListener());

        //判断是否从草稿箱来的
        mailid = getIntent().getIntExtra("mailid", -1);
        if (mailid > -1) {
            Uri uri = Uri.parse("content://com.caogaoxiangprovider");
            Cursor c = getContentResolver().query(uri, null, "mailfrom=? and id=?", new String[]{MyApplication.info.getUserName(), mailid + ""}, null);
            if (c.moveToNext()) {
                mail_to.setText(c.getString(2));
                mail_topic.setText(c.getString(3));
                mail_content.setText(c.getString(4));
            }

            uri = Uri.parse("content://com.attachmentprovider");
            c = getContentResolver().query(uri, null, "mailid=?", new String[]{mailid + ""}, null);
            List<Attachment> attachments = new ArrayList<Attachment>();
            while (c.moveToNext()) {
                Attachment att = new Attachment(c.getString(2), c.getString(1), c.getLong(3));
                attachments.add(att);
            }

            //显示附件
            if (attachments.size() > 0) {
                for (Attachment affInfos : attachments) {
                    adapter.appendToList(affInfos);
                    int a = adapter.getList().size();
                    int count = (int) Math.ceil(a / 4.0);
                    gridView.setLayoutParams(new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            (int) (94 * 1.5 * count)));
                }
            }
        }
    }

    //————————————————————————————————————————————————————————————————————————————————————————————————
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发送
            case R.id.send:
                sendThreeEmail();
                break;
            //接收
            case R.id.turnToRec:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            RecMail();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }

    }

    public void sendThreeEmail() {
        List<Attachment> mList = new ArrayList<>();
        File s = new File("/sdcard/xcxCase/");
        File[] files = s.listFiles();

        for (int i = 0; i < 3; i++) {
            File file = files[i];
            Log.d(TAG, "发送第 : " + i + " 封邮件...");
            Attachment affInfos = Attachment.GetFileInfo(file.getPath());
            if (affInfos == null) {
                return;
            }
            mList.add(affInfos);
            sendMail(mList);
        }
    }


    public void RecMail() throws Exception {


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
        Store store = session.getStore("imap");
        // 连接邮件服务器
        store.connect(MyApplication.info.getUserName(), MyApplication.info.getPassword());
        // 获得收件箱
        Folder folder = store.getFolder("INBOX");// 获得收件箱的邮件列表
        folder.open(Folder.READ_WRITE);
        javax.mail.Message[] msgs = folder.getMessages();
        javax.mail.Message[] message = folder.getMessages(msgs.length - 9, msgs.length);
        Log.d(TAG, "收件箱中邮件数目        : " + msgs.length);
        List<Integer> targetEmail = new ArrayList<>();
        for (int i = message.length - 1; i >= 0; i--) {
            // 自定义的邮件对象
            MailReciver re = new MailReciver((MimeMessage) message[i]);
            /*Log.d(TAG, "邮件对象    : "+re);
            Log.d(TAG, "邮件　" + i + "　主题:　" + re.getSubject()
                    + "邮件　" + i + "　发送时间:　" + re.getSentDate()
                    + "邮件　" + i + "　是否需要回复:　" + re.getReplySign()
                    + "邮件　" + i + "　是否已读:　" + re.isNew()
                    +"邮件　" + i + "　是否包含附件:　"+ re.isContainAttach(message[i])
                    + "邮件　" + i + "　发送人地址:　" + re.getFrom()
                    + "邮件　" + i + "　收信人地址:　" + re.getMailAddress("to")
                    + "邮件　" + i + "　抄送:　" + re.getMailAddress("cc")
                    + "邮件　" + i + "　暗抄:　" + re.getMailAddress("bcc")
                    + "yy年MM月dd日　HH:mm"
                    + "邮件　" + i + "　发送时间:　" + re.getSentDate()
                    + "邮件　" + i + "　邮件ID:　" + re.getMessageId());

            re.getMailContent(message[i]);*/
//            Log.d(TAG, "邮件　" + i + "　正文内容:　\n\n" + re.getBodyText()+"发送时间   ："+re.getSentDate());
//            Log.d(TAG, "第　" + (i+1) +"   封邮件发送时间   ："+re.getSentDate()+"   主题："+re.getSubject());


            if (re.getSubject().toString().equals("MainTheme")) {
//                ————————————保存附件————————————————
                Log.d(TAG, "目标邮件是第 " + i + " 封邮件" + folder);
                targetEmail.add(msgs.length - 9 + i);
//            re.setAttachPath("/sdcard/xcxCase/temp/");
//            re.saveAttachMent(message[i]);
            }
        }
        if (targetEmail.size() > 0) {
            Folder create = store.getDefaultFolder();

            newFolderName = "test" + (int) (Math.random() * 1000);

            if (createFolder(create, newFolderName)) {

                //遍历文件夹
                String s = "";
                Folder[] folders = store.getDefaultFolder().list();
                for (Folder f : folders) {
                    s += "folder.getName():     " + f.getName() + "\n";
                }
                Log.d(TAG, "文件夹 : \n" + s);
                store.close();

                Store newstore = session.getStore("imap");
                newstore.connect(MyApplication.info.getUserName(), MyApplication.info.getPassword());


                // 获得收件箱
                Folder newFolder = newstore.getFolder(newFolderName);// 获得收件箱的邮件列表
                //打开 移动 复制
                newFolder.open(Folder.READ_WRITE);


                Folder inboxFolder = newstore.getFolder("INBOX");
                inboxFolder.open(Folder.READ_WRITE);

                for (int i = 0; i < targetEmail.size(); i++) {
                    int position = targetEmail.get(i);
                    Log.d(TAG, "要移动的文件位置    : " + position);
                    javax.mail.Message[] moveMsg = inboxFolder.getMessages(position, position);
                    Log.d(TAG, "newFolder   : " + newFolder);

                    inboxFolder.copyMessages(moveMsg, newFolder);
                    Log.d(TAG, "执行完复制任务: ");
                    inboxFolder.setFlags(moveMsg, new Flags(Flags.Flag.DELETED), true);//删除源文件夹下的邮件
                }


                newFolder.close(true);
                inboxFolder.close(true);
                newstore.close();


                Store fujiantore = session.getStore("imap");
                fujiantore.connect(MyApplication.info.getUserName(), MyApplication.info.getPassword());
                // 获得收件箱
                Folder fujianFolder = fujiantore.getFolder(newFolderName);// 获得收件箱的邮件列表
                //打开 移动 复制
                fujianFolder.open(Folder.READ_WRITE);
                //附件
                javax.mail.Message[] mm = fujianFolder.getMessages();
                for (int i = 0; i < mm.length; i++) {
                    // 自定义的邮件对象
                    MailReciver re = new MailReciver((MimeMessage) message[i]);
                    re.setAttachPath("/sdcard/xcxCase/temp/");
                    re.saveAttachMent(mm[i]);
                }

                fujianFolder.close(true);
                fujiantore.close();
            }
//                Folder newFolder = store.getFolder(newName.toString());

//                Log.d(TAG, "newFolder: " + newName.toString() +"    收件箱对象：  " + folder);
//                newFolder.open(Folder.READ_WRITE);


        }
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
//————————————————————————————————————————————————————————————————————————————————————————————————————

    /**
     * 添加附件
     */
    private void addAttachment() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//允许用户选择特殊种类的数据，并返回（特殊种类的数据：照一张相片或录一段音）
        intent.setType("image/");// 查看类型，如果是其他类型，比如视频则替换成 video/*，或 */*
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri uri = null;
                    if (data != null) {
                        uri = data.getData();
                    }

//				String path = uri.getPath();
                    String path = UriUtil.getPath(MailEditActivity.this, uri);
//				Log.d(TAG, "新的方法获取的path: "+ newPath);
//				File file = new File(newPath);
//				if (!file.exists()){
//					Log.d(TAG, "新方法文件不存在！ ");
//				}else {
//					Log.d(TAG, "文件存在: ");
//				}

                    Attachment affInfos = Attachment.GetFileInfo(path);
                    adapter.appendToList(affInfos);
                    int a = adapter.getList().size();
                    int count = (int) Math.ceil(a / 4.0);
                    gridView.setLayoutParams(new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            (int) (94 * 1.5 * count)));
                    break;
            }
        }

        /**
         * 多个联系人
         */
        if (requestCode == 2) {
            List<String> chooseUsers = data.getStringArrayListExtra("chooseUsers");
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < chooseUsers.size(); i++) {
                if (i == chooseUsers.size() - 1) {
                    str.append("<" + chooseUsers.get(i) + ">");
                } else {
                    str.append("<" + chooseUsers.get(i) + ">,");
                }
            }
            mail_to.setText(str.toString());

        }
    }

    /**
     * 设置邮件数据
     */
    private void sendMail(List<Attachment> mList) {
//        MyApplication.info.setAttachmentInfos(adapter.getList());
        MyApplication.info.setAttachmentInfos(mList);
//        MyApplication.info.setFromAddress(mail_from.getText().toString().trim());
//        MyApplication.info.setSubject(mail_topic.getText().toString().trim());
//        MyApplication.info.setContent(mail_content.getText().toString().trim());
        MyApplication.info.setFromAddress("649011593@qq.com");
        MyApplication.info.setSubject("MainTheme".trim());
        MyApplication.info.setContent("MainContent".toString().trim());
        //收件人
//        String str = mail_to.getText().toString().trim();
        String str = "649011593@qq.com";
//        Log.d(TAG, "测试  str: 	" + str);
        String[] recevers = str.split(",");
        for (int i = 0; i < recevers.length; i++) {
            if (recevers[i].startsWith("<") && recevers[i].endsWith(">")) {
                recevers[i] = recevers[i].substring(recevers[i].lastIndexOf("<") + 1, recevers[i].lastIndexOf(">"));
            }
        }
        MyApplication.info.setReceivers(recevers);


        //发送邮件
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在发送");
        dialog.show();

        /**
         * 发送
         */
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "run ~~~~~  session  : " + MyApplication.session);
                boolean flag = util.sendTextMail(MyApplication.info, MyApplication.session);
                Message msg = new Message();
                if (flag) {
                    msg.what = SUCCESS;
                    handler.sendMessage(msg);
                } else {
                    msg.what = FAILED;
                    handler.sendMessage(msg);
                }
            }

        }.start();

    }

    /**
     * 点击事件
     *
     * @author Administrator
     */
    private class MyOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1,
                                final int arg2, long arg3) {
            Attachment infos = (Attachment) adapter.getItem(arg2);
            Builder builder = new Builder(
                    MailEditActivity.this);
            builder.setTitle(infos.getFileName());
//			builder.setIcon(getResources().getColor(R.color.transparent));
            builder.setMessage("是否删除当前附件");
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            adapter.clearPositionList(arg2);
                            int a = adapter.getList().size();
                            int count = (int) Math.ceil(a / 4.0);
                            gridView.setLayoutParams(new LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    (int) (94 * 1.5 * count)));
                        }
                    });
            builder.setPositiveButton("取消", null);
            builder.create().show();
        }
    }
}
