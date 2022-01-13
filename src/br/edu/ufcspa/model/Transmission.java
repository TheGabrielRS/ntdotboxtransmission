package br.edu.ufcspa.model;

import com.opencsv.bean.CsvBindByPosition;


public class Transmission {


    @CsvBindByPosition(position = IBGECODEPOSITION)
    public String ibgeCode;

    @CsvBindByPosition(position = STATEPOSITION)
    public String state;

    @CsvBindByPosition(position = VECTORPOSITION)
    public String vector;

    @CsvBindByPosition(position = HOSTPOSITION)
    public String host;

    @CsvBindByPosition(position = PATHOGENPOSITION)
    public String pathogen;

    @CsvBindByPosition(position = MANIFESTATIONPOSITION)
    public String manifestation;

    public static final int IBGECODEPOSITION = 0;

    public static final int STATEPOSITION = 1;

    public static final int VECTORPOSITION = 2;

    public static final int HOSTPOSITION = 3;

    public static final int PATHOGENPOSITION = 4;

    public static final int MANIFESTATIONPOSITION = 5;

}
