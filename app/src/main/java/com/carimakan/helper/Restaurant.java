package com.carimakan.helper;

/**
 * Created by Ravi on 13/05/15.
 */
public class Restaurant {
    public int id;
    public String title, alamat, urlpic;
    public Double lati, longi;

    public Restaurant() {
    }

    public Restaurant(int id, String title, String address, String urlpic, Double lati, Double longi) {
        this.title = title;
        this.id = id;
        this.alamat= address;
        this.urlpic = urlpic;
        this.lati = lati;
        this.longi = longi;
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
}
