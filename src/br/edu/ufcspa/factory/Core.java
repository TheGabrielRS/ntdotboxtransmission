package br.edu.ufcspa.factory;

import br.edu.ufcspa.model.ClassName;
import br.edu.ufcspa.model.PathogenTransferByVector;
import br.edu.ufcspa.model.PathologicalProcess;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;

import java.util.*;

public class Core {

    private OWLDataFactory owlDataFactory;

    private IRI iri;

    private OWLOntology owlOntology;

    private IRI biotopIRI;

    public HashMap<String, OWLClass> bioTopClasses;

    private IRI btl2IRI;

    public Core(OWLDataFactory owlDataFactory, IRI iri, OWLOntology owlOntology) {
        this.owlDataFactory = owlDataFactory;
        this.iri = iri;
        this.owlOntology = owlOntology;
        this.biotopIRI = IRI.create("http://purl.org/biotop/biotop.owl#");
        this.btl2IRI = IRI.create("http://purl.org/biotop/btl2.owl#");
        this.bioTopClasses = new HashMap<>();
        this.bioTopClassesInitiator();
    }

    private HashMap<String, OWLClass> bioTopClassesInitiator(){

        bioTopClasses.put(ClassName.PATHOLOGICALPROCESSBIOTOP, this.getClass(this.biotopIRI, ClassName.PATHOLOGICALPROCESSBIOTOP));

        this.declareAndStoreBioTopClass(this.biotopIRI, ClassName.VIRUS);

        this.declareAndStoreBioTopClass(this.biotopIRI, ClassName.INSECT);

        this.declareAndStoreBioTopClass(this.btl2IRI, ClassName.PROCESS);

        this.declareAndStoreBioTopClass(this.biotopIRI, ClassName.PATHOLOGICALDISPOSITION);

        this.declareAndStoreBioTopClass(this.btl2IRI, ClassName.DISPOSITION);


        this.declareAndStoreBioTopClass(this.biotopIRI,ClassName.GEOGRAPHICENTITY);

        this.declareAndStoreBioTopClass(this.btl2IRI, ClassName.IMMATERIALTHREEDIMENSIONAL);

        this.declareAndStoreBioTopClass(this.btl2IRI, ClassName.ORGANISM);

        return bioTopClasses;

    }

    private void declareAndStoreBioTopClass(IRI owlIRI, String className){
        this.declareClass(owlIRI, className);
        this.bioTopClasses.put(className, getClass(owlIRI, className));
    }

    public ChangeApplied declareClass(String className){

        OWLClass owlClass = this.getClass(className);
        OWLDeclarationAxiom declarationAxiom = this.owlDataFactory.getOWLDeclarationAxiom(owlClass);
        return this.owlOntology.add(declarationAxiom);
    }

    public ChangeApplied declareClass(IRI iri, String className){
        OWLClass owlClass = this.getClass(iri, className);
        OWLDeclarationAxiom declarationAxiom = this.owlDataFactory.getOWLDeclarationAxiom(owlClass);
        return this.owlOntology.add(declarationAxiom);
    }

    public ChangeApplied declareSubClassOf(String className, String subclassName){
        OWLClass owlClass = this.getClass(className);
        OWLClass owlSubClass = this.getClass(subclassName);
        OWLSubClassOfAxiom className_sub_subClassName = this.owlDataFactory.getOWLSubClassOfAxiom(owlSubClass, owlClass);
        return this.owlOntology.add(className_sub_subClassName);
    }

    public ChangeApplied declareSubClassOf(OWLClass superclass, OWLClass subClass){
        OWLSubClassOfAxiom className_sub_subClassName = this.owlDataFactory.getOWLSubClassOfAxiom(subClass, superclass);
        return this.owlOntology.add(className_sub_subClassName);
    }


