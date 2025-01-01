package com.example.demo.utils;

import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

@Component
public class EmailService {
    HashMap<String, Pair> map; // emailKey : Member + create Date

    public EmailService() {
        map = new HashMap<>();
    }

    public void sendEmial(String toEmail){
        String emailKey = createEmailKey();
        sendEmail(toEmail, emailKey);
        map.put(emailKey, new Pair(toEmail));
    }

    public boolean checkEmailKey(String email, String emailKey){
        Pair pair = map.get(emailKey);
        if(pair == null || !pair.email.equals(email))
            return false;

        // update map
        return true;
    }

    private String createEmailKey(){
        return "11111";
    }

    // check time before check Email Key method >> emailkey값을 확인하기전에 시간 지난것들은 모두 만료 처리
    private void checkTime(){

    }

    private void sendEmail(String toEmail, String emailKey){
        String myEmail = "jspproject2024@gmail.com", myPasswd = "ccdd thdn wdlz ywuw";

        // SMTP 설정
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS 설정
        props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // 최신 TLS 버전 지정

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myEmail, myPasswd);
            }
        });

        try {
            // 이메일 메시지 작성
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Login Email Subject");
            message.setText("인증 코드 5자리 : " + String.format("%05d", emailKey));

            // 이메일 전송
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    class Pair{
        String email;
        Date createDate;

        Pair(String email){
            this.email = email;
            this.createDate = new Date();
        }
    }
}