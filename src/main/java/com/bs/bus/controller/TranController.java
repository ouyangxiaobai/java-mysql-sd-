package com.bs.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.bus.common.sdk.ApiClient_tripQuery;
import com.bs.bus.entity.Temp;
import com.bs.bus.entity.Tran;
import com.bs.bus.service.ITranService;
import com.bs.bus.vo.TempVo;
import com.bs.bus.vo.TranVo;
import com.bs.sys.common.*;
import com.bs.sys.entity.User;
import com.bs.sys.service.IDeptService;
import com.bs.sys.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author XXX
 * @since 2020-05-07
 */
@RestController
@RequestMapping("/bus/tran")
public class TranController {
    @Autowired
    private IDeptService deptService;

    @Autowired
    private ITranService tranService;

    @Autowired
    private IUserService userService;


    @RequestMapping("tranjudge")
    public ResultObj tranJudge(Tran tran){
        User user = (User) WebUtils.getSession().getAttribute("user");
        tran.setDeptid(user.getDeptid());
        tran.setDeptname(deptService.getById(user.getDeptid()).getName());
        tran.setUid(user.getId());
        if(user.getLoginname().equals("admin")){
            tran.setCollege("高校返校疫情排查系统");
        }else {
            tran.setCollege(deptService.getById(userService.getById(user.getMgr()).getDeptid()).getName());
        }
        ApiClient_tripQuery apiClient;
        List<Tran> trans=new ArrayList<>();
        try {
            apiClient = new ApiClient_tripQuery();
            trans=apiClient.searchTran(tran.getArrive(),tran.getArrdate(),tran.getTrainno());
            for(Tran tran1:trans){
                if(tran1.getArrdate().equals(tran.getArrdate())&&tran1.getTrainno().equals(tran.getTrainno())){
                    tran.setRiskrank("高");
                    QueryWrapper<Tran> tranQueryWrapper=new QueryWrapper<>();
                    tranQueryWrapper.eq("uid",user.getId());
                    if(tranService.getOne(tranQueryWrapper)!=null){
                        tran.setId(tranService.getOne(tranQueryWrapper).getId());
                        tranService.updateById(tran);
                    }else {
                        tranService.save(tran);
                    }
                    return new ResultObj(Constast.OK,"您与患者可能同程，有感染风险！");
                }
            }
            apiClient.destoy();
            tran.setRiskrank("无");
            QueryWrapper<Tran> tranQueryWrapper=new QueryWrapper<>();
            tranQueryWrapper.eq("uid",user.getId());
            if(tranService.getOne(tranQueryWrapper)!=null){
                tran.setId(tranService.getOne(tranQueryWrapper).getId());
                tranService.updateById(tran);
            }else {
                tranService.save(tran);
            }
            return new ResultObj(Constast.OK,"您没有与患者同程，出门请戴口罩！与他人保持距离...");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResultObj(Constast.ERROR,"查询异常！");
        }

    }



