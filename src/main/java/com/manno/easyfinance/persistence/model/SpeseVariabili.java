package com.manno.easyfinance.persistence.model;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpeseVariabili {

    private String nomeConto;
    private double amount;
    private LocalDate data;
    private int id;
    private String descrizione;
    private Map.Entry<String, Integer> chiave;


    public void setId(int id) {
        this.id = id;
        this.chiave = new AbstractMap.SimpleEntry<String,Integer>(this.nomeConto, this.id);
    }

    public SpeseVariabili(String nomeConto, double amount, LocalDate data, String descrizione) {
        this.nomeConto = nomeConto;
        this.amount = amount;
        this.data = data;
        this.descrizione = descrizione;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpeseVariabili that = (SpeseVariabili) o;
        return Objects.equals(chiave, that.chiave);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chiave);
    }

    @Override
    public String toString() {
        return "SpeseVariabili{" +
                "nomeConto='" + nomeConto + '\'' +
                ", amount=" + amount +
                ", data=" + data +
                ", id=" + id +
                ", descrizione='" + descrizione + '\'' +
                ", chiave=" + chiave +
                '}';
    }

    public String getNomeConto() {
        return nomeConto;
    }

    public void setNomeConto(String nomeConto) {
        this.nomeConto = nomeConto;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
