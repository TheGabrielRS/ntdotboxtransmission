package br.edu.ufcspa.model;

import java.util.List;

public class PathologicalProcess {

    public String name;

    public List<String> isCausedBy;

    public String isIncludedIn;

    public String isRealizationOf;

    public String disposition;

    public PathologicalProcess(String name, List<String> isCausedBy, String isIncludedIn, String isRealizationOf) {
        this.name = name;
        this.isCausedBy = isCausedBy;
        this.isIncludedIn = isIncludedIn;
        this.isRealizationOf = isRealizationOf;
        this.disposition = this.name+"Disposition";
    }
}
