package no.uio.sabac.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Spring boot initiator class
 *
 * @author ugurb@ifi.uio.no
 */
@Component
@Order(1)
@Profile("dev")
public class AppDocumentLoader implements CommandLineRunner {
    private Logger LOGGER = LoggerFactory.getLogger(AppDocumentLoader.class);

    /**
     * Spring boot initiator.
     *
     * @param args system arguments for the application
     */
    @Override
    public void run(String... args) {
        System.out.println("Starting the app!");

    }
}
