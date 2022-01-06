package br.edu.ufcspa;

import br.edu.ufcspa.factory.Core;
import br.edu.ufcspa.model.Transmission;
import com.opencsv.bean.FieldAccess;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.List;


public class Main {

    public static void main(String[] args) throws OWLOntologyCreationException {

//        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
//
//        File ntdoFile = new File("C:\\Users\\Pichau\\IdeaProjects\\NTDOTBoxGenerator\\ntdo2.owl");


        IRI iri = IRI.create("ntdoOntology");
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();

        OWLOntology ntdoTboxTransmission = man.createOntology(iri);

        /*
        Criando classes base
         */

        OWLDataFactory dataFactory = ntdoTboxTransmission.getOWLOntologyManager().getOWLDataFactory();

        Core core = new Core(dataFactory, iri, ntdoTboxTransmission);

        String[] base = {"Transfer", "PathologicalDisposition", "GeographicEntity", "Organism"};

        for (String baseItem: base) {
            core.declareClass(baseItem);
        }



        core.declareSubClassOf("Organism", "Vertebrate");

        String[] classesDisjoint = {"Vertebrate", "Protist", "Arthropod"};
        core.disjointClasses(classesDisjoint);

//        OWLClass transfer = dataFactory.getOWLClass(iri+"#Transfer");
//        OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(transfer);
//        ntdoTboxTransmission.add(declarationAxiom);
//        OWLClass pathologicalDisposition = dataFactory.getOWLClass(iri+"#PathologicalDisposition");
//        OWLClass

/*
        FileReader dengueTbox = null;
        try {
            dengueTbox = new FileReader("C:\\Users\\Pichau\\IdeaProjects\\NTDOTBoxGenerator\\tbox-DENV.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<Transmission> denv = new CsvToBeanBuilder(dengueTbox)
                .withType(Transmission.class)
                .build()
                .parse();

        for(Transmission item : denv){
            System.out.println(item.state);
        }
*/
        File tboxFile = new File("C:\\Users\\Pichau\\IdeaProjects\\NTDOTBoxGenerator\\tboxTransmission.owl");
        try {
            man.saveOntology(ntdoTboxTransmission, new OWLXMLDocumentFormat(), new FileOutputStream(tboxFile));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

}
