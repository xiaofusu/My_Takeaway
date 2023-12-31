package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/10/29 16:00
 * @description: 套餐实现类
 */
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void save(SetmealDTO setmealDTO) {
        //1.将套餐基本信息插入套餐表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        //获取套餐的id 之后赋给套餐对应的菜品表
        Long setmealId = setmeal.getId();
        //2.将套餐对应的菜品信息批量插入setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(!setmealDishes.isEmpty()){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            //批量插入套餐关联菜品表
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据id获取套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //1.修改套餐基本信息
        setmealMapper.update(setmeal);
        //2.删除套餐对应原本菜品的信息
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        //3.向套餐菜品表插入修改之后的菜品信息
         List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if( !setmealDishes.isEmpty()){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 套餐的停售和起售
     * @param status
     * @param id
     */
    @Override
    public void status(Integer status, Long id) {
        //套餐中包含未起售菜品不能起售
        if(status==1){//起售
            //1.根据套餐id去setmeal_dish表中查询菜品信息
            List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
            for (SetmealDish setmealDish : setmealDishes) {
                Dish dish = dishMapper.getById(setmealDish.getDishId());
                if( dish.getStatus()== 0){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id).build();
        setmealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id->{
            SetmealVO setmealVO = setmealMapper.getById(id);
            if(setmealVO.getStatus()== StatusConstant.ENABLE){
                //起售的商品不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        //删除套餐基本信息
        setmealMapper.deleteBatch(ids);
        //删除套餐对应的菜品信息
        setmealDishMapper.deleteBatch(ids);
    }

    /**
     * 条件查询套餐
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> listQuery(Setmeal setmeal) {
        List<Setmeal> setmealList = setmealMapper.list(setmeal);
        return setmealList;
    }

}
