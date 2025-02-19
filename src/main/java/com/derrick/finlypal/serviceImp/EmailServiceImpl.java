package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender javaMailSender;

  @Override
  public void sendEmail(String to, String subject, String body)
      throws InternalServerErrorException {
    SimpleMailMessage message = new SimpleMailMessage();

    try {
      log.info("Received request to send email to {}", to);
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);

      log.info("Sending email to {}", to);
      javaMailSender.send(message);
      log.info("Email sent to {}", to);
    } catch (MailException e) {
      log.error("Failed to send email to {}", to, e);
      throw new InternalServerErrorException(
          "An error occurred while sending email to " + e.getMessage());
    }
  }
}
