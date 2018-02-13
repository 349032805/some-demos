package kitt.admin.controller;

import kitt.core.domain.District;
import kitt.core.persistence.RegionYMMapper;
import kitt.core.util.text.HanYuToPinYin;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.MapSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiangyang on 16/2/4.
 */
@RestController
public class RegionYMController {
    @Autowired
    private RegionYMMapper regionYMMapper;
    @Autowired
    private HanYuToPinYin hanYuToPinYin;

    @RequestMapping(value = "/regionYm", method = RequestMethod.GET)
    public Object loadAllProvince() {
        return regionYMMapper.getAllProvinces();
    }

    @RequestMapping(value = "/regionYm/{code}", method = RequestMethod.GET)
    public Object loadAllProvince(@PathVariable("code") String code) {
        return regionYMMapper.loadDistrictByParent(code);
    }

    @RequestMapping(value = "/addRegionYm", method = RequestMethod.POST)
    public Object addRegion(@RequestBody District district) throws BadHanyuPinyinOutputFormatCombination {
        Map<String,Object> maps = new HashMap<>();
        if(regionYMMapper.findNameExists(district)>0){
                maps.put("flag","exists");
                return maps;
        }
        district.setMold(hanYuToPinYin.HanYuToPinYinMethod(district.getName().substring(0,1),false).toCharArray()[0]);
        if(StringUtils.isEmpty(district.getCode())){
            if(district.getLevel()==1){
                String maxCode=regionYMMapper.loadMaxCode();
                if(maxCode==null){
                    district.setCode(String.valueOf("1"));
                }else{
                    district.setCode(String.valueOf(Long.valueOf(maxCode)+1));
                }
            }else{
                String maxCode=regionYMMapper.loadMaxCodeInParentId(district.getParent());
                if(maxCode==null){
                    district.setCode(district.getParent()+"100");
                }else{
                    district.setCode(String.valueOf(Long.valueOf(maxCode)+1));
                }
            }
            regionYMMapper.addRegion(district);
        }else{
            regionYMMapper.updateRegion(district);
        }
        maps.put("flag","success");
        return maps;
    }

    @RequestMapping(value = "/deleteRegionYm/{code}", method = RequestMethod.GET)
    public Object deleteRegion(@PathVariable("code") String code) {
         regionYMMapper.deleteRegion(code);
         return true;
    }
}
