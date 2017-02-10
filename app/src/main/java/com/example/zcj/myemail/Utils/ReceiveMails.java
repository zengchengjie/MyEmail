package com.example.zcj.myemail.Utils;

/**
 * Created by zcj on 2017/2/9.
 *
 *  IMAP方式
 *
 */

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

public class ReceiveMails {

    public static void main(String[] args) throws Exception {
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", "imap.qq.com");
        //143端口（IMAP）：143端口是为IMAP（INTERNET MESSAGE ACCESS PROTOCOL）服务开放的，是用于接收邮件的。
        props.setProperty("mail.imap.port", "143");

        /**  QQ邮箱需要建立ssl连接 */
        props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.starttls.enable","true");
        /**
         * 993端口（IMAPS）：993端口是为IMAPS（IMAP-over-SSL）协议服务开放的，这是IMAP协议基于SSL安全协议之上的一种变种协议，它继承了SSL安全协议的非对称加密的高度安全可靠性，可防止邮件泄露。
         */
        props.setProperty("mail.imap.socketFactory.port", "993");

        // 创建Session实例对象
        Session session = Session.getInstance(props);
        URLName urln = new URLName("imap", "imap.qq.com", 143, null,"601340241@qq.com","enwjrumuitsdbeii");
        // 创建IMAP协议的Store对象
        Store store = session.getStore(urln);
        store.connect();

        // 获得收件箱
        Folder folder = store.getFolder("INBOX");
        // 以读写模式打开收件箱
        folder.open(Folder.READ_WRITE);

        // 获得收件箱的邮件列表
        Message[] messages = folder.getMessages();

        // 打印不同状态的邮件数量
        System.out.println("收件箱中共" + messages.length + "封邮件!");
        System.out.println("收件箱中共" + folder.getUnreadMessageCount() + "封未读邮件!");
        System.out.println("收件箱中共" + folder.getNewMessageCount() + "封新邮件!");
        System.out.println("收件箱中共" + folder.getDeletedMessageCount() + "封已删除邮件!");

        System.out.println("------------------------开始解析邮件----------------------------------");

        // 解析邮件
        /** for (Message message : messages) {
         IMAPMessage msg = (IMAPMessage) message;
         String subject = MimeUtility.decodeText(msg.getSubject());
         System.out.println("[" + subject + "]未读，是否需要阅读此邮件（yes/no）？");
         BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         String answer = reader.readLine();
         if ("yes".equalsIgnoreCase(answer)) {
         // 第二个参数如果设置为true，则将修改反馈给服务器。false则不反馈给服务器
         msg.setFlag(Flag.SEEN, true);   //设置已读标志
         }
         } */

        // 关闭资源
        folder.close(false);
        store.close();
    }

}