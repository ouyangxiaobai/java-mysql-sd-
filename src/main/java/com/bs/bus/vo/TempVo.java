package com.bs.bus.vo;

import com.bs.bus.entity.Temp;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TempVo extends Temp {
    private Integer page=1;
    private Integer limit=10;
}


