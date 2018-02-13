package kitt.admin.service;

import kitt.admin.basic.exception.BusinessException;
import kitt.core.domain.Partner;
import kitt.core.persistence.PartnerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuxinjie on 15/9/20.
 */
@Service
public class PartnerService {
    @Autowired
    private PartnerMapper partnerMapper;
    @Autowired
    private FileService fileService;


    /**
     * 添加合作伙伴
     * @param partner                    partner对象
     * @return
     */
    @Transactional
    public boolean doAddPartnerMethod(Partner partner) {
        if(partnerMapper.doAddPartnerMethod(partner) == 1) return true;
        throw new BusinessException("添加合作伙伴出错，请联系技术人员！");
    }

    /**
     * 修改合作伙伴
     * @param partner                    partner对象
     * @return
     */
    @Transactional
    public boolean doUpdatePartnerMethod(Partner partner){
        if(partnerMapper.doUpdatePartnerMethod(partner)) return true;
        throw new BusinessException("修改合作伙伴出错，请联系技术人员！");
    }

    /**
     * 更改合作伙伴顺序
     * @param sequence
     * @param id
     * @return
     */
    @Transactional
    public boolean doChangePartnerSequenceMethod(int sequence, int id) {
        if(partnerMapper.doChangePartnerSequenceMethod(sequence, id)) return true;
        throw new BusinessException("更改顺序出错，请联系技术人员！");
    }

    /**
     * 设置或者取消合作伙伴在前台显示
     * @param id                    partner  id
     * @return
     */
    @Transactional
    public boolean doSetCancelePartnerShowMethod(int id){
        if(partnerMapper.doSetCancelPartnerShowMethod(id) == 1) return true;
        throw new BusinessException("设置在前台显示出错，请联系技术人员！");
    }

    /**
     * 删除合作伙伴
     * @param id                   partner id
     * @return
     */
    public boolean doDeletePartnerMethod(int id) {
        if(partnerMapper.doDeletePartnerMethod(id) == 1){
            return true;
        }
        throw new BusinessException("删除合作伙伴出错，请联系技术人员！");
    }
}