    public ChangeApplied disjointClasses(List<String> classesName){
        List<OWLClass> classesToBeDisjoint = new ArrayList<>();

        for (String className: classesName) {
            classesToBeDisjoint.add(this.getClass(className));
        }

        OWLDisjointClassesAxiom disjointClassesAxiom = this.owlDataFactory.getOWLDisjointClassesAxiom(classesToBeDisjoint);
        return this.owlOntology.add(disjointClassesAxiom);
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

        OWLObjectSomeValuesFrom hasAgentValuesFrom = this.objectUnionToObjectSomeValuesFrom(this.btl2IRI,"hasAgent", hasAgent);
        expressionsToBeIntersected.add(hasAgentValuesFrom);

        OWLObjectSomeValuesFrom hasGeographicLocationValuesFrom = this.objectUnionToObjectSomeValuesFrom(this.iri,"hasGeographicLocation", hasGeographicLocation);
        expressionsToBeIntersected.add(hasGeographicLocationValuesFrom);


        OWLObjectSomeValuesFrom isPhysicallyContainedInValuesFrom = this.objectUnionToObjectSomeValuesFrom(this.biotopIRI,"isPhysicallyContainedIn", isPhysicallyContainedIn);

        OWLObjectIntersectionOf hasPatientIntersectionProperty = this.owlDataFactory.getOWLObjectIntersectionOf(isPhysicallyContainedInValuesFrom, hasPatient);

        OWLObjectSomeValuesFrom hasPatientValuesFrom = this.intersectionOfMultipleOWLObjectsAsSomeValue(this.btl2IRI,"hasPatient", hasPatientIntersectionProperty);
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
        OWLObjectAllValuesFrom hasAgentValuesFrom = this.objectUnionToAllValuesFrom(this.btl2IRI,"hasAgent", hasAgent);
        expressionsToBeIntersected.add(hasAgentValuesFrom);

        OWLObjectAllValuesFrom hasGeographicLocationValuesFrom = this.objectUnionToAllValuesFrom(this.iri,"hasGeographicLocation", hasGeographicLocation);
        expressionsToBeIntersected.add(hasGeographicLocationValuesFrom);


        OWLObjectAllValuesFrom causesValuesFrom = this.objectUnionToAllValuesFrom(this.btl2IRI,"causes", causes);

        OWLObjectIntersectionOf hasPatientIntersectionProperty = this.owlDataFactory.getOWLObjectIntersectionOf(causesValuesFrom, hasPatient);

        OWLObjectAllValuesFrom hasPatientValuesFrom = this.intersectionOfMultipleOWLObjectsAsAllValue(this.btl2IRI,"hasPatient", hasPatientIntersectionProperty);
        expressionsToBeIntersected.add(hasPatientValuesFrom);

        OWLObjectIntersectionOf owlObjectIntersectionOf = this.owlDataFactory.getOWLObjectIntersectionOf(expressionsToBeIntersected);

        OWLSubClassOfAxiom subClassOfAxiom = this.owlDataFactory.getOWLSubClassOfAxiom(pathogenTransferOWLClass, owlObjectIntersectionOf);

        return this.owlOntology.add(subClassOfAxiom);


    }

    public OWLObjectAllValuesFrom objectUnionToAllValuesFrom(IRI propertyIRI, String propertyName, OWLObjectUnionOf objectUnionOf){

        OWLObjectPropertyExpression hasAgentExpression = this.getObjectProperty(propertyIRI, propertyName);
        OWLObjectAllValuesFrom owlObjectAllValuesFrom = this.owlDataFactory.getOWLObjectAllValuesFrom(hasAgentExpression, objectUnionOf);

        return owlObjectAllValuesFrom;
    }

    private OWLObjectAllValuesFrom intersectionOfMultipleOWLObjectsAsAllValue(IRI iri, String propertyName, OWLObjectIntersectionOf owlObjectIntersectionOf){
        OWLObjectPropertyExpression propertyExpression = this.getObjectProperty(iri, propertyName);
        OWLObjectAllValuesFrom owlObjectAllValuesFrom = this.owlDataFactory.getOWLObjectAllValuesFrom(propertyExpression, owlObjectIntersectionOf);

        return owlObjectAllValuesFrom;
    }

    public ChangeApplied manifestationPathologicalProcessAxiom(PathologicalProcess pathologicalProcess){

        OWLClass manifestationClass = this.getClass(pathologicalProcess.name);
        OWLClass pathologicalProcessMainEquivalentClass = this.bioTopClasses.get(ClassName.PATHOLOGICALPROCESSBIOTOP);

        this.declareSubClassOf(pathologicalProcessMainEquivalentClass, manifestationClass);

        ArrayList equivalentClasses = new ArrayList<>();

        equivalentClasses.add(pathologicalProcessMainEquivalentClass);

        OWLObjectUnionOf isCausedByUnionOf = this.unionOfClasses(pathologicalProcess.isCausedBy);
        OWLObjectSomeValuesFrom isCausedByValuesFrom = this.objectUnionToObjectSomeValuesFrom(this.btl2IRI, "isCausedBy", isCausedByUnionOf);
        equivalentClasses.add(isCausedByValuesFrom);

        OWLClass isIncludedIn = this.getClass(pathologicalProcess.isIncludedIn);

        OWLObjectPropertyExpression isIncludedInProperty = this.getObjectProperty(this.btl2IRI, "isIncludedIn");
        OWLObjectSomeValuesFrom isIncludedInValuesFrom = this.owlDataFactory.getOWLObjectSomeValuesFrom(isIncludedInProperty, isIncludedIn);

        equivalentClasses.add(isIncludedInValuesFrom);

        OWLClass isRealizationOf = this.getClass(pathologicalProcess.isRealizationOf);

        OWLObjectPropertyExpression isRealizationOfProperty = this.getObjectProperty(this.btl2IRI,"isRealizationOf");
        OWLObjectSomeValuesFrom isRealizationOfValuesFrom = this.owlDataFactory.getOWLObjectSomeValuesFrom(isRealizationOfProperty, isRealizationOf);

        equivalentClasses.add(isRealizationOfValuesFrom);

        OWLObjectIntersectionOf objectIntersectionOf = this.owlDataFactory.getOWLObjectIntersectionOf(equivalentClasses);

        OWLEquivalentClassesAxiom equivalentClassesAxiom = this.owlDataFactory.getOWLEquivalentClassesAxiom(manifestationClass, objectIntersectionOf);


        OWLObjectAllValuesFrom owlObjectAllValuesFrom = this.owlDataFactory.getOWLObjectAllValuesFrom(isRealizationOfProperty, this.getClass(pathologicalProcess.name+"Disposition"));
        this.owlOntology.add(this.owlDataFactory.getOWLSubClassOfAxiom(manifestationClass, owlObjectAllValuesFrom));


        return this.owlOntology.add(equivalentClassesAxiom);

    }

