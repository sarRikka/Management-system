package com.bjpowernode.service.impl;

import com.bjpowernode.mapper.AdminMapper;
import com.bjpowernode.pojo.Admin;
import com.bjpowernode.pojo.AdminExample;
import com.bjpowernode.service.AdminService;
import com.bjpowernode.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    //在业务逻辑层中，一定会有数据访问层对象
    @Autowired
    AdminMapper adminMapper;

    @Override
    public Admin login(String name, String pwd) {

        //根据用户名到数据库中查询对象

        //如果有条件，则一定要创建AdminExample对象，用来封装条件
        AdminExample example = new AdminExample();
        /*如何添加条件 where */
        //添加用户名条件
        example.createCriteria().andANameEqualTo(name);

        List<Admin> list=adminMapper.selectByExample(example);

        if(list.size()>0){
            Admin admin = list.get(0);
            //如果查询到用户，再进行密码比对

            String miPwd = MD5Util.getMD5(pwd);
            if(pwd.equals(admin.getaPass())){
                return admin;
            }

        }

        return null;
    }
}
