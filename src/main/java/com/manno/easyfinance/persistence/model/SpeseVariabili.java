package com.manno.easyfinance.persistence.model;

import java.sql.Date;
import java.util.AbstractMap;
import java.util.Objects;

public class SpeseVariabili {

    private String nomeConto;
    private double amount;
    private Date gg;
    private int id;
    private String descrizione;
    private AbstractMap.SimpleEntry<String, Integer> chiave;


    public void setId(int id) {
        this.id = id;
        this.chiave = new AbstractMap.SimpleEntry<>(this.nomeConto, this.id);
    }

    public SpeseVariabili(String nomeConto, double amount, Date gg, String descrizione) {
        this.nomeConto = nomeConto;
        this.amount = amount;
        this.gg = gg;
        this.descrizione = descrizione;
    }

    public SpeseVariabili(String nomeConto, double amount, Date gg, String descrizione, int id) {
        this.nomeConto = nomeConto;
        this.amount = amount;
        this.gg = gg;
        this.descrizione = descrizione;
        this.setId(id);
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
                ", gg=" + gg +
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

    public Date getData() {
        return gg;
    }

    public void setData(Date gg) {
        this.gg = gg;
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

    public AbstractMap.SimpleEntry<String, Integer> getChiave() {
        return chiave;
    }
}
