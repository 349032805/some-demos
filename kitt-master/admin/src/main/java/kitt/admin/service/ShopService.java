package kitt.admin.service;

import kitt.admin.basic.exception.BusinessException;
import kitt.core.domain.EnumSellInfo;
import kitt.core.domain.Shop;
import kitt.core.persistence.ShopMapper;
import kitt.core.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/9/9.
 */
@Service
public class ShopService {
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private FileService fileService;

    /**
     * 保存店铺方法（保存草稿）
     * @param shop
     * @return
     */
    @Transactional
    public boolean doSaveShopMethod(Shop shop){
        shop.setCreatetime(LocalDateTime.now());
        if(shopMapper.doSaveShop(shop) == 1) return true;
        throw new BusinessException("保存店铺草稿失败，请联系技术人员！");
    }

    /**
     * Update店铺方法（保存草稿）
     * @param shop
     * @return
     */
    public boolean doUpdateSaveShopMethod(Shop shop) {
        if(shopMapper.doUpdateSaveShop(shop) == 1) return true;
        throw new BusinessException("保存店铺草稿失败，请联系技术人员！");
    }

    /**
     * 添加店铺方法（提交）
     * @param shop
     * @return
     */
    @Transactional
    public boolean doAddShopMethod(Shop shop){
        shop.setCreatetime(LocalDateTime.now());
        shop.setStatus(2);
        if(shopMapper.doAddShop(shop) == 1) return true;
        throw new BusinessException("添加店铺失败，请联系技术人员！");
    }

    /**
     * Update店铺方法（提交）
     * @param shop
     * @return
     */
    @Transactional
    public boolean doUpdateAddShopMethod(Shop shop){
        shop.setStatus(2);
        shop.setShopid(shopMapper.getShopById(shop.getId()).getShopid());
        if(shopMapper.doUpdateAddShop(shop) == 1) return true;
        throw new BusinessException("添加店铺失败，请联系技术人员！");
    }

    /**
     * 上传店铺图片方法
     * @param file
     * @return
     * @throws FileStore.UnsupportedContentType
     * @throws IOException
     */
    @Transactional
    public Object doUploadShopPicMethod(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        return new Object() {
            public String filePath = fileService.doUploadShopPicture(file);
            public boolean success = true;
        };
    }

    /**
     * 上下，下架店铺
     * @param shop            Shop对象
     * @return
     */
    @Transactional
    public boolean doUpDownProductMethod(Shop shop) {
        int status = shop.getStatus() == 1 ? 2 : 1;
        if(status == 1){
            shopMapper.updateShopSellInfoStatus(EnumSellInfo.VerifyPass, EnumSellInfo.WaitShopRun, shop.getId());
            shopMapper.setShopSellInfoOutOfDate(shop.getId());
        } else if(status == 2){
            shopMapper.updateShopSellInfoStatus(EnumSellInfo.WaitShopRun, EnumSellInfo.VerifyPass, shop.getId());
        }
        if(shopMapper.doUpDownShopMethod(shop.getId(), status) == 1){
            return true;
        }
        throw new BusinessException("系统出错，请刷新页面重试！");
    }

}
