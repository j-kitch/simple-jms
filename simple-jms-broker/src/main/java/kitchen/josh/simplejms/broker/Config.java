package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public PropertyModelFactory propertyModelFactory() {
        return new PropertyModelFactory();
    }

    @Bean
    public BodyModelFactory bodyModelFactory() {
        return new BodyModelFactory();
    }

    @Bean
    public MessageModelFactory messageModelFactory() {
        return new MessageModelFactory(propertyModelFactory(), bodyModelFactory());
    }

    @Bean
    public PropertiesFactory propertiesFactory() {
        return new PropertiesFactory();
    }

    @Bean
    public BodyFactory bodyFactory() {
        return new BodyFactory();
    }

    @Bean
    public MessageFactory messageFactory() {
        return new MessageFactory(propertiesFactory(), bodyFactory());
    }
}
