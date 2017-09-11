package de.hsheilbronn.mi.ui;

public class Svm2DPoint {

    private double x;
    private double y;
    private byte category;

    public Svm2DPoint(double x, double y, byte category) {
        this.x = x;
        this.y = y;
        this.category = category;
    }

    public Svm2DPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public byte getCategory() {
        return category;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return category + "," + x + "," + y;
    }
}
