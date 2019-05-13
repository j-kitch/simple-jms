package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.MessageFactory;
import kitchen.josh.simplejms.common.MessageModelFactory;
import kitchen.josh.simplejms.common.PropertiesFactory;
import kitchen.josh.simplejms.common.PropertyModelFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public PropertyModelFactory propertyModelFactory() {
        return new PropertyModelFactory();
    }

    @Bean
    public MessageModelFactory messageModelFactory() {
        return new MessageModelFactory(propertyModelFactory());
    }

    @Bean
    public PropertiesFactory propertiesFactory() {
        return new PropertiesFactory();
    }

    @Bean
    public MessageFactory messageFactory() {
        return new MessageFactory(propertiesFactory());
    }
}
