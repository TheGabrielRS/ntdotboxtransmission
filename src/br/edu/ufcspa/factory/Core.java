package br.edu.ufcspa.factory;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Core {

    private OWLDataFactory owlDataFactory;

    private IRI iri;

    private OWLOntology owlOntology;

    public Core(OWLDataFactory owlDataFactory, IRI iri, OWLOntology owlOntology) {
        this.owlDataFactory = owlDataFactory;
        this.iri = iri;
        this.owlOntology = owlOntology;
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

    private OWLClass getClass(String className){
        return this.owlDataFactory.getOWLClass(this.iri+"#"+className);
    }



}
