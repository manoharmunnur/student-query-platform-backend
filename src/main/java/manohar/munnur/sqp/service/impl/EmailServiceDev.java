package manohar.munnur.sqp.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import manohar.munnur.sqp.service.EmailService;

@Service
@Profile("dev")
public class EmailServiceDev implements EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtpEmail(String to, String otp) {

        String url = "https://api.brevo.com/v3/smtp/email";

        String body = """
        {
          "sender": { "email": "%s" },
          "to": [{ "email": "%s" }],
          "subject": "Your OTP Code",
          "htmlContent": "<p>Your OTP is <b>%s</b><br/>Valid for 5 minutes</p>"
        }
        """.formatted(senderEmail, to, otp);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
