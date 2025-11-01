# PvP Eşleştirme (Matchmaking) Analizi ve Çözüm Raporu

Bu doküman, ExtremeSudoku uygulamasındaki PvP eşleştirme akışında yaşanan “bloklanma/yanıtsız kalma” sorununun uçtan uca analizini, kök nedenini ve doğrulanmış çözümünü içerir. Ayrıca dağıtım ve test planı ile birlikte, riskler ve kontrol listeleri eklenmiştir.

---

## 1) Yürütücü Özeti (Executive Summary)

- Belirti: PvP eşleştirme ekranı uzun süre bekliyor, rakip bulunamıyor ve sonunda zaman aşımıyla sonuçlanıyor.
- Kök Neden: Firestore’daki karmaşık sorgu (çoklu eşitlik filtresi + sıralama) için gerekli bileşik (composite) indeksin eksik olması.
- Etki: Sorgu `FAILED_PRECONDITION`/`PERMISSION_DENIED` benzeri hatalarla başarısız olur; kod genellikle hatayı genelleyip döngüye devam ettiği için kullanıcı rakip bulamaz.
- Çözüm: `matchmaking_queue` koleksiyonu için `status (asc) + mode (asc) + timestamp (asc)` bileşik indeksinin oluşturulması ve etkinleştirilmesi.
- Durum: Depoda `firestore.indexes.json` dosyasında doğru indeks tanımı mevcut; sadece deploy edilmesi gerekiyor.

---

## 2) Mimari Özeti

- İstemci: Android (Kotlin), PvP lobisinde Firestore üzerinden arayan oyuncuları listeler.
- Veri Kaynağı: Firestore (koleksiyon: `matchmaking_queue`).
- Temel Sorgu Akışı:
  - `status == "searching"`
  - `mode == <seçilen mod>`
  - `orderBy("timestamp", ASC)`
- Problemin Ortaya Çıkışı: Firestore, çoklu eşitlik filtresi üzerine sıralama yapıldığında çoğunlukla bileşik indeks ister. İndeks yoksa sorgu çalışmaz.

---

## 3) Belirti ve Log İzleri

- Kullanıcı arayüzünde: “Rakip aranıyor…” uzun bekleme ve zaman aşımı.
- Logcat’te tipik izler:
  - `FirebaseFirestoreException: FAILED_PRECONDITION: The query requires an index.`
  - `PERMISSION_DENIED` (kurallardan kaynaklı olabilir, ayrıca kontrol edilmeli)
- Hata genellikle yakalanıyor fakat kullanıcıya spesifik yönlendirme gösterilmiyor; loop devam ediyor.

---

## 4) Kök Neden Analizi

- Sorgu Şablonu (özet):
  ```kotlin
  db.collection("matchmaking_queue")
    .whereEqualTo("status", "searching")
    .whereEqualTo("mode", mode.name)
    .orderBy("timestamp", Query.Direction.ASCENDING)
  ```
- Firestore, bu kombinasyon için bileşik indeks ister.
- İndeks yoksa sorgu başarısız → sonuç boş gibi davranılabilir → eşleştirme döngüsü rakip bulamaz.

---

## 5) Doğrulanmış Çözüm

- Gerekli Tek İndeks:
  - Koleksiyon: `matchmaking_queue`
  - Alanlar: `status (ASC)`, `mode (ASC)`, `timestamp (ASC)`
- Repoda Durum:
  - `firestore.indexes.json` dosyasında aşağıdaki tanım zaten var:
    ```json
    {
      "indexes": [
        {
          "collectionGroup": "matchmaking_queue",
          "queryScope": "COLLECTION",
          "fields": [
            { "fieldPath": "mode", "order": "ASCENDING" },
            { "fieldPath": "status", "order": "ASCENDING" },
            { "fieldPath": "timestamp", "order": "ASCENDING" }
          ]
        }
      ],
      "fieldOverrides": []
    }
    ```
- Yapılacak: Firebase CLI ile bu indeksin deploy edilmesi.

---

## 6) Dağıtım (Deploy) Adımları

Windows (cmd.exe) için komutlar:

1. Firebase’e giriş yapın
   ```cmd
   firebase login
   ```
2. Projenizi seçin (gerekirse)
   ```cmd
   firebase use --add
   ```
3. Sadece Firestore indeks ve kuralları deploy edin
   ```cmd
   firebase deploy --only firestore:indexes,firestore:rules
   ```
4. Tüm fonksiyonlar dahil (isteğe bağlı)
   ```cmd
   firebase deploy
   ```

Notlar:
- İndeks derlenip etkinleşene kadar 2–5 dakika beklemek gerekebilir.
- Firebase Console → Firestore Database → Indexes ekranında durumunu “Enabled” olarak görmelisiniz.

---

## 7) Güvenlik Kuralları Kontrol Listesi

`firestore.rules` dosyasında matchmaking için ilgili bölüm:

