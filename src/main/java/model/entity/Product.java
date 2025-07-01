package model.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Product {
    private Integer productId;
    private Integer categoryId;
    private String productName;
    private String imageUrl;
    private String briefInfo;
    private String description;
    private String color;
    private String memory;
    private int quantity;
    private Long priceSale;
    private Long priceOrigin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private List<ProductImage> productImages;

    // --- GETTERS ---
    public Integer getProductId() { return productId; }
    public Integer getCategoryId() { return categoryId; }
    public String getProductName() { return productName; } // JSP cần cái này
    public String getImageUrl() { return imageUrl; } // JSP cần cái này
    public String getBriefInfo() { return briefInfo; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
    public String getMemory() { return memory; }
    public int getQuantity() { return quantity; }
    public Long getPriceSale() { return priceSale; }
    public Long getPriceOrigin() { return priceOrigin; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isDeleted() { return isDeleted; }
    public List<ProductImage> getProductImages() { return productImages; }

    // --- SETTERS ---
    public void setProductId(Integer productId) { this.productId = productId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setBriefInfo(String briefInfo) { this.briefInfo = briefInfo; }
    public void setDescription(String description) { this.description = description; }
    public void setColor(String color) { this.color = color; }
    public void setMemory(String memory) { this.memory = memory; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPriceSale(Long priceSale) { this.priceSale = priceSale; }
    public void setPriceOrigin(Long priceOrigin) { this.priceOrigin = priceOrigin; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public void setProductImages(List<ProductImage> productImages) { this.productImages = productImages; }
}