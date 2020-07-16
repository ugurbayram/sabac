package no.uio.sabac.domain;

import java.io.Serializable;

/**
 * @author ugurb@ifi.uio.no
 */
public class Document implements Serializable {

    private boolean isContext;

    private Object attributes;

    protected Document() {
    }

    protected Document(boolean isContext, Object attributes) {
        this.isContext = isContext;
        this.attributes = attributes;
    }

    public boolean isContext() {
        return isContext;
    }

    public void setContext(boolean context) {
        isContext = context;
    }

    public Object getAttributes() {
        return attributes;
    }

    public void setAttributes(Object attributes) {
        this.attributes = attributes;
    }
}
