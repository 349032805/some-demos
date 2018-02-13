package kitt.core.bl;

import kitt.core.exception.SQLExcutionErrorException;
import kitt.core.entity.BPotentialCompany;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuxinjie on 16/3/3.
 */
@Service
public class CoreCompanyService {

    /**
     * 保存潜在公司信息
     */
    @Transactional
    public int addPotentialCompanyMethod(BPotentialCompany company) throws SQLExcutionErrorException {
        /*if (companyMapper.addPotentialCompanyMethod(company) != 1) {
            throw new SQLExcutionErrorException();
        }
        return potentialCompany.getId();
        */
        return 1;
    }


    /**
     * 更改潜在公司信息
     */
    @Transactional
    public boolean updatePotentialCompanyMethod(BPotentialCompany company) {
        //return companyMapper.updatePotentialCompanyMethod(potentialCompany) == 1;
        return true;
    }
}
