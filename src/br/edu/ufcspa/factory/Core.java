package br.edu.ufcspa.factory;

import br.edu.ufcspa.model.ClassName;
import br.edu.ufcspa.model.PathogenTransferByVector;
import br.edu.ufcspa.model.Transmission;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;

import java.util.*;

public class Core {

    private OWLDataFactory owlDataFactory;

    private IRI iri;

    private OWLOntology owlOntology;

    private HashMap<String, OWLClass> bioTopClasses;

    public Core(OWLDataFactory owlDataFactory, IRI iri, OWLOntology owlOntology) {
        this.owlDataFactory = owlDataFactory;
        this.iri = iri;
        this.owlOntology = owlOntology;
        this.bioTopClasses = this.bioTopClassesInitiator();
    }

    private HashMap<String, OWLClass> bioTopClassesInitiator(){

        HashMap<String, OWLClass> bioTopClasses = new HashMap<String, OWLClass>();

        IRI biotopIRI = IRI.create("http://purl.org/biotop/biotop.owl#");

        bioTopClasses.put(ClassName.PATHOLOGICALPROCESSBIOTOP, this.getClass(biotopIRI+ClassName.PATHOLOGICALPROCESSBIOTOP));



        return bioTopClasses;

    }

    public ChangeApplied declareClass(String className){

        OWLClass owlClass = this.owlDataFactory.getOWLClass(this.iri+"#"+className);
        OWLDeclarationAxiom declarationAxiom = this.owlDataFactory.getOWLDeclarationAxiom(owlClass);
        return this.owlOntology.add(declarationAxiom);
    }

    public ChangeApplied declareSubClassOf(String className, String subclassName){
        OWLClass owlClass = this.owlDataFactory.getOWLClass(this.iri+"#"+className);
        OWLClass owlSubClass = this.owlDataFactory.getOWLClass(this.iri+"#"+subclassName);
        OWLSubClassOfAxiom className_sub_subClassName = this.owlDataFactory.getOWLSubClassOfAxiom(owlSubClass, owlClass);
        return this.owlOntology.add(className_sub_subClassName);
    }

    public ChangeApplied disjointClasses(String[] classesName){

        List<String> classes = new ArrayList<>(Arrays.asList(classesName));

        return this.disjointClasses(classes);

    }

    public ChangeApplied disjointClasses(List<String> classesName){
        List<OWLClass> classesToBeDisjoint = new ArrayList<>();

        for (String className: classesName) {
            classesToBeDisjoint.add(this.getClass(className));
        }

        OWLDisjointClassesAxiom disjointClassesAxiom = this.owlDataFactory.getOWLDisjointClassesAxiom(classesToBeDisjoint);
        return this.owlOntology.add(disjointClassesAxiom);
    }

    public ChangeApplied equivalentClasses(List<OWLClass> equivalentClasses){

        OWLEquivalentClassesAxiom equivalentClassesAxiom = this.owlDataFactory.getOWLEquivalentClassesAxiom(equivalentClasses);
        return this.owlOntology.add(equivalentClassesAxiom);

    }



    public ChangeApplied equivalentClassToUnion(String className, List<String> classesToBeUnion){
        OWLObjectUnionOf unionOfClasses = this.unionOfClasses(classesToBeUnion);

        OWLClass classEquivalent = this.getClass(className);

        OWLEquivalentClassesAxiom equivalentClassesAxiom = this.owlDataFactory.getOWLEquivalentClassesAxiom(classEquivalent, unionOfClasses);

        return this.owlOntology.add(equivalentClassesAxiom);
    }

    public ChangeApplied pathogenTransferByVectorExistentialAxiom(PathogenTransferByVector pathogenTransferByVector){

        OWLClass pathogenTransferOWLClass = this.getClass(pathogenTransferByVector.className);
        //subClassOf
        OWLClass transfer = this.getClass(ClassName.TRANSFER);
        //and
        OWLObjectUnionOf hasAgent = this.unionOfClasses(pathogenTransferByVector.hasAgent);
        OWLObjectUnionOf hasGeographicLocation = this.unionOfClasses(pathogenTransferByVector.hasLocusGeographic);
        //or
        OWLObjectUnionOf hasPatient = this.unionOfClasses(pathogenTransferByVector.hasPatient);
        OWLObjectUnionOf isPhysicallyContainedIn = this.unionOfClasses(pathogenTransferByVector.hasLocusHost);

        List<OWLClassExpression> expressionsToBeIntersected = new ArrayList<>();
        expressionsToBeIntersected.add(transfer);

        OWLObjectSomeValuesFrom hasAgentValuesFrom = this.objectUnionToObjectSomeValuesFrom("#hasAgent", hasAgent);
        expressionsToBeIntersected.add(hasAgentValuesFrom);

        OWLObjectSomeValuesFrom hasGeographicLocationValuesFrom = this.objectUnionToObjectSomeValuesFrom("#hasGeographicLocation", hasGeographicLocation);
        expressionsToBeIntersected.add(hasGeographicLocationValuesFrom);


        OWLObjectSomeValuesFrom isPhysicallyContainedInValuesFrom = this.objectUnionToObjectSomeValuesFrom("#isPhysicallyContainedIn", isPhysicallyContainedIn);

        OWLObjectIntersectionOf hasPatientIntersectionProperty = this.owlDataFactory.getOWLObjectIntersectionOf(isPhysicallyContainedInValuesFrom, hasPatient);

        OWLObjectSomeValuesFrom hasPatientValuesFrom = this.intersectionOfMultipleOWLObjectsAsSomeValue("#hasPatient", hasPatientIntersectionProperty);
        expressionsToBeIntersected.add(hasPatientValuesFrom);

        OWLObjectIntersectionOf owlObjectIntersectionOf = this.owlDataFactory.getOWLObjectIntersectionOf(expressionsToBeIntersected);
        OWLEquivalentClassesAxiom equivalentClassesAxiom = this.owlDataFactory.getOWLEquivalentClassesAxiom(pathogenTransferOWLClass, owlObjectIntersectionOf);

        return this.owlOntology.add(equivalentClassesAxiom);

    }

