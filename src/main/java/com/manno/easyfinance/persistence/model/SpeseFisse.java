package com.manno.easyfinance.persistence.model;

import java.util.Arrays;
import java.util.Objects;

public class SpeseFisse {

    private String nomeConto;
    private String descrizione;
    private double amount;
    private String[] chiave;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpeseFisse that = (SpeseFisse) o;
        return Double.compare(amount, that.amount) == 0 && Objects.equals(nomeConto, that.nomeConto) && Objects.equals(descrizione, that.descrizione) && Objects.deepEquals(chiave, that.chiave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeConto, descrizione, amount, Arrays.hashCode(chiave));
    }

    @Override
    public String toString() {
        return "SpeseFisse{" +
                "nomeConto='" + nomeConto + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", amount=" + amount +
                ", chiave=" + Arrays.toString(chiave) +
                '}';
    }

    public String[] getChiave() {
        return chiave;
    }

    public void setChiave(String[] chiave) {
        this.chiave = chiave;
    }

    public SpeseFisse(String nomeConto, String descrizione, double amount) {
        this.nomeConto = nomeConto;
        this.descrizione = descrizione;
        this.amount = amount;
        this.chiave=new String[]{nomeConto, descrizione};
    }

    public String getNomeConto() {
        return nomeConto;
    }

    public void setNomeConto(String nomeConto) {
        this.nomeConto = nomeConto;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
