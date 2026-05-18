# Perencanaan Refactoring: Monolith ke Event-Driven Architecture (EDA) & Monorepo

Dokumen ini berisi perencanaan strategis untuk melakukan refactoring pada aplikasi **Yomu Backend** dari arsitektur monolitik menjadi **Event-Driven Architecture (EDA)** dalam struktur **monorepo**. Tujuan utama refactoring ini adalah untuk memenuhi constraint sistem (decoupling antar-modul) dan meningkatkan kualitas *Software Architecture*.

## 1. Persiapan Sebelum Refactoring

### 1.1 Identifikasi *Bounded Contexts*
Berdasarkan dokumen `overall_tugas.md` dan struktur *codebase* saat ini, sistem dapat dibagi menjadi beberapa *Bounded Contexts* yang independen:
- **Auth Context (Modul 1)**: Manajemen pengguna, identitas, dan autentikasi.
- **Reading & Quiz Context (Modul 2)**: Logika pengerjaan kuis, verifikasi teks bacaan, dan kalkulasi akurasi.
- **Achievement Context (Modul 3)**: Gamifikasi, *Daily Mission*, dan pencapaian pengguna (termasuk `AchievementService` dan `DailyMissionService`).
- **Clan & League Context (Modul 4)**: Tier system, kalkulasi skor Clan, buff/debuff dinamis.
- **Discussion Context (Modul 5)**: Thread komentar bersarang dan reaksi.

### 1.2 Pendefinisian Kontrak Event (*Event Schemas*)
Saat ini, file `AchievementEvent.java` hanya berupa kumpulan *string constants* (`READING_COMPLETED`, `QUIZ_FINISHED`, dll). Struktur ini akan diubah menjadi representasi *event payload* yang konkret untuk menjembatani komunikasi antar-konteks.
Contoh kontrak event:
```java
// Akan disimpan di sub-project/module shared-events
public record QuizFinishedEvent(UUID userId, UUID quizId, int score, boolean isPerfectScore, LocalDateTime timestamp) {}
public record ReadingCompletedEvent(UUID userId, UUID readingId, LocalDateTime timestamp) {}
public record ClanPromotionEvent(UUID clanId, String newTier, LocalDateTime timestamp) {}
```

### 1.3 Pengecekan *Test Coverage*
Sebelum merombak logika utama (khususnya `AchievementServiceImpl` dan `DailyMissionServiceImpl`), *test coverage* (seperti pada `DailyMissionServiceTest`) wajib diverifikasi mencapai 100% menggunakan **Jacoco**. Tes ini akan bertindak sebagai *safety net* untuk memastikan logika inti tidak rusak pasca-refactoring.

## 2. Langkah-langkah Refactoring (Step-by-Step)

### 2.1 Transformasi Struktur Gradle (Monorepo)
Ubah struktur tunggal `yomu-backend` menjadi arsitektur Gradle *multi-project build* (monorepo).
```text
yomu-backend/
├── shared-events/      # (Kontrak DTO & Event)
├── yomu-auth/
├── yomu-quiz/
├── yomu-achievement/   # (Modul Achievement & Daily Mission)
├── yomu-clan/
└── settings.gradle     # (Deklarasi include sub-projects)
```

### 2.2 Migrasi Logika Sinkron ke Asinkron Berbasis Event
Saat ini, interaksi kemungkinan masih bersifat *tightly-coupled* (pemanggilan *method* langsung ke service lain).
1.  **Transisi Fase 1**: Gunakan `ApplicationEventPublisher` dan `@EventListener` dari Spring untuk transisi awal ke pola asinkron di dalam JVM yang sama.
2.  **Transisi Fase 2 (Distributed)**: Implementasikan Message Broker (seperti **RabbitMQ** atau **Redis Pub/Sub**). Modul kuis hanya akan mem-*publish* event, tanpa perlu tahu modul apa saja yang mendengarkan.

### 2.3 Refactoring `AchievementServiceImpl` dan `DailyMissionServiceImpl`
Alih-alih service lain memanggil `processEvent(userId, eventType)`, modul Achievement akan mendengarkan event yang masuk:
```java
@Component
@RequiredArgsConstructor
public class AchievementEventListener {
    private final AchievementService achievementService;
    private final DailyMissionService dailyMissionService;

    @RabbitListener(queues = "quiz.finished.queue") // Atau @EventListener
    public void handleQuizFinished(QuizFinishedEvent event) {
        // Logika asinkron memproses achievement
        achievementService.processEvent(event.userId(), AchievementEvent.QUIZ_FINISHED);
        if (event.isPerfectScore()) {
            achievementService.processEvent(event.userId(), AchievementEvent.PERFECT_QUIZ_SCORE);
        }
        
        // Logika memproses daily mission
        dailyMissionService.processDailyEvent(event.userId(), AchievementEvent.QUIZ_FINISHED);
    }
}
```

