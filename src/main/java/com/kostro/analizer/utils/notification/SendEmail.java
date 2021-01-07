package com.kostro.analizer.utils.notification;

import com.kostro.analizer.wallet.Candle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.Properties;

@Slf4j
@Service
public class SendEmail implements Notification {

  public void volume(String market, Candle candle, Candle fiveMins, Candle oneHour, Candle twoHours, Candle oneDay) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      String subject;
      if (candle.getClose() > 1000) {
        subject = MessageFormat.format("{0} - {1} {2, number, #.##} to {3, number, #.##} with v:{4, number, #.##}",
                market,
                candle.getClose() > candle.getOpen() ? "RISING" : "FALLING",
                candle.getClose() - candle.getOpen(),
                candle.getClose(),
                candle.getVolume());
      } else {
        subject = MessageFormat.format("{0} - {1} {2, number, #.#####} to {3, number, #.#####} with v:{4, number, #.#####}",
                market,
                candle.getClose() > candle.getOpen() ? "RISING" : "FALLING",
                candle.getClose() - candle.getOpen(),
                candle.getClose(),
                candle.getVolume());
      }

      message.setSubject(subject);
      Object content;
      if (candle.getClose() > 1000) {
        content = MessageFormat.format("VOLUME: {0, number, #.##} at {1}<br> {2}  -> change: {7, number, #.##}<br>5 mins candle: {3} -> change: {8, number, #.##}<br>1 hour candle: {4} -> change: {9, number, #.##}<br>2 hours candle: {5} -> change: {10, number, #.##}<br>1 day candle: {6} -> change: {11, number, #.##}",
                candle.getVolume(), candle.getTime(), candle, fiveMins, oneHour, twoHours, oneDay,
                candle.getClose() - candle.getOpen(),//7
                candle.getClose() - fiveMins.getOpen(),//8
                candle.getClose() - oneHour.getOpen(),//9
                candle.getClose() - twoHours.getOpen(),//10
                candle.getClose() - oneDay.getOpen()//11
        );
      } else {
        content = MessageFormat.format("VOLUME: {0, number, #.#####} at {1}<br> {2}  -> change: {7, number, #.#####}<br>5 mins candle: {3} -> change: {8, number, #.#####}<br>1 hour candle: {4} -> change: {9, number, #.#####}<br>2 hours candle: {5} -> change: {10, number, #.#####}<br>1 day candle: {6} -> change: {11, number, #.#####}",
                candle.getVolume(), candle.getTime(), candle, fiveMins, oneHour, twoHours, oneDay,
                candle.getClose() - candle.getOpen(),//7
                candle.getClose() - fiveMins.getOpen(),//8
                candle.getClose() - oneHour.getOpen(),//9
                candle.getClose() - twoHours.getOpen(),//10
                candle.getClose() - oneDay.getOpen()//11
        );
      }

      message.setContent(
          content,
          "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public void level(String market, Candle candle, double level, double max, boolean rised) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      String subject;
      if (candle.getClose() > 1000)
        subject = MessageFormat.format("{0} - {1} {2, number, #} at {3}",
                market,
                rised ? "ABOVE" : "BELOW",
                level,
                candle.getTime());
      else
        subject = MessageFormat.format("{0} - {1} {2, number, #.###} at {3}",
                market,
                rised ? "ABOVE" : "BELOW",
                level,
                candle.getTime());
      message.setSubject(subject);

      Object content;
      if (candle.getClose() > 1000) {
        content = MessageFormat.format("{0, number, #.} {1} max: {2}<br>{3} to {4, number, #.##} at {5}",
                max - level,//0
                rised ? "to" : "from",//1
                max,//2
                candle.getClose() > candle.getOpen() ? "RISING" : "FALLING",//3
                candle.getClose(),//4
                candle.getTime());
      } else {
        content = MessageFormat.format("{0, number, #.#####} {1} max: {2}<br>{3} to {4, number, #.#####} at {5}",
                max - level,//0
                rised ? "to" : "from",//1
                max,//2
                candle.getClose() > candle.getOpen() ? "RISING" : "FALLING",//3
                candle.getClose(),//4
                candle.getTime());
      }
      message.setContent(content, "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  private static Session prepareSession() {
    Properties props = new Properties();
    props.put("mail.smtp.host", System.getenv("MAIL_HOST"));
    props.put("mail.smtp.socketFactory.port", System.getenv("MAIL_PORT"));
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", System.getenv("MAIL_PORT"));
    Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(System.getenv("MAIL_USERNAME"), System.getenv("MAIL_PASSWORD"));
      }
    });
    return session;
  }
}