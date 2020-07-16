package no.uio.sabac.bootstrap;

import no.uio.sabac.utils.Consts;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.springframework.util.ResourceUtils;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Main service class holding the complete business logic to parse the ontology
 *
 * @author ugurb@ifi.uio.no
 */
public class OWLDecision {

    private static OWLOntology healthOntology = null;
    private static OWLDataFactory dataFactory = null;
    private static OWLReasoner reasoner = null;
    private static OWLOntologyManager manager = null;

    /**
     * /query-by-role endpoint handler.
     *
     * @param roleName endpoint query parameter.
     * @return Map of string of string.
     */
    public static Map<String, String> queryByRole(String roleName) {
        Map context = new HashMap();
        context.put(Consts.IS_CONTEXT, false);
        Map individualTypeIndex = new HashMap<String, Set>();
        String index = "";
        DefaultPrefixManager pm = new DefaultPrefixManager(null, null, Consts.DEFAULT_PREFIX);
        OWLClass persons = getSingletonDataFactory().getOWLClass(IRI.create(pm.getDefaultPrefix(), Consts.STAFF));
        NodeSet<OWLNamedIndividual> individualsNodeSet = getReasoner().getInstances(persons, false);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();

        for (OWLNamedIndividual individual : individuals) {
            Stream<OWLClassExpression> typeOf = EntitySearcher.getTypes(individual, getSingletonOntology());
            OWLClassExpression individualType = typeOf.findFirst().get();
            String typeName = ((OWLClassImpl) individualType).getIRI().getRemainder().get();
            typeName = typeName.trim().toLowerCase();
            String roleStr = pm.getShortForm(individual).replace(":", "");

            //Init or add individual index attribute (synonym)
            if (!individualTypeIndex.containsKey(typeName)) {
                individualTypeIndex.put(typeName, new HashSet<String>(Arrays.asList(roleStr)));
            } else {
                ((HashSet) individualTypeIndex.get(typeName)).add(roleStr);
            }

            if (roleStr.toLowerCase().equals(roleName.trim().toLowerCase())) {
                index = typeName;
            }

        }
        if (!"".equals(index) && individualTypeIndex.containsKey(index)) {
            context.put(Consts.IS_CONTEXT, true);
            context.put(Consts.ATTRIBUTES, (new ArrayList<String>((HashSet) individualTypeIndex.get(index)).toArray()));
        }
        return context;
    }

    /**
     * /query-by-access-type endpoint handler.
     *
     * @param accessType endpoint query parameter.
     * @return
     */
    public static Map<String, String> queryByAccessType(String accessType) {
        Map context = new HashMap();
        context.put(Consts.IS_CONTEXT, false);
        Map individualTypeIndex = new HashMap<String, Set>();
        String index = "";
        DefaultPrefixManager pm = new DefaultPrefixManager(null, null, Consts.DEFAULT_PREFIX);
        OWLClass access = getSingletonDataFactory().getOWLClass(IRI.create(pm.getDefaultPrefix(), Consts.ACCESS));
        NodeSet<OWLNamedIndividual> individualsNodeSet = getReasoner().getInstances(access, false);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();

        for (OWLNamedIndividual individual : individuals) {
            Stream<OWLClassExpression> typeOf = EntitySearcher.getTypes(individual, getSingletonOntology());
            OWLClassExpression individualType = typeOf.findFirst().get();
            String typeName = ((OWLClassImpl) individualType).getIRI().getRemainder().get();
            typeName = typeName.trim().toLowerCase();
            String accessTypeStr = pm.getShortForm(individual).replace(":", "");
            //Init or add individual index attribute (synonym)
            if (!individualTypeIndex.containsKey(typeName)) {
                individualTypeIndex.put(typeName, new HashSet<String>(Arrays.asList(accessTypeStr)));
            } else {
                ((HashSet) individualTypeIndex.get(typeName)).add(accessTypeStr);
            }
            if (accessTypeStr.toLowerCase().equals(accessType.toLowerCase())) {
                index = typeName;
            }
        }
        if (!"".equals(index) && individualTypeIndex.containsKey(index)) {
            //if (roles.size() > 0) {
            context.put(Consts.IS_CONTEXT, true);
            context.put(Consts.ATTRIBUTES, (new ArrayList<String>((HashSet) individualTypeIndex.get(index)).toArray()));
        }
        return context;
    }

