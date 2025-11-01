# Firestore Bileşik İndeks Oluşturma Kılavuzu (Matchmaking)

Bu kılavuz, ExtremeSudoku uygulamasındaki PvP eşleştirme sorguları için gerekli bileşik (composite) indeksin nasıl oluşturulacağını ve yayımlanacağını adım adım açıklar. Hem Firebase Console üzerinden görsel adımlar hem de Firebase CLI ile sürüm kontrollü dağıtım yöntemleri yer alır.

---

## Ne İçin Gerekli?

Eşleştirme sorgusu şu kombinasyonu kullanır:
- Filtreler: `status == "searching"` ve `mode == <seçilen mod>`
- Sıralama: `orderBy("timestamp", ASC)`

Firestore, bu tip çoklu eşitlik + sıralama sorguları için çoğunlukla bileşik indeks ister. İndeks olmadan sorgu `FAILED_PRECONDITION: The query requires an index.` hatası verir.

---

## İndeks Tanımı

- Koleksiyon: `matchmaking_queue`
- Alanlar ve sıralama:
  1) `status` (Ascending)
  2) `mode` (Ascending)
  3) `timestamp` (Ascending)

Repoda bu indeks `firestore.indexes.json` içinde tanımlı.

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

Not: Alan sırası `mode → status → timestamp` olarak tanımda yer alıyor. Sorgu tarafında da bu iki eşitlik filtresi + timestamp sıralaması beraber kullanıldığı için uygundur. Firestore, alanların birebir sırası konusunda esnek olabilir; ancak pratikte bu tanım, ihtiyaç duyulan kombinasyonu karşılar.

---

## Yöntem 1 — Firebase Console ile (Hızlı Başlangıç)

1) Firebase Console → Firestore Database → Indexes
2) “Create index” butonuna tıklayın
3) Ayarlar:
   - Collection ID: `matchmaking_queue`
   - Fields:
     - `status` — Ascending
     - `mode` — Ascending
     - `timestamp` — Ascending
4) Kaydedin ve 2–5 dakika derlenmesini bekleyin
5) Durum “Enabled” olduğunda test edebilirsiniz

Artılar:
- Arayüz üzerinden hızlı kurulum
Eksiler:
- Sürüm kontrolü ile senkronize değildir (ayrıca JSON’a aktarmanız gerekir)

---

## Yöntem 2 — Firebase CLI ile (Önerilen)

Bu yöntem, ekip çalışması ve sürüm kontrolü için idealdir. Repoda `firebase.json` dosyası `firestore.indexes.json` dosyasını zaten referans ediyor.

Windows (cmd.exe):

1) Firebase’e giriş
```cmd
firebase login
```

2) Projeyi seçin (gerekirse ekleyin)
```cmd
firebase use --add
```

3) Yalnızca indeks ve kuralları deploy edin
```cmd
firebase deploy --only firestore:indexes,firestore:rules
```

4) (İsteğe bağlı) Tüm kaynakları deploy edin
```cmd
firebase deploy
```

Doğrulama:
- Console → Firestore → Indexes sayfasında ilgili indeks “Enabled” görünmeli
- Loglarda `FAILED_PRECONDITION` hatası kaybolmalı

---

## Doğrulama (Uygulama İçi Test)

1) İki cihaz/emülatörle aynı moda girip eşleştirmeyi başlatın
2) 0–5 sn içinde eşleşme beklenir
3) Hata alınırsa Logcat’te hata koduna bakın:
   - `FAILED_PRECONDITION`: İndeks henüz hazır olmayabilir — birkaç dakika daha bekleyin
   - `PERMISSION_DENIED`: Firestore kurallarını kontrol edin
   - `UNAVAILABLE`: Ağ problemi

---

## Sık Görülen Sorunlar ve Çözümler

- S: İndeksi oluşturdum ama hâlâ hata var
  - C: İndeksin durumu “Building” olabilir. “Enabled” olana kadar bekleyin ve tekrar deneyin.

- S: Hangi projeye deploy ediyorum emin değilim
  - C: `firebase use` ile aktif projeyi görün. Gerekirse `firebase use --add` ile doğru projeyi seçin.

- S: `PERMISSION_DENIED` alıyorum
  - C: `firestore.rules` dosyanızdaki `matchmaking_queue` izinlerini gözden geçirin. En azından okuma ve gerekli güncellemeler için `request.auth != null` sağlanmalı.

---

## İleri Düzey: İndeks Gereksinimini Kodda Belirleme (Opsiyonel)

Hata yakalama sırasında geliştirici loglarına yönlendirme ekleyebilirsiniz:

```kotlin
catch (e: Exception) {
  if (e is FirebaseFirestoreException &&
      e.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
    Log.w(TAG, "İndeks eksik. CLI: 'firebase deploy --only firestore:indexes' veya Console → Indexes üzerinden oluşturun.")
  }
}
```

Bu, sahada teşhis süresini kısaltır.

---

## Sonuç

- İndeks gereksinimi, eşleştirme sorgusunun doğasından kaynaklanır.
- Repoda indeks JSON tanımı hazır; tek adım deploy ve etkinleşmesini beklemektir.
- Etkinleştirildikten sonra eşleştirme akışı beklenen hızda çalışacaktır.
