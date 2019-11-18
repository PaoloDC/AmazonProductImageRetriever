package it.paolo.amazonapp.entities;

public class Product {

  private String name;
  private String code;
  private String sellerShop;
  private String pezzi;
  private String url;
  private String ppFee;
  private String note;

  public Product(){

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPpFee() {
    return ppFee;
  }

  public void setPpFee(String ppFee) {
    this.ppFee = ppFee;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getSellerShop() {
    return sellerShop;
  }

  public void setSellerShop(String sellerShop) {
    this.sellerShop = sellerShop;
  }

  public String getPezzi() {
    return pezzi;
  }

  public void setPezzi(String pezzi) {
    this.pezzi = pezzi;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}