    public ChangeApplied pathogenTransferByVectorQuantificationAxiom(PathogenTransferByVector pathogenTransferByVector){

        OWLClass pathogenTransferOWLClass = this.getClass(pathogenTransferByVector.className);
        //subClassOf
        OWLClass transfer = this.getClass(ClassName.TRANSFER);
        //and
        OWLObjectUnionOf hasAgent = this.unionOfClasses(pathogenTransferByVector.hasAgent);
        OWLObjectUnionOf hasGeographicLocation = this.unionOfClasses(pathogenTransferByVector.hasLocusGeographic);
        //or
        OWLObjectUnionOf hasPatient = this.unionOfClasses(pathogenTransferByVector.hasPatient);
        OWLObjectUnionOf causes = this.unionOfClasses(pathogenTransferByVector.causes);

        List<OWLClassExpression> expressionsToBeIntersected = new ArrayList<>();
        OWLObjectSomeValuesFrom hasAgentValuesFrom = this.objectUnionToObjectSomeValuesFrom("#hasAgent", hasAgent);
        expressionsToBeIntersected.add(hasAgentValuesFrom);

        OWLObjectSomeValuesFrom hasGeographicLocationValuesFrom = this.objectUnionToObjectSomeValuesFrom("#hasGeographicLocation", hasGeographicLocation);
        expressionsToBeIntersected.add(hasGeographicLocationValuesFrom);


        OWLObjectSomeValuesFrom causesValuesFrom = this.objectUnionToObjectSomeValuesFrom("#causes", causes);

        OWLObjectIntersectionOf hasPatientIntersectionProperty = this.owlDataFactory.getOWLObjectIntersectionOf(causesValuesFrom, hasPatient);

        OWLObjectSomeValuesFrom hasPatientValuesFrom = this.intersectionOfMultipleOWLObjectsAsSomeValue("#hasPatient", hasPatientIntersectionProperty);
        expressionsToBeIntersected.add(hasPatientValuesFrom);

        OWLObjectIntersectionOf owlObjectIntersectionOf = this.owlDataFactory.getOWLObjectIntersectionOf(expressionsToBeIntersected);

        OWLSubClassOfAxiom subClassOfAxiom = this.owlDataFactory.getOWLSubClassOfAxiom(pathogenTransferOWLClass, owlObjectIntersectionOf);

        return this.owlOntology.add(subClassOfAxiom);


    }

    public ChangeApplied manifestationEquivalentToPathologicalProcess(List<String> manifestations){

        List<OWLClass> manifestationClasses = this.getCollectionOfClasses(manifestations);
        OWLClass pathologicalProcess = this.getClass("PathologicalProcess");

        for(OWLClass manifestationClass : manifestationClasses){
            ArrayList equivalentClasses = new ArrayList<>();

            equivalentClasses.add(manifestationClass);
            equivalentClasses.add(pathologicalProcess);

            OWLEquivalentClassesAxiom equivalentClassesAxiom = this.owlDataFactory.getOWLEquivalentClassesAxiom(equivalentClasses);
            

        }
        return null;

    }

    private ChangeApplied equivalentClassesAxiomStatement(List<OWLClassExpression> objectList){

        OWLEquivalentClassesAxiom equivalentClassesAxiom = this.owlDataFactory.getOWLEquivalentClassesAxiom(objectList);

        return this.owlOntology.add(equivalentClassesAxiom);

    }

    private OWLObjectSomeValuesFrom objectUnionToObjectSomeValuesFrom(String propertyName, OWLObjectUnionOf objectUnionOf){

        OWLObjectPropertyExpression hasAgentExpression = this.owlDataFactory.getOWLObjectProperty(propertyName);
        OWLObjectSomeValuesFrom owlObjectSomeValuesFrom = this.owlDataFactory.getOWLObjectSomeValuesFrom(hasAgentExpression, objectUnionOf);

        return owlObjectSomeValuesFrom;

    }

    private OWLObjectSomeValuesFrom intersectionOfMultipleOWLObjectsAsSomeValue(String propertyName, OWLObjectIntersectionOf owlObjectIntersectionOf){
        OWLObjectPropertyExpression propertyExpression = this.owlDataFactory.getOWLObjectProperty(propertyName);
        OWLObjectSomeValuesFrom owlObjectSomeValuesFrom = this.owlDataFactory.getOWLObjectSomeValuesFrom(propertyExpression, owlObjectIntersectionOf);

        return owlObjectSomeValuesFrom;
    }

    private OWLObjectUnionOf unionOfClasses(List<String> unionClasses){

        List<OWLClass> owlClasses = this.getCollectionOfClasses(unionClasses);

        return this.owlDataFactory.getOWLObjectUnionOf(owlClasses);

    }

    private List<OWLClass> getCollectionOfClasses(List<String> classNames){

        List<OWLClass> owlClasses = new ArrayList<>();
        for(String className : classNames){
            owlClasses.add(this.getClass(className));
        }
        return owlClasses;
    }

    private OWLClass getClass(String className){
        return this.owlDataFactory.getOWLClass(this.iri+"#"+className);
    }

    private OWLClass getClass(String uri, String className){
        return this.owlDataFactory.getOWLClass(uri+"#"+className);
    }

    private OWLClass getClass(IRI iri, String className){
        return this.owlDataFactory.getOWLClass(iri+className);
    }

}
