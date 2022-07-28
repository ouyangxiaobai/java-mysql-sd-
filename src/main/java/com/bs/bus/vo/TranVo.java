package com.bs.bus.vo;


import com.bs.bus.entity.Tran;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TranVo extends Tran {

    private Integer page=1;
    private Integer limit=10;
}
