package br.edu.ufcspa.factory;

import br.edu.ufcspa.model.Transmission;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.OWLClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tools {

    public ArrayList<String> identifyClassesFromSingleColumn(List<Transmission> lines, int position){

        ArrayList<String> equalRecords = new ArrayList<String>();
        for(Transmission line : lines){

            String recordLine = this.getValueFromProperty(line, position);

            if(recordLine == null)
                return null;

            String[] recordsLine = recordLine.split(" ");

            for(String singleRecord : recordsLine){
                if(!equalRecords.contains(singleRecord))
                    equalRecords.add(singleRecord);
            }
        }

        return equalRecords;
    }

    public List<String> identifyClassesFromSingleLineByColumn(Transmission line, int position){
        String lineContent = this.getValueFromProperty(line, position);

        if(position == Transmission.STATEPOSITION)
            lineContent = this.getLocationClassName(lineContent);

        List<String> classesFromSingleLineByColumn = Arrays.asList(lineContent.split(" "));

        return classesFromSingleLineByColumn;
    }

    private String getLocationClassName(String className){

        className = StringUtils.deleteWhitespace(className);

        return className+"Location";
    }

    private String getValueFromProperty(Transmission line, int position){
        switch (position){
            case Transmission.IBGECODEPOSITION:
                return line.ibgeCode;
            case Transmission.STATEPOSITION:
                return line.state;
            case Transmission.VECTORPOSITION:
                return line.vector;
            case Transmission.HOSTPOSITION:
                return line.host;
            case Transmission.PATHOGENPOSITION:
                return line.pathogen;
            case Transmission.MANIFESTATIONPOSITION:
                return  line.manifestation;
        }
        return null;
    }


}