    public ChangeApplied manifestationDisposition(String dispositionName, String manifestation){
        this.declareClass(dispositionName);

        OWLClass pathologicalDisposition = this.getClass(this.biotopIRI, ClassName.PATHOLOGICALDISPOSITION);
        OWLClass dispositionClass = this.getNTDOClass(dispositionName);
        this.declareSubClassOf(pathologicalDisposition, dispositionClass);

        OWLObjectProperty hasRealization = this.getObjectProperty(this.btl2IRI, "hasRealization");
        OWLClass manifestationClass = this.getNTDOClass(manifestation);
        OWLObjectAllValuesFrom owlObjectAllValuesFrom = this.owlDataFactory.getOWLObjectAllValuesFrom(hasRealization, manifestationClass);

        return this.owlOntology.add(this.owlDataFactory.getOWLSubClassOfAxiom(dispositionClass, owlObjectAllValuesFrom));

    }

    public ChangeApplied generalClassAxiomsHomoSapiens(PathologicalProcess pathologicalProcess){

        OWLClass isIncludedIn = this.getNTDOClass(pathologicalProcess.isIncludedIn);

        OWLObjectProperty includes = this.getObjectProperty(this.btl2IRI, "includes");
        OWLObjectUnionOf unionOfClasses = this.unionOfClasses(pathologicalProcess.isCausedBy);
        OWLObjectSomeValuesFrom includesSomeValuesFrom = this.owlDataFactory.getOWLObjectSomeValuesFrom(includes, unionOfClasses);

        OWLObjectIntersectionOf intersectionOf = this.owlDataFactory.getOWLObjectIntersectionOf(isIncludedIn, includesSomeValuesFrom);

        OWLObjectProperty isBearerOf = this.getObjectProperty(this.btl2IRI, "isBearerOf");
        OWLClass disposition = this.getNTDOClass(pathologicalProcess.disposition);
        OWLObjectSomeValuesFrom isBearerOfsomeValuesFrom = this.owlDataFactory.getOWLObjectSomeValuesFrom(isBearerOf, disposition);

        OWLEquivalentClassesAxiom equivalentClassesAxiom = this.owlDataFactory.getOWLEquivalentClassesAxiom(intersectionOf, isBearerOfsomeValuesFrom);

        return this.owlOntology.add(equivalentClassesAxiom);

    }

    private OWLObjectProperty getObjectProperty(IRI iri, String propertyName){

        return this.owlDataFactory.getOWLObjectProperty(iri.getIRIString()+propertyName);

    }

    private OWLObjectSomeValuesFrom objectUnionToObjectSomeValuesFrom(IRI propertyIRI, String propertyName, OWLObjectUnionOf objectUnionOf){

        OWLObjectPropertyExpression hasAgentExpression = this.getObjectProperty(propertyIRI, propertyName);
        OWLObjectSomeValuesFrom owlObjectSomeValuesFrom = this.owlDataFactory.getOWLObjectSomeValuesFrom(hasAgentExpression, objectUnionOf);

        return owlObjectSomeValuesFrom;

    }

    private OWLObjectSomeValuesFrom intersectionOfMultipleOWLObjectsAsSomeValue(String propertyName, OWLObjectIntersectionOf owlObjectIntersectionOf){
        OWLObjectPropertyExpression propertyExpression = this.owlDataFactory.getOWLObjectProperty(propertyName);
        OWLObjectSomeValuesFrom owlObjectSomeValuesFrom = this.owlDataFactory.getOWLObjectSomeValuesFrom(propertyExpression, owlObjectIntersectionOf);

        return owlObjectSomeValuesFrom;
    }

    private OWLObjectSomeValuesFrom intersectionOfMultipleOWLObjectsAsSomeValue(IRI iri, String propertyName, OWLObjectIntersectionOf owlObjectIntersectionOf){
        OWLObjectPropertyExpression propertyExpression = this.getObjectProperty(iri, propertyName);
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
        return this.owlDataFactory.getOWLClass(this.iri+className);
    }

    public OWLClass getNTDOClass(String className){
        return this.owlDataFactory.getOWLClass(this.iri+className);
    }


    private OWLClass getClass(String uri, String className){
        return this.owlDataFactory.getOWLClass(uri+"#"+className);
    }

    private OWLClass getClass(IRI iri, String className){
        return this.owlDataFactory.getOWLClass(iri.getIRIString()+className);
    }

}
