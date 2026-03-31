package edu.polytech.cpmolgenapi.model;

import java.util.List;

public class GenerationResponse {

    private boolean isSucces;
    private String message;
    private List<int[][]> data;
    private double time;
    private int size;

    public GenerationResponse(boolean isSucces, String message, List<int[][]> data, double time) {
        this.isSucces = isSucces;
        this.message = message;
        this.data = data;
        this.time = time;
        if(data!=null) {
            this.size = data.size();
        }else{
            this.size = 0;
        }
    }

    public boolean isSucces() {
        return isSucces;
    }

    public void setSucces(boolean succes) {
        isSucces = succes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<int[][]> getData() {
        return data;
    }

    public void setData(List<int[][]> data) {
        this.data = data;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getSize() {
        if (this.data!=null)
            return this.data.size();
        else
            return 0;
    }

}