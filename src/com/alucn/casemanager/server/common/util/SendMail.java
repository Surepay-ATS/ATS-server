package com.alucn.casemanager.server.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;


public class SendMail {
    
    private String[] recipients=null;
    private String subject=null;
    private String context=null;

    
  public SendMail(String[] recipients,String subject,String context){
    this.recipients=recipients;
    this.subject=subject;
    this.context=context;
  }
  
  public void sendEmail() throws MessagingException, UnsupportedEncodingException{
	  
    if(recipients!=null && recipients.length>0){
       System.setProperty("java.net.preferIPv4Stack", "true");
       Properties pro=new Properties();
	   pro.put("mail.transport.protocol","smtp");
	   pro.put("mail.smtp.host", "172.24.146.133");
	   pro.put("mail.smtp.connectiontimeout","10000");
	   pro.put("mail.smtp.timeout", "10000");
	   pro.setProperty("mail.smtp.from", "lei.k.huang@alcatel-lucent.com");
	   pro.setProperty("mail.smtp.auth", "false");

	  //create session
	   Session session=Session.getInstance(pro);
	  // Session session=Session.getDefaultInstance(pro,null);
	  // Session session=Session.getDefaultInstance(pro,authentication);
	   session.setDebug(true);
	   Transport ts;
	   ts=session.getTransport();
	   ts.connect();
       Message message=new MimeMessage(session);
       InternetAddress addressFron=new InternetAddress("No-Reply.SurepayCaseServer@alcatel-lucent.com");
       message.setFrom(addressFron);
	   InternetAddress[] addressTo=new InternetAddress[recipients.length];
	   for(int i=0;i<recipients.length;i++){
		   addressTo[i]=new InternetAddress(recipients[i]);
	   }
	   message.setRecipients(Message.RecipientType.TO, addressTo);
	   subject=MimeUtility.encodeText(new String(subject.getBytes(),"GB2312"), "GB2312", "B");
	   message.setSubject(subject);
	   message.setContent(context,"text/html;charset=UTF-8");
	   ts.sendMessage(message,message.getAllRecipients());
	   ts.close();
   }

  }
	public static void main(String[] args) throws UnsupportedEncodingException, MessagingException {
		String[] strings={"Haiqi.Wang@alcatel-lucent.com"};
		SendMail sendmail=new SendMail( strings, "For Test","hello world");
		sendmail.sendEmail();
	}
}
