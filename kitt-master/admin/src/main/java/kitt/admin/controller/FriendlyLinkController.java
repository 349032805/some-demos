package kitt.admin.controller;

import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Session;
import kitt.core.bl.CustomerService;
import kitt.core.domain.FriendlyLink;
import kitt.core.persistence.FriendlyLinkMapper;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuxinjie on 15/12/16.
 */
@RestController
@RequestMapping("/customer/friendlylink")
public class FriendlyLinkController {
    @Autowired
    private FriendlyLinkMapper friendlyLinkMapper;
    @Autowired
    private Session session;
    @Autowired
    private CustomerService customerService;

    /**
     * 友情链接list
     * @param content           搜索框里内容
     * @param page              页码
     */
    @RequestMapping("/list")
    public Object getAllFriendlyLinkList(@RequestParam(value = "searchcontent", required = false, defaultValue = "")String content,
                                         @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return new Object(){
            public Pager<FriendlyLink> friendlyLinkList = friendlyLinkMapper.pageFriendlyLink(Where.$like$(content), page, 10);
            public String searchcontent = content;
        };
    }

    /**
     * 友情链接详细页面
     * @param id                  friendlylink  id
     * @return
     */
    @RequestMapping("/detail")
    public Object doGetPartnerDetail(@RequestParam(value = "id", required = false, defaultValue = "0")int id){
        return friendlyLinkMapper.getFriendlyLinkById(id);
    }

    /**
     * 添加或者更改友情链接
     * @param friendlyLink        友情链接对象
     * @return
     */
    @RequestMapping("/addupdate")
    @Transactional
    public Object doAddUpdatePartner(FriendlyLink friendlyLink){
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        if(!checkParnterRepeat(friendlyLink)){
            map.put("error", "已经存在此友情链接！");
        } else {
            if (!friendlyLink.getUrl().startsWith("http://")) friendlyLink.setUrl("http://" + friendlyLink.getUrl());
            if (friendlyLink.getSequence() == 0) friendlyLink.setSequence(300);
            friendlyLink.setLasteditmanid(session.getAdmin().getId());
            friendlyLink.setLasteditmanusername(session.getAdmin().getUsername());
            if (friendlyLink.getId() == 0) {
                if (!customerService.doAddFriendlyLinkMethod(friendlyLink)) {
                    throw new BusinessException("添加失败,请联系技术人员!");
                }
            } else {
                if (friendlyLinkMapper.getFriendlyLinkById(friendlyLink.getId()) == null) throw new NotFoundException();
                if (!customerService.doUpdateFriendlyLinkMethod(friendlyLink)){
                    throw new BusinessException("更改失败,请联系技术人员!");
                }
            }
            success = true;
        }
        map.put("success", success);
        return map;
    }

    public boolean checkParnterRepeat(FriendlyLink friendlyLink){
        if(friendlyLinkMapper.getFriendlyLinkByCompanyName(friendlyLink.getCompanyname()) == null) return true;
        else if(friendlyLink.getId() != 0 && friendlyLink.getId() == friendlyLinkMapper.getFriendlyLinkByCompanyName(friendlyLink.getCompanyname()).getId()) return true;
        else return false;
    }

    /**
     * 更改合作伙伴顺序
     * @param id                  friendlylink  id
     * @param sequence            friendlylink  sequence
     * @return
     */
    @RequestMapping("/changeSequence")
    public boolean doChangePartnerSequence(@RequestParam(value = "id", required = true)int id,
                                           @RequestParam(value = "sequence", required = true)int sequence){
        if(friendlyLinkMapper.getFriendlyLinkById(id) == null) throw new NotFoundException();
        return customerService.doChangeFriendlyLinkSequenceMethod(sequence, id);
    }

    /**
     * 设置或者取消友情链接在前台显示
     * @param id                  friendlylink  id
     * @return
     */
    @RequestMapping("/setCancelShow")
    public boolean doSetCancelPartnerShow(@RequestParam(value = "id", required = true)int id){
        if(friendlyLinkMapper.getFriendlyLinkById(id) == null) throw new NotFoundException();
        return customerService.doSetCanceleFriendlyLinkShowMethod(id);
    }


    @RequestMapping("/delete")
    public boolean doDeletePartner(@RequestParam(value = "id", required = true)int id){
        if(friendlyLinkMapper.getFriendlyLinkById(id) == null) throw new NotFoundException();
        return customerService.doDeleteFriendlyLinkMethod(id);
    }


}
