package br.edu.ufcspa.model;

import com.opencsv.bean.CsvBindByPosition;


public class Transmission {


    @CsvBindByPosition(position = 0)
    private String ibgeCode;

    @CsvBindByPosition(position = 1)
    public String state;

    @CsvBindByPosition(position = 2)
    private String vector;

    @CsvBindByPosition(position = 3)
    private String host;

    @CsvBindByPosition(position = 4)
    private String pathogen;

    @CsvBindByPosition(position = 5)
    private String manifestation;

}
