package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.JsonController;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.FileService;
import kitt.core.bl.ManageService;
import kitt.core.domain.*;
import kitt.core.persistence.ChartMapper;
import kitt.core.persistence.DataMarketMapper;
import kitt.core.persistence.IndexBannerMapper;
import kitt.ext.mybatis.Where;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Created by fanjun on 15-2-7.
 */
@RestController
public class ChartController extends JsonController {
    @Autowired
    private ChartMapper chartMapper;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected IndexBannerMapper indexBannerMapper;
    @Autowired
    private DataMarketMapper dataMarketMapper;
    @Autowired
    private Auth auth;
    @Autowired
    private ManageService manageService;

    @RequestMapping("/chart/list")
    public Object list(@RequestParam(value = "type", required = true) String type,
                       @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        Map map = new HashMap();
        map.put("chart", chartMapper.pageAllCHartsByType(type, page, 10));
        return map;
    }

    //保存图表数据
    @RequestMapping(value = "/chart/saveChart", method = RequestMethod.POST)
    public Object saveChart(Chart chart){
        chart.setName(chartMapper.getNameByType(chart.getType()));
        chartMapper.addChart(chart);
        return true;
    }

    //根据id删除记录
    @RequestMapping("/chart/deleteOne")
    public boolean deleteOne(Integer id) {
        chartMapper.deleteOne(id);
        return true;
    }

    //验证交易日期唯一性
    @RequestMapping("/chart/checkTradedateSole")
    public Object checkTradedateSole(String tradedate,String type) {
        int i = chartMapper.checkTradedateSole(tradedate,type);
        Map map = new HashMap<>();
        map.put("num",i);
        return map;
    }

    /**
     * 获取首页图片所有数据
     */
    @RequestMapping("/chart/getAllIndexBanners")
    public Object getAllIndexBanners() {
        return new Object(){
            public List<IndexBanner> webBannerList = indexBannerMapper.getAdminIndexBannnersWithLimit("web", 5);
            public List<IndexBanner> wechatBannerList = indexBannerMapper.getAdminIndexBannnersWithLimit("wechat", 5);
            public List<IndexBanner> advertBannerList = indexBannerMapper.getAdminIndexBannnersWithLimit("advert", 5);
        };
    }

    /**
     * 上传首页图片
     */
    @RequestMapping(value = "/banner/uploadBannerPic", method = RequestMethod.POST)
    public Object changeIndexPic(@RequestParam("file") MultipartFile file) throws Exception {
        return auth.uploadPicMethod(EnumFileType.File_IndexBanner.toString(), EnumFileType.IMG.toString(), file, null, null);
    }

    /**
     * 设置招标Banner图片path
     * @param id           Banner图片id indexbanners 表 id
     * @param path         Banner图片path indexbanners 表 path
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @RequestMapping("/banner/addBannerPicPath")
    public boolean doAddBannerPicPath(@RequestParam(value = "id", required = true)int id,
                                      @RequestParam(value = "path", required = true)String path){
        if (indexBannerMapper.getIndexBannerById(id) == null) throw new NotFoundException();
        return manageService.changeTenderCompanyPicture(id, path, session.getAdmin());
    }

    /**
     * 删除Banner图片path
     * @param id          Banner图片id  indindexbanners 表 id
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/banner/deleteBannerPicPath")
    public boolean doDeleteBannerPicPath(@RequestParam(value = "id", required = true)int id){
        if(indexBannerMapper.getIndexBannerById(id) == null) throw new NotFoundException();
        return manageService.changeTenderCompanyPicture(id, "", session.getAdmin());
    }

    /**
     * 设置,取消Banner图片在前台显示
     * @param id
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @RequestMapping("/banner/setCancelShowBannerPic")
    public boolean doSetCancelShowBannerPic(@RequestParam(value = "id", required = true)int id){
        if(indexBannerMapper.getIndexBannerById(id) == null) throw new NotFoundException();
        return manageService.setCancelShowBannerPic(id, session.getAdmin());
    }

    /**
     * 改变招标Banner图片的顺序
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @RequestMapping("/banner/changeSequence")
    public boolean doChangeBannerSequence(@RequestParam(value = "id", required = true)int id,
                                          @RequestParam(value = "sequence", required = true)int sequence) {
        if(indexBannerMapper.getIndexBannerById(id) == null) throw new NotFoundException();
        return manageService.changeBannerSequence(id, sequence, session.getAdmin());
    }

    /**
     * 更改图片的链接
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @RequestMapping("/banner/changeBannerURL")
    public boolean doChangeBadderLinkURL(@RequestParam(value = "id", required = true)int id,
                                      @RequestParam(value = "linkurl", required = true)String linkurl) {
        if (indexBannerMapper.getIndexBannerById(id) == null) throw new NotFoundException();
        if (!StringUtils.isBlank(linkurl) && !linkurl.startsWith("http://")) linkurl = "http://" + linkurl;
        return manageService.changeBannerURL(id, linkurl, session.getAdmin());
    }

    //获取最新的TC主力合约名称
    @RequestMapping("/chart/getNewTCname")
    public Object getNewTCname() {
        Chart chart =  chartMapper.getNewTC();
        Map map = new HashMap<>();
        if(chart != null) {
            map.put("TCname", chart.getType());
        }else{
            map.put("TCname","TC");
        }
        return map;
    }

    /**
     * lich --  易煤行情---start
     */

