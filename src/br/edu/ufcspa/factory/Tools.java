package br.edu.ufcspa.factory;

import br.edu.ufcspa.model.Transmission;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    public HashMap<String, List<String>> identifyWhichAgentCauseManifestation(List<Transmission> lines, ArrayList<String> manifestations){

        HashMap<String, List<String>> combination = new HashMap<>();

        for(Transmission line : lines){
            for(String manifestation : manifestations){
                if(line.manifestation.equalsIgnoreCase(manifestation)){
                    List<String> agents;
                    agents = this.identifyClassesFromSingleLineByColumn(line, Transmission.PATHOGENPOSITION);
                    if(combination.get(manifestation) == null){
                        combination.put(manifestation, agents);
                    }
                    else {
                        List<String> storedAgents;
                        storedAgents = new ArrayList<>(combination.get(manifestation));
                        for(String agentToBeStored : agents){
                            if(!storedAgents.contains(agentToBeStored)){
                                storedAgents.add(agentToBeStored);
                                combination.replace(manifestation, storedAgents);
                            }
                        }
                    }
                }
            }
        }
        return combination;
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
