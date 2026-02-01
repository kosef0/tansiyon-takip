package com.tansiyon.tansiyon_takip.dto;

import lombok.Data;

@Data
public class OzetResponse {
    private int toplamOlcumSayisi;

    private Double sabahBuyukOrt;
    private Double sabahKucukOrt;

    private Double aksamBuyukOrt;
    private Double aksamKucukOrt;

    private Double genelBuyukOrt;
    private Double genelKucukOrt;

    private String durum;
    private String mesaj;
}