    @RequestMapping("loadTreeRiskTran")
    public DataGridView loadTreeRiskTran(TranVo tranVo){

        QueryWrapper<Tran> tempQueryWrapper=new QueryWrapper<>();
        tempQueryWrapper.select("distinct riskrank");
        List<Tran> list = tranService.list(tempQueryWrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        int i=1;
        for (Tran tran: list) {
            treeNodes.add(new TreeNode(i++,tran.getRiskrank(),0));
        }
        return new DataGridView(treeNodes);
    }

    @RequestMapping("loadTreeDepartTran")
    public DataGridView loadTreeDepartTran(TranVo tranVo){
        QueryWrapper<Tran> tranQueryWrapper=new QueryWrapper<>();
        tranQueryWrapper.select("distinct department");
        List<Tran> list = tranService.list(tranQueryWrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        int i=1;
        for (Tran tran : list) {
            treeNodes.add(new TreeNode(i++,tran.getDepartment(),0));
        }
        return new DataGridView(treeNodes);
    }


    @RequestMapping("loadAllTran")
    public DataGridView loadAllTran(TranVo tranVo){
        IPage<Tran> page = new Page<Tran>(tranVo.getPage(),tranVo.getLimit());
        QueryWrapper<Tran> queryWrapper = new QueryWrapper<Tran>();
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq(StringUtils.isNotBlank(tranVo.getStuname()),"name",tranVo.getStuname());
        //进行模糊查询
        if(StringUtils.isNotBlank(tranVo.getStuname())){
            if(userService.getOne(userQueryWrapper)!=null){
                tranVo.setUid(userService.getOne(userQueryWrapper).getId());
            }

        }
        queryWrapper.eq(tranVo.getUid()!=null,"uid",tranVo.getUid());

        QueryWrapper<Tran> tranQueryWrapper=new QueryWrapper<>();
        tranQueryWrapper.select("distinct college");
        List<Tran> depts=tranService.list(tranQueryWrapper);
        Boolean flag=true;
        for(Tran tran:depts){
            if(StringUtils.isNotBlank(tranVo.getDeptname())){
                if(tranVo.getDeptname().equals("高校返校疫情排查系统")){
                    tranVo.setDeptname("");
                }
                if(tran.getCollege().equals(tranVo.getDeptname())){
                    flag=false;
                }
            }

        }

        if(flag){
            queryWrapper.like(StringUtils.isNotBlank(tranVo.getDeptname()),"deptname",tranVo.getDeptname());
        }else {
            queryWrapper.eq(StringUtils.isNotBlank(tranVo.getDeptname()),"college",tranVo.getDeptname());
        }
        queryWrapper.eq(StringUtils.isNotBlank(tranVo.getArrdate()),"arrdate",tranVo.getArrdate());
        queryWrapper.like(StringUtils.isNotBlank(tranVo.getRiskrank()),"riskrank",tranVo.getRiskrank());
        queryWrapper.like(StringUtils.isNotBlank(tranVo.getDepartment()),"department",tranVo.getDepartment());
        queryWrapper.like(StringUtils.isNotBlank(tranVo.getTrainno()),"trainno",tranVo.getTrainno());
        queryWrapper.orderByDesc("arrdate");
        tranService.page(page,queryWrapper);
        List<Tran> trans=page.getRecords();
        for (Tran tran:trans){
            tran.setStuname(userService.getById(tran.getUid()).getName());
            tran.setStunum(userService.getById(tran.getUid()).getLoginname());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    @RequestMapping("areatran")
    public List allManData(){
        Map<String,Map<String,MapData>> map=new HashMap<>();
        List<Tran> trans=tranService.list();
        for (Tran tran:trans){
            if(map.get(tran.getFromarea())!=null){
                map.get(tran.getFromarea()).get(tran.getFromarea()).setValue(map.get(tran.getFromarea()).get(tran.getFromarea()).getValue()+1);
            }else {
                Map<String,MapData> areaMap=new HashMap<>();
                areaMap.put(tran.getFromarea(),new MapData(tran.getArrive(),1));
                map.put(tran.getFromarea(),areaMap);
            }
        }
        List lists=new ArrayList<>();

        Iterator iterator=map.entrySet().iterator();
        while (iterator.hasNext()){
            List midlist=new ArrayList();
            Map.Entry entry= (Map.Entry) iterator.next();
            List list=new ArrayList();
            List countmap=new ArrayList();
            countmap.add(new AreaName((String) entry.getKey()));
            countmap.add(map.get(entry.getKey()).get(entry.getKey()));
            List countlist=new ArrayList<>();
            countlist.add(countmap);
            midlist.add((String)entry.getKey());
            midlist.add(countlist);
            lists.add(midlist);
        }
        return lists;
    }

    @RequestMapping("tranchart")
    public List tranChart(){
        List list=new ArrayList();
        QueryWrapper<Tran> tranQueryWrapper=new QueryWrapper<>();
        tranQueryWrapper.select("distinct trainno");
        List<Tran> trans=tranService.list(tranQueryWrapper);
        List tranno=new ArrayList();
        List norisk=new ArrayList();
        List hasrisk=new ArrayList();
        for(Tran tran:trans){
            tranno.add(tran.getTrainno());
            QueryWrapper<Tran> queryWrapper=new QueryWrapper<>();
            QueryWrapper<Tran> allquery=new QueryWrapper<>();
            allquery.eq("trainno",tran.getTrainno());
            queryWrapper.eq("trainno",tran.getTrainno());
            queryWrapper.eq("riskrank","无");
            norisk.add(tranService.list(queryWrapper).size());
            hasrisk.add(tranService.list(allquery).size()-tranService.list(queryWrapper).size());
        }
        list.add(tranno);
        list.add(norisk);
        list.add(hasrisk);
        return list;
    }

    /**
     * 返程区域分析
     */
    @RequestMapping("tranorigin")
    public List tranOrigin(){
        List list=new ArrayList();
        QueryWrapper<Tran> tranQueryWrapper=new QueryWrapper<>();
        tranQueryWrapper.select("distinct fromarea");
        List<Tran> trans=tranService.list(tranQueryWrapper);
        for(Tran tran:trans){
           QueryWrapper<Tran> queryWrapper=new QueryWrapper<>();
           queryWrapper.eq("fromarea",tran.getFromarea());
           list.add(new MapData(tran.getFromarea(),tranService.list(queryWrapper).size()));
        }
        return list;
    }

}

