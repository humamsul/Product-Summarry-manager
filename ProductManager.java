// ============================================================
//  FILE: ProductManager.java
//  PERAN: Service / Manager class — semua logika bisnis ada di sini
//         (Indexing, Filtering, Sorting, CRUD)
// ============================================================

package productmanager;

import java.util.*;

/**
 * ProductManager adalah inti dari sistem ini.
 *
 * STRUKTUR DATA YANG DIGUNAKAN:
 *
 * 1. ArrayList<Product> catalog
 *    → Menyimpan semua produk. Akses index O(1), iterasi O(n).
 *
 * 2. HashMap<String, List<Product>> keywordIndex
 *    → Inverted Index: keyword → [produk yang mengandung keyword tsb]
 *    → Lookup O(1) rata-rata (hash-based).
 *
 * 3. HashMap<String, List<Product>> categoryIndex
 *    → Index per kategori untuk filter cepat O(1).
 *
 * 4. HashSet<String> forbiddenWords
 *    → Set kata terlarang. contains() O(1) karena hash-based.
 *    → Jauh lebih cepat dari List.contains() yang O(n).
 */
public class ProductManager {

    // ── Katalog utama ──────────────────────────────────────────
    private final List<Product> catalog = new ArrayList<>();

    // ── Inverted Index: keyword → list produk ──────────────────
    // Kompleksitas build: O(P × K) di mana P=jumlah produk, K=rata-rata keyword per produk
    private final Map<String, List<Product>> keywordIndex  = new HashMap<>();
    private final Map<String, List<Product>> categoryIndex = new HashMap<>();

    // ── Forbidden words set ────────────────────────────────────
    // HashSet dipilih agar contains() → O(1)
    private final Set<String> forbiddenWords = new HashSet<>();

    // ── ID counter ─────────────────────────────────────────────
    private int nextId = 1;

    // ===========================================================
    //  KONSTRUKTOR — inisialisasi data dummy & forbidden words
    // ===========================================================
    public ProductManager() {
        initForbiddenWords();
        loadDummyData();
    }

    // ===========================================================
    //  INISIALISASI FORBIDDEN WORDS
    // ===========================================================
    /**
     * Mengisi HashSet dengan kata-kata terlarang.
     * Semua disimpan lowercase agar perbandingan case-insensitive.
     * Kompleksitas: O(F) di mana F = jumlah forbidden words.
     */
    private void initForbiddenWords() {
        String[] banned = {
            "palsu", "kw", "replika", "bajakan", "tiruan",
            "abal-abal", "murah meriah", "grade abal",
            "supercopy", "master copy", "imitasi"
        };
        // HashSet.add() → O(1) amortized
        for (String word : banned) {
            forbiddenWords.add(word.toLowerCase());
        }
    }

