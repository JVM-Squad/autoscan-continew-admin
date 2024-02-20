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

package top.charles7c.continew.admin.monitor.model.query;

import cn.hutool.core.date.DatePattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import top.charles7c.continew.starter.data.core.annotation.Query;
import top.charles7c.continew.starter.data.core.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 登录日志查询条件
 *
 * @author Charles7c
 * @since 2023/1/16 23:25
 */
@Data
@Schema(description = "登录日志查询条件")
public class LoginLogQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录状态
     */
    @Schema(description = "登录状态（1：成功；2：失败）", example = "1")
    private Integer status;

    /**
     * 登录时间
     */
    @Schema(description = "登录时间", example = "2023-08-08 00:00:00,2023-08-08 23:59:59")
    @Query(type = QueryType.BETWEEN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private List<Date> createTime;
}
