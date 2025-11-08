package com.cemihsankurt.foodAppProject.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements  IEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    @Override
    public void sendVerificationEmail(String to, String link) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlMsg = "<h3>Welcome to FoodApp</h3>"
                    + "<p>To verify, press the link below:</p>"
                    + "<a href='" + link + "'>Verify</a>"
                    + "<br><br>"
                    + "<p>Link çalışmıyorsa: " + link + "</p>";

            helper.setTo(to);
            helper.setSubject("Hesabınızı Doğrulayın - FoodApp");
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}