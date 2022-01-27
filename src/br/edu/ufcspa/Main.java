package br.edu.ufcspa;

import br.edu.ufcspa.factory.Core;
import br.edu.ufcspa.factory.Tools;
import br.edu.ufcspa.model.ClassName;
import br.edu.ufcspa.model.PathogenTransferByVector;
import br.edu.ufcspa.model.PathologicalProcess;
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
import java.util.HashMap;
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

        String[] base = {ClassName.TRANSFER, ClassName.HUMAN};

        for (String baseItem: base) {
            core.declareClass(baseItem);
        }

        core.declareSubClassOf(core.bioTopClasses.get(ClassName.HUMANBIOTOP), core.getNTDOClass(ClassName.HUMAN));

        core.declareSubClassOf(core.bioTopClasses.get(ClassName.PROCESS), core.getNTDOClass(ClassName.TRANSFER));
//        core.declareSubClassOf(core.bioTopClasses.get(ClassName.DISPOSITION), core.getNTDOClass(ClassName.PATHOLOGICALDISPOSITION));



        FileReader dengueTbox = null;
        FileReader zikaTbox = null;
        FileReader chikTbox = null;
        try {
            dengueTbox = new FileReader("C:\\Users\\Pichau\\IdeaProjects\\NTDOTBoxGenerator\\tbox-DENV.csv");
            zikaTbox = new FileReader("C:\\Users\\Pichau\\IdeaProjects\\NTDOTBoxGenerator\\tbox-ZIKV.csv");
            chikTbox = new FileReader("C:\\Users\\Pichau\\IdeaProjects\\NTDOTBoxGenerator\\tbox-CHIKV.csv");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<FileReader> tboxFiles = new ArrayList<>();

        tboxFiles.add(dengueTbox);
        tboxFiles.add(zikaTbox);
        tboxFiles.add(chikTbox);

        ArrayList<String> everyPathogen = new ArrayList<>();

        for(FileReader tbox : tboxFiles){
            List<Transmission> denv = new CsvToBeanBuilder(tbox)
                    .withType(Transmission.class)
                    .build()
                    .parse();
            denv.remove(0); //Remove header

/*
Sanitization
 */
            for(Transmission line : denv){
                String clearStateName = StringUtils.stripAccents(StringUtils.deleteWhitespace(line.state));
                line.state = clearStateName;

                line.host = ClassName.HUMAN;
            }

/*
Location
 */

            core.declareClass(ClassName.BRAZILOCATION);
            core.declareSubClassOf(core.bioTopClasses.get(ClassName.GEOGRAPHICENTITY), core.getNTDOClass(ClassName.BRAZILOCATION));
            for(Transmission line : denv){

                String locationClassName = line.state+"Location";

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
                core.declareSubClassOf(core.bioTopClasses.get(ClassName.VIRUS), core.getNTDOClass(pathogen));
            }
            if(pathogens.size() > 1)
                core.disjointClasses(pathogens);
            everyPathogen.addAll(pathogens);

/*
Vector
 */
            ArrayList<String> vectors = tools.identifyClassesFromSingleColumn(denv, Transmission.VECTORPOSITION);

            for(String vector : vectors){
                core.declareClass(vector);
                core.declareSubClassOf(core.bioTopClasses.get(ClassName.INSECT), core.getNTDOClass(vector));
            }
            if(vectors.size() > 1)
                core.disjointClasses(vectors);

/*
Manifestation/Disposition
 */

            ArrayList<String> manifestations = tools.identifyClassesFromSingleColumn(denv, Transmission.MANIFESTATIONPOSITION);
            HashMap<String, List<String>> manifestationIsCausedBy = tools.identifyWhichAgentCauseManifestation(denv, manifestations);

            for(String manifestation : manifestations){
                String manifestationDisposition = manifestation+"Disposition";

                core.declareClass(manifestation);
                PathologicalProcess pathologicalProcess = new PathologicalProcess(
                        manifestation,
                        manifestationIsCausedBy.get(manifestation),
                        ClassName.HUMAN,
                        manifestationDisposition
                );
                core.manifestationPathologicalProcessAxiom(pathologicalProcess);
                core.generalClassAxiomsHomoSapiens(pathologicalProcess);

                core.manifestationDisposition(manifestationDisposition);

            }

/*
PathogenTransferByVector
 */

            String manifestationName;
            switch (denv.get(0).manifestation){
                case "DengueFever":
                    manifestationName = "Dengue";
                    break;
                case "ZikaFever":
                    manifestationName = "Zika";
                    break;
                case "ChikungunyaFever":
                    manifestationName = "Chikungunya";
                    break;
                default:
                    manifestationName = "Pathogen";
            }
            String manifestationClassName = manifestationName+ClassName.PATHOGENTRANSFERBYVECTOR;
            core.declareClass(manifestationClassName);
            core.declareSubClassOf(ClassName.TRANSFER, manifestationClassName);
            List<String> pathogenTransferByVectorClassesName = new ArrayList<>();
            int lineNumber = 1;
            for(Transmission line : denv){

                String className = manifestationName+ClassName.PATHOGENTRANSFERBYVECTOR+"_"+lineNumber;
                lineNumber++;

                PathogenTransferByVector pathogenTransferByVector = new PathogenTransferByVector(
                        className,
                        tools.identifyClassesFromSingleLineByColumn(line, Transmission.VECTORPOSITION),
                        tools.identifyClassesFromSingleLineByColumn(line, Transmission.STATEPOSITION),
                        tools.identifyClassesFromSingleLineByColumn(line, Transmission.PATHOGENPOSITION),
                        tools.identifyClassesFromSingleLineByColumn(line, Transmission.HOSTPOSITION),
                        tools.identifyClassesFromSingleLineByColumn(line, Transmission.MANIFESTATIONPOSITION)
                );

                core.declareClass(pathogenTransferByVector.className);
                pathogenTransferByVectorClassesName.add(pathogenTransferByVector.className);

                core.pathogenTransferByVectorExistentialAxiom(pathogenTransferByVector);
                core.pathogenTransferByVectorQuantificationAxiom(pathogenTransferByVector);

            }
            core.equivalentClassToUnion(manifestationClassName, pathogenTransferByVectorClassesName);
            core.disjointClasses(pathogenTransferByVectorClassesName);



//        OWLClass transfer = dataFactory.getOWLClass(iri+"#Transfer");
//        OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(transfer);
//        ntdoTboxTransmission.add(declarationAxiom);
//        OWLClass pathologicalDisposition = dataFactory.getOWLClass(iri+"#PathologicalDisposition");
//        OWLClass



        }

        core.disjointClasses(everyPathogen);

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
