package kitt.core.entity;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by xiangyang on 15-1-10.
 */
public class CoalBaseData {

    //低位热值  NCV NCV02
    @Range(min = 1,max = 7500,message = "低位热值必须是{min}-{max}之间的整数")
    public Integer NCV;
    @Range(min = 1,max = 7500,message = "低位热值必须是{min}-{max}之间的整数")
    public Integer NCV02;

    //空干基硫分 ADS ADS02
    @Digits(integer = 2,fraction =2,message = "空干基硫分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "空干基硫分不包括{value}")
    @DecimalMax(value = "10",message = "空干基硫分不能大于{value}")
    public BigDecimal ADS;
    @Digits(integer = 2,fraction =2,message = "空干基硫分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "空干基硫分不包括{value}")
    @DecimalMax(value = "10",message = "空干基硫分不能大于{value}")
    public BigDecimal ADS02;

    //收到基硫分 RS RS02
    @NotNull(message = "收到基硫份不能为空")
    @Digits(integer = 2,fraction =2,message = "收到基硫分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "收到基硫分不包括{value}")
    @DecimalMax(value = "10",inclusive = false,message = "收到基硫分不能大于{value}")
    public BigDecimal RS;
    @NotNull(message = "收到基硫份不能为空")
    @Digits(integer = 2,fraction =2,message = "收到基硫分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "收到基硫分不包括{value}")
    @DecimalMax(value = "10",inclusive = false,message = "收到基硫分不能大于{value}")
    public BigDecimal RS02;

    //全水分  TM TM02
    @NotNull(message = "全水分不能为空")
    @Digits(integer = 2,fraction =2,message = "全水分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "全水分不包括{value}")
    @DecimalMax(value = "50",message = "全水分不能大于{value}")
    public BigDecimal TM;
    @NotNull(message = "全水分不能为空")
    @Digits(integer = 2,fraction =2,message = "全水分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "全水分不包括{value}")
    @DecimalMax(value = "50",message = "全水分不能大于{value}")
    public BigDecimal TM02;

    //内水分  IM IM02
    @Digits(integer = 2,fraction =2,message = "内水分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "内水分不包括{value}")
    @DecimalMax(value = "50",message = "内水分不能大于{value}")
    public BigDecimal IM;
    @Digits(integer = 2,fraction =2,message = "内水分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "内水分不包括{value}")
    @DecimalMax(value = "50",message = "内水分不能大于{value}")
    public BigDecimal IM02;

    //空干基挥发分  ADV ADV02
    @NotNull(message = "空干基挥发份不能为空")
    @Digits(integer = 2,fraction =2,message = "空干基挥发分不能>{fraction}位小数")
    @DecimalMax(value = "50",message = "空干基挥发分不能大于{value}")
    @DecimalMin(value = "0",inclusive = false,message = "空干基挥发分不包括{value}")
    public BigDecimal ADV;
    @NotNull(message = "空干基挥发份不能为空")
    @Digits(integer = 2,fraction =2,message = "空干基挥发分不能>{fraction}位小数")
    @DecimalMax(value = "50",message = "空干基挥发分不能大于{value}")
    @DecimalMin(value = "0",inclusive = false,message = "空干基挥发分不包括{value}")
    public BigDecimal ADV02;

    //收到基挥发分
    @Digits(integer = 2,fraction =2,message = "收到基挥发分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "收到基挥发分不包括{value}")
    @DecimalMax(value = "50",message = "收到基挥发分不能大于{value}")
    public BigDecimal RV;
    @Digits(integer = 2,fraction =2,message = "收到基挥发分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "收到基挥发分不包括{value}")
    @DecimalMax(value = "50",message = "收到基挥发分不能大于{value}")
    public BigDecimal RV02;

    //灰熔点
    @Range(min = 900,max = 1600,message = "灰熔点必须是{min}-{max}之间的整数")
    public Integer AFT;

    //灰分  ASH ASH02
    @Digits(integer = 2,fraction =1,message = "灰分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "灰分不包括{value}")
    @DecimalMax(value = "50",message = "灰分不能大于{value}")
    public BigDecimal ASH;
    @Digits(integer = 2,fraction =1,message = "灰分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "灰分不包括{value}")
    @DecimalMax(value = "50",message = "灰分不能大于{value}")
    public BigDecimal ASH02;

    //哈氏可磨
    @Range(min = 1,max = 100,message = "哈氏可磨必须是1-100之间的整数")
    public Integer HGI;

    //G值
    @Range(min = 1,max = 100,message = "G值必须是{min}-{max}之间的整数")
    public Integer GV;
    @Range(min = 1,max = 100,message = "G值必须是{min}-{max}之间的整数")
    public Integer GV02;

    //Y值
    @Range(min = 1,max = 100,message = "Y值必须是{min}-{max}之间的整数")
    public Integer YV;
    @Range(min = 1,max = 100,message = "Y值必须是{min}-{max}之间的整数")
    public Integer YV02;

    //固定碳
    @Range(min = 1,max = 100,message = "固定碳必须是{min}-{max}之间的整数")
    public Integer FC;
    @Range(min = 1,max = 100,message = "固定碳必须是{min}-{max}之间的整数")
    public Integer FC02;

    //颗粒度
    public Integer PS;
    //颗粒度名字
    public String PSName;

    //焦渣特征
    public Integer CRC;
    public Integer CRC02;

    //粘结指数
    @Range(min = 1,max = 200,message = "粘结指数必须是{min}-{max}之间的整数")
    public Integer bondindex;
    @Range(min = 1,max = 200,message = "粘结指数必须是{min}-{max}之间的整数")
    public Integer bondindex02;

    @Override
    public String toString() {
        return "CoalBaseData{" +
                "NCV=" + NCV +
                ", NCV02=" + NCV02 +
                ", ADS=" + ADS +
                ", ADS02=" + ADS02 +
                ", RS=" + RS +
                ", RS02=" + RS02 +
                ", TM=" + TM +
                ", TM02=" + TM02 +
                ", IM=" + IM +
                ", IM02=" + IM02 +
                ", ADV=" + ADV +
                ", ADV02=" + ADV02 +
                ", RV=" + RV +
                ", RV02=" + RV02 +
                ", AFT=" + AFT +
                ", ASH=" + ASH +
                ", ASH02=" + ASH02 +
                ", HGI=" + HGI +
                ", GV=" + GV +
                ", GV02=" + GV02 +
                ", YV=" + YV +
                ", YV02=" + YV02 +
                ", FC=" + FC +
                ", FC02=" + FC02 +
                ", PS=" + PS +
                ", PSName='" + PSName + '\'' +
                '}';
    }

    public CoalBaseData(){}

    public CoalBaseData(Integer NCV, BigDecimal ADS, BigDecimal RS, BigDecimal TM, BigDecimal IM, BigDecimal ADV, BigDecimal RV, Integer AFT, BigDecimal ASH, Integer HGI) {
        this.NCV = NCV;
        this.ADS = ADS;
        this.RS = RS;
        this.TM = TM;
        this.IM = IM;
        this.ADV = ADV;
        this.RV = RV;
        this.AFT = AFT;
        this.ASH = ASH;
        this.HGI = HGI;
    }

    public CoalBaseData(Integer NCV, BigDecimal ADS, BigDecimal RS, BigDecimal TM, BigDecimal IM, BigDecimal ADV, BigDecimal RV, Integer AFT, BigDecimal ASH, Integer HGI, Integer GV, Integer YV, Integer FC, Integer PS, String PSName) {
        this.NCV = NCV;
        this.ADS = ADS;
        this.RS = RS;
        this.TM = TM;
        this.IM = IM;
        this.ADV = ADV;
        this.RV = RV;
        this.AFT = AFT;
        this.ASH = ASH;
        this.HGI = HGI;
        this.GV = GV;
        this.YV = YV;
        this.FC = FC;
        this.PS = PS;
        this.PSName = PSName;
    }

    public CoalBaseData(Integer NCV, Integer NCV02, BigDecimal ADS, BigDecimal ADS02, BigDecimal RS, BigDecimal RS02, BigDecimal TM, BigDecimal TM02, BigDecimal IM, BigDecimal IM02, BigDecimal ADV, BigDecimal ADV02, BigDecimal RV, BigDecimal RV02, Integer AFT, BigDecimal ASH, BigDecimal ASH02, Integer HGI, Integer GV, Integer GV02, Integer YV, Integer YV02, Integer FC, Integer FC02, Integer PS, String PSName) {
        this.NCV = NCV;
        this.NCV02 = NCV02;
        this.ADS = ADS;
        this.ADS02 = ADS02;
        this.RS = RS;
        this.RS02 = RS02;
        this.TM = TM;
        this.TM02 = TM02;
        this.IM = IM;
        this.IM02 = IM02;
        this.ADV = ADV;
        this.ADV02 = ADV02;
        this.RV = RV;
        this.RV02 = RV02;
        this.AFT = AFT;
        this.ASH = ASH;
        this.ASH02 = ASH02;
        this.HGI = HGI;
        this.GV = GV;
        this.GV02 = GV02;
        this.YV = YV;
        this.YV02 = YV02;
        this.FC = FC;
        this.FC02 = FC02;
        this.PS = PS;
        this.PSName = PSName;
    }

    public CoalBaseData(Integer NCV, Integer NCV02, BigDecimal RS, BigDecimal RS02, BigDecimal TM, BigDecimal TM02, BigDecimal RV, BigDecimal RV02, BigDecimal ASH, BigDecimal ASH02, Integer bondindex, Integer bondindex02) {
        this.NCV = NCV;
        this.NCV02 = NCV02;
        this.RS = RS;
        this.RS02 = RS02;
        this.TM = TM;
        this.TM02 = TM02;
        this.RV = RV;
        this.RV02 = RV02;
        this.ASH = ASH;
        this.ASH02 = ASH02;
        this.bondindex = bondindex;
        this.bondindex02 = bondindex02;
    }

    public CoalBaseData(Integer NCV, Integer NCV02, BigDecimal ADS, BigDecimal ADS02, BigDecimal RS, BigDecimal RS02, BigDecimal TM, BigDecimal TM02, BigDecimal IM, BigDecimal IM02, BigDecimal ADV, BigDecimal ADV02, BigDecimal RV, BigDecimal RV02, Integer AFT, BigDecimal ASH, BigDecimal ASH02, Integer HGI, Integer GV, Integer GV02, Integer YV, Integer YV02, Integer FC, Integer FC02, Integer PS, String PSName, Integer CRC, Integer CRC02) {
        this.NCV = NCV;
        this.NCV02 = NCV02;
        this.ADS = ADS;
        this.ADS02 = ADS02;
        this.RS = RS;
        this.RS02 = RS02;
        this.TM = TM;
        this.TM02 = TM02;
        this.IM = IM;
        this.IM02 = IM02;
        this.ADV = ADV;
        this.ADV02 = ADV02;
        this.RV = RV;
        this.RV02 = RV02;
        this.AFT = AFT;
        this.ASH = ASH;
        this.ASH02 = ASH02;
        this.HGI = HGI;
        this.GV = GV;
        this.GV02 = GV02;
        this.YV = YV;
        this.YV02 = YV02;
        this.FC = FC;
        this.FC02 = FC02;
        this.PS = PS;
        this.PSName = PSName;
        this.CRC = CRC;
        this.CRC02 = CRC02;
    }

    public Integer getNCV() {
        return NCV;
    }

    public void setNCV(Integer NCV) {
        this.NCV = NCV;
    }

    public Integer getNCV02() {
        return NCV02;
    }

    public void setNCV02(Integer NCV02) {
        this.NCV02 = NCV02;
    }

    public BigDecimal getADS() {
        return ADS;
    }

    public void setADS(BigDecimal ADS) {
        this.ADS = ADS;
    }

    public BigDecimal getADS02() {
        return ADS02;
    }

    public void setADS02(BigDecimal ADS02) {
        this.ADS02 = ADS02;
    }

    public BigDecimal getRS() {
        return RS;
    }

    public void setRS(BigDecimal RS) {
        this.RS = RS;
    }

    public BigDecimal getRS02() {
        return RS02;
    }

    public void setRS02(BigDecimal RS02) {
        this.RS02 = RS02;
    }

    public BigDecimal getTM() {
        return TM;
    }

    public void setTM(BigDecimal TM) {
        this.TM = TM;
    }

    public BigDecimal getTM02() {
        return TM02;
    }

    public void setTM02(BigDecimal TM02) {
        this.TM02 = TM02;
    }

    public BigDecimal getIM() {
        return IM;
    }

    public void setIM(BigDecimal IM) {
        this.IM = IM;
    }

    public BigDecimal getIM02() {
        return IM02;
    }

    public void setIM02(BigDecimal IM02) {
        this.IM02 = IM02;
    }

    public BigDecimal getADV() {
        return ADV;
    }

    public void setADV(BigDecimal ADV) {
        this.ADV = ADV;
    }

    public BigDecimal getADV02() {
        return ADV02;
    }

    public void setADV02(BigDecimal ADV02) {
        this.ADV02 = ADV02;
    }

    public BigDecimal getRV() {
        return RV;
    }

    public void setRV(BigDecimal RV) {
        this.RV = RV;
    }

    public BigDecimal getRV02() {
        return RV02;
    }

    public void setRV02(BigDecimal RV02) {
        this.RV02 = RV02;
    }

    public Integer getAFT() {
        return AFT;
    }

    public void setAFT(Integer AFT) {
        this.AFT = AFT;
    }

    public BigDecimal getASH() {
        return ASH;
    }

    public void setASH(BigDecimal ASH) {
        this.ASH = ASH;
    }

    public BigDecimal getASH02() {
        return ASH02;
    }

    public void setASH02(BigDecimal ASH02) {
        this.ASH02 = ASH02;
    }

    public Integer getHGI() {
        return HGI;
    }

    public void setHGI(Integer HGI) {
        this.HGI = HGI;
    }

    public Integer getGV() {
        return GV;
    }

    public void setGV(Integer GV) {
        this.GV = GV;
    }

    public Integer getGV02() {
        return GV02;
    }

    public void setGV02(Integer GV02) {
        this.GV02 = GV02;
    }

    public Integer getYV() {
        return YV;
    }

    public void setYV(Integer YV) {
        this.YV = YV;
    }

    public Integer getYV02() {
        return YV02;
    }

    public void setYV02(Integer YV02) {
        this.YV02 = YV02;
    }

    public Integer getFC() {
        return FC;
    }

    public void setFC(Integer FC) {
        this.FC = FC;
    }

    public Integer getFC02() {
        return FC02;
    }

    public void setFC02(Integer FC02) {
        this.FC02 = FC02;
    }

    public Integer getPS() {
        return PS;
    }

    public void setPS(Integer PS) {
        this.PS = PS;
    }

    public String getPSName() {
        return PSName;
    }

    public void setPSName(String PSName) {
        this.PSName = PSName;
    }

    public Integer getCRC() {
        return CRC;
    }

    public void setCRC(Integer CRC) {
        this.CRC = CRC;
    }

    public Integer getCRC02() {
        return CRC02;
    }

    public void setCRC02(Integer CRC02) {
        this.CRC02 = CRC02;
    }
}
