package kitchen.josh.simplejms.broker;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class TopicController {

    private final TopicService topicService;

    TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping(path = "/topic/consumer")
    ConsumerId createConsumer() {
        return new ConsumerId(topicService.createConsumer());
    }

    @PostMapping(path = "/topic/receive/{consumerId}")
    MessageModel readMessage(@PathVariable UUID consumerId) {
        return topicService.readMessage(consumerId)
                .map(MessageModel::new)
                .orElse(new MessageModel(null));
    }

    @PostMapping(path = "/topic/send")
    void sendMessage(@RequestBody MessageModel messageModel) {
        topicService.addMessage(messageModel.getMessage());
    }
}
