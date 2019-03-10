package dto;

import java.sql.Timestamp;

public class Snapshot {
    private int id;
    private String name;
    private Timestamp prcDate;

    public Snapshot(String name) {
        this.name = name;
    }

    public Snapshot() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getPrcDate() {
        return prcDate;
    }

    public void setPrcDate(Timestamp prcDate) {
        this.prcDate = prcDate;
    }
}
