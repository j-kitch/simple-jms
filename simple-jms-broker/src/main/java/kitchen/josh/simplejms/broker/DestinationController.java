package kitchen.josh.simplejms.broker;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @PostMapping(path = "/{destinationType}")
    public IdModel createDestination(@PathVariable String destinationType) {
        return new IdModel(destinationService.createDestination(toType(destinationType)));
    }

    @PostMapping(path = "/{destinationType}/{destinationId}/consumer")
    public IdModel createConsumer(@PathVariable String destinationType, @PathVariable UUID destinationId) {
        DestinationType destination = DestinationType.valueOf(destinationType.toUpperCase());
        UUID consumerId = destinationService.findDestination(destination, destinationId)
                .map(SingleDestinationService::createConsumer)
                .orElse(null);
        return new IdModel(consumerId);
    }

    @PostMapping(path = "/{destinationType}/{destinationId}/producer")
    public IdModel createProducer(@PathVariable String destinationType, @PathVariable UUID destinationId) {
        DestinationType destination = DestinationType.valueOf(destinationType.toUpperCase());
        UUID consumerId = destinationService.findDestination(destination, destinationId)
                .map(SingleDestinationService::createProducer)
                .orElse(null);
        return new IdModel(consumerId);
    }

    @DeleteMapping(path = "/{destinationType}/{destinationId}/consumer/{consumerId}")
    public void deleteConsumer(@PathVariable String destinationType, @PathVariable UUID destinationId,
                               @PathVariable UUID consumerId) {
        DestinationType destination = DestinationType.valueOf(destinationType.toUpperCase());
        destinationService.findDestination(destination, destinationId)
                .ifPresent(service -> service.removeConsumer(consumerId));
    }

    @DeleteMapping(path = "/{destinationType}/{destinationId}/producer/{producerId}")
    public void deleteProducer(@PathVariable String destinationType, @PathVariable UUID destinationId,
                               @PathVariable UUID producerId) {
        DestinationType destination = DestinationType.valueOf(destinationType.toUpperCase());
        destinationService.findDestination(destination, destinationId)
                .ifPresent(service -> service.removeProducer(producerId));
    }

    @PostMapping(path = "/{destinationType}/{destinationId}/producer/{producerId}/send")
    public void sendMessage(@PathVariable String destinationType, @PathVariable UUID destinationId,
                            @PathVariable UUID producerId, @RequestBody MessageModel message) {
        DestinationType destination = DestinationType.valueOf(destinationType.toUpperCase());
        destinationService.findDestination(destination, destinationId)
                .ifPresent(service -> service.addMessage(producerId, message.getMessage()));
    }

    @PostMapping(path = "/{destinationType}/{destinationId}/consumer/{consumerId}/receive")
    public MessageModel receiveMessage(@PathVariable String destinationType, @PathVariable UUID destinationId,
                                       @PathVariable UUID consumerId) {
        DestinationType destination = DestinationType.valueOf(destinationType.toUpperCase());
        String message = destinationService.findDestination(destination, destinationId)
                .flatMap(service -> service.readMessage(consumerId))
                .orElse(null);
        return new MessageModel(message);
    }

    private static DestinationType toType(String type) {
        try {
            return DestinationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
