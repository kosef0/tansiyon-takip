package com.tansiyon.tansiyon_takip.repository;

import com.tansiyon.tansiyon_takip.entity.TansiyonOlcumu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TansiyonOlcumuRepository extends JpaRepository<TansiyonOlcumu, Long> {

    Optional<TansiyonOlcumu> findByTarihAndZamanDilimi(LocalDate tarih, String zamanDilimi);

    List<TansiyonOlcumu> findByTarihBetween(LocalDate baslangic, LocalDate bitis);
}