    // ===========================================================
    //  DATA DUMMY (langsung bisa demo ke dosen)
    // ===========================================================
    private void loadDummyData() {
        addProduct("Sepatu Lari Nike Air Max 270",    "Sepatu",      "Sepatu lari premium dengan teknologi Air cushioning, nyaman untuk lari maraton, material mesh breathable asli.",                                 850_000,  4.8, new String[]{"sepatu","lari","nike","olahraga","premium"});
        addProduct("Sepatu Casual Adidas Stan Smith", "Sepatu",      "Sepatu casual legendaris Adidas, sol karet vulkanisasi, upper leather asli, cocok untuk gaya sehari-hari.",                                     650_000,  4.6, new String[]{"sepatu","casual","adidas","kulit","klasik"});
        addProduct("Tas Ransel Laptop 15inch",        "Tas",         "Tas ransel anti air dengan kompartemen laptop, bahan nylon kualitas premium, desain ergonomis untuk perjalanan jauh.",                           320_000,  4.5, new String[]{"tas","ransel","laptop","anti air","ergonomis"});
        addProduct("Tas Selempang Kulit Pria",        "Tas",         "Tas selempang bahan kulit sapi asli, jahitan rapi, tersedia slot kartu dan dompet, cocok untuk kerja maupun santai.",                            280_000,  4.3, new String[]{"tas","kulit","pria","selempang","kerja"});
        addProduct("Kemeja Flanel Pria Premium",      "Pakaian",     "Kemeja flanel pria bahan cotton 100% asli, motif kotak-kotak, tersedia ukuran S-XXL, nyaman di segala cuaca.",                                  195_000,  4.7, new String[]{"kemeja","flanel","pria","cotton","premium"});
        addProduct("Kaos Polos Oversize Wanita",      "Pakaian",     "Kaos oversize wanita, bahan cotton combed 30s, tersedia 20 pilihan warna, jahitan double-needle tahan lama.",                                   89_000,   4.4, new String[]{"kaos","oversize","wanita","cotton","polos"});
        addProduct("Jam Tangan Casio G-Shock GA-100", "Jam Tangan",  "Jam tangan pria anti air 200m, shock resistant, fitur stopwatch dan world time, garansi resmi 2 tahun.",                                        1_250_000, 4.9, new String[]{"jam","tangan","casio","gshock","anti air","sporty"});
        addProduct("Smartwatch Xiaomi Mi Band 7",     "Jam Tangan",  "Smartwatch dengan sensor detak jantung SpO2, baterai tahan 14 hari, layar AMOLED 1.62 inch, tahan air 5ATM.",                                  450_000,  4.5, new String[]{"jam","tangan","smartwatch","xiaomi","fitness"});
        addProduct("Headphone Sony WH-1000XM5",       "Elektronik",  "Headphone noise cancelling terbaik di kelasnya, baterai 30 jam, konektivitas Bluetooth 5.2, suara Hi-Res Audio.",                              4_500_000, 4.9, new String[]{"headphone","sony","noise cancelling","bluetooth","audio"});
        addProduct("Earphone TWS JBL Tune 230",       "Elektronik",  "True wireless earphone, bass yang dalam dan kencang, IPX4 tahan keringat, case charging baterai 40 jam total.",                                 750_000,  4.6, new String[]{"earphone","tws","jbl","wireless","bass"});
        addProduct("Kacamata Hitam Polarized",        "Aksesoris",   "Kacamata hitam lensa polarized UV400, frame TR90 ringan, cocok untuk berkendara dan outdoor, termasuk hard case.",                              125_000,  4.2, new String[]{"kacamata","hitam","polarized","outdoor","aksesoris"});
        addProduct("Dompet Kulit Pria RFID Blocking", "Aksesoris",   "Dompet slim pria teknologi RFID blocking untuk keamanan kartu, bahan kulit genuine, muat 8 kartu dan uang.",                                   175_000,  4.5, new String[]{"dompet","kulit","pria","rfid","slim"});

        // ── Produk dengan KATA TERLARANG di deskripsinya (untuk demo filter) ──
        addProduct("Sepatu Sport Grade Ori",          "Sepatu",      "Sepatu sport model terkini, bahan mirip asli, ada yang bilang ini replika tapi kualitas mirip premium, harga terjangkau.",                       99_000,   3.1, new String[]{"sepatu","sport","murah"});
        addProduct("Jam Tangan Fashion Pria",         "Jam Tangan",  "Jam tangan fashion pria, desain mewah dengan harga murah meriah, banyak yang bilang kw tapi tampilan mirip ori banget.",                        75_000,   2.8, new String[]{"jam","tangan","fashion","murah"});
        addProduct("Tas Branded Lookalike",           "Tas",         "Tas model branded, bahan imitasi kulit sintetis, mirip asli 95%, supercopy terbaik di pasaran, banyak peminat.",                                150_000,  2.5, new String[]{"tas","branded","murah"});
    }

    // ===========================================================
    //  CRUD — TAMBAH PRODUK
    // ===========================================================
    /**
     * Menambahkan produk baru ke katalog dan memperbarui semua index.
     * Kompleksitas: O(K) di mana K = jumlah keyword produk tersebut.
     */
    public Product addProduct(String name, String category, String description,
                              double price, double rating, String[] keywords) {
        Product p = new Product(nextId++, name, category, description, price, rating, keywords);
        catalog.add(p);
        indexProduct(p);
        return p;
    }

    /**
     * Membangun inverted index untuk satu produk.
     * Setiap keyword → ditambahkan ke list produk di HashMap.
     * HashMap.computeIfAbsent() + List.add() → O(1) amortized.
     */
    private void indexProduct(Product p) {
        // Index berdasarkan keyword
        for (String kw : p.getKeywords()) {
            String key = kw.toLowerCase().trim();
            keywordIndex.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }
        // Index berdasarkan kategori
        String cat = p.getCategory().toLowerCase().trim();
        categoryIndex.computeIfAbsent(cat, k -> new ArrayList<>()).add(p);
    }

