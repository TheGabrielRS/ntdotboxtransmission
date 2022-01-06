package br.edu.ufcspa;

import br.edu.ufcspa.model.Transmission;
import com.opencsv.bean.FieldAccess;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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

        OWLOntologyManager man = OWLManager.createOWLOntologyManager();

        File tboxFile = new File("C:\\\\Users\\\\Pichau\\\\IdeaProjects\\\\NTDOTBoxGenerator\\\\tboxTransmission.owl\"");

        OWLOntology ntdoTboxTransmission = man.loadOntologyFromOntologyDocument(tboxFile);







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


//        man.saveOntology(ntdoTboxTransmission, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(tboxFile));




    }

}
