Sistem manajemen informasi produk berbasis CLI (Command Line Interface) yang mengindeks ringkasan produk, menyaring kata terlarang, dan menyajikan rekomendasi produk terurut.

Dibuat sebagai tugas kelompok mata kuliah **Struktur Data & Algoritma** menggunakan **Pure Java** (`java.util.*` only).

---

## Anggota Kelompok

| Nama | NIM |
|------|-----|
| Diaz Spectral Rabbani | L0125142 |
| Humam Sulthon Hakim | L0125102 |
| Alvinas Fajar Saputra | L0125126 |

---

## 🎯 Fitur Utama

| Fitur | Deskripsi | Struktur Data |
|-------|-----------|---------------|
| 🔍 **Pengindeksan Teks** | Pencarian produk berdasarkan keyword atau kategori secara efisien | `HashMap<String, List<Product>>` |
| 🔐 **Filter Kata Terlarang** | Otomatis menyensor kata seperti `palsu`, `kw`, `replika` → `***` | `HashSet<String>` |
| 📊 **Sorting Katalog** | Urutkan produk berdasarkan rating tertinggi atau harga termurah | `ArrayList` + `PriorityQueue` |
| ➕ **Manajemen Produk** | Tambah produk baru yang langsung terindeks | `ArrayList<Product>` |
| ⚙️ **Kelola Kata Terlarang** | Tambah / hapus kata terlarang secara dinamis saat runtime | `HashSet<String>` |

---

## 🗂️ Struktur Project

```
ProductSummaryManager/
├── Product.java          ← Model/Entity — merepresentasikan data produk
├── ProductManager.java   ← Service/Logic — indexing, filtering, sorting
├── Main.java             ← CLI Entry Point — Scanner, menu interaktif
├── CHEAT_SHEET.md        ← Analisis Big-O & bahan code review
└── README.md             ← Dokumentasi ini
```

---

## ⚡ Cara Menjalankan

**Prasyarat:** Java JDK 11 atau lebih baru.

```bash
# 1. Clone repository
git clone https://github.com/username/product-summary-manager.git
cd product-summary-manager

# 2. Kompilasi semua file
javac -d . Product.java ProductManager.java Main.java

# 3. Jalankan program
java productmanager.Main
```

---

## 🖥️ Tampilan CLI

```
  ╔═══════════════════════════════════════════════════╗
  ║       PRODUCT SUMMARY MANAGER SYSTEM v1.0        ║
  ║    Tugas Kelompok — Struktur Data & Algoritma     ║
  ╠═══════════════════════════════════════════════════╣
  ║  Fitur: Indexing | Filter Kata | Sorting Katalog  ║
  ╚═══════════════════════════════════════════════════╝

╔══════════════════════════════════════╗
║     PRODUCT SUMMARY MANAGER v1.0    ║
╠══════════════════════════════════════╣
║  [1] 📦 Lihat Semua Produk           ║
║  [2] 🔍 Cari Produk                  ║
║  [3] 📊 Katalog Terurut (Sorting)    ║
║  [4] 🔐 Demo Filter Kata Terlarang   ║
║  [5] ➕ Tambah Produk Baru           ║
║  [6] ⚙️  Kelola Kata Terlarang       ║
║  [0] 🚪 Keluar                       ║
╚══════════════════════════════════════╝
```

---

## 📊 Arsitektur & Analisis Kompleksitas

### Pemilihan Struktur Data

#### 1. Inverted Index — `HashMap<String, List<Product>>`
Keyword dan kategori di-*hash* saat produk pertama kali ditambahkan (saat startup). Pencarian selanjutnya menjadi **O(1)** rata-rata, jauh lebih efisien dibanding linear scan O(n).

```
"sepatu" → [Sepatu Nike, Sepatu Adidas, Sepatu Sport]
"elektronik" → [Headphone Sony, Earphone JBL]
```

#### 2. HashSet untuk Forbidden Words
Setiap kata dalam deskripsi dicek terhadap set kata terlarang. `HashSet.contains()` berjalan dalam **O(1)**, sedangkan `ArrayList.contains()` membutuhkan O(n).

```
// O(1) per pengecekan — HashSet
forbiddenWords.contains("replika")  ✅

// O(n) per pengecekan — ArrayList (TIDAK digunakan)
forbiddenWordsList.contains("replika")  ❌
```

#### 3. Sorting dengan Timsort & Min-Heap

| Metode | Struktur Data | Algoritma | Kompleksitas |
|--------|--------------|-----------|--------------|
| Sort by Rating | `ArrayList + Comparator` | Timsort | **O(n log n)** |
| Sort by Harga | `PriorityQueue` (min-heap) | Heap Sort | **O(n log n)** |
| Ambil 1 produk termurah | `PriorityQueue.poll()` | Sift-down | **O(log n)** |

### Ringkasan Big-O

| Operasi | Kompleksitas |
|---------|-------------|
| Pencarian keyword (index hit) | **O(1)** |
| Pencarian keyword (fallback scan) | **O(n)** |
| Filter deskripsi (T kata) | **O(T)** |
| Sorting produk | **O(n log n)** |
| Tambah produk + indexing | **O(K)** — K = jumlah keyword |

---

## 🧩 Konsep OOP yang Diterapkan

- **Encapsulation** — Semua field `Product` bersifat `private`, diakses via getter/setter.
- **Single Responsibility Principle** — Setiap class punya satu tanggung jawab utama.
- **Separation of Concerns** — Data (`Product`), Logic (`ProductManager`), Presentation (`Main`) dipisah.

---

## 📝 Lisensi

Proyek ini dibuat untuk keperluan akademik — Mata Kuliah Struktur Data & Algoritma.