    //易煤行情数据列表
    @RequestMapping(value="/chart/market/list")
    public Object list(@RequestParam(value = "page", required = false, defaultValue = "1")int page,
                       @RequestParam(value = "status", required = false, defaultValue = "prodCoalPrice")String status) {
        int sequence=0;
        if(status.equals("prodCoalPrice")){
            sequence=1;
        }else{
            sequence=2;
        }
        Map map = new HashMap();
        map.put("dataMarket", dataMarketMapper.pageAllCHartsByType(sequence, page, 10));
        return map;
    }

    //获取数据类型
    @RequestMapping("/chart/market/getType")
    public Object getType() {
        List<DataBook> dataBookList=  dataMarketMapper.getType();
        Map map = new HashMap<>();
        map.put("dataBookList",dataBookList);
        return map;
    }

    //添加,修改产地价格, 港口价格数据
    @RequestMapping(value="/chart/market/saveData")
    public boolean addData(DataMarket dataMarket,String dId) {
        if(dId!=null&&!dId.equals("")){
            dataMarket.setId(Integer.parseInt(dId));
            return dataMarketMapper.updateData(dataMarket);
        }else{
            return dataMarketMapper.addData(dataMarket);
        }

    }

    /**
     * 改变顺序
     * @param id
     * @param sequence
     * @return
     */
    @RequestMapping("/chart/market/changeSequence")
    public boolean doChangeAdverSequence(@RequestParam(value = "id", required = true)int id,
                                         @RequestParam(value = "sequence", required = true)int sequence){
        return dataMarketMapper.doChangeAdverpicSequenceMethod(id, sequence);
    }


    // 根据name和date查询
    @RequestMapping(value="/chart/market/findMaket")
    public boolean findByNameAndDate(String name,String date,String type) {
        List<DataMarket> list=  dataMarketMapper.findByNameAndDate(name, date, Integer.parseInt(type));
        if(list!=null&&list.size()>0){
            return false;
        }
        return true;
    }

    //易煤行情添加数据输入指标名称时下拉
    @RequestMapping(value="/chart/market/findByName")
    public Object findByName(String name,String type) {
        if(name==null || name.equals("")) {
            return dataMarketMapper.findByName("%%", Integer.parseInt(type));
        } else {
            return dataMarketMapper.findByName(Where.$like$(name), Integer.parseInt(type));
        }
    }

    //删除产地价格,港口价格数据
    @RequestMapping(value="/chart/market/delete")
    public boolean deleteData(@RequestParam(value = "id")int id) {
        return dataMarketMapper.deleteData(id);
    }

    /**
     * 根据类型改变名称
     * @param type             类型
     * @param name             名称
     * @return                 success: true ? false, error: 错误信息
     */
    @RequestMapping(value = "/chart/changename")
    public Object changeChartName(@RequestParam(value = "type", required = true)String type,
                                  @RequestParam(value = "name", required = true)String name){
        if(chartMapper.getNameByType(type) != null && chartMapper.getNameByType(type).equals(name)){
            return new Object(){
                public boolean success = false;
                public String error = "新名称和原名称相同";
            };
        } else {
            return new Object(){
                public boolean success = chartMapper.changeNameByType(type, name) == 1;
            };
        }
    }


}
