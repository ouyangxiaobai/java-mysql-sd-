package com.bs.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.bus.entity.Badman;
import com.bs.bus.entity.Temp;
import com.bs.bus.entity.Tran;
import com.bs.bus.service.IBadmanService;
import com.bs.bus.service.ITempService;
import com.bs.bus.service.ITranService;
import com.bs.bus.vo.BadmanVo;
import com.bs.bus.vo.TempVo;
import com.bs.sys.common.*;
import com.bs.sys.entity.User;
import com.bs.sys.service.IDeptService;
import com.bs.sys.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author XXX
 * @since 2020-05-12
 */
@RestController
@RequestMapping("/bus/badman")
public class BadmanController {

    @Autowired
    private IBadmanService badmanService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ITempService tempService;

    @Autowired
    private ITranService tranService;

    @Autowired
    private IDeptService deptService;


    @RequestMapping("loadAllBadMan")
    public DataGridView loadAllBadMan(BadmanVo badmanVo){
        //将体温数据和车次数据的风险等级进行合并分析，其中体温数据，两个有一个为高就显示为高，以两个表风险等级的最高数据为基准
        List<Tran> trans=tranService.list();
        List<Temp> temps=tempService.list();
        List<Badman> badmanList=new ArrayList<>();
        Badman badman=null;
        for (Tran tran:trans){
            badman=new Badman();
            badman.setStuid(tran.getUid());
            badman.setCollege(tran.getCollege());
            badman.setDepartment(tran.getDepartment());
            badman.setDeptname(tran.getDeptname());
            for (Temp temp:temps){
                if(tran.getUid()==temp.getUid()){
                    if(badman.getRiskrank()!=null&&badman.getRiskrank().equals("高")){

                    }else if(badman.getRiskrank()!=null&&badman.getRiskrank().equals("中")){
                        if(temp.getRiskrank().equals("高")){
                            badman.setRiskrank("高");
                        }else {
                            badman.setRiskrank("中");
                        }
                    }else{
                        if(temp.getRiskrank().equals("高")||tran.getRiskrank().equals("高")){
                            badman.setRiskrank("高");
                        }else if (temp.getRiskrank().equals("中")){
                            badman.setRiskrank("中");
                        }else {
                            badman.setRiskrank("无");
                        }
                    }
                }
            }
            badmanList.add(badman);
        }
        QueryWrapper<Badman> badmanQueryWrapper;
        for(Badman badman3:badmanList){
            badmanQueryWrapper=new QueryWrapper<>();
            badmanQueryWrapper.eq("stuid",badman3.getStuid());
            if(badmanService.getOne(badmanQueryWrapper)==null){
                badmanService.save(badman3);
            }else {
                badman3.setId(badmanService.getOne(badmanQueryWrapper).getId());
                String riskrank=badmanService.getOne(badmanQueryWrapper).getRiskrank();
                if(riskrank.equals("确诊")){
                    //不更新
                }else if (riskrank.equals("高")||badman3.equals("高")){
                    badman3.setRiskrank("高");
                    badmanService.updateById(badman3);
                }else if(riskrank.equals("中")||badman3.equals("中")){
                    badman3.setRiskrank("中");
                    badmanService.updateById(badman3);
                }else {
                    badmanService.updateById(badman3);
                }
            }
        }
        //查询展示数据
        IPage<Badman> page = new Page<Badman>(badmanVo.getPage(),badmanVo.getLimit());
        QueryWrapper<Badman> queryWrapper = new QueryWrapper<Badman>();
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq(StringUtils.isNotBlank(badmanVo.getStuname()),"name",badmanVo.getStuname());
        //进行模糊查询
        if(StringUtils.isNotBlank(badmanVo.getStuname())){
            badmanVo.setStuid(userService.getOne(userQueryWrapper).getId());
        }
        queryWrapper.eq(badmanVo.getStuid()!=null,"stuid",badmanVo.getStuid());

        QueryWrapper<Badman> badmanQueryWrapper1=new QueryWrapper<>();
        badmanQueryWrapper1.select("distinct college");
        List<Badman> depts=badmanService.list(badmanQueryWrapper1);
        Boolean flag=true;
        for(Badman badman1:depts){
            if(StringUtils.isNotBlank(badmanVo.getDeptname())){
                if(badmanVo.getDeptname().equals("高校返校疫情排查系统")){
                        badmanVo.setDeptname("");
                }
                if(badman1.getCollege().equals(badmanVo.getDeptname())){
                    flag=false;
                }
            }

        }

        if(flag){
            queryWrapper.like(StringUtils.isNotBlank(badmanVo.getDeptname()),"deptname",badmanVo.getDeptname());
        }else {
            queryWrapper.eq(StringUtils.isNotBlank(badmanVo.getDeptname()),"college",badmanVo.getDeptname());
        }
        queryWrapper.like(StringUtils.isNotBlank(badmanVo.getRiskrank()),"riskrank",badmanVo.getRiskrank());
        queryWrapper.like(StringUtils.isNotBlank(badmanVo.getDepartment()),"department",badmanVo.getDepartment());
        queryWrapper.orderByDesc("stuid");
        badmanService.page(page,queryWrapper);
        List<Badman> badmanList1=page.getRecords();
        for (Badman badman2:badmanList1){
            badman2.setStuname(userService.getById(badman2.getStuid()).getName());
            badman2.setStunum(userService.getById(badman2.getStuid()).getLoginname());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    @RequestMapping("loadAllCol")
    public DataGridView loadAllCol(BadmanVo badmanVo){
        User user = (User) WebUtils.getSession().getAttribute("user");
        //查询展示数据
        IPage<Badman> page = new Page<Badman>(badmanVo.getPage(),badmanVo.getLimit());
        QueryWrapper<Badman> queryWrapper = new QueryWrapper<Badman>();
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq(StringUtils.isNotBlank(badmanVo.getStuname()),"name",badmanVo.getStuname());
        //进行模糊查询
        if(StringUtils.isNotBlank(badmanVo.getStuname())){
            badmanVo.setStuid(userService.getOne(userQueryWrapper).getId());
        }
        queryWrapper.eq(badmanVo.getStuid()!=null,"uid",badmanVo.getStuid());
        queryWrapper.eq("college",deptService.getById(user.getDeptid()).getName());

        queryWrapper.like(StringUtils.isNotBlank(badmanVo.getRiskrank()),"riskrank",badmanVo.getRiskrank());
        queryWrapper.like(StringUtils.isNotBlank(badmanVo.getDepartment()),"department",badmanVo.getDepartment());
        queryWrapper.orderByDesc("stuid");
        badmanService.page(page,queryWrapper);
        List<Badman> badmanList1=page.getRecords();
        for (Badman badman2:badmanList1){
            badman2.setStuname(userService.getById(badman2.getStuid()).getName());
            badman2.setStunum(userService.getById(badman2.getStuid()).getLoginname());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }


    @RequestMapping("updatebad")
    public ResultObj updateBad(Badman badman){
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if(badman.getRiskrank().equals("确诊")){
                badman.setBaddate(sdf.format(date));
            }
            if(badman.getRiskrank().equals("治愈")){
                badman.setRiskrank("无");
                badman.setRecover("已治愈"+"---时间："+sdf.format(date));
            }
           badmanService.updateById(badman);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }




    @RequestMapping("loadTreeRisk")
    public DataGridView loadTreeRiskBadman(Badman badman){

        QueryWrapper<Badman> badmanQueryWrapper=new QueryWrapper<>();
        badmanQueryWrapper.select("distinct riskrank");
        List<Badman> list = badmanService.list(badmanQueryWrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        int i=1;
        for (Badman badman1: list) {
            treeNodes.add(new TreeNode(i++,badman1.getRiskrank(),0));
        }
        return new DataGridView(treeNodes);
    }

    @RequestMapping("mapdata")
    public Map<String,Object> mapDate(){
        Map<String,Object> map=new HashMap<>();
        QueryWrapper<Badman> badmanQueryWrapper=new QueryWrapper<>();
        badmanQueryWrapper.eq("riskrank","确诊");
        List<Badman> list=badmanService.list(badmanQueryWrapper);
        List<MapData> mapData=new ArrayList<>();
        mapData.add(new MapData("学一",0));
        mapData.add(new MapData("学二",0));
        mapData.add(new MapData("学三",0));
        mapData.add(new MapData("学四",0));
        for (MapData mapData1:mapData){
            for(Badman badman:list){
                if(mapData1.getName().equals(badman.getDepartment())){
                    mapData1.setValue(mapData1.getValue()+1);
                }
            }
        }
        map.put("data",mapData);
        return map;
    }

    @RequestMapping("allmandata")
    public Map<String,Object> allManData(){
        Map<String,Object> map=new HashMap<>();
        QueryWrapper<Badman> badmanQueryWrapper=new QueryWrapper<>();
        badmanQueryWrapper.eq("riskrank","确诊").or().eq("riskrank","高").or().eq("riskrank","中");
        Integer riskman=badmanService.list(badmanQueryWrapper).size();
        map.put("riskman",riskman);
        map.put("allman",badmanService.list().size());
        return map;
    }

    /*
    无、中、高、确诊、治愈数据
     */
    @RequestMapping("allrisks")
    public Map<String,Object> allRisk(){
        Map<String,Object> map=new HashMap<>();
        QueryWrapper<Badman> badmanQueryWrapper=new QueryWrapper<>();
        badmanQueryWrapper.eq("riskrank","无");
        map.put("无",badmanService.list(badmanQueryWrapper).size());
        badmanQueryWrapper=new QueryWrapper<>();
        badmanQueryWrapper.eq("riskrank","中");
        map.put("中",badmanService.list(badmanQueryWrapper).size());
        badmanQueryWrapper=new QueryWrapper<>();
        badmanQueryWrapper.eq("riskrank","高");
        map.put("高",badmanService.list(badmanQueryWrapper).size());
        badmanQueryWrapper=new QueryWrapper<>();
        badmanQueryWrapper.eq("riskrank","确诊");
        map.put("确诊",badmanService.list(badmanQueryWrapper).size());
        badmanQueryWrapper=new QueryWrapper<>();
        badmanQueryWrapper.like("recover","治愈");
        map.put("治愈",badmanService.list(badmanQueryWrapper).size());
        return map;

    }

    /*
    分析4个宿舍区中等风险以上人数
     */
    @RequestMapping("departmentsrisk")
    public Map<String,Object> allDepartment(){
        Map<String,Object> map=new HashMap<>();
        QueryWrapper<Badman> badmanQueryWrapper=new QueryWrapper<>();
        QueryWrapper<Badman> queryWrapper=new QueryWrapper<>();
        badmanQueryWrapper.eq("riskrank","无");
        badmanQueryWrapper.eq("department","学一");
        queryWrapper.eq("department","学一");
        map.put("学一",badmanService.list(queryWrapper).size()-badmanService.list(badmanQueryWrapper).size());
        map.put("学一",badmanService.list(queryWrapper).size());
        badmanQueryWrapper=new QueryWrapper<>();
        queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("department","学二");
        badmanQueryWrapper.eq("riskrank","无");
        badmanQueryWrapper.eq("department","学二");
        map.put("学二",badmanService.list(queryWrapper).size());
        map.put("学二",badmanService.list(queryWrapper).size()-badmanService.list(badmanQueryWrapper).size());
        badmanQueryWrapper=new QueryWrapper<>();
        queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("department","学三");
        badmanQueryWrapper.eq("riskrank","无");
        badmanQueryWrapper.eq("department","学三");
        map.put("学三",badmanService.list(queryWrapper).size());
        map.put("学三",badmanService.list(queryWrapper).size()-badmanService.list(badmanQueryWrapper).size());
        badmanQueryWrapper=new QueryWrapper<>();
        queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("department","学四");
        badmanQueryWrapper.eq("riskrank","无");
        badmanQueryWrapper.eq("department","学四");
        map.put("学四",badmanService.list(queryWrapper).size());
        map.put("学四",badmanService.list(queryWrapper).size()-badmanService.list(badmanQueryWrapper).size());
        return map;

    }





}

