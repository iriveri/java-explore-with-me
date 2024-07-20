package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
public class ClientStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String app;
    private String uri;
    private String ip;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
}