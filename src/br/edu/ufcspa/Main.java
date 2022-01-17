package br.edu.ufcspa;

import br.edu.ufcspa.factory.Core;
import br.edu.ufcspa.factory.Tools;
import br.edu.ufcspa.model.ClassName;
import br.edu.ufcspa.model.PathogenTransferByVector;
import br.edu.ufcspa.model.Transmission;
import com.opencsv.bean.FieldAccess;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {

    public static void main(String[] args) throws OWLOntologyCreationException {

//        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
//
//        File ntdoFile = new File("C:\\Users\\Pichau\\IdeaProjects\\NTDOTBoxGenerator\\ntdo2.owl");

//
        IRI iri = IRI.create("http://purl.org/ntdo2/");
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();

        OWLOntology ntdoTboxTransmission = man.createOntology(iri);

        /*
        Criando classes base
         */

        OWLDataFactory dataFactory = ntdoTboxTransmission.getOWLOntologyManager().getOWLDataFactory();

        Core core = new Core(dataFactory, iri, ntdoTboxTransmission);

        String[] base = {ClassName.TRANSFER, ClassName.PATHOLOGICALDISPOSITION, ClassName.GEOGRAPHICENTITY, ClassName.ORGANISM, ClassName.VIRUS, ClassName.HUMAN};

        for (String baseItem: base) {
            core.declareClass(baseItem);
        }

        core.declareSubClassOf(ClassName.ORGANISM, ClassName.VERTEBRATE);
        core.declareSubClassOf(ClassName.VERTEBRATE, ClassName.HUMAN);



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
        denv.remove(0); //Remove header

/*
Location
 */

        core.declareClass(ClassName.BRAZILOCATION);
        core.declareSubClassOf(ClassName.GEOGRAPHICENTITY, ClassName.BRAZILOCATION);
        for(Transmission line : denv){

            String clearStateName = StringUtils.stripAccents(StringUtils.deleteWhitespace(line.state));
            String locationClassName = clearStateName+"Location";

            core.declareClass(locationClassName);
            core.declareSubClassOf(ClassName.BRAZILOCATION, locationClassName);

        }

/*
Pathogen
*/
/*
DENV
*/
            Tools tools = new Tools();
            ArrayList<String> pathogens = tools.identifyClassesFromSingleColumn(denv, Transmission.PATHOGENPOSITION);

            for(String pathogen : pathogens){
                core.declareClass(pathogen);
                core.declareSubClassOf(ClassName.VIRUS, pathogen);
            }
            core.disjointClasses(pathogens);

/*
Vector
 */
        ArrayList<String> vectors = tools.identifyClassesFromSingleColumn(denv, Transmission.VECTORPOSITION);

        for(String vector : vectors){
            core.declareClass(vector);
        }
        core.disjointClasses(vectors);

/*
Manifestation/Disposition
 */

        ArrayList<String> manifestations = tools.identifyClassesFromSingleColumn(denv, Transmission.MANIFESTATIONPOSITION);

        for(String manifestation : manifestations){
            String manifestationDisposition = manifestation+"Disposition";
            core.declareClass(manifestation);
            core.declareClass(manifestationDisposition;

            core.declareSubClassOf(ClassName.PATHOLOGICALDISPOSITION, manifestationDisposition);

        }

/*
PathogenTransferByVector
 */
        core.declareClass(ClassName.PATHOGENTRANSFERBYVECTOR);
        core.declareSubClassOf(ClassName.TRANSFER, ClassName.PATHOGENTRANSFERBYVECTOR);
        List<String> pathogenTransferByVectorClassesName = new ArrayList<>();
        int lineNumber = 1;
        for(Transmission line : denv){
            String className = ClassName.PATHOGENTRANSFERBYVECTOR+"_"+lineNumber;
            lineNumber++;

            PathogenTransferByVector pathogenTransferByVector = new PathogenTransferByVector(
                    className,
                    tools.identifyClassesFromSingleLineByColumn(line, Transmission.VECTORPOSITION),
                    tools.identifyClassesFromSingleLineByColumn(line, Transmission.STATEPOSITION),
                    tools.identifyClassesFromSingleLineByColumn(line, Transmission.PATHOGENPOSITION),
                    tools.identifyClassesFromSingleLineByColumn(line, Transmission.HOSTPOSITION)
            );
            System.out.println(pathogenTransferByVector);

            core.declareClass(pathogenTransferByVector.className);
            pathogenTransferByVectorClassesName.add(pathogenTransferByVector.className);

            core.pathogenTransferByVectorExistentialAxiom(pathogenTransferByVector);

        }
        core.equivalentClassToUnion(ClassName.PATHOGENTRANSFERBYVECTOR, pathogenTransferByVectorClassesName);
        core.disjointClasses(pathogenTransferByVectorClassesName);



//        OWLClass transfer = dataFactory.getOWLClass(iri+"#Transfer");
//        OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(transfer);
//        ntdoTboxTransmission.add(declarationAxiom);
//        OWLClass pathologicalDisposition = dataFactory.getOWLClass(iri+"#PathologicalDisposition");
//        OWLClass


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
