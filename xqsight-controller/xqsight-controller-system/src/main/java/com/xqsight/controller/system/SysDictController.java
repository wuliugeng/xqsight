/**
 * 新启工作室
 * Copyright (c) 1994-2015 All Rights Reserved.
 */
package com.xqsight.controller.system;

import com.xqsight.common.core.orm.MatchType;
import com.xqsight.common.core.orm.PropertyFilter;
import com.xqsight.common.core.orm.PropertyType;
import com.xqsight.common.core.orm.Sort;
import com.xqsight.common.core.orm.builder.PropertyFilterBuilder;
import com.xqsight.common.core.orm.builder.SortBuilder;
import com.xqsight.common.model.constants.Constants;
import com.xqsight.common.support.MessageSupport;
import com.xqsight.common.support.TreeSupport;
import com.xqsight.system.model.SysDict;
import com.xqsight.system.service.SysDictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>系统字典表 controller</p>
 * <p>Table: sys_dict - 系统字典表</p>
 *
 * @author wangganggang
 * @since 2017-01-07 11:57:14
 */
@RestController
@RequestMapping("/sys/dict/")
public class SysDictController {

    @Autowired
    private SysDictService sysDictService;

    @RequestMapping("save")
    public Object save(SysDict sysDict) {
        if(StringUtils.equals(sysDict.getParentId(),"0")){
            sysDict.setParentIds(",0,");
        }else{
            SysDict parentDict = sysDictService.get(Long.valueOf(sysDict.getParentId()));
            sysDict.setParentIds(parentDict.getParentIds() + parentDict.getDictId() + Constants.COMMA_SIGN_SPLIT_NAME);
        }

        sysDictService.save(sysDict, true);
        return MessageSupport.successMsg("保存成功");
    }

    @RequestMapping("update")
    public Object update(SysDict sysDict) {
        SysDict subDict = sysDictService.get(sysDict.getDictId());
        if (subDict.getEditable() == Constants.DISABLE)
            return MessageSupport.failureMsg("该条字典是系统内置数据不可修改");

        sysDictService.update(sysDict, true);
        return MessageSupport.successMsg("修改成功");
    }

    @RequestMapping("delete")
    public Object delete(Long dictId) {
        SysDict subDict = sysDictService.get(dictId);
        if (subDict.getEditable() == Constants.DISABLE)
            return MessageSupport.failureMsg("该条字典是系统内置数据不可删除");

        List<PropertyFilter> propertyFilters = PropertyFilterBuilder.create().matchTye(MatchType.EQ)
                .propertyType(PropertyType.L).add("parent_id", "" + dictId).end();
        List<SysDict> sysDicts = sysDictService.search(propertyFilters);
        if (sysDicts != null && sysDicts.size() > 0)
            return MessageSupport.failureMsg("该字典项还有子项不可删除");

        sysDictService.delete(dictId);
        return MessageSupport.successMsg("删除成功");
    }

    @RequestMapping("logicDel")
    public Object logicDel(Long dictId) {
        sysDictService.logicDel(dictId);
        return MessageSupport.successMsg("删除成功");
    }

    @RequestMapping("query")
    public Object query(String dictName, String dictCode, String parentId) {
        List<PropertyFilter> propertyFilters = PropertyFilterBuilder.create().matchTye(MatchType.LIKE)
                .propertyType(PropertyType.S).add("dict_name", StringUtils.trimToEmpty(dictName))
                .add("dict_code", StringUtils.trimToEmpty(dictCode))
                .matchTye(MatchType.EQ).propertyType(PropertyType.L).add("parent_id", parentId).end();
        List<Sort> sorts = SortBuilder.create().addAsc("dict_name").end();
        List<SysDict> sysDicts = sysDictService.search(propertyFilters, sorts);
        return MessageSupport.successDataMsg(sysDicts, "查询成功");
    }

    @RequestMapping("querybyid")
    public Object queryById(Long dictId) {
        SysDict sysDict = sysDictService.get(dictId);
        return MessageSupport.successDataMsg(sysDict, "查询成功");
    }

    @RequestMapping("querytree")
    public Object queryTree(String dictName, String dictCode) {
        List<PropertyFilter> propertyFilters = PropertyFilterBuilder.create().matchTye(MatchType.LIKE)
                .propertyType(PropertyType.S).add("dict_name", StringUtils.trimToEmpty(dictName))
                .add("dict_code", StringUtils.trimToEmpty(dictCode)).end();
        List<Sort> sorts = SortBuilder.create().addAsc("dict_name").end();
        List<SysDict> sysDicts = sysDictService.search(propertyFilters, sorts);
        sysDicts = new TreeSupport<SysDict>().generateTree(sysDicts);
        return MessageSupport.successDataMsg(sysDicts, "查询成功");
    }

}