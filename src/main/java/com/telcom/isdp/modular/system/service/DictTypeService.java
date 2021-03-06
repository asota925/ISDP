package com.telcom.isdp.modular.system.service;

import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.telcom.isdp.core.common.constant.state.CommonStatus;
import com.telcom.isdp.core.common.exception.BizExceptionEnum;
import com.telcom.isdp.core.common.page.LayuiPageFactory;
import com.telcom.isdp.core.common.page.LayuiPageInfo;
import com.telcom.isdp.modular.system.entity.DictType;
import com.telcom.isdp.modular.system.mapper.DictTypeMapper;
import com.telcom.isdp.modular.system.model.params.DictTypeParam;
import com.telcom.isdp.modular.system.model.result.DictTypeResult;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class DictTypeService extends ServiceImpl<DictTypeMapper, DictType> {

    public void add(DictTypeParam param) {

        //判断是否已经存在同编码或同名称字典
        QueryWrapper<DictType> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("code", param.getCode()).or().eq("name", param.getName());
        List<DictType> list = this.list(dictQueryWrapper);
        if (list != null && list.size() > 0) {
            throw new ServiceException(BizExceptionEnum.DICT_EXISTED);
        }

        DictType entity = getEntity(param);

        //设置状态
        entity.setStatus(CommonStatus.ENABLE.getCode());

        this.save(entity);
    }

    public void delete(DictTypeParam param) {
        this.removeById(getKey(param));
    }

    public void update(DictTypeParam param) {
        DictType oldEntity = getOldEntity(param);
        DictType newEntity = getEntity(param);
        ToolUtil.copyProperties(newEntity, oldEntity);

        //判断编码是否重复
        QueryWrapper<DictType> wrapper = new QueryWrapper<DictType>()
                .and(i -> i.eq("code", newEntity.getCode()).or().eq("name", newEntity.getName()))
                .and(i -> i.ne("dict_type_id", newEntity.getDictTypeId()));
        int dicts = this.count(wrapper);
        if (dicts > 0) {
            throw new ServiceException(BizExceptionEnum.DICT_EXISTED);
        }

        this.updateById(newEntity);
    }

    public DictTypeResult findBySpec(DictTypeParam param) {
        return null;
    }

    public List<DictTypeResult> findListBySpec(DictTypeParam param) {
        return null;
    }

    public LayuiPageInfo findPageBySpec(DictTypeParam param) {
        Page pageContext = getPageContext();
        QueryWrapper<DictType> objectQueryWrapper = new QueryWrapper<>();
        if (ToolUtil.isNotEmpty(param.getCondition())) {
            objectQueryWrapper.and(i -> i.eq("code", param.getCondition()).or().eq("name", param.getCondition()));
        }
        if (ToolUtil.isNotEmpty(param.getStatus())) {
            objectQueryWrapper.and(i -> i.eq("status", param.getStatus()));
        }
        if (ToolUtil.isNotEmpty(param.getSystemFlag())) {
            objectQueryWrapper.and(i -> i.eq("system_flag", param.getSystemFlag()));
        }

        pageContext.setAsc("sort");

        IPage page = this.page(pageContext, objectQueryWrapper);
        return LayuiPageFactory.createPageInfo(page);
    }

    private Serializable getKey(DictTypeParam param) {
        return param.getDictTypeId();
    }

    private Page getPageContext() {
        return LayuiPageFactory.defaultPage();
    }

    private DictType getOldEntity(DictTypeParam param) {
        return this.getById(getKey(param));
    }

    private DictType getEntity(DictTypeParam param) {
        DictType entity = new DictType();
        ToolUtil.copyProperties(param, entity);
        return entity;
    }

}
