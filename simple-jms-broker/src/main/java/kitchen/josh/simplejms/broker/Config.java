package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.message.MessageFactory;
import kitchen.josh.simplejms.common.message.MessageModelFactory;
import kitchen.josh.simplejms.common.message.body.BodyFactory;
import kitchen.josh.simplejms.common.message.body.BodyModelFactory;
import kitchen.josh.simplejms.common.message.headers.HeadersFactory;
import kitchen.josh.simplejms.common.message.headers.HeadersModelFactory;
import kitchen.josh.simplejms.common.message.properties.PropertiesFactory;
import kitchen.josh.simplejms.common.message.properties.PropertyModelFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public HeadersModelFactory headersModelFactory() {
        return new HeadersModelFactory();
    }

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
        return new MessageModelFactory(headersModelFactory(), propertyModelFactory(), bodyModelFactory());
    }

    @Bean
    public HeadersFactory headersFactory() {
        return new HeadersFactory();
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
        return new MessageFactory(headersFactory(), propertiesFactory(), bodyFactory());
    }
}
