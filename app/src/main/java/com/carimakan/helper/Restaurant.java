package com.carimakan.helper;

/**
 * Created by Ersa Rizki Dimitri on 17/05/2015.
 */
public class Restaurant {
    public int id;
    public String title;
    public String alamat;
    public String urlpic;
    public String detail;
    public String kategori;
    public String menu;
    public Double lati, longi;

    public Restaurant() {
    }

    public Restaurant(int id, String title, String address, String detail, String urlpic, Double lati, Double longi, String kategori, String menu) {
        this.title = title;
        this.id = id;
        this.alamat= address;
        this.detail= detail;
        this.urlpic = urlpic;
        this.lati = lati;
        this.longi = longi;
        this.kategori = kategori;
        this.menu = menu;
    }

    public String getUrlpic() {
        return urlpic;
    }

    public void setUrlpic(String urlpic) {
        this.urlpic = urlpic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Double getLongi() {
        return longi;
    }

    public void setLongi(Double longi) {
        this.longi = longi;
    }

    public Double getLati() {
        return lati;
    }

    public void setLati(Double lati) {
        this.lati = lati;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getText() {
        return title;
    }
}