    // ===========================================================
    //  FITUR 1 — PENCARIAN / INDEXING
    // ===========================================================
    /**
     * Mencari produk berdasarkan keyword.
     *
     * Algoritma:
     *  1. Normalisasi query ke lowercase → O(|query|)
     *  2. Lookup di HashMap → O(1) rata-rata
     *  3. Jika tidak ada hasil, fallback ke linear scan deskripsi → O(n)
     *
     * Total best-case: O(1), worst-case: O(n)
     */
    public List<Product> searchByKeyword(String query) {
        String key = query.toLowerCase().trim();

        // Cek index dulu — O(1)
        if (keywordIndex.containsKey(key)) {
            return new ArrayList<>(keywordIndex.get(key)); // return copy
        }

        // Fallback: cari substring di nama atau deskripsi — O(n × |string|)
        List<Product> results = new ArrayList<>();
        for (Product p : catalog) {
            if (p.getName().toLowerCase().contains(key)
                    || p.getDescription().toLowerCase().contains(key)
                    || p.getCategory().toLowerCase().contains(key)) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Mencari produk berdasarkan kategori.
     * Kompleksitas: O(1) rata-rata (HashMap lookup).
     */
    public List<Product> searchByCategory(String category) {
        String key = category.toLowerCase().trim();
        return categoryIndex.getOrDefault(key, new ArrayList<>());
    }

    /**
     * Menampilkan semua kategori yang tersedia.
     * Kompleksitas: O(C) di mana C = jumlah kategori unik.
     */
    public Set<String> getAllCategories() {
        return categoryIndex.keySet();
    }

    // ===========================================================
    //  FITUR 2 — FILTER / SENSOR KATA TERLARANG
    // ===========================================================
    /**
     * Menyensor kata terlarang dalam teks deskripsi.
     *
     * Algoritma:
     *  1. Split teks menjadi token (kata) → O(|text|)
     *  2. Untuk setiap token, cek di HashSet → O(1) per token
     *  3. Ganti dengan "***" jika ditemukan
     *  4. Gabungkan kembali → O(|text|)
     *
     * Total Kompleksitas: O(|text|)
     * Bandingkan: jika pakai List<String>, cek = O(F) per token → O(|text| × F)
     * HashSet membuat F menjadi konstan O(1) → LEBIH EFISIEN!
     */
    public String filterDescription(String rawDescription) {
        // Split berdasarkan spasi sambil mempertahankan tanda baca
        String[] tokens = rawDescription.split("(?<=\\s)|(?=\\s)");
        StringBuilder sb = new StringBuilder();

        for (String token : tokens) {
            // Hapus tanda baca untuk pengecekan, tapi tetap tampilkan token asli
            String cleanToken = token.trim().replaceAll("[^a-zA-Z0-9\\-]", "").toLowerCase();

            // HashSet.contains() → O(1)
            if (!cleanToken.isEmpty() && forbiddenWords.contains(cleanToken)) {
                sb.append("***");
            } else {
                sb.append(token);
            }
        }
        return sb.toString();
    }

    /**
     * Menampilkan detail produk dengan deskripsi yang sudah disensor.
     */
    public void printProductDetail(Product p) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf( "║  [ID: %d] %s%n", p.getId(), p.getName());
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf( "║  Kategori  : %s%n", p.getCategory());
        System.out.printf( "║  Harga     : Rp%,.0f%n", p.getPrice());
        System.out.printf( "║  Rating    : %.1f / 5.0  %s%n", p.getRating(), getStars(p.getRating()));
        System.out.println("║  Keywords  : " + String.join(", ", p.getKeywords()));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  DESKRIPSI (tersensor):");
        System.out.println("║  " + filterDescription(p.getDescription())); // ← FILTER DI SINI
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    // ===========================================================
    //  FITUR 3 — SORTING / KATALOG TERURUT
    // ===========================================================
    /**
     * Mengurutkan semua produk berdasarkan RATING TERTINGGI.
     *
     * Menggunakan: Collections.sort() dengan Comparator.
     * Algoritma: Timsort (hybrid merge + insertion sort).
     * Kompleksitas: O(n log n) — optimal untuk comparison-based sort.
     * Stabil: Ya (urutan relatif produk dengan rating sama dipertahankan).
     */
    public List<Product> getSortedByRatingDesc() {
        List<Product> sorted = new ArrayList<>(catalog); // copy agar catalog asli tidak berubah

        // Comparator: rating lebih tinggi → urutan lebih awal (descending)
        Collections.sort(sorted, (a, b) -> Double.compare(b.getRating(), a.getRating()));

        return sorted;
    }

    /**
     * Mengurutkan semua produk berdasarkan HARGA TERMURAH (ascending).
     *
     * Menggunakan: PriorityQueue (Min-Heap).
     * Kompleksitas:
     *   - Build heap: O(n log n)  [n × offer() yang masing-masing O(log n)]
     *   - Poll semua: O(n log n)  [n × poll() yang masing-masing O(log n)]
     * Total: O(n log n)
     *
     * PriorityQueue cocok untuk kasus "ambil Top-K termurah" → O(K log n).
     */
    public List<Product> getSortedByPriceAsc() {
        // Min-heap: produk dengan harga lebih rendah → priority lebih tinggi
        PriorityQueue<Product> minHeap = new PriorityQueue<>(
                (a, b) -> Double.compare(a.getPrice(), b.getPrice())
        );

        // Masukkan semua produk ke heap → O(n log n)
        minHeap.addAll(catalog);

        // Keluarkan dari heap → selalu keluar yang termurah duluan
        List<Product> sorted = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            sorted.add(minHeap.poll()); // poll() → O(log n)
        }
        return sorted;
    }

    // ===========================================================
    //  FITUR MANAJEMEN FORBIDDEN WORDS (bonus)
    // ===========================================================
    public void addForbiddenWord(String word) {
        forbiddenWords.add(word.toLowerCase().trim());
        System.out.println("✅ Kata '" + word + "' ditambahkan ke daftar terlarang.");
    }

    public void removeForbiddenWord(String word) {
        if (forbiddenWords.remove(word.toLowerCase().trim())) {
            System.out.println("✅ Kata '" + word + "' dihapus dari daftar terlarang.");
        } else {
            System.out.println("⚠️  Kata '" + word + "' tidak ada di daftar terlarang.");
        }
    }

    public Set<String> getForbiddenWords() {
        return Collections.unmodifiableSet(forbiddenWords); // read-only view
    }

    // ===========================================================
    //  HELPERS
    // ===========================================================
    public List<Product> getAllProducts() {
        return Collections.unmodifiableList(catalog);
    }

    public Product findById(int id) {
        for (Product p : catalog) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    // ===========================================================
    //  FITUR CHECKOUT
    // ===========================================================
    /**
     * Hasil dari proses checkout — dibungkus dalam objek agar
     * Main.java tidak perlu logika bisnis, cukup tampilkan hasil.
     */
    public static class CheckoutResult {
        public final boolean success;
        public final String message;
        public final double change; // kembalian (jika sukses)

        public CheckoutResult(boolean success, String message, double change) {
            this.success = success;
            this.message = message;
            this.change  = change;
        }
    }

    /**
     * Memproses checkout untuk satu produk.
     *
     * Algoritma:
     *  1. Cari produk berdasarkan ID → O(n) (linear scan di catalog)
     *  2. Bandingkan uang yang dimasukkan vs harga produk → O(1)
     *  3a. Jika cukup  → hapus produk dari catalog & index, hitung kembalian
     *  3b. Jika kurang → kembalikan pesan gagal, produk TIDAK dihapus
     *
     * Kompleksitas total: O(n + K) di mana K = jumlah keyword produk
     * (n untuk cari produk, K untuk bersihkan index saat penghapusan)
     */
    public CheckoutResult checkout(int productId, double moneyGiven) {
        Product p = findById(productId);

        if (p == null) {
            return new CheckoutResult(false, "Produk dengan ID " + productId + " tidak ditemukan.", 0);
        }

        if (moneyGiven < p.getPrice()) {
            double shortage = p.getPrice() - moneyGiven;
            return new CheckoutResult(false,
                    String.format("Maaf, uang yang Anda miliki tidak cukup. Kekurangan: Rp%,.0f", shortage),
                    0);
        }

        // Uang cukup → proses checkout
        double change = moneyGiven - p.getPrice();
        removeProduct(p); // hapus dari catalog + index

        String message = String.format(
                "Payment berhasil! Anda membeli \"%s\" seharga Rp%,.0f. Kembalian: Rp%,.0f",
                p.getName(), p.getPrice(), change);

        return new CheckoutResult(true, message, change);
    }

    /**
     * Menghapus produk dari catalog DAN dari semua index (keyword & category).
     * Konsistensi data wajib dijaga: jika produk dihapus dari catalog tapi
     * lupa dihapus dari index, search/sorting akan menampilkan data basi (stale).
     *
     * Kompleksitas: O(K) di mana K = jumlah keyword produk tersebut
     * (catalog.remove() sendiri O(n), tapi ini wajar untuk ArrayList).
     */
    private void removeProduct(Product p) {
        catalog.remove(p); // O(n) — pencarian referensi objek di ArrayList

        // Bersihkan dari keywordIndex
        for (String kw : p.getKeywords()) {
            String key = kw.toLowerCase().trim();
            List<Product> list = keywordIndex.get(key);
            if (list != null) {
                list.remove(p);
                if (list.isEmpty()) keywordIndex.remove(key); // bersihkan bucket kosong
            }
        }

        // Bersihkan dari categoryIndex
        String cat = p.getCategory().toLowerCase().trim();
        List<Product> catList = categoryIndex.get(cat);
        if (catList != null) {
            catList.remove(p);
            if (catList.isEmpty()) categoryIndex.remove(cat);
        }
    }

    /** Menghasilkan bintang visual untuk rating */
    private String getStars(double rating) {
        int full  = (int) rating;
        int empty = 5 - full;
        return "★".repeat(full) + "☆".repeat(empty);
    }
}