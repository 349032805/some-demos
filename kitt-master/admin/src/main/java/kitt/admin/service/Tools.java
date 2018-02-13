package kitt.admin.service;

import kitt.core.domain.Areaport;
import kitt.core.persistence.AreaportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 4/22/15.
 */
@Service
public class Tools {
    @Autowired
    private AreaportMapper areaportMapper;

    //查询省份
    public List<Areaport> getMallProvinces(Integer id){
        if(id == null || id == 0){
            return new ArrayList<>();
        } else {
            return areaportMapper.getProcvincesOrPortsByParentid(id);
        }
    }

    //查询港口
    public List<Areaport> getMallPorts(Integer id){
        List<Areaport> deliveryplace = new ArrayList<>();
        if(id != null && id != 0) {
            deliveryplace.addAll(areaportMapper.getProcvincesOrPortsByParentid(id));
        }
        deliveryplace.add(new Areaport(-1, "其它"));
        return deliveryplace;
    }

    public List<Areaport> getMallPorts1(Integer id,int portId){
        List<Areaport> deliveryplace = new ArrayList<>();
        if(id != null && id != 0) {
            deliveryplace.addAll(areaportMapper.getProcvincesOrPortsByParentid1(id,portId));
        }
        deliveryplace.add(new Areaport(-1, "其它"));
        return deliveryplace;
    }

}
