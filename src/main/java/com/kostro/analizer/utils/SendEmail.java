package com.kostro.analizer.utils;

import com.kostro.analizer.scheduler.Scheduler;
import com.kostro.analizer.wallet.Candle;
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
import java.util.Properties;

public class SendEmail {

  private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
  
  public static void volume(Candle candle, Candle fiveMins, Candle oneHour, Candle twoHours, Candle oneDay) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      String subject = MessageFormat.format("{0} {1, number, #.##} to {2, number, #.##} with v:{3, number, #.##}",
              candle.getClose() > candle.getOpen() ? "RISING" : "FALLING",
              candle.getClose() - candle.getOpen(),
              candle.getClose(),
              candle.getVolume());

      message.setSubject(subject);
      message.setContent(
          MessageFormat.format("VOLUME: {0, number, #.##} at {1}<br> {2}  -> change: {7, number, #.##}<br>5 mins candle: {3} -> change: {8, number, #.##}<br>1 hour candle: {4} -> change: {9, number, #.##}<br>2 hours candle: {5} -> change: {10, number, #.##}<br>1 day candle: {6} -> change: {11, number, #.##}",
                  candle.getVolume(), candle.getTime(), candle, fiveMins, oneHour, twoHours, oneDay,
                  candle.getClose() - candle.getOpen(),//7
                  candle.getClose() - fiveMins.getOpen(),//8
                  candle.getClose() - oneHour.getOpen(),//9
                  candle.getClose() - twoHours.getOpen(),//10
                  candle.getClose() - oneDay.getOpen()//11
              ),
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

  public static void buy(Candle candle) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      message.setSubject("TRYING TO BUY: for " + candle.getClose() + " from " + candle.getTime());
      message.setContent(
              MessageFormat.format("TRYING TO BUY: for: {0} from {1}, volume: {2}",
                      new Object[] {candle.getOpen(), candle.getTime(), candle.getVolume()}),
              "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public static void bought(Candle candle) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      message.setSubject("BOUGHT: " + candle);
      message.setContent(
              MessageFormat.format("BOUGHT: {0}",
                      new Object[] {candle}),
              "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public static void sell(Candle candle) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      message.setSubject("TRYING TO SELL: for " + candle.getClose() + " from " + candle.getTime());
      message.setContent(
              MessageFormat.format("TRYING TO SELL: for: {0} from {1}, volume: {2}",
                      new Object[] {candle.getOpen(), candle.getTime(), candle.getVolume()}),
              "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public static void sold(Candle candle) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      message.setSubject("SOLD: " + candle);
      message.setContent(
              MessageFormat.format("SOLD: {0}",
                      new Object[] {candle}),
              "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public static void level(Candle candle, int level, double max, boolean rised) {
    try {
      Message message = new MimeMessage(prepareSession());
      message.setFrom(new InternetAddress("Market Analizer <expense_system@mailplus.pl>"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("miketo@o2.pl"));

      String subject = MessageFormat.format("{0} {1, number, #} at {2}",
              rised ? "ABOVE" : "BELOW",
              level,
              candle.getTime());

      message.setSubject(subject);
      message.setContent(
              MessageFormat.format("{0, number, #.} {1} max: {2}<br>{3} to {4, number, #.##} at {5}",
                      max - level,//0
                      rised ? "to" : "from",//1
                      max,//2
                      candle.getClose() > candle.getOpen() ? "RISING" : "FALLING",//3
                      candle.getClose(),//4
                      candle.getTime()),//5
              "text/html; charset=UTF-8");

      log.info("SendEmail: Sending");
      Transport.send(message);
      log.info("SendEmail: Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}