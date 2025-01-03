package com.example.demo.utils;

import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Component
public class EmailService {
    HashMap<String, Pair> map; // emailKey : Member + create Date

    public EmailService() {
        map = new HashMap<>();
    }

    public void sendEmial(String toEmail){
        String emailKey = createEmailKey();
        sendEmail(toEmail, emailKey);
        System.out.println("email: "+ toEmail +", emailKey: "+ emailKey);
        map.put(emailKey, new Pair(toEmail)); // 같은 key값에 새로운 값을 넣으면 덮어쓰기 처리됨
    }

    public boolean checkEmailKey(String email, String emailKey){
        // update Key Map
        checkTime();

        Pair pair = map.get(emailKey);
        if(pair == null || !pair.email.equals(email))
            return false;

        // update map  erase key
        map.remove(emailKey);

        return true;
    }

    private String createEmailKey(){
        Random random = new Random(System.currentTimeMillis());
        String key = String.format("%05d", random.nextInt(100000));

        if(map.get(key) != null)
            key = createEmailKey();

        return key;
    }

    // check time before check Email Key method >> emailkey값을 확인하기전에 시간 지난것들은 모두 만료 처리
    private void checkTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 3);
        Date now = calendar.getTime(); // 1분 전으로 설정 (1분전에 생성된건 모두 버림)

        for(String e : map.keySet()){
            if(now.before(map.get(e).createDate))
                map.remove(e);
        }
    }

    private void sendEmail(String toEmail, String emailKey){
        String myEmail = "jspproject2024@gmail.com", myPasswd = "ccdd thdn wdlz ywuw";

        // SMTP 설정
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS 설정

        // 세션 생성
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
            message.setText("인증 코드 5자리 : " + emailKey);

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