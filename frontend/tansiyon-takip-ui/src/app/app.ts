import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TansiyonApi } from './services/tansiyon-api.service';
import { TansiyonOlcumu, OlcumEkleRequest, OzetResponse, ZamanDilimi } from './models/tansiyon.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private readonly api = inject(TansiyonApi);

  olcumler = signal<TansiyonOlcumu[]>([]);
  loading = signal(false);
  mesaj = signal<{ tip: 'ok' | 'err'; text: string } | null>(null);

  // Özet veriler
  gunlukOzet = signal<OzetResponse | null>(null);
  haftalikOzet = signal<OzetResponse | null>(null);
  onbesGunlukOzet = signal<OzetResponse | null>(null);

  formTarih = signal(this.bugunTarih());
  formZaman = signal<ZamanDilimi>('SABAH');
  formBuyuk = signal<number | null>(null);
  formKucuk = signal<number | null>(null);

  duzenlemeModu = signal(false);
  duzenlenenId = signal<number | null>(null);

  ngOnInit(): void {
    this.verileriYukle();
  }

  verileriYukle(): void {
    this.loading.set(true);

    this.api.tumOlcumleriGetir().subscribe({
      next: (data) => {
        // Tarihe göre azalan sırala
        const sirali = data.sort((a, b) => {
          const tarihKarsilastir = b.tarih.localeCompare(a.tarih);
          if (tarihKarsilastir !== 0) return tarihKarsilastir;
          return a.zamanDilimi === 'AKSAM' ? -1 : 1;
        });
        this.olcumler.set(sirali);
      },
      error: (err) => this.hataGoster('Ölçümler yüklenemedi: ' + err.message)
    });

    this.api.gunlukOzet(this.bugunTarih()).subscribe({
      next: (data) => this.gunlukOzet.set(data),
      error: () => this.gunlukOzet.set(null)
    });

    this.api.haftalikOzet().subscribe({
      next: (data) => this.haftalikOzet.set(data),
      error: () => this.haftalikOzet.set(null)
    });

    this.api.ozetGetir().subscribe({
      next: (data) => {
        this.onbesGunlukOzet.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.hataGoster('Özet yüklenemedi: ' + err.message);
        this.loading.set(false);
      }
    });
  }

  olcumEkle(): void {
    const tarih = this.formTarih();
    const zaman = this.formZaman();
    const buyuk = this.formBuyuk();
    const kucuk = this.formKucuk();

    if (!tarih || !buyuk || !kucuk) {
      this.hataGoster('Lütfen tüm alanları doldurun.');
      return;
    }

    if (buyuk < 50 || buyuk > 250) {
      this.hataGoster('Büyük tansiyon 50-250 arasında olmalı.');
      return;
    }

    if (kucuk < 30 || kucuk > 150) {
      this.hataGoster('Küçük tansiyon 30-150 arasında olmalı.');
      return;
    }

    const request: OlcumEkleRequest = {
      tarih,
      zamanDilimi: zaman,
      buyuk,
      kucuk
    };

    this.loading.set(true);

    if (this.duzenlemeModu() && this.duzenlenenId()) {
      this.api.olcumGuncelle(this.duzenlenenId()!, request).subscribe({
        next: () => {
          this.basariGoster('Ölçüm başarıyla güncellendi!');
          this.formTemizle();
          this.duzenlemeyiIptalEt();
          this.verileriYukle();
        },
        error: (err) => {
          const mesaj = err.error?.message || err.message || 'Güncelleme hatası';
          this.hataGoster(mesaj);
          this.loading.set(false);
        }
      });
    } else {
      this.api.olcumEkle(request).subscribe({
        next: (msg) => {
          this.basariGoster(msg || 'Ölçüm başarıyla eklendi!');
          this.formTemizle();
          this.verileriYukle();
        },
        error: (err) => {
          const mesaj = err.error?.message || err.error || err.message || 'Bilinmeyen hata';
          this.hataGoster(mesaj);
          this.loading.set(false);
        }
      });
    }
  }

  duzenle(olcum: TansiyonOlcumu): void {
    this.duzenlemeModu.set(true);
    this.duzenlenenId.set(olcum.id);
    this.formTarih.set(olcum.tarih);
    this.formZaman.set(olcum.zamanDilimi);
    this.formBuyuk.set(olcum.buyuk);
    this.formKucuk.set(olcum.kucuk);

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  duzenlemeyiIptalEt(): void {
    this.duzenlemeModu.set(false);
    this.duzenlenenId.set(null);
    this.formTemizle();
    this.formTarih.set(this.bugunTarih());
    this.formZaman.set('SABAH');
  }

  sil(olcum: TansiyonOlcumu): void {
    if (!confirm(`${this.formatTarih(olcum.tarih)} ${olcum.zamanDilimi} ölçümünü silmek istediğinizden emin misiniz?`)) {
      return;
    }

    this.loading.set(true);
    this.api.olcumSil(olcum.id).subscribe({
      next: () => {
        this.basariGoster('Ölçüm başarıyla silindi!');
        this.verileriYukle();
      },
      error: (err) => {
        const mesaj = err.error?.message || err.message || 'Silme hatası';
        this.hataGoster(mesaj);
        this.loading.set(false);
      }
    });
  }

  formTemizle(): void {
    this.formBuyuk.set(null);
    this.formKucuk.set(null);
  }

  durumRengi(durum: string | undefined): string {
    switch (durum) {
      case 'NORMAL': return 'durum-normal';
      case 'HIPERTANSIYON_SUPHESI': return 'durum-yuksek';
      case 'HIPOTANSIYON_SUPHESI': return 'durum-dusuk';
      default: return 'durum-bos';
    }
  }

  durumEmoji(durum: string | undefined): string {
    switch (durum) {
      case 'NORMAL': return '✅';
      case 'HIPERTANSIYON_SUPHESI': return '🔴';
      case 'HIPOTANSIYON_SUPHESI': return '🟡';
      default: return '⚪';
    }
  }

  durumBaslik(durum: string | undefined): string {
    switch (durum) {
      case 'NORMAL': return 'Normal';
      case 'HIPERTANSIYON_SUPHESI': return 'Yüksek Tansiyon Riski';
      case 'HIPOTANSIYON_SUPHESI': return 'Düşük Tansiyon Riski';
      case 'VERI_YOK': return 'Veri Yok';
      default: return 'Bilinmiyor';
    }
  }

  formatOrt(val: number | null | undefined): string {
    if (val === null || val === undefined) return '-';
    return val.toFixed(0);
  }

  formatTarih(tarih: string): string {
    const [yil, ay, gun] = tarih.split('-');
    return `${gun}.${ay}.${yil}`;
  }

  private bugunTarih(): string {
    return new Date().toISOString().split('T')[0];
  }

  private basariGoster(text: string): void {
    this.mesaj.set({ tip: 'ok', text });
    setTimeout(() => this.mesaj.set(null), 4000);
  }

  private hataGoster(text: string): void {
    this.mesaj.set({ tip: 'err', text });
    setTimeout(() => this.mesaj.set(null), 5000);
  }
}
