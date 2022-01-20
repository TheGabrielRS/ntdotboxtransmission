package br.edu.ufcspa.model;

import org.semanticweb.owlapi.model.OWLClass;

import java.util.List;

public class PathogenTransferByVector {

    public String className;

    public List<String> hasAgent;

    public List<String> hasLocusGeographic;

    public List<String> hasPatient;

    public List<String> hasLocusHost;

    public List<String> causes;

    public PathogenTransferByVector(String className, List<String> hasAgent, List<String> hasLocusGeographic, List<String> hasPatient, List<String> hasLocusHost, List<String> causes) {
        this.className = className;
        this.hasAgent = hasAgent;
        this.hasLocusGeographic = hasLocusGeographic;
        this.hasPatient = hasPatient;
        this.hasLocusHost = hasLocusHost;
        this.causes = causes;
    }

    @Override
    public String toString() {
        return "PathogenTransferByVector{" +
                "hasAgent=" + hasAgent +
                ", hasLocusGeographic=" + hasLocusGeographic +
                ", hasPatient=" + hasPatient +
                ", hasLocusHost=" + hasLocusHost +
                '}';
    }
}
