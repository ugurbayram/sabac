package no.uio.sabac.domain;

/**
 * @author ugurb@ifi.uio.no
 */
public class DocumentBuilder {

    private boolean isContext;
    private Object attributes;

    public DocumentBuilder setContext(boolean context) {
        isContext = context;
        return this;
    }

    public DocumentBuilder setAttributes(Object attributes) {
        this.attributes = attributes;
        return this;
    }

    public Document createDocument() {
        return new Document(isContext, attributes);
    }
}
