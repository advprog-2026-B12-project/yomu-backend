# Arsitektur Platform Yomu

Dokumen ini mendeskripsikan arsitektur perangkat lunak Platform Yomu menggunakan model C4 — mencakup level Context, Container, dan Component. Stack teknologi yang digunakan meliputi **Next.js** (frontend), **Spring Boot** (backend API), **Neon PostgreSQL** (database), **Vercel** (deployment frontend), **Railway** (deployment backend), **Prometheus** (pengumpul metrik), dan **Grafana** (visualisasi monitoring). CI/CD dikelola melalui **GitHub Actions** pada kedua repositori.

---

## 1. System Context Diagram

Diagram Context menampilkan Platform Yomu beserta aktor eksternal dan sistem-sistem yang berinteraksi dengannya.

```plantuml
@startuml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml

LAYOUT_WITH_LEGEND()

title System Context Diagram — Platform Yomu

Person(pelajar, "Pelajar", "Pengguna platform yang membaca teks,\nmengerjakan kuis, dan bergabung dalam clan.")
Person_Ext(admin, "Admin", "Mengelola materi bacaan, kuis,\ndan misi harian.")

System(yomu, "Platform Yomu", "Sistem Gamifikasi Membaca\ndan Literasi Digital.")

System_Ext(google_oauth, "Google OAuth", "Identity Provider untuk\nlayanan SSO.")
System_Ext(prometheus, "Prometheus", "Mengumpulkan dan menyimpan\nmetrik dari backend.")
System_Ext(grafana, "Grafana", "Memvisualisasikan metrik\ndari Prometheus.")

Rel(pelajar, yomu, "Menggunakan", "HTTPS")
Rel(admin, yomu, "Mengelola", "HTTPS")
Rel(yomu, google_oauth, "Memverifikasi identitas pengguna via", "HTTPS")
Rel(prometheus, yomu, "Men-scrape metrik dari", "HTTP / /actuator/prometheus")
Rel(grafana, prometheus, "Membaca metrik dari", "PromQL / HTTPS")

@enduml
```
---

## 2. Container Diagram

Diagram Container menampilkan arsitektur teknis tingkat tinggi — bagaimana sistem di-deploy di Vercel dan Railway, serta bagaimana masing-masing container berinteraksi dengan database Neon.

```plantuml
@startuml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

LAYOUT_WITH_LEGEND()

title Container Diagram — Platform Yomu

Person(pelajar, "Pelajar", "Pengguna platform.")
Person_Ext(admin, "Admin", "Pengelola platform.")

System_Ext(google_oauth, "Google OAuth", "Identity Provider")
System_Ext(prometheus, "Prometheus", "Scrape & simpan metrik")
System_Ext(grafana, "Grafana", "Monitoring & Observabilitas")

System_Boundary(yomu, "Platform Yomu") {

    Container(frontend, "Web Application", "Next.js", "Menyediakan antarmuka pengguna\nuntuk pelajar dan admin.\nDi-deploy di Vercel.")

    Container(backend, "API Application", "Spring Boot", "Menangani logika bisnis,\nproses berbasis event, dan\nmelayani permintaan API REST.\nDi-deploy di Railway.")

    ContainerDb(database, "Database", "Neon PostgreSQL", "Menyimpan data pengguna, kuis,\nclan, pencapaian, misi, dan\nleaderboard.")

}

Rel(pelajar, frontend, "Mengakses antarmuka", "HTTPS")
Rel(admin, frontend, "Mengelola konten", "HTTPS")

Rel(frontend, backend, "Mengirim permintaan API", "JSON / HTTPS")
Rel(backend, database, "Membaca dan menulis data", "JDBC")
Rel(backend, google_oauth, "Memverifikasi ID Token", "HTTPS")
Rel(prometheus, backend, "Men-scrape endpoint metrik", "HTTP / /actuator/prometheus")
Rel(grafana, prometheus, "Membaca & memvisualisasikan metrik", "PromQL / HTTPS")

@enduml
```

## 3. Component Diagram — API Application

Diagram Component memperbesar bagian Spring Boot backend untuk menampilkan struktur **modular monolith**-nya. Komunikasi antar modul domain dilakukan melalui **Event-Driven Architecture (EDA)** menggunakan `ApplicationEventPublisher` bawaan Spring, sehingga setiap modul tetap terdekopel satu sama lain.

```plantuml
@startuml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml

LAYOUT_WITH_LEGEND()

title Component Diagram — API Application (Spring Boot)

ContainerDb(database, "Database", "Neon PostgreSQL", "Database bersama\nseluruh modul domain.")
System_Ext(google_oauth, "Google OAuth", "Identity Provider")

Container_Boundary(backend, "API Application (Spring Boot — Modular Monolith)") {

    Component(api_layer, "Controllers / API Layer", "Spring Web (REST)", "Menerima dan merutekan\npermintaan HTTP ke modul\ndomain yang sesuai.")

    Component(auth_mod, "Auth & User Module", "Spring Boot", "Autentikasi, manajemen sesi,\ndan profil pengguna.")

    Component(quiz_mod, "Quiz & Reading Module", "Spring Boot", "Manajemen bacaan, soal kuis,\ndan progres belajar pengguna.")

    Component(ach_mod, "Achievements Module", "Spring Boot", "Misi harian, lencana,\ndan pencapaian pengguna.")

    Component(clan_mod, "Clan & League Module", "Spring Boot", "Manajemen clan, liga kompetisi,\ndan leaderboard.")

    Component(disc_mod, "Discussion Module", "Spring Boot", "Komentar, balasan,\ndan reaksi pada konten bacaan.")

    Component(event_bus, "Event Bus", "Spring ApplicationEventPublisher", "Message broker in-memory.\nMenjadi penghubung antar modul\ntanpa ketergantungan langsung.")

}

' --- Routing dari API Layer ke modul domain ---
Rel_D(api_layer, auth_mod, "Meneruskan request")
Rel_D(api_layer, quiz_mod, "Meneruskan request")
Rel_D(api_layer, ach_mod, "Meneruskan request")
Rel_D(api_layer, clan_mod, "Meneruskan request")
Rel_D(api_layer, disc_mod, "Meneruskan request")

' --- Akses ke sistem eksternal ---
Rel(auth_mod, google_oauth, "Memverifikasi ID Token", "HTTPS")

' --- Akses database per modul ---
Rel(auth_mod, database, "Baca / Tulis", "JDBC")
Rel(quiz_mod, database, "Baca / Tulis", "JDBC")
Rel(ach_mod, database, "Baca / Tulis", "JDBC")
Rel(clan_mod, database, "Baca / Tulis", "JDBC")
Rel(disc_mod, database, "Baca / Tulis", "JDBC")

' --- Publisher: modul → event bus ---
Rel(auth_mod, event_bus, "Mempublikasikan event")
Rel(quiz_mod, event_bus, "Mempublikasikan event")
Rel(clan_mod, event_bus, "Mempublikasikan event")
Rel(ach_mod, event_bus, "Mempublikasikan event")

' --- Listener: event bus → modul ---
Rel(event_bus, ach_mod,  "Mentrigger listener")
Rel(event_bus, quiz_mod, "Mentrigger listener")
Rel(event_bus, disc_mod, "Mentrigger listener")
Rel(event_bus, auth_mod, "Mentrigger listener")

@enduml
```