/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.charles7c.cnadmin.webapi.controller.common;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

import top.charles7c.cnadmin.common.base.BaseEnum;
import top.charles7c.cnadmin.common.config.properties.ProjectProperties;
import top.charles7c.cnadmin.common.constant.CacheConsts;
import top.charles7c.cnadmin.common.model.query.SortQuery;
import top.charles7c.cnadmin.common.model.vo.LabelValueVO;
import top.charles7c.cnadmin.common.model.vo.R;
import top.charles7c.cnadmin.monitor.annotation.Log;
import top.charles7c.cnadmin.system.model.query.DeptQuery;
import top.charles7c.cnadmin.system.model.query.MenuQuery;
import top.charles7c.cnadmin.system.model.query.RoleQuery;
import top.charles7c.cnadmin.system.model.vo.RoleVO;
import top.charles7c.cnadmin.system.service.*;

/**
 * 公共 API
 *
 * @author Charles7c
 * @since 2023/1/22 21:48
 */
@Tag(name = "公共 API")
@Log(ignore = true)
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {

    private final DeptService deptService;
    private final MenuService menuService;
    private final RoleService roleService;
    private final DictItemService dictItemService;
    private final ProjectProperties projectProperties;

    @Operation(summary = "查询部门树", description = "查询树结构的部门列表")
    @GetMapping("/tree/dept")
    public R<List<Tree<Long>>> listDeptTree(DeptQuery query, SortQuery sortQuery) {
        List<Tree<Long>> treeList = deptService.tree(query, sortQuery, true);
        return R.ok(treeList);
    }

    @Operation(summary = "查询菜单树", description = "查询树结构的菜单列表")
    @GetMapping("/tree/menu")
    public R<List<Tree<Long>>> listMenuTree(MenuQuery query, SortQuery sortQuery) {
        List<Tree<Long>> treeList = menuService.tree(query, sortQuery, true);
        return R.ok(treeList);
    }

    @Operation(summary = "查询角色字典", description = "查询角色字典列表")
    @GetMapping("/dict/role")
    public R<List<LabelValueVO<Long>>> listRoleDict(RoleQuery query, SortQuery sortQuery) {
        List<RoleVO> list = roleService.list(query, sortQuery);
        List<LabelValueVO<Long>> labelValueVOList = roleService.buildDict(list);
        return R.ok(labelValueVOList);
    }

    @Operation(summary = "查询字典", description = "查询字典列表")
    @Parameter(name = "code", description = "字典编码", example = "announcement_type", in = ParameterIn.PATH)
    @GetMapping("/dict/{code}")
    @Cacheable(key = "#code", cacheNames = CacheConsts.DICT_KEY_PREFIX)
    public R<List<LabelValueVO>> listDict(@PathVariable String code) {
        Optional<Class<?>> enumClass = this.getEnumClassByName(code);
        return enumClass.map(this::listEnumDict).orElseGet(() -> R.ok(dictItemService.listByDictCode(code)));
    }

    /**
     * 根据枚举类名查询
     *
     * @param enumClassName
     *            枚举类名
     * @return 枚举类型
     */
    private Optional<Class<?>> getEnumClassByName(String enumClassName) {
        Set<Class<?>> classSet = ClassUtil.scanPackageBySuper(projectProperties.getBasePackage(), BaseEnum.class);
        return classSet.stream()
            .filter(
                c -> StrUtil.equalsAnyIgnoreCase(c.getSimpleName(), enumClassName, StrUtil.toCamelCase(enumClassName)))
            .findFirst();
    }

    /**
     * 查询枚举字典
     * 
     * @param enumClass
     *            枚举类型
     * @return 枚举字典
     */
    private R<List<LabelValueVO>> listEnumDict(Class<?> enumClass) {
        Object[] enumConstants = enumClass.getEnumConstants();
        List<LabelValueVO> labelValueList = Arrays.stream(enumConstants).map(e -> {
            BaseEnum<Integer> baseEnum = (BaseEnum<Integer>)e;
            return new LabelValueVO<>(baseEnum.getDescription(), baseEnum.getValue(), baseEnum.getColor());
        }).collect(Collectors.toList());
        return R.ok(labelValueList);
    }
}
