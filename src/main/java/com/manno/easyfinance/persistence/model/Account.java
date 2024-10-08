package com.manno.easyfinance.persistence.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

public class Account {

    private String nomeConto;
    private double monthltyIncome;
    private double bilancio;

    private Date dataCreazione;

    public Account(String nomeConto, double monthltyIncome, double bilancio) {
        this.nomeConto = nomeConto;
        this.monthltyIncome = monthltyIncome;
        this.bilancio = bilancio;
        setDataCreazione(new Date(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()));
    }

    public Account(String nomeConto, double monthltyIncome, double bilancio, Date dataCreazione) {
        this.nomeConto = nomeConto;
        this.monthltyIncome = monthltyIncome;
        this.bilancio = bilancio;
        this.dataCreazione=dataCreazione;
    }

    public Date getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public String getNomeConto() {
        return nomeConto;
    }

    public void setNomeConto(String nomeConto) {
        this.nomeConto = nomeConto;
    }

    public double getMonthltyIncome() {
        return monthltyIncome;
    }

    public void setMonthltyIncome(double monthltyIncome) {
        this.monthltyIncome = monthltyIncome;
    }

    public double getBilancio() {
        return bilancio;
    }

    public void setBilancio(double bilancio) {
        this.bilancio = bilancio;
    }

    @Override
    public String toString() {
        return "Account{" +
                "nomeConto='" + nomeConto + '\'' +
                ", monthltyIncome=" + monthltyIncome +
                ", bilancio=" + bilancio +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Double.compare(monthltyIncome, account.monthltyIncome) == 0 && Double.compare(bilancio, account.bilancio) == 0 && Objects.equals(nomeConto, account.nomeConto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeConto, monthltyIncome, bilancio);
    }
}
