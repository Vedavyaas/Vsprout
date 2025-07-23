package com.Vsprout.Sprout.Database;

import jakarta.persistence.*;

@Entity
@Table(name = "MEMORY")
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String code;

    public HistoryEntity(){
    }

    public HistoryEntity(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }
}
