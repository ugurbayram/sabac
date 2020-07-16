package no.uio.sabac;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author ugurb@ifi.uio.no
 */
public class ServletInitializer extends SpringBootServletInitializer {

    //@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SabacApplication.class);
    }

}
