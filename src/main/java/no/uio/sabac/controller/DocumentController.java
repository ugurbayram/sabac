package no.uio.sabac.controller;

import no.uio.sabac.bootstrap.OWLDecision;
import no.uio.sabac.domain.Document;
import no.uio.sabac.domain.DocumentBuilder;
import no.uio.sabac.utils.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Spring boot controller class.
 * HTTP Request handler for all three endpoints.
 *
 * @author ugurb@ifi.uio.no
 */
@RestController
@RequestMapping("/semantic-reasoner")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    /**
     * Http request handler for GET operation on /query-by-role/{roleName}
     *
     * @param roleName Subject role name
     * @return default return object Document
     */
    @GetMapping("/query-by-role/{roleName}")
    public ResponseEntity<Document> getByRole(@PathVariable(value = "roleName") String roleName) {
        Map decision = OWLDecision.queryByRole(roleName);
        if (!decision.isEmpty()) {
            Document document = new DocumentBuilder()
                    .setContext((Boolean) decision.get(Consts.IS_CONTEXT))
                    .setAttributes(decision.get(Consts.ATTRIBUTES))
                    .createDocument();
            return ResponseEntity.ok(document);
        }
        return ResponseEntity.ok(new DocumentBuilder().setContext(false).setAttributes(null).createDocument());
    }

    /**
     * Http request handler for GET operation on /query-by-access-type/{accessTypeName}/for/{roleName}.
     *
     * @param accessTypeName Access type name for object
     * @param roleName       Subject role name
     * @return default return object Document
     */
    @GetMapping("/query-by-access-type/{accessTypeName}/for/{roleName}")
    public ResponseEntity<Document> getByAccessType(@PathVariable(value = "accessTypeName") String accessTypeName, @PathVariable(value = "roleName") String roleName) {
        Map decision = OWLDecision.queryByAccessType(roleName, accessTypeName);
        if (!decision.isEmpty()) {
            Document document = new DocumentBuilder()
                    .setContext((Boolean) decision.get(Consts.IS_CONTEXT))
                    .setAttributes(decision.get(Consts.ATTRIBUTES))
                    .createDocument();
            return ResponseEntity.ok(document);
        }
        return ResponseEntity.ok(new DocumentBuilder().setContext(false).setAttributes(null).createDocument());
    }

    /**
     * Http request handler for GET operation on /query-by-access-type/{accessType}.
     *
     * @param accessType Access type name for object
     * @return default return object Document
     */
    @GetMapping("/query-by-access-type/{accessType}")
    public ResponseEntity<Document> getByAccessType(@PathVariable(value = "accessType") String accessType) {
        Map decision = OWLDecision.queryByAccessType(accessType);
        if (!decision.isEmpty()) {
            Document document = new DocumentBuilder()
                    .setContext((Boolean) decision.get(Consts.IS_CONTEXT))
                    .setAttributes(decision.get(Consts.ATTRIBUTES))
                    .createDocument();
            return ResponseEntity.ok(document);
        }
        return ResponseEntity.ok(new DocumentBuilder().setContext(false).setAttributes(null).createDocument());
    }


}



