package kitt.admin.service;

import kitt.admin.basic.exception.BusinessException;
import kitt.core.domain.AboutUs;
import kitt.core.persistence.AboutUsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuxinjie on 15/9/28.
 */
@Service
public class AboutUsService {
    @Autowired
    private AboutUsMapper aboutUsMapper;

    /**
     * 添加方法
     * @param aboutUs
     * @return
     */
    @Transactional
    public boolean doAddAboutUsMethod(AboutUs aboutUs){
        if(aboutUsMapper.addAboutUs(aboutUs) == 1){
            return true;
        }
        throw new BusinessException("添加出错，请联系技术人员！");
    }

    /**
     * 修改方法
     * @param aboutUs
     * @return
     */
    @Transactional
    public boolean doUpdateAboutUsMethod(AboutUs aboutUs){
        if(aboutUsMapper.modifyAboutUs(aboutUs)){
            return true;
        }
        throw new BusinessException("修改出错，请联系技术人员！");
    }

}
