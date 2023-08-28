package com.example.sellapplingen;

public class SetSettings{
    String parameter;
    String value;

    SetSettings(String parameter, String value) {
        this.parameter = parameter;
        this.value = value;
    }
    @Override
    public String toString() {
        return "SetSettings{" +
                "parameter='" + parameter + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}