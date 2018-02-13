package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.Session;
import kitt.core.bl.DataService;
import kitt.core.domain.*;
import kitt.core.persistence.PricePortMapper;
import kitt.core.service.ReadExcel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuxinjie on 16/1/21.
 */
@RestController
@RequestMapping("/data/pricePort")
public class PricePortController {
    @Autowired
    private PricePortMapper pricePortMapper;
    @Autowired
    private ReadExcel readExcel;
    @Autowired
    private Auth auth;
    @Autowired
    private Session session;
    @Autowired
    private DataService dataService;

    /**
     * 港口价格列表
     * @param page
     */
    @RequestMapping("/list")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object getPricePortList(@RequestParam(value = "page", required = false, defaultValue = "1")int page) {
        return pricePortMapper.pagePricePort(page, 10);
    }

    /**
     * 港口价格详细页面
     * @param id                 PricePort 表 Id
     */
    @RequestMapping("/detail")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object getPricePortDetail(@RequestParam(value = "id", required = true)int id,
                                     @RequestParam(value = "page", required = false, defaultValue = "1")int page) {
        PricePort pricePort = pricePortMapper.getPricePortById(id);
        if (pricePort == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pricePort", pricePort);
        map.put("pricePortValueList", pricePortMapper.pagePricePortValueListByPricePortId(id, page, 10));
        return map;
    }

    /**
     * 从 Excel 中导入数据
     */
    @RequestMapping("/importData")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doImportExcelData(@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
        MapObject map = readExcel.doReadPricePortExcel(auth.uploadPicMethod(EnumFileType.File_DataCenter.toString(), EnumFileType.EXCEL.toString(), file, null, null).get("filePath").toString(), session.getAdmin());
        if (!map.isSuccess() || (!StringUtils.isBlank(map.getError()) && map.getError().length() != 0)) {
            throw new BusinessException(map.getError());
        }
        return true;
    }

    /**
     * 设置 / 取消 港口价格在前台显示
     */
    @RequestMapping("/setCancelShow")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doSetCancelShow(@RequestParam(value = "id", required = true)int id) {
        PricePort pricePort = pricePortMapper.getPricePortById(id);
        if (pricePort == null) throw new NotFoundException();
        MapObject map = new MapObject();
        if (!pricePort.isshow() && pricePortMapper.getPricePortValueCountByPortId(pricePort.getId()) == 0) {
            map.setSuccess(false);
            map.setError("此条信息没有价格数据,不能在前台显示!");
            return map;
        }
        map.setSuccess(dataService.doSetCancelShowPricePort(id));
        return map;
    }

    /**
     * 改变港口价格在前台显示顺序
     */
    @RequestMapping("/changeSequence")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doChangePricePortSequence(@RequestParam(value = "id", required = true)int id,
                                             @RequestParam(value = "sequence", required = true)int sequence) {
        PricePort pricePort = pricePortMapper.getPricePortById(id);
        if (pricePort == null) throw new NotFoundException();
        return dataService.doChangePricePortSequence(id, sequence);
    }

    /**
     * 删除港口价格
     */
    @RequestMapping("/deletePricePort")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeletePricePort(@RequestParam(value = "id", required = true)int id) {
        PricePort pricePort = pricePortMapper.getPricePortById(id);
        if (pricePort == null) throw new NotFoundException();
        return dataService.doDeletePricePort(id);
    }

    /**
     * 删除港口价格一条数值
     */
    @RequestMapping("/deleteValue")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeletePricePortValue(@RequestParam(value = "id", required = true)int id) {
        PricePortValue pricePortValue = pricePortMapper.getPricePortValueById(id);
        if (pricePortValue == null) throw new NotFoundException();
        return dataService.doDeletePricePortValue(id);
    }

}
