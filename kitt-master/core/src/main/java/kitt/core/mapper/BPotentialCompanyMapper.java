package kitt.core.mapper;

import kitt.core.entity.BPotentialCompany;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by liuxinjie on 16/2/26.
 */
public interface BPotentialCompanyMapper {

    /**
     * 通过 id 查询潜在公司信息
     */
    @Select("select * from bpotentialcompanies where id=#{id}")
    BPotentialCompany getBPotentialCompanyById(int id);

    /**
     * 添加潜在公司信息
     */
    @Insert(" insert into bpotentialcompanies(companyName, companySimpleName, businessOwner, userPhone, traderId," +
            " traderName, traderPhone, officeProvinceCode, officeProvince, officeCityCode, officeCity, officeCountryCode," +
            " officeCountry, officeDetailAddress, registerProvinceCode, registerProvince, registerCityCode, registerCity," +
            " registerCountryCode, registerCountry, registerDetailAddress, officePhone, openingBank, legalPersonName, " +
            " account, identificationNumword, fax, zipCode, enterpriseTypeNumber, enterpriseTypeName, enterpriseNatureNumber, " +
            " enterpriseNatureName, planOutput, actualOutput, annualSales, annualCoalConsum, annualPurchase, installCapacity, " +
            " annual, industry, nickName, wechat, qq, mail, remark, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " +
            " values (#{companyName}, #{companySimpleName}, " +
            " #{businessOwner}, #{userPhone}, #{traderId}, #{traderName}, #{traderPhone}, #{officeProvinceCode}, " +
            " #{officeProvince}, #{officeCityCode}, #{officeCity}, #{officeCountryCode}, #{officeCountry}, " +
            " #{officeDetailAddress}, #{registerProvinceCode}, #{registerProvince}, #{registerCityCode}, #{registerCity}," +
            " #{registerCountryCode}, #{registerCountry}, #{registerDetailAddress}, #{officePhone}, #{openingBank}," +
            " #{legalPersonName}, #{account}, #{identificationNumword}, #{fax}, #{zipCode}, #{enterpriseTypeNumber}, " +
            " #{enterpriseTypeName}, #{enterpriseNatureNumber},  #{enterpriseNatureName}, #{planOutput}, #{actualOutput}," +
            " #{annualSales}, #{annualCoalConsum}, #{annualPurchase}, #{installCapacity}, " +
            " #{annual}, #{industry}, #{nickName}, #{wechat}, #{qq}, #{mail}, #{remark}, #{createdBy}, now(), " +
            " #{lastModifiedBy}, now())")
    @Options(useGeneratedKeys = true)
    int addPotentialCompanyMethod(BPotentialCompany potentialCompany);

    /**
     * 修改潜在公司信息
     */
    @Update(" update potentialcompanies set companyName=#{companyName}, companySimpleName=#{companySimpleName}, " +
            " businessOwner=#{businessOwner}, userPhone=#{userPhone}, traderId=#{traderId}, traderName=#{traderName}," +
            " traderPhone=#{traderPhone}, officeProvinceCode=#{officeProvinceCode}, officeProvince=#{officeProvince}," +
            " officeCityCode=#{officeCityCode}, officeCity=#{officeCity}, officeCountryCode=#{officeCountryCode}, " +
            " officeCountry=#{officeCountry}, officeDetailAddress=#{officeDetailAddress}," +
            " registerProvinceCode=#{registerProvinceCode}, registerProvince=#{registerProvince}," +
            " registerCityCode=#{registerCityCode}, registerCity=#{registerCity}, registerCountryCode=#{registerCountryCode}," +
            " registerCountry=#{registerCountry}, registerDetailAddress=#{registerDetailAddress}, officePhone=#{officePhone}, " +
            " openingBank=#{openingBank}, legalPersonName=#{legalPersonName}, account=#{account}," +
            " identificationNumword=#{identificationNumword}, fax=#{fax}, zipCode=#{zipCode}, " +
            " enterpriseTypeNumber=#{enterpriseTypeNumber}, enterpriseTypeName=#{enterpriseTypeName}, " +
            " enterpriseNatureNumber=#{enterpriseNatureNumber}, enterpriseNatureName=#{enterpriseNatureName}, " +
            " planOutput=#{planOutput}, actualOutput=#{actualOutput}, annualSales=#{annualSales}, " +
            " annualCoalConsum=#{annualCoalConsum}, annualPurchase=#{annualPurchase}, installCapacity=#{installCapacity}, " +
            " annual=#{annual}, industry=#{industry}, nickName=#{nickName}, wechat=#{wechat}, qq=#{qq}, mail=#{mail}, " +
            " remark=#{remark}, lastModifiedBy=#{lastModifiedBy}, lastModifiedDate=now() where id=#{id}")
    int updatePotentialCompanyMethod(BPotentialCompany potentialCompany);

}
