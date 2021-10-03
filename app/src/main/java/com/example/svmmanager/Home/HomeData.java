package com.example.svmmanager.Home;

public class HomeData {
    private String DRID;    //음료 ID
    private String DRCode;  //음료 코드
    private String DRStock; //음료 개수
    private String DRPrice; //음료 가격

    public String getDRID() {
        return DRID;
    }

    public String getDRCode() {
        return DRCode;
    }

    public String getDRStock() {
        return DRStock;
    }

    public String getDRPrice() { return DRPrice;}

    public void setDRID(String DRID) {
        this.DRID = DRID;
    }

    public void setDRCode(String DRCode) {
        this.DRCode = DRCode;
    }

    public void setDRStock(String DRStock) {
        this.DRStock = DRStock;
    }

    public void setDRPrice(String DRPrice) { this.DRPrice = DRPrice; }
}

