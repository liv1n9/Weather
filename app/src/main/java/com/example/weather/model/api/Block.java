package com.example.weather.model.api;

import java.util.ArrayList;

public class Block {
    private ArrayList<Data> data;

    public Block(ArrayList<Data> data) {
        this.data = data;
    }

    public ArrayList<Data> getData() {
        return data;
    }
}
