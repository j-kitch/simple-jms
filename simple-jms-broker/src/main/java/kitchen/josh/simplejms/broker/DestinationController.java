package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.ErrorModel;
import kitchen.josh.simplejms.common.IdModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    /**
     * Create a new destination.
     *
     * @param destinationType the type of destination to create
     * @return the id of the created destination
     */
    @PostMapping(path = "/{destinationType}")
    public IdModel createDestination(@PathVariable String destinationType) {
        return new IdModel(destinationService.createDestination(toType(destinationType)));
    }

    /**
     * Handle an {@link ApiException} by returning 400 and the exception's message.
     *
     * @param apiException the exception to handle
     * @return the exception's message
     */
    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel apiExceptionHandler(ApiException apiException) {
        return new ErrorModel(apiException.getMessage());
    }

    private static DestinationType toType(String type) {
        try {
            return DestinationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
