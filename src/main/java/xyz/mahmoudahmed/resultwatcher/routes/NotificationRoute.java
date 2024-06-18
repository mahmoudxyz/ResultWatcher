package xyz.mahmoudahmed.resultwatcher.routes;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotificationRoute extends RouteBuilder {

    private final ProducerTemplate producerTemplate;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${notification.email.to}")
    private String to;

    @Value("${telegram.subscribers}")
    private String telegramSubscribers;

    @Value("${email.subscribers}")
    private String emailSubscribers;

    private List<Long> getTelegramSubscribers() {
        return Arrays.stream(telegramSubscribers.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    private List<String> getEmailSubscribers() {
        return Arrays.asList(emailSubscribers.split(","));
    }

    public NotificationRoute(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public void configure() {

        from("direct:sendEmail")
                .process(exchange -> {
                    // Loop through the subscribers and send the email
                    for (String email : getEmailSubscribers()) {
                        exchange.getIn().setHeader("to", email);
                        producerTemplate.sendBody("direct:sendEmailToSubscriber", exchange);
                    }
                });

        // Route to send email to a single subscriber
        from("direct:sendEmailToSubscriber")
                .setHeader("subject", constant("النتيجة ظهرت"))
                .setHeader("from", constant("mahmoudahmedxyz@gmail.com")) // Set your email here
                .setBody(simple("النتيجة ظهرت"))
                .toD("smtps://smtp.gmail.com:465?username={{spring.mail.username}}&password={{spring.mail.password}}");

        from("direct:sendTelegramMsgToAll")
                .process(exchange -> {
                    for (Long subscriber : getTelegramSubscribers()) {
                        exchange.getMessage().setHeader("chatId", subscriber);
                        producerTemplate.sendBody("telegram:bots?authorizationToken=7386190888:AAGsBuyTj9VuQTyNqYU2U6BfD3y-iJE8Yl8&chatId=" + subscriber, "النتيجة ظهرت, ربنا معاك ويفرح قلبك");

                    }
                });
    }
}
