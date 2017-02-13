package com.example.zcj.myemail.bean;

import com.example.zcj.myemail.Utils.Attachment;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * 邮件的基本信息
 * @author Administrator
 *
 */
public class MailInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	// 发送邮件的服务器的IP和端口
	private String mailServerHost;
	private String mailServerPort = "25";
	// 登陆邮件发送服务器的用户名和密码
	private String userName;
	private String password;
	// 是否需要身份验证
	private boolean validate = false;
	// 邮件发送者的地址
	private String fromAddress;
	// 邮件主题
	private String subject;
	// 邮件的文本内容
	private String content;
	// 邮件附件的路径
	private List<Attachment> attachmentInfos;
	// 邮件的接收者，可以有多个
	private String[] receivers;

	/**
	 * 获得邮件会话属性
	 */
	public Properties getProperties() {

		Properties props = new Properties();
		props.put("mail.smtp.host", this.mailServerHost);
		props.put("mail.smtp.port", this.mailServerPort);
		props.put("mail.smtp.auth", validate ? "true" : "false");
		/**  QQ邮箱需要建立ssl连接 */
		props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.imap.socketFactory.fallback", "false");
		props.setProperty("mail.imap.starttls.enable","true");
//		props.setProperty("mail.store.protocol", "imap");
//		props.setProperty("mail.imap.host", "imap.163.com");
//		props.setProperty("mail.imap.port", "143");
		return props;
	}

	public String[] getReceivers() {
		return receivers;
	}

	public void setReceivers(String[] receivers) {
		this.receivers = receivers;
	}

	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}


	public List<Attachment> getAttachmentInfos() {
		return attachmentInfos;
	}

	public void setAttachmentInfos(List<Attachment> attachmentInfos) {
		this.attachmentInfos = attachmentInfos;
	}



	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}
}
