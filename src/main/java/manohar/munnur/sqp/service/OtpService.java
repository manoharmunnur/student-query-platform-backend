package manohar.munnur.sqp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    public void sendOtp(String toEmail, String otp) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        String body = """
        {
          "sender": { "email": "%s" },
          "to": [{ "email": "%s" }],
          "subject": "OTP Verification",
          "htmlContent": "<p>Your OTP is <b>%s</b></p>"
        }
        """.formatted(senderEmail, toEmail, otp);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }
}


//@Service
//@RequiredArgsConstructor
//public class OtpService {
//
//    private final JavaMailSender mailSender;
//
//    public String generateOtp() {
//        return String.valueOf(100000 + new Random().nextInt(900000));
//    }
//
//    public void sendOtpEmail(String email, String otp) {
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(email);
//        msg.setSubject("Password Reset OTP");
//        msg.setText("Your OTP is: " + otp + "\nValid for 5 minutes.");
//        mailSender.send(msg);
//    }
//}
//
