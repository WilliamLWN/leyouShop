package com.leyou.item.dto;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import lombok.Data;

import java.util.List;

@Data
public class SpecGroupDTO extends SpecGroup {
    private List<SpecParam> params;//组内参数
}
