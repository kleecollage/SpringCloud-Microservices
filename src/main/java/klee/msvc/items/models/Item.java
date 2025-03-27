package klee.msvc.items.models;

public class Item {
    private ProductDto product;
    private int quantity;

    public Item() {
    }

    public Item(ProductDto product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getTotal() {
        return product.getPrice() * quantity;
    }
}
