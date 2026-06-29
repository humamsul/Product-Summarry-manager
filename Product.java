// ============================================================
//  FILE: Product.java
//  PERAN: Model / Entity class — merepresentasikan satu produk
// ============================================================

package productmanager;

/**
 * Kelas Product merepresentasikan entitas produk dalam sistem.
 * Menerapkan prinsip Encapsulation (OOP): semua field private,
 * diakses via getter/setter.
 */
public class Product {

    // --- Fields ---
    private int    id;
    private String name;
    private String category;
    private String description;  // deskripsi ASLI (raw), disimpan tanpa sensor
    private double price;
    private double rating;       // skala 0.0 - 5.0
    private String[] keywords;   // kata kunci untuk indexing

    // --- Constructor ---
    public Product(int id, String name, String category,
                   String description, double price, double rating,
                   String[] keywords) {
        this.id          = id;
        this.name        = name;
        this.category    = category;
        this.description = description;
        this.price       = price;
        this.rating      = rating;
        this.keywords    = keywords;
    }

    // --- Getters ---
    public int      getId()          { return id; }
    public String   getName()        { return name; }
    public String   getCategory()    { return category; }
    public String   getDescription() { return description; }
    public double   getPrice()       { return price; }
    public double   getRating()      { return rating; }
    public String[] getKeywords()    { return keywords; }

    // --- Setters (untuk fitur edit jika dikembangkan) ---
    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price)             { this.price = price; }
    public void setRating(double rating)           { this.rating = rating; }

    /**
     * toString ringkas untuk tampilan list.
     * Deskripsi RAW ditampilkan di sini; filtering dilakukan di ProductManager.
     */
    @Override
    public String toString() {
        return String.format("[ID:%d] %-30s | Kategori: %-15s | Harga: Rp%,-12.0f | Rating: %.1f/5.0",
                id, name, category, price, rating);
    }
}
