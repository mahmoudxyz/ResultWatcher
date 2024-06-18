package xyz.mahmoudahmed.resultwatcher.routes;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasksRoute extends RouteBuilder {


    private final ProducerTemplate producerTemplate;

    public ScheduledTasksRoute(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Value("${check.url}")
    private String url;


    @Override
    public void configure() throws Exception {
        from("timer:myTimer?period=5000")
                .routeId("ScheduledTasksRoute")
                .log("checking result")
                .to("http://" + url)
                .process(ex -> {
                    String content = ex.getIn().getBody(String.class);
                    if (content.contains("صيدله") || content.contains("صيدلة")) {
                        System.out.println("Result found and an notification should be sent in seconds");
                        producerTemplate.sendBody("direct:sendEmail", null);
                        producerTemplate.sendBody("direct:sendTelegramMsgToAll", null);
                    } else {
                        System.out.println("Result not found");
                    }
                });
    }
}