    /**
     * /query-by-access-type endpoint handler.
     *
     * @param roleName       endpoint query parameter.
     * @param accessTypeName endpoint query parameter.
     * @return
     */
    public static Map<String, String> queryByAccessType(String roleName, String accessTypeName) {
        Map context = new HashMap();
        context.put(Consts.IS_CONTEXT, false);
        String index = "";
        Map individualTypeIndex = new HashMap<String, Set>();
        if (!"".equals(roleName)) {
            Map roleSynonyms = queryByRole(roleName);
            if ((boolean) roleSynonyms.get(Consts.IS_CONTEXT)) {
                Object[] roleSynonymList = (Object[]) roleSynonyms.get(Consts.ATTRIBUTES);
                for (Object roleSynonym : roleSynonymList) {
                    DefaultPrefixManager pm = new DefaultPrefixManager(null, null, Consts.DEFAULT_PREFIX);
                    OWLNamedIndividual individual = new OWLNamedIndividualImpl(IRI.create(pm.getDefaultPrefix() + roleSynonym.toString()));
                    if (individual != null) {
                        OWLObjectPropertyImpl hasAccessType = new OWLObjectPropertyImpl(IRI.create(pm.getDefaultPrefix() + Consts.HAS_ACCESS_TYPE));
                        Stream<OWLIndividual> accessTypeSet = EntitySearcher.getObjectPropertyValues(individual, hasAccessType, getSingletonOntology());
                        Iterator it = accessTypeSet.iterator();
                        while (it.hasNext()) {
                            OWLIndividual propertyIndividual = (OWLIndividual) it.next();
                            String owlAccessTypeStr = pm.getShortForm((OWLEntity) propertyIndividual);
                            owlAccessTypeStr = owlAccessTypeStr.replace(":", "");

                            Stream<OWLClassExpression> typeOf = EntitySearcher.getTypes(propertyIndividual, getSingletonOntology());
                            OWLClassExpression individualType = typeOf.findFirst().get();
                            String typeName = ((OWLClassImpl) individualType).getIRI().getRemainder().get();
                            typeName = typeName.trim().toLowerCase();


                            //Init or add individual index attribute (synonym)
                            if (!individualTypeIndex.containsKey(typeName)) {
                                Map accessTypeSynonyms = queryByAccessType(owlAccessTypeStr);
                                if (accessTypeSynonyms != null && (boolean) accessTypeSynonyms.get(Consts.IS_CONTEXT)) {

                                    individualTypeIndex.put(typeName, accessTypeSynonyms.get(Consts.ATTRIBUTES));
                                    List<String> synonymList = convertToArrayList((Object[]) accessTypeSynonyms.get(Consts.ATTRIBUTES));
                                    if (synonymList.stream().anyMatch(accessTypeName::equalsIgnoreCase)) {
                                        index = typeName;
                                    }
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }
            }
        }
        if (!"".equals(index) && individualTypeIndex.containsKey(index)) {
            context.put(Consts.IS_CONTEXT, true);
            context.put(Consts.ATTRIBUTES, (individualTypeIndex.get(index)));
        }
        return context;
    }

    /**
     * Helper function converting array to list.
     *
     * @param objects
     * @return
     */
    private static List<String> convertToArrayList(Object[] objects) {
        List<String> list = new ArrayList<>();
        for (Object object : objects) {
            list.add(object.toString());
        }
        return list;
    }

    /**
     * Static function to get OWLReasoner as singleton.
     *
     * @return
     */
    private static OWLReasoner getReasoner() {
        if (reasoner == null) {
            try {

                System.out.println("Loaded ontology: " + getSingletonOntology().getOntologyID());
                IRI location = getSingletonOntologyManager().getOntologyDocumentIRI(getSingletonOntology());
                System.out.println("\tfrom: " + location);

                long time = System.currentTimeMillis();
                // get and configure a reasoner (HermiT)
                OWLReasonerFactory reasonerFactory = new ReasonerFactory();
                ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
                OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);

                // create the reasoner instance, classify and compute inferences
                reasoner = reasonerFactory.createReasoner(getSingletonOntology(), config);
                // perform all the inferences now, to avoid subsequent ad-hoc
                // reasoner calls
                reasoner.precomputeInferences(InferenceType.values());
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        return reasoner;

    }


    /**
     * Static function to get OWLOntologyManager as singleton.
     *
     * @return
     */
    private static OWLOntologyManager getSingletonOntologyManager() {
        if (manager == null) {
            manager = OWLManager.createOWLOntologyManager();
        }
        return manager;
    }

    /**
     * Static function to get OWLOntology as singleton.
     *
     * @return
     */
    private static OWLOntology getSingletonOntology() {
        if (healthOntology == null) {
            try {
                // load the (local) OWL ontology
                File file = ResourceUtils.getFile("classpath:healthrole-v3.owl");
                healthOntology = getSingletonOntologyManager().loadOntologyFromOntologyDocument(file);
            } catch (OWLOntologyCreationException e) {
                System.err.println("Impossible to load ");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.err.println("OWL File not found");
            }
        }
        return healthOntology;
    }

    /**
     * Static function to get OWLDataFactory as singleton.
     *
     * @return
     */
    private static OWLDataFactory getSingletonDataFactory() {
        if (dataFactory == null) {
            // init prefix manager
            dataFactory = getSingletonOntologyManager().getOWLDataFactory();
        }
        return dataFactory;
    }

}
