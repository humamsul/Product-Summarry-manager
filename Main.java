// ============================================================
//  FILE: Main.java
//  PERAN: Entry point + CLI interaktif menggunakan Scanner
// ============================================================

package productmanager;

import java.util.*;

/**
 * Main adalah kelas CLI (Command Line Interface) interaktif.
 * Mengelola input user via Scanner dan memanggil ProductManager.
 */
public class Main {

    // Scanner sebagai field class agar bisa dipakai di semua method
    private static final Scanner scanner = new Scanner(System.in);
    private static ProductManager manager;

    // ── Konstanta warna ANSI untuk CLI yang lebih menarik ─────
    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED    = "\u001B[31m";
    private static final String BLUE   = "\u001B[34m";

    // ===========================================================
    //  MAIN — Entry Point
    // ===========================================================
    public static void main(String[] args) {
        printBanner();
        manager = new ProductManager(); // inisialisasi + load dummy data

        System.out.println(GREEN + "\nSistem berhasil diinisialisasi dengan "
                + manager.getAllProducts().size() + " produk dummy." + RESET);

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Pilih menu: ");

            switch (choice) {
                case 1  -> menuCatalog();
                case 2  -> menuSearch();
                case 3  -> menuSorting();
                case 4  -> menuFilterDemo();
                case 5  -> menuAddProduct();
                case 6  -> menuForbiddenWords();
                case 7  -> menuCheckout();
                case 0  -> running = false;
                default -> System.out.println(RED + "Pilihan tidak valid!" + RESET);
            }
        }

