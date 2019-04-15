package kitchen.josh.simplejms.broker;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
