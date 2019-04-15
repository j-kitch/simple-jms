package kitchen.josh.simplejms.broker;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class TopicController {

    private final TopicService topicService;

    TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping(path = "/consumer")
    ConsumerId createConsumer() {
        return new ConsumerId(topicService.createConsumer());
    }

    @PostMapping(path = "/consumer/{consumerId}")
    Message readMessage(@PathVariable UUID consumerId) {
        return topicService.readMessage(consumerId)
                .map(Message::new)
                .orElse(new Message(null));
    }
}
