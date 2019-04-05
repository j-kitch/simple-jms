package kitchen.josh.simplejms.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BrokerTest {

    private Broker broker;

    @Before
    public void setUp() {
        this.broker = new Broker();
    }

    @Test
    public void stubTest() {
        assertThat(broker).isNotNull();
    }
}