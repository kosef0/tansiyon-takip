package com.tansiyon.tansiyon_takip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Tansiyon ölçümü entity sınıfı.
 * Her gün için sabah ve akşam olmak üzere iki ölçüm kaydedilebilir.
 */
@Entity
@Table(name = "tansiyon_olcumleri", uniqueConstraints = @UniqueConstraint(columnNames = { "tarih", "zaman_dilimi" }))
@Getter
@Setter
@NoArgsConstructor
public class TansiyonOlcumu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate tarih;

    /** SABAH veya AKSAM */
    @Column(name = "zaman_dilimi", nullable = false)
    private String zamanDilimi;

    /** Sistolik nüyük tansiyon */
    @Column(nullable = false)
    private Integer buyuk;

    /** Diyastolik küçük tansiyon */
    @Column(nullable = false)
    private Integer kucuk;
}
