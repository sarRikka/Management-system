package com.bjpowernode;

import com.bjpowernode.mapper.ProductInfoMapper;
import com.bjpowernode.pojo.ProductInfo;
import com.bjpowernode.pojo.vo.ProductInfoVo;
import com.bjpowernode.utils.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext_dao.xml","classpath:applicationContext_service.xml"})
public class MyTest {

    @Autowired
    ProductInfoMapper mapper;

    @Test
    public void testMD5()
    {
        String mi = MD5Util.getMD5("00000");
        System.out.println(mi);
    }

    @Test
    public void testSelectCondition(){
        ProductInfoVo vo =new ProductInfoVo();

        vo.setLprice(3099);
        vo.setTypeid(3);
        vo.setHprice(4000);
        List<ProductInfo> list = mapper.selectCondition(vo);


        for(ProductInfo i:list)
        {
            System.out.println(i);
        }
    }

}
