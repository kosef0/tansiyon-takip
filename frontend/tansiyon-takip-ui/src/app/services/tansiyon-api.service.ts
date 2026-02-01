import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TansiyonOlcumu, OlcumEkleRequest, OzetResponse } from '../models/tansiyon.model';

@Injectable({
  providedIn: 'root',
})
export class TansiyonApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/olcumler';

  // Yeni ölçüm ekle
  olcumEkle(request: OlcumEkleRequest): Observable<string> {
    return this.http.post(this.baseUrl, request, { responseType: 'text' });
  }

  // Tüm ölçümleri getir
  tumOlcumleriGetir(): Observable<TansiyonOlcumu[]> {
    return this.http.get<TansiyonOlcumu[]>(this.baseUrl);
  }

  // Genel özet (varsayılan: son 15 gün)
  ozetGetir(baslangic?: string, bitis?: string): Observable<OzetResponse> {
    let params = new HttpParams();
    if (baslangic) params = params.set('baslangic', baslangic);
    if (bitis) params = params.set('bitis', bitis);
    return this.http.get<OzetResponse>(`${this.baseUrl}/ozet`, { params });
  }

  // Haftalık özet
  haftalikOzet(): Observable<OzetResponse> {
    return this.http.get<OzetResponse>(`${this.baseUrl}/ozet/haftalik`);
  }

  // Günlük özet
  gunlukOzet(tarih: string): Observable<OzetResponse> {
    const params = new HttpParams().set('tarih', tarih);
    return this.http.get<OzetResponse>(`${this.baseUrl}/ozet/gunluk`, { params });
  }

  // Ölçüm sil
  olcumSil(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Ölçüm güncelle
  olcumGuncelle(id: number, request: OlcumEkleRequest): Observable<TansiyonOlcumu> {
    return this.http.put<TansiyonOlcumu>(`${this.baseUrl}/${id}`, request);
  }
}
