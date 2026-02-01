package com.tansiyon.tansiyon_takip.controller;

import com.tansiyon.tansiyon_takip.dto.OlcumEkleRequest;
import com.tansiyon.tansiyon_takip.dto.OzetResponse;
import com.tansiyon.tansiyon_takip.entity.TansiyonOlcumu;
import com.tansiyon.tansiyon_takip.service.TansiyonService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/olcumler")
public class TansiyonOlcumController {

    private final TansiyonService tansiyonService;

    public TansiyonOlcumController(TansiyonService tansiyonService) {
        this.tansiyonService = tansiyonService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TansiyonOlcumu olcumEkle(@RequestBody OlcumEkleRequest request) {
        return tansiyonService.olcumEkle(request);
    }

    @GetMapping
    public List<TansiyonOlcumu> tumOlcumleriGetir() {
        return tansiyonService.tumOlcumleriGetir();
    }

    @GetMapping("/ozet")
    public OzetResponse ozetGetir(
            @RequestParam(required = false) String baslangic,
            @RequestParam(required = false) String bitis) {
        if (baslangic != null && bitis != null) {
            LocalDate baslangicTarih = LocalDate.parse(baslangic);
            LocalDate bitisTarih = LocalDate.parse(bitis);
            return tansiyonService.ozetHesapla(baslangicTarih, bitisTarih);
        }
        return tansiyonService.onbesGunlukOzetGetir();
    }

    @GetMapping("/ozet/haftalik")
    public OzetResponse haftalikOzet() {
        return tansiyonService.haftalikOzetGetir();
    }

    @GetMapping("/ozet/gunluk")
    public OzetResponse gunlukOzet(@RequestParam String tarih) {
        LocalDate gun = LocalDate.parse(tarih);
        return tansiyonService.gunlukOzetGetir(gun);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void olcumSil(@PathVariable Long id) {
        tansiyonService.olcumSil(id);
    }

    @PutMapping("/{id}")
    public TansiyonOlcumu olcumGuncelle(
            @PathVariable Long id,
            @RequestBody OlcumEkleRequest request) {
        return tansiyonService.olcumGuncelle(id, request);
    }
}
