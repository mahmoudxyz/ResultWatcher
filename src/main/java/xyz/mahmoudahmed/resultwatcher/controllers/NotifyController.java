package xyz.mahmoudahmed.resultwatcher.controllers;

import org.apache.camel.ProducerTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RequestMapping
@RestController
public class NotifyController {

    private final ProducerTemplate producerTemplate;

    public NotifyController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @GetMapping()
    public String greeting() {
        return "Hello World";
    }

    @PostMapping("users/notify")
    public String notifyUsersUsingTelegram(@RequestBody String message) {
        producerTemplate.sendBody("direct:sendTelegramMsgToAll", message.getBytes(StandardCharsets.UTF_8));
        return "Users should be Notified";
    }
}

