package com.Vsprout.Sprout.Database;

import jakarta.persistence.*;

@Entity
@Table(name = "NOTES")
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    private String note;

    public Notes() {
    }

    public Notes(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }
}
