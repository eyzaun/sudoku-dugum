Android Başlatıcı Simge Paketi

Dizin yapısı:
  res/
    mipmap-*/ic_launcher.png              (legacy simge)
    mipmap-*/ic_launcher_round.png        (yuvarlak)
    mipmap-*/ic_launcher_foreground.png   (adaptive foreground)
    mipmap-*/ic_launcher_monochrome.png   (Android 13+)
    mipmap-anydpi-v26/ic_launcher.xml     (adaptive)
    mipmap-anydpi-v26/ic_launcher_round.xml
  res/values/colors.xml                   (arka plan rengi #0d1b2a)
  marketing/icon-512.png                  (Play Store için 512x512)

Kullanım:
- Android Studio’da “mipmap” klasörüne bu dosyaları kopyalayın (varsa üzerine yazın).
- AndroidManifest.xml içinde uygulama simgesi:
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
- Adaptive simge arka plan rengi values/colors.xml içinde tanımlıdır.
- Monokrom simge Android 13+ cihazlarda otomatik kullanılır.