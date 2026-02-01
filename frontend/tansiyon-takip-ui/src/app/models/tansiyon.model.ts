// Tansiyon ölçümü 
export interface TansiyonOlcumu {
  id: number;
  tarih: string; 
  zamanDilimi: ZamanDilimi;
  buyuk: number; 
  kucuk: number; 
}


export type ZamanDilimi = 'SABAH' | 'AKSAM';

export interface OlcumEkleRequest {
  tarih: string;
  zamanDilimi: ZamanDilimi;
  buyuk: number;
  kucuk: number;
}

export interface OzetResponse {
  toplamOlcumSayisi: number;
  sabahBuyukOrt: number | null;
  sabahKucukOrt: number | null;
  aksamBuyukOrt: number | null;
  aksamKucukOrt: number | null;
  genelBuyukOrt: number | null;
  genelKucukOrt: number | null;
  durum: TansiyonDurum;
  mesaj: string;
}

// Tansiyon durum 
export type TansiyonDurum = 
  | 'NORMAL' 
  | 'HIPERTANSIYON_SUPHESI' 
  | 'HIPOTANSIYON_SUPHESI' 
  | 'VERI_YOK';
