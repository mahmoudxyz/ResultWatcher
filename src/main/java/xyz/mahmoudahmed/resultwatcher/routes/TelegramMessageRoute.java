package xyz.mahmoudahmed.resultwatcher.routes;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class TelegramMessageRoute extends RouteBuilder {

    @Value("${telegram.token}")
    private String telegramToken;

    private final ProducerTemplate producerTemplate;

    public TelegramMessageRoute(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }


    @Override
    public void configure() {
        from("telegram:bots?authorizationToken=" + telegramToken)
                .process(exchange -> {
                    String chatId = exchange.getIn().getHeader("CamelTelegramChatId", String.class);
                    IncomingMessage incomingMessage = exchange.getIn().getBody(IncomingMessage.class);
                    exchange.setProperty("chatId", Long.valueOf(chatId));
                    System.out.println("Stored chat ID: " + chatId + incomingMessage.getFrom());
                    producerTemplate.sendBody("telegram:bots?authorizationToken=" + telegramToken + "&chatId=5182296519L", chatId + " " + incomingMessage.getFrom());
                });
    }

}
