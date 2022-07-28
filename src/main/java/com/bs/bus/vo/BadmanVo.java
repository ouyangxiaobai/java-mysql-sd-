package com.bs.bus.vo;


import com.bs.bus.entity.Badman;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BadmanVo extends Badman {

    private Integer page=1;
    private Integer limit=10;

}
