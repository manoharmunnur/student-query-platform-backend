package manohar.munnur.sqp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import manohar.munnur.sqp.service.EmailService;

@Service
@Profile("local")
public class EmailServiceLocal implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + "\nValid for 5 minutes");
        mailSender.send(message);
    }
}
