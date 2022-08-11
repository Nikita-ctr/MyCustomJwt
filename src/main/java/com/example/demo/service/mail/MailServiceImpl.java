package com.example.demo.service.mail;

import com.example.demo.domain.Email;
import com.example.demo.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;


    public MailServiceImpl(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendMail(Email email)  {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            Context context = new Context();
            context.setVariables(email.getProperties());
            helper.setFrom(email.getFrom());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            String html = templateEngine.process(email.getTemplate(), context);
            helper.setText(html, true);

            logger.debug("Sending email: {} with html body: {}", email, html);
            emailSender.send(message);
        }  catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }
    public Email generateEmail(User user, String token){

        String link = "http://localhost:8080/auth/accountVerification/" + token;

        Email email = new Email();
        email.setTo(user.getEmail());
        email.setFrom("nikita@bonado.com");
        email.setSubject("Register email");
        email.setTemplate("mailTemplate.html");

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", user.getUsername());
        properties.put("link",link);

        email.setProperties(properties);

        return email;
    }
}