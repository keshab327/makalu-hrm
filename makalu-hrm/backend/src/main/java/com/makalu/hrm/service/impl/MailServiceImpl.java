package com.makalu.hrm.service.impl;

import com.makalu.hrm.model.RestResponseDto;
import com.makalu.hrm.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Override
    public RestResponseDto sendMail(String to, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            javaMailSender.send(mail);
            return RestResponseDto.INSTANCE().success();
        } catch (Exception ex) {
            log.error("Error sending mail", ex);
            return RestResponseDto.INSTANCE().internalServerError();
        }
    }

    @Override
    public RestResponseDto sendHtmMail(String to, String subject, String body) {
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(mail);
            return RestResponseDto.INSTANCE().success();
        } catch (Exception ex) {
            log.error("Error sending mail", ex);
            return RestResponseDto.INSTANCE().internalServerError();
        }
    }
}


