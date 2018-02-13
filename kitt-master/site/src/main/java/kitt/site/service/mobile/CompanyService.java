package kitt.site.service.mobile;

import kitt.core.domain.Company;
import kitt.core.persistence.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xiangyang on 15-5-20.
 */
@Service
@Transactional(readOnly = true)
public class CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    public Company loadByUserId(int userId) {
        Company company=companyMapper.getCompanyByUserid(userId);
        return company;
    }
}
