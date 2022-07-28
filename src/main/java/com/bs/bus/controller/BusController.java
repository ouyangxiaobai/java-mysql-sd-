package com.bs.bus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("bus")
public class BusController {
    @RequestMapping("info")
    public String tempInfo(){
        return "bus/info/info";
    }

    @RequestMapping("map")
    public String yqMap(){
        return "bus/info/map";
    }

    @RequestMapping("temp")
    public String submitTemp(){ return "bus/info/temp"; }

    @RequestMapping("tran")
    public String tranInfo(){
        return "bus/info/tran";
    }

    @RequestMapping("tempmange")
    public String tempMange(){
        return "bus/mange/tempmange";
    }

    @RequestMapping("badman")
    public String badMan(){
        return "bus/mange/badman";
    }


    @RequestMapping("tranmange")
    public String tranMange(){
        return "bus/mange/tranmange";
    }

    @RequestMapping("colmange")
    public String colMange(){
        return "bus/mange/colmange";
    }
}
