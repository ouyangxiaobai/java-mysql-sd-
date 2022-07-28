package com.bs.sys.common;

import lombok.Data;

@Data
public class AreaName {
    private String name;
    public AreaName(String name){
        this.name=name;
    }
}
