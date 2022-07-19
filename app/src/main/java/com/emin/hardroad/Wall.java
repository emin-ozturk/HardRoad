package com.emin.hardroad;

public class Wall {
    private float x, y, width, height;
    private int direction;
    private boolean isScroll;
    public Wall(float x, float y, float width, float height, boolean isScroll, int direction) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isScroll = isScroll;
        this.direction = direction;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean getIsScroll() {
        return isScroll;
    }

    public int getDirection() {
        return direction;
    }
}
