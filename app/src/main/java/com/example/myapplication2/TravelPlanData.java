package com.example.myapplication2;

public class TravelPlanData {

    public int days;
    public String dest;
    public String plans = "";

    public TravelPlanData(int days, String dest, String plans) {
        this.days = days;
        this.dest = dest;
        this.plans = plans;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getPlans() {
        return plans;
    }

    public void setPlans(String plans) {
        this.plans = plans;
    }
}
