package com.tansiyon.tansiyon_takip.service;

import com.tansiyon.tansiyon_takip.dto.OlcumEkleRequest;
import com.tansiyon.tansiyon_takip.dto.OzetResponse;
import com.tansiyon.tansiyon_takip.entity.TansiyonOlcumu;

import java.time.LocalDate;
import java.util.List;

// Tansiyon ölçüm işlemleri için servis arayüzü 
public interface TansiyonService {

   
    TansiyonOlcumu olcumEkle(OlcumEkleRequest request);

    List<TansiyonOlcumu> tumOlcumleriGetir();


    OzetResponse ozetHesapla(LocalDate baslangic, LocalDate bitis);

    
    OzetResponse haftalikOzetGetir();


    OzetResponse gunlukOzetGetir(LocalDate tarih);

  
    OzetResponse onbesGunlukOzetGetir();

    void olcumSil(Long id);

    
    TansiyonOlcumu olcumGuncelle(Long id, OlcumEkleRequest request);
}
