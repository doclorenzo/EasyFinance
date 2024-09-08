package com.manno.easyfinance.controller.DeatiledTableManager;

import java.util.Objects;

public class TableSpeseVariabiliHandler implements Comparable<TableSpeseVariabiliHandler> {

    private int giorno;
    private double spesatot;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableSpeseVariabiliHandler that = (TableSpeseVariabiliHandler) o;
        return giorno == that.giorno && Double.compare(spesatot, that.spesatot) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(giorno, spesatot);
    }

    @Override
    public String toString() {
        return "TableSpeseVariabiliHandler{" +
                "giorno=" + giorno +
                ", spesatot=" + spesatot +
                '}';
    }

    public int getGiorno() {
        return giorno;
    }

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    public double getSpesatot() {
        return spesatot;
    }

    public void setSpesatot(double spesatot) {
        this.spesatot = spesatot;
    }

    public TableSpeseVariabiliHandler(int giorno, double spesatot) {
        this.giorno = giorno;
        this.spesatot = spesatot;
    }

    @Override
    public int compareTo(TableSpeseVariabiliHandler o) {
        return o.getGiorno()-this.giorno;
    }

    public void addSpesa(double x){
        this.spesatot+=x;
    }

    public void subSpesa(double x){
        this.spesatot-=x;
    }
}
