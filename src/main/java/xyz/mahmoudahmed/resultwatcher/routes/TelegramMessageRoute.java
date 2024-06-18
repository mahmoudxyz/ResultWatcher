package xyz.mahmoudahmed.resultwatcher.routes;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class TelegramMessageRoute extends RouteBuilder {

    private final ProducerTemplate producerTemplate;

    public TelegramMessageRoute(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }


    @Override
    public void configure() {
        from("telegram:bots?authorizationToken=7386190888:AAGsBuyTj9VuQTyNqYU2U6BfD3y-iJE8Yl8")
                .process(exchange -> {
                    String chatId = exchange.getIn().getHeader("CamelTelegramChatId", String.class);
                    System.out.println();
                    exchange.setProperty("chatId", Long.valueOf(chatId));
                    System.out.println("Stored chat ID: " + chatId);
                    producerTemplate.sendBody("telegram:bots?authorizationToken=7386190888:AAGsBuyTj9VuQTyNqYU2U6BfD3y-iJE8Yl8&chatId=5182296519L", chatId);
                });
    }

}
