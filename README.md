# Yomu – Local Development Setup

Yomu adalah aplikasi full-stack dengan:
- **Backend:** Spring Boot (Gradle)
- **Database:** PostgreSQL (Docker)
- **Frontend:** Next.js

---

## Links Penting

| Resource | Link                                                    |
|---|---------------------------------------------------------|
| 📋 Pembagian Tugas | https://docs.google.com/spreadsheets/d/1dRFlgDaZWAtUU1hQ_497yyQgwkQrAIhemYF9O7nA_ng/edit?usp=sharing |
| 🖥️ Repository Frontend | https://github.com/advprog-2026-B12-project/yomu-frontend|
| ⚙️ Repository Backend | https://github.com/advprog-2026-B12-project/yomu-backend|

---

## Branching Strategy

| Branch | Kegunaan |
|---|---|
| `main` | Production-ready code |
| `staging` | Testing & integrasi sebelum ke main |
| `feat/<nama-fitur>` | Development fitur baru |

Alur: `feat/*` → `staging` → `main`

---

## 1. Prerequisites

Pastikan sudah menginstall:
- Git
- Docker Desktop
- Java 21
- Node.js 18+
- npm

> **Pastikan Docker sedang running sebelum memulai.**

---

## 2. Setup Backend

Buka terminal pertama, lalu clone repository backend:

```bash
git clone https://github.com/advprog-2026-B12-project/yomu-backend
cd yomu-backend
```

---

## 3. Jalankan Database dengan Docker

Di dalam folder yomu-backend, jalankan:

```bash
docker compose up -d
```

Untuk memverifikasi container berjalan:

```bash
docker ps
```

---

## 4. Jalankan Backend (Spring Boot)

jalankan:

```bash
./gradlew bootRun
```

Jika berhasil, backend berjalan di: **http://localhost:8080**

---

## 5. Setup Frontend

Buka **terminal kedua** (window terpisah), lalu clone repository frontend:

```bash
git clone https://github.com/advprog-2026-B12-project/yomu-frontend
cd yomu-frontend
```

---

## 6. Install Dependencies Frontend

```bash
npm install
```

---

## 7. Jalankan Frontend

```bash
npm run dev
```

Frontend akan berjalan di: **http://localhost:3000**

---

## 8. Urutan Menjalankan Project

Setiap kali ingin development, jalankan dalam urutan berikut:

1.  Jalankan Docker database
2. Jalankan backend
3. Jalankan frontend

---

## 9. Stop Project

Menghentikan database:

```bash
docker compose down
```

Menghentikan database **dan menghapus volume** (data akan hilang):

```bash
docker compose down -v
```