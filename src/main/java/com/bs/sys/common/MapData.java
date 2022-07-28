package com.bs.sys.common;

import lombok.Data;

@Data
public class MapData {

    private String name;
    private Integer value;

    public MapData(String name){
        this.name=name;
    }
    public MapData(String name,Integer value){
        this.name=name;
        this.value=value;
    }
}
