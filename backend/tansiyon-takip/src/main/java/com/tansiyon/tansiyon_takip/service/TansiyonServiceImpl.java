package com.tansiyon.tansiyon_takip.service;

import com.tansiyon.tansiyon_takip.dto.OlcumEkleRequest;
import com.tansiyon.tansiyon_takip.dto.OzetResponse;
import com.tansiyon.tansiyon_takip.entity.TansiyonOlcumu;
import com.tansiyon.tansiyon_takip.exception.DuplicateRecordException;
import com.tansiyon.tansiyon_takip.exception.ValidationException;
import com.tansiyon.tansiyon_takip.repository.TansiyonOlcumuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TansiyonServiceImpl implements TansiyonService {

    private final TansiyonOlcumuRepository repository;

    @Override
    public TansiyonOlcumu olcumEkle(OlcumEkleRequest request) {
        log.info("Yeni ölçüm ekleme isteği: tarih={}, zaman={}, buyuk={}, kucuk={}",
                request.getTarih(), request.getZamanDilimi(),
                request.getBuyuk(), request.getKucuk());

        if (request.getTarih() == null) {
            throw new ValidationException("Tarih zorunludur.");
        }
        if (request.getZamanDilimi() == null || request.getZamanDilimi().isBlank()) {
            throw new ValidationException("Zaman dilimi zorunludur.");
        }
        if (request.getBuyuk() == null) {
            throw new ValidationException("Büyük tansiyon zorunludur.");
        }
        if (request.getKucuk() == null) {
            throw new ValidationException("Küçük tansiyon zorunludur.");
        }

        String zamanDilimi = request.getZamanDilimi().trim().toUpperCase();
        if (!zamanDilimi.equals("SABAH") && !zamanDilimi.equals("AKSAM")) {
            throw new ValidationException("Zaman dilimi SABAH veya AKSAM olmalı.");
        }

        if (request.getBuyuk() < 50 || request.getBuyuk() > 250) {
            throw new ValidationException("Büyük tansiyon 50-250 arasında olmalı.");
        }
        if (request.getKucuk() < 30 || request.getKucuk() > 150) {
            throw new ValidationException("Küçük tansiyon 30-150 arasında olmalı.");
        }

        if (repository.findByTarihAndZamanDilimi(request.getTarih(), zamanDilimi).isPresent()) {
            log.warn("Duplicate kayıt denemesi: tarih={}, zaman={}", request.getTarih(), zamanDilimi);
            throw new DuplicateRecordException(request.getTarih().toString(), zamanDilimi);
        }

        TansiyonOlcumu olcum = new TansiyonOlcumu();
        olcum.setTarih(request.getTarih());
        olcum.setZamanDilimi(zamanDilimi);
        olcum.setBuyuk(request.getBuyuk());
        olcum.setKucuk(request.getKucuk());

        TansiyonOlcumu saved = repository.save(olcum);
        log.info("Ölçüm başarıyla kaydedildi: id={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TansiyonOlcumu> tumOlcumleriGetir() {
        log.debug("Tüm ölçümler getiriliyor");
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public OzetResponse ozetHesapla(LocalDate baslangic, LocalDate bitis) {
        log.debug("Özet hesaplanıyor: {} - {}", baslangic, bitis);
        List<TansiyonOlcumu> liste = repository.findByTarihBetween(baslangic, bitis);
        return hesaplaOzet(liste);
    }

    @Override
    @Transactional(readOnly = true)
    public OzetResponse haftalikOzetGetir() {
        LocalDate bitis = LocalDate.now();
        LocalDate baslangic = bitis.minusDays(6); // 7 gün
        log.debug("Haftalık özet getiriliyor: {} - {}", baslangic, bitis);
        return ozetHesapla(baslangic, bitis);
    }

    @Override
    @Transactional(readOnly = true)
    public OzetResponse gunlukOzetGetir(LocalDate tarih) {
        log.debug("Günlük özet getiriliyor: {}", tarih);
        return ozetHesapla(tarih, tarih);
    }

    @Override
    @Transactional(readOnly = true)
    public OzetResponse onbesGunlukOzetGetir() {
        LocalDate bitis = LocalDate.now();
        LocalDate baslangic = bitis.minusDays(14); // 15 gün
        log.debug("15 günlük özet getiriliyor: {} - {}", baslangic, bitis);
        return ozetHesapla(baslangic, bitis);
    }
    private OzetResponse hesaplaOzet(List<TansiyonOlcumu> liste) {
        OzetResponse response = new OzetResponse();
        response.setToplamOlcumSayisi(liste.size());

        if (liste.isEmpty()) {
            response.setDurum("VERI_YOK");
            response.setMesaj("Seçilen tarih aralığında ölçüm bulunamadı.");
            return response;
        }

        double sabahBuyukTop = 0, sabahKucukTop = 0;
        int sabahSay = 0;
        double aksamBuyukTop = 0, aksamKucukTop = 0;
        int aksamSay = 0;
        double genelBuyukTop = 0, genelKucukTop = 0;
        int genelSay = 0;

        for (TansiyonOlcumu olcum : liste) {
            genelBuyukTop += olcum.getBuyuk();
            genelKucukTop += olcum.getKucuk();
            genelSay++;

            if ("SABAH".equalsIgnoreCase(olcum.getZamanDilimi())) {
                sabahBuyukTop += olcum.getBuyuk();
                sabahKucukTop += olcum.getKucuk();
                sabahSay++;
            } else if ("AKSAM".equalsIgnoreCase(olcum.getZamanDilimi())) {
                aksamBuyukTop += olcum.getBuyuk();
                aksamKucukTop += olcum.getKucuk();
                aksamSay++;
            }
        }

        response.setGenelBuyukOrt(genelSay == 0 ? null : genelBuyukTop / genelSay);
        response.setGenelKucukOrt(genelSay == 0 ? null : genelKucukTop / genelSay);
        response.setSabahBuyukOrt(sabahSay == 0 ? null : sabahBuyukTop / sabahSay);
        response.setSabahKucukOrt(sabahSay == 0 ? null : sabahKucukTop / sabahSay);
        response.setAksamBuyukOrt(aksamSay == 0 ? null : aksamBuyukTop / aksamSay);
        response.setAksamKucukOrt(aksamSay == 0 ? null : aksamKucukTop / aksamSay);

        evaluateDurum(response);

        return response;
    }

    private void evaluateDurum(OzetResponse response) {
        Double buyuk = response.getGenelBuyukOrt();
        Double kucuk = response.getGenelKucukOrt();

        if (buyuk == null || kucuk == null) {
            response.setDurum("VERI_YOK");
            response.setMesaj("Değerlendirme için yeterli veri yok.");
            return;
        }

        if (buyuk >= 140 || kucuk >= 90) {
            response.setDurum("HIPERTANSIYON_SUPHESI");
            response.setMesaj(String.format(
                    "Dikkat! Ortalama değerleriniz (%.0f/%.0f) yüksek. Hipertansiyon riski olabilir. Lütfen doktorunuza danışın.",
                    buyuk, kucuk));
        } else if (buyuk < 90 || kucuk < 60) {
            response.setDurum("HIPOTANSIYON_SUPHESI");
            response.setMesaj(String.format(
                    "Dikkat! Ortalama değerleriniz (%.0f/%.0f) düşük. Hipotansiyon riski olabilir. Lütfen doktorunuza danışın.",
                    buyuk, kucuk));
        } else {
            response.setDurum("NORMAL");
            response.setMesaj(String.format(
                    "Tebrikler! Ortalama değerleriniz (%.0f/%.0f) normal aralıkta. Sağlıklı yaşamınızı sürdürün.",
                    buyuk, kucuk));
        }
    }

    @Override
    public void olcumSil(Long id) {
        log.info("Ölçüm silme isteği: id={}", id);

        if (!repository.existsById(id)) {
            log.warn("Silinecek ölçüm bulunamadı: id={}", id);
            throw new com.tansiyon.tansiyon_takip.exception.ResourceNotFoundException("Ölçüm", id);
        }

        repository.deleteById(id);
        log.info("Ölçüm silindi: id={}", id);
    }

    @Override
    public TansiyonOlcumu olcumGuncelle(Long id, OlcumEkleRequest request) {
        log.info("Ölçüm güncelleme isteği: id={}, yeni değerler: buyuk={}, kucuk={}",
                id, request.getBuyuk(), request.getKucuk());

        TansiyonOlcumu olcum = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Güncellenecek ölçüm bulunamadı: id={}", id);
                    return new com.tansiyon.tansiyon_takip.exception.ResourceNotFoundException("Ölçüm", id);
                });

        if (request.getBuyuk() != null) {
            if (request.getBuyuk() < 50 || request.getBuyuk() > 250) {
                throw new com.tansiyon.tansiyon_takip.exception.ValidationException(
                        "Büyük tansiyon 50-250 arasında olmalı.");
            }
            olcum.setBuyuk(request.getBuyuk());
        }

        if (request.getKucuk() != null) {
            if (request.getKucuk() < 30 || request.getKucuk() > 150) {
                throw new com.tansiyon.tansiyon_takip.exception.ValidationException(
                        "Küçük tansiyon 30-150 arasında olmalı.");
            }
            olcum.setKucuk(request.getKucuk());
        }

        if (request.getTarih() != null && request.getZamanDilimi() != null) {
            String yeniZaman = request.getZamanDilimi().trim().toUpperCase();

            if (!olcum.getTarih().equals(request.getTarih()) || !olcum.getZamanDilimi().equals(yeniZaman)) {
                if (repository.findByTarihAndZamanDilimi(request.getTarih(), yeniZaman).isPresent()) {
                    throw new com.tansiyon.tansiyon_takip.exception.DuplicateRecordException(
                            request.getTarih().toString(), yeniZaman);
                }
                olcum.setTarih(request.getTarih());
                olcum.setZamanDilimi(yeniZaman);
            }
        }

        TansiyonOlcumu saved = repository.save(olcum);
        log.info("Ölçüm güncellendi: id={}", saved.getId());

        return saved;
    }
}
