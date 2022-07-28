package com.bs.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.bus.entity.Temp;
import com.bs.bus.service.ITempService;
import com.bs.bus.vo.TempVo;
import com.bs.sys.common.*;
import com.bs.sys.entity.Dept;
import com.bs.sys.entity.Notice;
import com.bs.sys.entity.User;
import com.bs.sys.service.IDeptService;
import com.bs.sys.service.IUserService;
import com.bs.sys.vo.DeptVo;
import com.bs.sys.vo.NoticeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author XXX
 * @since 2020-05-08
 */
@RestController
@RequestMapping("/bus/temp")
public class TempController {
    @Autowired
    private IDeptService deptService;

    @Autowired
    private ITempService tempService;

    @Autowired
    private IUserService userService;


    @RequestMapping("temsubmit")
    public ResultObj temSubmit(Temp temp){
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            temp.setUid(user.getId());
            temp.setDeptid(user.getDeptid());
            if(user.getMgr()!=null){
                temp.setCollege(deptService.getById(userService.getById(user.getMgr()).getDeptid()).getName());
            }else{
                temp.setCollege("");
            }
            temp.setDeptname(deptService.getById(user.getDeptid()).getName());
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            temp.setSubmitdate(sdf.format(date));
            QueryWrapper<Temp> tempQueryWrapper=new QueryWrapper<>();
            tempQueryWrapper.eq("submitdate",sdf.format(date));
            tempQueryWrapper.eq("uid",user.getId());
            if(tempService.getOne(tempQueryWrapper)!=null){
                temp.setId(tempService.getOne(tempQueryWrapper).getId());
                if(36.5<=temp.getTemp()&&temp.getTemp()<=37.5){
                    if(temp.getSymptoms().equals("无")){
                        temp.setRiskrank("无");
                        tempService.updateById(temp);
                        return new ResultObj(Constast.OK,"更新成功！你很健康");
                    }else {
                        temp.setRiskrank("中");
                        tempService.updateById(temp);
                        return new ResultObj(Constast.OK,"更新成功！您身体欠佳，请及时与辅导员联系");
                    }
                }else {
                    temp.setRiskrank("高");
                    tempService.updateById(temp);
                    return new ResultObj(Constast.OK,"更新成功！务必要和辅导员联系");
                }

            }else {
                if(36.5<=temp.getTemp()&&temp.getTemp()<=37.5){
                    if(temp.getSymptoms().equals("无")){
                        temp.setRiskrank("无");
                        tempService.save(temp);
                        return new ResultObj(Constast.OK,"提交成功！你很健康");
                    }else {
                        temp.setRiskrank("中");
                        tempService.save(temp);
                        return new ResultObj(Constast.OK,"提交成功！您身体欠佳，请及时与辅导员联系");
                    }
                }else {
                    temp.setRiskrank("高");
                    tempService.save(temp);
                    return new ResultObj(Constast.OK,"提交成功！务必要和辅导员联系");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResultObj(Constast.ERROR,"提交失败！");
        }
    }

    @RequestMapping("loadAllTemp")
    public DataGridView loadAllTemp(TempVo tempVo){
        IPage<Temp> page = new Page<Temp>(tempVo.getPage(),tempVo.getLimit());
        QueryWrapper<Temp> queryWrapper = new QueryWrapper<Temp>();
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq(StringUtils.isNotBlank(tempVo.getStuname()),"name",tempVo.getStuname());
        //进行模糊查询
        if(StringUtils.isNotBlank(tempVo.getStuname())){
            tempVo.setUid(userService.getOne(userQueryWrapper).getId());
        }
        queryWrapper.eq(tempVo.getUid()!=null,"uid",tempVo.getUid());

        QueryWrapper<Temp> tempQueryWrapper=new QueryWrapper<>();
        tempQueryWrapper.select("distinct college");
        List<Temp> depts=tempService.list(tempQueryWrapper);
        Boolean flag=true;
        for(Temp temp:depts){
            if(StringUtils.isNotBlank(tempVo.getDeptname())){
                if(tempVo.getDeptname().equals("高校返校疫情排查系统")){
                    tempVo.setDeptname("");
                }
                if(temp.getCollege().equals(tempVo.getDeptname())){
                   flag=false;
                }
            }

        }

        if(flag){
            queryWrapper.like(StringUtils.isNotBlank(tempVo.getDeptname()),"deptname",tempVo.getDeptname());
        }else {
            queryWrapper.eq(StringUtils.isNotBlank(tempVo.getDeptname()),"college",tempVo.getDeptname());
        }
        queryWrapper.eq(StringUtils.isNotBlank(tempVo.getSubmitdate()),"submitdate",tempVo.getSubmitdate());
        queryWrapper.like(StringUtils.isNotBlank(tempVo.getRiskrank()),"riskrank",tempVo.getRiskrank());
        queryWrapper.like(StringUtils.isNotBlank(tempVo.getDepartment()),"department",tempVo.getDepartment());
        queryWrapper.orderByDesc("submitdate");
        tempService.page(page,queryWrapper);
        List<Temp> temps=page.getRecords();
        for (Temp temp:temps){
            temp.setStuname(userService.getById(temp.getUid()).getName());
            temp.setStunum(userService.getById(temp.getUid()).getLoginname());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    @RequestMapping("loadTreeRisk")
    public DataGridView loadTreeRisk(TempVo tempVo){

        QueryWrapper<Temp> tempQueryWrapper=new QueryWrapper<>();
        tempQueryWrapper.select("distinct riskrank");
        List<Temp> list = tempService.list(tempQueryWrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        int i=1;
        for (Temp temp : list) {
            treeNodes.add(new TreeNode(i++,temp.getRiskrank(),0));
        }
        return new DataGridView(treeNodes);
    }


    @RequestMapping("loadTreeDepart")
    public DataGridView loadTreeDepart(TempVo tempVo){
        QueryWrapper<Temp> tempQueryWrapper=new QueryWrapper<>();
        tempQueryWrapper.select("distinct department");
        List<Temp> list = tempService.list(tempQueryWrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        int i=1;
        for (Temp temp : list) {
            treeNodes.add(new TreeNode(i++,temp.getDepartment(),0));
        }
        return new DataGridView(treeNodes);
    }


    @RequestMapping("tempdata")
    public List tempData(){
        List list=new ArrayList();
        QueryWrapper<Temp> tempQueryWrapper=new QueryWrapper<>();
        tempQueryWrapper.select("distinct submitdate");
        List<Temp> temps=tempService.list(tempQueryWrapper);
        List tempdate=new ArrayList();
        List nocount=new ArrayList();
        List midcount=new ArrayList();
        List hightcount=new ArrayList();
        for(Temp temp:temps){
            tempdate.add(temp.getSubmitdate());
            tempQueryWrapper=new QueryWrapper<>();
            tempQueryWrapper.eq("submitdate",temp.getSubmitdate());
            tempQueryWrapper.eq("riskrank","无");
            nocount.add(tempService.list(tempQueryWrapper).size());
            tempQueryWrapper=new QueryWrapper<>();
            tempQueryWrapper.eq("submitdate",temp.getSubmitdate());
            tempQueryWrapper.eq("riskrank","中");
            midcount.add(tempService.list(tempQueryWrapper).size());
            tempQueryWrapper=new QueryWrapper<>();
            tempQueryWrapper.eq("submitdate",temp.getSubmitdate());
            tempQueryWrapper.eq("riskrank","高");
            hightcount.add(tempService.list(tempQueryWrapper).size());
        }
        list.add(tempdate);
        list.add(nocount);
        list.add(midcount);
        list.add(hightcount);
        return list;
    }

}

