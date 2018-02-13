package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.PartnerService;
import kitt.admin.service.Session;
import kitt.core.domain.AuthenticationRole;
import kitt.core.domain.EnumFileType;
import kitt.core.domain.Partner;
import kitt.core.persistence.PartnerMapper;
import kitt.core.service.FileStore;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liuxinjie on 15/9/19.
 * 合作伙伴
 */
@RestController
@RequestMapping("/customer/partner")
public class PartnerController {
    @Autowired
    private PartnerMapper partnerMapper;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private Session session;
    @Autowired
    private Auth auth;

    /**
     * 合作伙伴列表
     * @param page
     * @return
     */
    @RequestMapping("/list")
    public Object doGetPartnerList(@RequestParam(value = "searchcontent", required = false, defaultValue = "")String content,
                                   @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return new Object(){
            public Pager<Partner> partnerList = partnerMapper.pagePartner(Where.$like$(content), page, 10);
            public String searchcontent = content;
        };
    }

    /**
     * 添加或者更改合作伙伴
     * @param partner
     * @return
     */
    @RequestMapping("/addupdate")
    public boolean doAddUpdatePartner(Partner partner){
        if(!checkParnterRepeat(partner)){
            throw new BusinessException("已经存在此合作伙伴！");
        }
        if(!partner.getLinkurl().startsWith("http://")) partner.setLinkurl("http://" + partner.getLinkurl());
        if(partner.getSequence() == 0) partner.setSequence(100);
        partner.setLasteditman("工号：" + session.getAdmin().getJobnum() + ", 姓名：" + session.getAdmin().getName());
        if(partner.getId() == 0){
            return partnerService.doAddPartnerMethod(partner);
        } else{
            if(partnerMapper.getPartnerById(partner.getId()) == null) throw new NotFoundException();
            return partnerService.doUpdatePartnerMethod(partner);
        }
    }

    /**
     * 合作伙伴详细页面
     * @param id                  partner  id
     * @return
     */
    @RequestMapping("/detail")
    public Object doGetPartnerDetail(@RequestParam(value = "id", required = false, defaultValue = "0")int id){
        return partnerMapper.getPartnerById(id);
    }

    /**
     * 更改合作伙伴顺序
     * @param id                  partner  id
     * @param sequence            partner  sequence
     * @return
     */
    @RequestMapping("/changeSequence")
    public boolean doChangePartnerSequence(@RequestParam(value = "id", required = true)int id,
                                           @RequestParam(value = "sequence", required = true)int sequence){
        if(partnerMapper.getPartnerById(id) == null) throw new NotFoundException();
        return partnerService.doChangePartnerSequenceMethod(sequence, id);
    }

    /**
     * 设置或者取消合作伙伴在前台显示
     * @param id                  partner  id
     * @return
     */
    @RequestMapping("/setCancelPartnerShow")
    public boolean doSetCancelPartnerShow(@RequestParam(value = "id", required = true)int id){
        if(partnerMapper.getPartnerById(id) == null) throw new NotFoundException();
        return partnerService.doSetCancelePartnerShowMethod(id);
    }


    @RequestMapping("/delete")
    public boolean doDeletePartner(@RequestParam(value = "id", required = true)int id){
        if(partnerMapper.getPartnerById(id) == null) throw new NotFoundException();
        return partnerService.doDeletePartnerMethod(id);
    }

    /**
     * 添加合作伙伴logo图片
     * @param file
     */
    @RequestMapping("/addLogoPic")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object addLogoPic(@RequestParam("file") MultipartFile file) throws Exception {
        return auth.uploadPicMethod(EnumFileType.File_PartnerLogo.toString(), EnumFileType.IMG.toString(), file, null, null);
    }

    public boolean checkParnterRepeat(Partner partner){
        if(partnerMapper.getPartnerByCompanyName(partner.getCompanyname()) == null) return true;
        else if(partner.getId() != 0 && partner.getId() == partnerMapper.getPartnerByCompanyName(partner.getCompanyname()).getId()) return true;
        else return false;
    }



}
