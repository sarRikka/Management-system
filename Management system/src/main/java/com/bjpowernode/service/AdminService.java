package com.bjpowernode.service;

import com.bjpowernode.pojo.Admin;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {

    Admin login(String name, String pwd);

}
