package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@CrossOrigin
@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {


    @Autowired
    private HospitalSetService hospitalSetService;


    /**
     * 查询医院设置表所有信息
     * @return
     */
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitaLSet() {
        List<HospitalSet> list = hospitalSetService.list();
//        int i = 1/0;
        return Result.ok(list);
    }

    /**
     * 逻辑删除医院设置
     * @param id
     * @return
     */
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 带条件分页查询
     * @param current
     * @param limit
     * @param hospitalSetQueryVo
     * @return
     */
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet (@PathVariable long current,
                                   @PathVariable long limit,
                                   @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        // 创建 page 对象 传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current,limit);

        // 构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();

        // 医院名称
        String hosname = hospitalSetQueryVo.getHosname();
        // 医院编号
        String hoscode = hospitalSetQueryVo.getHoscode();

        if(StringUtils.isNotEmpty(hosname)) {
            wrapper.like("hosname",hosname);
        }

        if (StringUtils.isNotEmpty(hoscode)) {
            wrapper.like("hoscode",hoscode);
        }

        // 调用方法 实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page,wrapper);
        // 返回结果
        return Result.ok(pageHospitalSet);
    }

    /**
     * 添加医院
     * @param hospitalSet
     * @return
     */
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        // 设置状态 1使用 0不能使用
        hospitalSet.setStatus(1);
        // 签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        // 调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 根据id获取医院设置
     * @param id
     * @return
     */
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    /**
     * 修改医院设置
     * @param hospitalSet
     * @return
     */
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if(flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 批量删除医院设置
     * @param idList
     * @return
     */
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList) {
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    /**
     * 医院设置锁定和解锁
     * @param id
     * @param status
     * @return
     */
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status) {

        HospitalSet hospitalSet = hospitalSetService.getById(id);
        // 设置状态
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }


    /**
     * 发送签名秘钥
     * @param id
     * @return
     */
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }




}
