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
        UUID consumerId = destinationService.findDestination(toType(destinationType), destinationId)
                .map(SingleDestinationService::createConsumer)
                .orElseThrow(() -> new ApiException("Failed to create consumer for " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."));
        return new IdModel(consumerId);
    }

    @PostMapping(path = "/{destinationType}/{destinationId}/producer")
    public IdModel createProducer(@PathVariable String destinationType, @PathVariable UUID destinationId) {
        UUID consumerId = destinationService.findDestination(toType(destinationType), destinationId)
                .map(SingleDestinationService::createProducer)
                .orElseThrow(() -> new ApiException("Failed to create producer for " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."));
        return new IdModel(consumerId);
    }

    @DeleteMapping(path = "/{destinationType}/{destinationId}/consumer/{consumerId}")
    public void deleteConsumer(@PathVariable String destinationType, @PathVariable UUID destinationId,
                               @PathVariable UUID consumerId) {
        try {
            destinationService.findDestination(toType(destinationType), destinationId)
                    .orElseThrow(() -> new ApiException("Failed to delete consumer " + consumerId + " for " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."))
                    .removeConsumer(consumerId);
        } catch (ConsumerDoesNotExistException e) {
            throw new ApiException("Failed to delete consumer " + consumerId + " for " + destinationType + " " + destinationId + ": the consumer does not exist.");
        }
    }

    @DeleteMapping(path = "/{destinationType}/{destinationId}/producer/{producerId}")
    public void deleteProducer(@PathVariable String destinationType, @PathVariable UUID destinationId,
                               @PathVariable UUID producerId) {
        try {
            destinationService.findDestination(toType(destinationType), destinationId)
                    .orElseThrow(() -> new ApiException("Failed to delete producer " + producerId + " for " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."))
                    .removeProducer(producerId);
        } catch (ProducerDoesNotExistException e) {
            throw new ApiException("Failed to delete producer " + producerId + " for " + destinationType + " " + destinationId + ": the producer does not exist.");
        }
    }

    @PostMapping(path = "/{destinationType}/{destinationId}/producer/{producerId}/send")
    public void sendMessage(@PathVariable String destinationType, @PathVariable UUID destinationId,
                            @PathVariable UUID producerId, @RequestBody MessageModel message) {
        try {
            destinationService.findDestination(toType(destinationType), destinationId)
                    .orElseThrow(() -> new ApiException("Failed to send message to " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."))
                    .addMessage(producerId, message.getMessage());
        } catch (ProducerDoesNotExistException e) {
            throw new ApiException("Failed to send message to " + destinationType + " " + destinationId + ": the producer " + producerId + " does not exist.");
        }
    }

    @PostMapping(path = "/{destinationType}/{destinationId}/consumer/{consumerId}/receive")
    public MessageModel receiveMessage(@PathVariable String destinationType, @PathVariable UUID destinationId,
                                       @PathVariable UUID consumerId) {
        try {
            String message = destinationService.findDestination(toType(destinationType), destinationId)
                    .orElseThrow(() -> new ApiException("Failed to receive message: the " + destinationType + " " + destinationId + " does not exist."))
                    .readMessage(consumerId)
                    .orElse(null);
            return new MessageModel(message);
        } catch (ConsumerDoesNotExistException e) {
            throw new ApiException("Failed to receive message: the consumer " + consumerId + " does not exist.");
        }
    }

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageModel apiExceptionHandler(ApiException apiException) {
        return new MessageModel(apiException.getMessage());
    }

    private static DestinationType toType(String type) {
        try {
            return DestinationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