### 2.4 Pemisahan Dependensi Repository
Setiap modul tidak boleh lagi melakukan *dependency injection* terhadap repository modul lain (misal modul Quiz meng-*inject* `AchievementRepository`). Pemisahan ini menjamin tidak ada pembagian *state database* secara langsung.

## 3. Aktivitas Setelah Refactoring

### 3.1 *Integration Testing*
Uji coba aliran data (event) antar-modul menggunakan *Testcontainers* untuk RabbitMQ/Redis dan Postgres. Pengujian fokus pada apakah *publishing* event dari modul Quiz berhasil memicu penambahan progres di *UserAchievement*.

### 3.2 Pembaruan Konfigurasi Infrastruktur & CI/CD
-   **Docker (`Dockerfile` & `docker-compose.yml`)**: `docker-compose.yml` akan diperbarui untuk menjalankan instance Message Broker (RabbitMQ) dan setiap microservice secara terpisah. `Dockerfile` akan dibuat *multi-stage* berdasarkan sub-proyek (module) Gradle.
-   **Pipeline CI/CD (`cd.yaml` & `deploy.yml`)**: Modifikasi GitHub Actions workflow untuk mendukung deteksi perubahan per modul (menggunakan *matrix strategy*). Sehingga modul yang tidak berubah kodenya, tidak perlu di-build dan di-deploy ulang.

### 3.3 Monitoring dan *Dead Letter Queue* (DLQ)
Terapkan DLQ di sisi RabbitMQ. Jika `yomu-achievement` sedang *down* saat event `QuizFinished` dipublikasikan, pesan tersebut tidak akan hilang. Pesan ditampung di DLQ dan akan di-*retry* setelah service kembali *up*.

## 4. Manfaat Refactoring

-   **Peningkatan Skalabilitas & *Maintainability***: Tiap modul dapat diskalakan sesuai kebutuhan (contoh: modul kuis di-*scale-out* karena *read/write heavy*, sementara modul diskusi dibiarkan kecil).
-   **Pengurangan *Coupling***: Modul Bacaan & Kuis (Modul 2) tidak perlu peduli terhadap proses *milestone achievement* atau status *Clan Buff* (Modul 3 & 4), memenuhi constraint sistem "tidak ada pemanggilan langsung antar komponen".
-   **Peningkatan Nilai Arsitektur**: Menunjukkan penguasaan tingkat lanjut atas prinsip *Domain-Driven Design* (DDD) dan *Microservices Pattern*, yang sangat bernilai tinggi di mata dosen/penguji.

## 5. Risiko, Kekurangan, dan Strategi Presentasi

### 5.1 Risiko dan Kekurangan
-   ***Eventual Consistency***: Perubahan tidak selalu real-time di layar pengguna. Pengguna mungkin menyelesaikan kuis sekarang, tapi notifikasi penyelesaian *achievement* baru muncul dalam 1-2 detik karena *queue delay*.
-   **Kompleksitas *Debugging***: Lacak *error* (*traceability*) lebih sulit antar layanan yang terpisah tanpa *stacktrace* konvensional.

### 5.2 Strategi Presentasi
-   **Transparansi *Trade-off***: Akui adanya masalah *eventual consistency* dan kompleksitas, lalu *frame* hal tersebut sebagai "harga yang harus dibayar" (*trade-off*) untuk mencapai skalabilitas dan *loose-coupling* seperti yang dituntut oleh constraint tugas.
-   **Sorot Fitur Reliabilitas**: Jelaskan mekanisme DLQ dan *Event-Carried State Transfer*. Hal ini akan meyakinkan penguji bahwa kelonggaran konsistensi ditutupi dengan garansi pesan pasti sampai (*delivery guarantee*).
-   **Gunakan Diagram (Before/After)**: Saat presentasi, gunakan arsitektur *spaghetti code* masa lalu berhadapan dengan diagram EDA rapi, menunjukkan aliran data satu arah melalui *event bus*.
