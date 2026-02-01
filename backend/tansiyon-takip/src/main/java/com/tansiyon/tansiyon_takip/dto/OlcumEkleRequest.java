package com.tansiyon.tansiyon_takip.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OlcumEkleRequest {
    private LocalDate tarih;
    private String zamanDilimi; 
    private Integer buyuk; 
    private Integer kucuk; 
}
