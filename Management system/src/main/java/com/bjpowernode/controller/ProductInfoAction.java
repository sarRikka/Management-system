package com.bjpowernode.controller;

import com.bjpowernode.pojo.ProductInfo;
import com.bjpowernode.pojo.vo.ProductInfoVo;
import com.bjpowernode.service.ProductInfoService;
import com.bjpowernode.utils.FileNameUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/prod")
public class ProductInfoAction {

    //每页显示的记录数
    public static final int PAGE_SIZE=5;

    //异步上传图片名称
    String saveFileName="";
    @Autowired
    ProductInfoService productInfoService;

    @RequestMapping("/getAll")
    public String getAll(HttpServletRequest request){
        List<ProductInfo> list = productInfoService.getAll();
        request.setAttribute("list",list);
        return "product";
    }

    //显示第一页的五条记录
    @RequestMapping("/split")
    public String split(HttpServletRequest request)
    {

        PageInfo info = null;
        Object vo = request.getSession().getAttribute("prodVo");
        if(vo != null ){
            info = productInfoService.splitPageVo((ProductInfoVo)vo,PAGE_SIZE);
            request.getSession().removeAttribute("prodVo");
        }
        else {
            info = productInfoService.splitPage(1,PAGE_SIZE);
        }
        request.setAttribute("info",info);
        return "product";
    }

    //ajax分页翻页处理
    @ResponseBody
    @RequestMapping("/ajaxSplit")
    public void ajaxSplit(ProductInfoVo vo, HttpSession session)
    {
        //取得当前page参数的页面数据
        PageInfo info = productInfoService.splitPageVo(vo,PAGE_SIZE);
        session.setAttribute("info",info);

    }

    //多条件查询功能的实现
    @ResponseBody
    @RequestMapping("/condition")
    public void condition(ProductInfoVo vo, HttpSession session){
        List<ProductInfo> list = productInfoService.selectCondtion(vo);
        session.setAttribute("list",list);

    }

    @ResponseBody
    @RequestMapping("/ajaxImg")
    public Object ajaxImg(MultipartFile pimage,HttpServletRequest request){
        //提取生成的文件名UUID+上传图片后缀.jpg
        saveFileName = FileNameUtil.getUUIDFileName()+FileNameUtil.getFileType(pimage.getOriginalFilename());
        //得到项目中图片存储的路径
        String path = request.getServletContext().getRealPath("/image_big");
        //转存D:\idea\project\mimissm\mimissm\image_big\saveFilename
        try {
            pimage.transferTo(new File(path+ File.separator+saveFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject object = new JSONObject();
        object.put("imgurl",saveFileName);
        return object.toString();
    }

    @RequestMapping("/save")
    public String save(ProductInfo info , HttpServletRequest request)
    {
        info.setpImage(saveFileName);
        info.setpDate(new Date());
        int num=-1;

        try {
            num=productInfoService.save(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(num>0){
            request.setAttribute("msg","增加成功！");
        }
        else {
            request.setAttribute("msg","增加失败！");
        }
        //清空saveFileName变量中的内容，为下次增加或修改的异步ajax的上传处理
        saveFileName="";
        //增加成功应该重新访问数据库，跳转到分页显示的action上
        return "forward:/prod/split.action";

    }

    @RequestMapping("/one")
    public String one(int pid,ProductInfoVo vo ,Model model,HttpSession session)
    {
        ProductInfo info = productInfoService.getByID(pid);
        model.addAttribute("prod",info);
        //将多条件及页码放入session中，更新处理结束后分页时读取条件和页码进行处理
        session.setAttribute("prodVo",vo);
        return "update";
    }

    @RequestMapping("update")
    public String update(ProductInfo info,HttpServletRequest request){

        //如果没有异步ajax里的图片上传则saveFile="",
        //实体类info使用隐藏表单提供上来的pImage提供的名称

        if(!saveFileName.equals("")){
            info.setpImage(saveFileName);
        }

        int num=-1;

        try {
            num=productInfoService.update(info);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(num>0){
            //更新成功
            request.setAttribute("msg","更新成功！");
        }
        else {
            //更新失败
            request.setAttribute("msg","更新失败！");
        }
        //下一次更新时saveFile会作为判断依据，必须清空
        saveFileName="";
        return "forward:/prod/split.action";

    }

    @RequestMapping("/delete")
    public String delete(int pid,ProductInfoVo vo,HttpServletRequest request)
    {
        int num=-1;

        try {
            num=productInfoService.delete(pid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(num>0)
        {
            request.setAttribute("msg","删除成功!");
            request.getSession().setAttribute("deleteProdVo",vo);

        }
        else
        {
            request.setAttribute("msg","删除失败!");
        }
        return "forward:/prod/deleteAjaxSplit.action";
    }

    @ResponseBody
    @RequestMapping(value = "/deleteAjaxSplit",produces = "text/html;charset=UTF-8")
    public Object deleteAjaxSplit(HttpServletRequest request)
    {
        PageInfo info = null;
        Object vo = request.getSession().getAttribute("deleteProdVo");
        if(vo != null){
            info = productInfoService.splitPageVo((ProductInfoVo)vo,PAGE_SIZE);
        }
        else {
            info = productInfoService.splitPage(1, PAGE_SIZE);
        }
        request.getSession().setAttribute("info",info);
        return request.getAttribute("msg");
    }

    @RequestMapping("/deleteBatch")
    public String deleteBatch(String pids,HttpServletRequest request)
    {
        //将上传上来的字符串截断，形成字符数组
        String []ps = pids.split(",");



        try {
            int num = productInfoService.deleteBatch(ps);
            if(num>0) {
                request.setAttribute("msg", "批量删除成功！");
            }
            else
            {
                request.setAttribute("msg", "批量删除失败！");
            }
        } catch (Exception e) {
            request.setAttribute("msg","商品不可删除！");
        }


        return "forward:/prod/deleteAjaxSplit.action";
    }



}
