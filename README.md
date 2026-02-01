# 🩺 Tansiyon Takip

Kan basıncı ölçümlerini takip eden full-stack web uygulaması.

## Teknolojiler

**Backend:** Spring Boot , PostgreSQL 
**Frontend:** Angular , TypeScript

## Kurulum

### Veritabanı
PostgreSQL'de `tns_takip` adında veritabanı oluşturun:
```sql
CREATE DATABASE tns_takip;
```

### Backend
```bash
cd backend/tansiyon-takip
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend/tansiyon-takip-ui
npm install
npm start
```

## Özellikler

- ✅ Ölçüm ekleme, düzenleme, silme
- 📊 Günlük, haftalık, 15 günlük özet
- 🔴 Hipertansiyon / Hipotansiyon uyarısı
- 🌙 Sabah / Akşam ölçüm ayrımı
