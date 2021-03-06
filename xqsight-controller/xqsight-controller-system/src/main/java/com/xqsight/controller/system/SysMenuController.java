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
import com.xqsight.system.model.SysMenu;
import com.xqsight.system.service.SysMenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>菜单信息表 controller</p>
 * <p>Table: sys_menu - 菜单信息表</p>
 *
 * @author wangganggang
 * @since 2017-01-07 11:57:32
 */
@RestController
@RequestMapping("/sys/menu/")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @RequestMapping("save")
    public Object save(SysMenu sysMenu) {
        if (StringUtils.equals(sysMenu.getParentId(), "0")) {
            sysMenu.setParentIds(",0,");
        } else {
            SysMenu parentMenu = sysMenuService.get(Long.valueOf(sysMenu.getParentId()));
            sysMenu.setParentIds(parentMenu.getParentIds() + parentMenu.getMenuId() + Constants.COMMA_SIGN_SPLIT_NAME);
        }

        sysMenuService.save(sysMenu, true);
        return MessageSupport.successMsg("保存成功");
    }

    @RequestMapping("update")
    public Object update(SysMenu sysMenu) {
        sysMenuService.update(sysMenu, true);
        return MessageSupport.successMsg("修改成功");
    }

    @RequestMapping("delete")
    public Object delete(Long menuId) {
        sysMenuService.delete(menuId);
        return MessageSupport.successMsg("删除成功");
    }

    @RequestMapping("logicDel")
    public Object logicDel(Long menuId) {
        sysMenuService.logicDel(menuId);
        return MessageSupport.successMsg("删除成功");
    }

    @RequestMapping("query")
    public Object query(String menuName, String parentId) {
        List<PropertyFilter> propertyFilters = PropertyFilterBuilder.create().matchTye(MatchType.LIKE)
                .propertyType(PropertyType.S).add("menu_name", StringUtils.trimToEmpty(menuName))
                .matchTye(MatchType.EQ).propertyType(PropertyType.L)
                .add("parent_id", parentId).end();
        List<Sort> sorts = SortBuilder.create().addAsc("sort").addAsc("menu_name").end();
        List<SysMenu> sysMenus = sysMenuService.search(propertyFilters, sorts);
        return MessageSupport.successDataMsg(sysMenus, "查询成功");
    }

    @RequestMapping("querybyid")
    public Object queryById(Long menuId) {
        SysMenu sysMenu = sysMenuService.get(menuId);
        return MessageSupport.successDataMsg(sysMenu, "查询成功");
    }


    @RequestMapping("querytree")
    public Object queryTree(String type) {
        type = StringUtils.isBlank(type) ? "0" : "1";
        List<PropertyFilter> propertyFilters = PropertyFilterBuilder.create().matchTye(MatchType.EQ)
                .propertyType(PropertyType.I).add("type", type).end();

        List<Sort> sorts = SortBuilder.create().addAsc("sort").end();
        List<SysMenu> sysMenus = sysMenuService.search(propertyFilters, sorts);
        sysMenus = new TreeSupport<SysMenu>().generateTree(sysMenus);
        return MessageSupport.successDataMsg(sysMenus, "查询成功");
    }
}