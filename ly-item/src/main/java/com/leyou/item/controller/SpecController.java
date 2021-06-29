package com.leyou.item.controller;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 规格参数
 */
@RestController
public class SpecController {
    @Autowired
    private SpecService specService;

    /**
     * 查询规格组
     */
    @GetMapping("/spec/params")
    public ResponseEntity<List<SpecParam>> findSpecParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value="cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        List<SpecParam> specParams = specService.findSpecParams(gid,cid,searching);
        return ResponseEntity.ok(specParams);
    }

    /**
     * 查询规格参数
     */
    @GetMapping("/spec/groups/of/category")
    public ResponseEntity<List<SpecGroup>> findSpecGroups(
            @RequestParam(value = "id") Long cid
    ){
        List<SpecGroup> specGroup = specService.findSpecGroups(cid);
        return ResponseEntity.ok(specGroup);
    }

    /**
     * 根据分类ID查询规格组（包含组内参数）
     */
    @GetMapping("/spec/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecGroupDTOByCid(@RequestParam("id") Long id){
        List<SpecGroupDTO> specGroupDTOS = specService.findSpecGroupDTOByCid(id);
        return ResponseEntity.ok(specGroupDTOS);
    }
}