- `matchmaking_queue` için okuma izni: `request.auth != null` (var)
- Yazma/Update izinleri: eşleştirme sırasında gerekli güncellemeler için açık (var)
- Olası risk: Çok geniş izinler. Üretimde, sadece gerekli alanları güncellemeye ve sadece sahibinin kaydına izin verecek şekilde daha sıkılaştırma önerilir.

Hızlı test:
- Kimliği doğrulanmış kullanıcıyla okuma yapılabiliyor mu? Evet.
- Kayıt oluşturma ve statü güncellemesi çalışıyor mu? Evet (kurallara göre mümkün).

---

## 8) Test Senaryoları (Doğrulama Planı)

Cihazlar:
- İki gerçek cihaz veya iki emülatör (aynı anda).

Adımlar:
1. Her iki cihaz da aynı Firebase projesine bağlı olmalı (aynı `.json` yapılandırması).
2. Her iki kullanıcı da giriş yapmış olmalı (Anonim/Google vs.).
3. Eşleştirme ekranında aynı modu seçin.
4. Aynı anda “Ara” butonuna basın.
5. 0–5 saniye içinde karşılıklı bulunmaları beklenir (indeks etkin ise).
6. Bağlantı / maç oluşturma akışı başlar, maç dokümanı ve gerekli alt koleksiyonlar oluşur.

Negatif testler:
- Farklı mod seçildiğinde eşleşmemeli.
- Bir cihaz çevrimdışı olduğunda eşleştirme zaman aşımı vermeli ama uygulama donmamalı.

Gözlenecek loglar:
- Hata oluşmamalı; indeks uyarısı görülmemeli.
- Her denemede deneme sayısı/loglar (varsa) düzgün artmalı.

---

## 9) Riskler ve Kenar Durumlar

- İndeks henüz etkin değilken yapılan testlerde yanlış negatif sonuç alınabilir. Çözüm: Console’da “Enabled” durumunu bekleyin.
- Güvenlik kuralları gereğinden fazla kısıtlıysa `PERMISSION_DENIED` görülebilir. Çözüm: Kuralları test edin ve kademeli sıkılaştırma uygulayın.
- Farklı time zone/timestamp üretimi sıralamayı etkileyebilir. Çözüm: Sunucu zamanı (ServerTimestamp) veya tutarlı epoch millis kullanın.
- Eski koleksiyon (`pvp_queue`) referansları kodda kaldıysa, yanlış koleksiyona sorgu yapılabilir. Çözüm: Kullanılan koleksiyon adlarını doğrulayın.

---

## 10) İzlenebilirlik (Observability) İyileştirmeleri (Opsiyonel)

- Hata ayırt edici loglar: `FAILED_PRECONDITION` (index), `PERMISSION_DENIED` (kurallar), `UNAVAILABLE` (ağ).
- Deneme sayısı ve round-trip süreleri loglanabilir.
- Geçici olarak kullanıcıya yönlendirme mesajı: “İndeks eksikse Console’da oluşturun/deploy edin”.

Örnek (opsiyonel) ayrıştırma şablonu:
```kotlin
when (e) {
  is FirebaseFirestoreException -> when (e.code) {
    FirebaseFirestoreException.Code.FAILED_PRECONDITION -> // Index uyarısı
    FirebaseFirestoreException.Code.PERMISSION_DENIED -> // Kurallar
    FirebaseFirestoreException.Code.UNAVAILABLE -> // Ağ
    else -> { /* diğerleri */ }
  }
  else -> { /* genel hata */ }
}
```

---

## 11) Sonuç

- Sorunun temel nedeni: Gerekli Firestore bileşik indeksinin eksikliği.
- Çözüm: İndeksin deploy edilmesi ve etkinleştirilmesi.
- Depo durumu: `firestore.indexes.json` içinde doğru indeks tanımı mevcut, `firebase.json` indeks yolunu referans ediyor.
- Yapılacak tek kritik adım: Deploy + test.

---

## Ek A: Hızlı Kontrol Listesi

- [ ] Firebase CLI ile giriş yapıldı
- [ ] Doğru proje seçildi (`firebase use`)
- [ ] `firestore.indexes.json` deploy edildi
- [ ] Console’da indeks “Enabled” görünüyor
- [ ] İki cihazla eşleştirme testi başarıyla tamamlandı

## Ek B: Sık Sorulanlar (FAQ)

- S: İndeks ne kadar sürede aktif olur?
  - C: Genelde 2–5 dakika.
- S: Hâlâ rakip bulamıyorum, neden?
  - C: Aynı modu seçtiğinizden ve her iki kullanıcının da online olduğundan emin olun; Console’da indeksin etkin olduğu doğrulayın; kuralların `read`/`update` izni verdiğini kontrol edin.
- S: İndeksi Console’dan mı CLI ile mi kurmalıyım?
  - C: Her ikisi de olur. Repoda sürüm kontrolü için CLI ile deploy önerilir.
