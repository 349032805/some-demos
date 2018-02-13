package kitt.admin.service;

import kitt.core.domain.Focusimage;
import kitt.core.persistence.FocusImageMapper;
import kitt.core.util.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lich on 16/2/16.
 */
@Service
public class FocusImageService {
    @Autowired
    private FocusImageMapper focusImageMapper;


    public Pager<Focusimage> getFocusImage(int page, int pageSize) {
        return focusImageMapper.getFocusImage(page,pageSize);
    }

    public boolean doDeleteFocusImage(int id,int ftype) {
        return focusImageMapper.doDeleteFocusImage(id,ftype);
    }


    public boolean findByPicTitle(String pictitle){
        List<Focusimage> list=focusImageMapper.findByTitle(pictitle);
        if(list!=null&&list.size()>0){
            return false;
        }else{
            return true;
        }
    }
    //添加焦点图片
    public int addFocusPic(Focusimage focusimage){
        return focusImageMapper.addFocusPic(focusimage);
    }

   //修改焦点图片
    public int updateFocusPic(Focusimage focusimage){
        return focusImageMapper.updateFocusPic(focusimage);
    }



}
