package kitchen.josh.simplejms.broker;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class QueueController {

    private final QueueService queueService;

    QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping(path = "/queue/consumer")
    IdModel createConsumer() {
        return new IdModel(queueService.createConsumer());
    }

    @PostMapping(path = "/queue/receive/{id}")
    MessageModel readMessage(@PathVariable UUID id) {
        return queueService.readMessage(id)
                .map(MessageModel::new)
                .orElse(new MessageModel(null));
    }

    @PostMapping(path = "/queue/send")
    void addMessage(@RequestBody MessageModel message) {
        queueService.addMessage(message.getMessage());
    }
}