        System.out.println(CYAN + "\nTerima kasih telah menggunakan Product Summary Manager!" + RESET);
        scanner.close();
    }

    // ===========================================================
    //  MENU UTAMA
    // ===========================================================
    private static void printMainMenu() {
        System.out.println("\n" + BOLD + CYAN + "╔══════════════════════════════════════╗");
        System.out.println(               "║     PRODUCT SUMMARY MANAGER v1.0    ║");
        System.out.println(               "╠══════════════════════════════════════╣" + RESET);
        System.out.println(               "║  [1] Lihat Semua Produk              ║");
        System.out.println(               "║  [2] Cari Produk                     ║");
        System.out.println(               "║  [3] Katalog Terurut (Sorting)       ║");
        System.out.println(               "║  [4] Demo Filter Kata Terlarang      ║");
        System.out.println(               "║  [5] Tambah Produk Baru              ║");
        System.out.println(               "║  [6] Kelola Kata Terlarang           ║");
        System.out.println(               "║  [7] Checkout / Beli Produk          ║");
        System.out.println(               "║  [0] Keluar                          ║");
        System.out.println(BOLD + CYAN +  "╚══════════════════════════════════════╝" + RESET);
    }

    // ===========================================================
    //  MENU 1 — KATALOG SEMUA PRODUK
    // ===========================================================
    private static void menuCatalog() {
        List<Product> products = manager.getAllProducts();
        printHeader("KATALOG SEMUA PRODUK (" + products.size() + " item)");

        if (products.isEmpty()) {
            System.out.println("Belum ada produk.");
            return;
        }

        for (int i = 0; i < products.size(); i++) {
            System.out.printf("%3d. %s%n", i + 1, products.get(i));
        }

        // Sub-menu: lihat detail produk
        System.out.print("\nLihat detail produk? Masukkan ID (0 = kembali): ");
        int id = readInt("");
        if (id > 0) {
            Product p = manager.findById(id);
            if (p != null) {
                manager.printProductDetail(p);
            } else {
                System.out.println(RED + "Produk dengan ID " + id + " tidak ditemukan." + RESET);
            }
        }
    }

    // ===========================================================
    //  MENU 2 — PENCARIAN
    // ===========================================================
    private static void menuSearch() {
        printHeader("PENCARIAN PRODUK");
        System.out.println("  [1] Cari berdasarkan Keyword");
        System.out.println("  [2] Cari berdasarkan Kategori");
        System.out.println("  [0] Kembali");

        int choice = readInt("Pilih: ");

        switch (choice) {
            case 1 -> {
                System.out.print("Masukkan keyword pencarian: ");
                String kw = scanner.nextLine().trim();
                long start = System.nanoTime();
                List<Product> results = manager.searchByKeyword(kw);
                long elapsed = System.nanoTime() - start;

                printHeader("HASIL PENCARIAN KEYWORD: \"" + kw + "\"");
                if (results.isEmpty()) {
                    System.out.println("Tidak ada produk yang cocok.");
                } else {
                    results.forEach(p -> System.out.println("  → " + p));
                    System.out.printf(YELLOW + "%n⏱️  Waktu pencarian: %.3f ms | Ditemukan: %d produk%n" + RESET,
                            elapsed / 1_000_000.0, results.size());
                }
            }
            case 2 -> {
                System.out.println("Kategori tersedia: " + manager.getAllCategories());
                System.out.print("Masukkan nama kategori: ");
                String cat = scanner.nextLine().trim();
                List<Product> results = manager.searchByCategory(cat);

                printHeader("HASIL PENCARIAN KATEGORI: \"" + cat + "\"");
                if (results.isEmpty()) {
                    System.out.println("Tidak ada produk dalam kategori ini.");
                } else {
                    results.forEach(p -> System.out.println("  → " + p));
                }
            }
            case 0 -> { /* kembali */ }
            default -> System.out.println(RED + "Pilihan tidak valid." + RESET);
        }
    }

    // ===========================================================
    //  MENU 3 — SORTING
    // ===========================================================
    private static void menuSorting() {
        printHeader("KATALOG TERURUT");
        System.out.println("  [1] ⭐ Urutkan berdasarkan Rating Tertinggi");
        System.out.println("  [2] 💰 Urutkan berdasarkan Harga Termurah");
        System.out.println("  [0] Kembali");

        int choice = readInt("Pilih: ");

        switch (choice) {
            case 1 -> {
                long start = System.nanoTime();
                List<Product> sorted = manager.getSortedByRatingDesc();
                long elapsed = System.nanoTime() - start;

                printHeader("⭐ RANKING PRODUK (Rating Tertinggi → Terendah)");
                printRankedList(sorted);
                System.out.printf(YELLOW + "%n⏱️  Sorting selesai dalam: %.3f ms (Timsort O(n log n))%n" + RESET,
                        elapsed / 1_000_000.0);
            }
            case 2 -> {
                long start = System.nanoTime();
                List<Product> sorted = manager.getSortedByPriceAsc();
                long elapsed = System.nanoTime() - start;

                printHeader("💰 PRODUK TERMURAH (Harga Terendah → Tertinggi)");
                printRankedList(sorted);
                System.out.printf(YELLOW + "%n⏱️  Sorting selesai dalam: %.3f ms (PriorityQueue Min-Heap O(n log n))%n" + RESET,
                        elapsed / 1_000_000.0);
            }
            case 0 -> { /* kembali */ }
            default -> System.out.println(RED + "Pilihan tidak valid." + RESET);
        }
    }

    // ===========================================================
    //  MENU 4 — DEMO FILTER KATA TERLARANG
    // ===========================================================
    private static void menuFilterDemo() {
        printHeader("DEMO FILTER KATA TERLARANG");
        System.out.println("Produk berikut mengandung kata terlarang dalam deskripsinya:");
        System.out.println("(Deskripsi RAW vs Deskripsi TERSENSOR akan ditampilkan)\n");

        // Tampilkan produk yang mengandung kata terlarang
        // Cara cek: deskripsi tersensor ≠ deskripsi asli
        int count = 0;
        for (Product p : manager.getAllProducts()) {
            String raw      = p.getDescription();
            String filtered = manager.filterDescription(raw);
            if (!raw.equals(filtered)) {
                count++;
                System.out.println(BOLD + "  Produk : " + p.getName() + RESET);
                System.out.println(RED    + "  RAW    : " + raw      + RESET);
                System.out.println(GREEN  + "  SENSOR : " + filtered + RESET);
                System.out.println("  " + "─".repeat(60));
            }
        }

        if (count == 0) System.out.println("Tidak ada produk dengan kata terlarang.");

        System.out.println(YELLOW + "\nTIP: Coba ketikkan deskripsi sendiri untuk difilter!" + RESET);
        System.out.print("Masukkan teks (Enter langsung = lewati): ");
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            System.out.println("\n  RAW    : " + input);
            System.out.println("  SENSOR : " + manager.filterDescription(input));
        }
    }

    // ===========================================================
    //  MENU 5 — TAMBAH PRODUK BARU
    // ===========================================================
    private static void menuAddProduct() {
        printHeader("TAMBAH PRODUK BARU");

        System.out.print("Nama produk      : ");
        String name = scanner.nextLine().trim();

        System.out.println("Kategori tersedia: " + manager.getAllCategories());
        System.out.print("Kategori         : ");
        String category = scanner.nextLine().trim();

        System.out.print("Deskripsi        : ");
        String desc = scanner.nextLine().trim();

        double price  = readDouble("Harga (Rp)       : ");
        double rating = readDouble("Rating (0.0-5.0) : ");

        System.out.print("Keywords (pisah koma): ");
        String[] keywords = scanner.nextLine().trim().split(",");
        for (int i = 0; i < keywords.length; i++) {
            keywords[i] = keywords[i].trim();
        }

        Product newProduct = manager.addProduct(name, category, desc, price, rating, keywords);
        System.out.println(GREEN + "\nProduk berhasil ditambahkan:" + RESET);
        System.out.println("   " + newProduct);
    }

    // ===========================================================
    //  MENU 6 — KELOLA KATA TERLARANG
    // ===========================================================
    private static void menuForbiddenWords() {
        printHeader("KELOLA KATA TERLARANG");

        System.out.println("Daftar kata terlarang saat ini:");
        List<String> sorted = new ArrayList<>(manager.getForbiddenWords());
        Collections.sort(sorted);
        System.out.println(RED + "  " + sorted + RESET);

        System.out.println("\n  [1] Tambah kata terlarang baru");
        System.out.println("  [2] Hapus kata terlarang");
        System.out.println("  [0] Kembali");

        int choice = readInt("Pilih: ");
        switch (choice) {
            case 1 -> {
                System.out.print("Kata baru: ");
                manager.addForbiddenWord(scanner.nextLine().trim());
            }
            case 2 -> {
                System.out.print("Kata yang dihapus: ");
                manager.removeForbiddenWord(scanner.nextLine().trim());
            }
            case 0 -> { /* kembali */ }
        }
    }

    // ===========================================================
    //  MENU 7 — CHECKOUT / BELI PRODUK
    // ===========================================================
    /**
     * Alur checkout:
     *  1. Tampilkan katalog agar user pilih produk via ID
     *  2. User masukkan nominal uang yang dimiliki
     *  3. ProductManager.checkout() menentukan sukses/gagal
     *  4. Jika sukses → produk otomatis hilang dari katalog (lihat removeProduct)
     *  5. Tampilkan struk pembayaran / pesan gagal
     */
    private static void menuCheckout() {
        printHeader("CHECKOUT / BELI PRODUK");

        List<Product> products = manager.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Katalog kosong, tidak ada produk untuk di-checkout.");
            return;
        }

        System.out.println("Daftar produk yang tersedia:");
        for (Product p : products) {
            System.out.println("  → " + p);
        }

        int id = readInt("\nMasukkan ID produk yang ingin dibeli (0 = batal): ");
        if (id == 0) return;

        Product target = manager.findById(id);
        if (target == null) {
            System.out.println(RED + "Produk dengan ID " + id + " tidak ditemukan." + RESET);
            return;
        }

        System.out.printf("Anda memilih: %s (Harga: Rp%,.0f)%n", target.getName(), target.getPrice());
        double money = readDouble("Masukkan jumlah uang Anda (Rp): ");

        // Logika checkout sepenuhnya ada di ProductManager (Separation of Concerns)
        ProductManager.CheckoutResult result = manager.checkout(id, money);

        System.out.println();
        if (result.success) {
            System.out.println(GREEN + "============================================" + RESET);
            System.out.println(GREEN + "  " + result.message + RESET);
            System.out.println(GREEN + "============================================" + RESET);
            System.out.println("Sisa produk di katalog: " + manager.getAllProducts().size());
        } else {
            System.out.println(RED + "============================================" + RESET);
            System.out.println(RED + "  " + result.message + RESET);
            System.out.println(RED + "============================================" + RESET);
        }
    }

    // ===========================================================
    //  HELPER METHODS
    // ===========================================================
    private static void printBanner() {
        System.out.println(BOLD + BLUE);
        System.out.println("  ╔═══════════════════════════════════════════════════╗");
        System.out.println("  ║       PRODUCT SUMMARY MANAGER SYSTEM v1.0        ║");
        System.out.println("  ║    Tugas Kelompok - Struktur Data & Algoritma     ║");
        System.out.println("  ╠═══════════════════════════════════════════════════╣");
        System.out.println("  ║  Fitur: Indexing | Filter Kata | Sorting | Checkout║");
        System.out.println("  ╚═══════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private static void printHeader(String title) {
        System.out.println("\n" + BOLD + CYAN + "══ " + title + " ══" + RESET);
    }

    private static void printRankedList(List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            String medal = switch (i) {
                case 0 -> "🥇";
                case 1 -> "🥈";
                case 2 -> "🥉";
                default -> String.format("%2d.", i + 1);
            };
            System.out.printf("  %s %s%n", medal, p);
        }
    }

    /** Membaca integer dari input user dengan validasi */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Masukkan angka yang valid!" + RESET);
            }
        }
    }

    /** Membaca double dari input user dengan validasi */
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Masukkan angka desimal yang valid!" + RESET);
            }
        }
    }
}