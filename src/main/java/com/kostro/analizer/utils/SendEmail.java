package com.kostro.analizer.utils;

import com.kostro.analizer.scheduler.Scheduler;
import com.kostro.analizer.wallet.Candel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

public class SendEmail {

  private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
  
  public static void volume(Candel candel, Candel fiveMins, Candel oneHour, Candel twoHours, Candel oneDay) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));
      
      message.setSubject("HUGE VOLUME: " + candel.getVolume() + " on " + candel.getTime());
      message.setContent(
          MessageFormat.format("HUGE VOLUME: {0} on {1}<br> {2}  -> change: {7}<br>5 mins candel: {3} -> change: {8}<br>1 hour candel: {4} -> change: {9}<br>2 hours candel: {5} -> change: {10}<br>1 day candel: {6} -> change: {11}",
              new Object[] {candel.getVolume(), candel.getTime(), candel, fiveMins, oneHour, twoHours, oneDay,
                      candel.getClose() > candel.getOpen() ? candel.getHigh()-candel.getLow() : candel.getLow() - candel.getHigh(),
                      candel.getClose() - fiveMins.getHigh(),
                      candel.getClose() - oneHour.getHigh(),
                      candel.getClose() - twoHours.getHigh(),
                      candel.getClose() - oneDay.getHigh(),
              }),
          "text/html; charset=UTF-8");

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

  public static void buy(Candel candel) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      message.setSubject("TRYING TO BUY: for " + candel.getClose() + " from " + candel.getTime());
      message.setContent(
              MessageFormat.format("TRYING TO BUY: for: {0} from {1}, volume: {2}",
                      new Object[] {candel.getOpen(), candel.getTime(), candel.getVolume()}),
              "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public static void bought(Candel candel) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      message.setSubject("BOUGHT: " + candel);
      message.setContent(
              MessageFormat.format("BOUGHT: {0}",
                      new Object[] {candel}),
              "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public static void sell(Candel candel) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      message.setSubject("TRYING TO SELL: for " + candel.getClose() + " from " + candel.getTime());
      message.setContent(
              MessageFormat.format("TRYING TO SELL: for: {0} from {1}, volume: {2}",
                      new Object[] {candel.getOpen(), candel.getTime(), candel.getVolume()}),
              "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public static void sold(Candel candel) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      message.setSubject("SOLD: " + candel);
      message.setContent(
              MessageFormat.format("SOLD: {0}",
                      new Object[] {candel}),
              "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}