package kitt.core.util;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liuxinjie on 16/1/15.
 */
@Service
public final class NewsConfig {

    /**
     * 获取所有资讯模块第一级目录
     */
    public static List<String> getNewsContentList() {
        return Arrays.asList(new String[]{"/行业要闻", "/宏观经济/国内", "宏观经济/国际", "/国际煤炭", "/企业动态", "/政策追踪", "/相关行业/电力", "/相关行业/钢铁", "/相关行业/建材", "/相关行业/化工", "/独家视点/日评", "/独家视点/周报", "/独家视点/月评", "/独家视点/专题"});
    }

    /**
     * 获取热点1模块目录
     */
    public static List<String> getHotNew1ContentList() {
        return Arrays.asList(new String[]{});
    }

    /**
     * 获取电厂库存电厂名称列表
     */
    public static String[] getFactoryStockFactoryNames() {
        return new String[] {"浙电", "上电", "粤电", "国电", "大唐", "华能"};
    }

    /**
     * 获取电厂库存对应数据中心中名称列表
     */
    public static String[] getFactoryStockFactoryStockNames() {
        return new String[] {"库存:浙电", "库存:上电", "库存:粤电", "库存:国电", "库存:大唐", "库存:华能"};
    }

    /**
     * 获取电厂库存可用天数对应数据中心名称列表
     */
    public static String[] getFactoryStockAvailableStockNames() {
        return new String[] {"库存可用天数:浙电", "库存可用天数:上电", "库存可用天数:粤电", "库存可用天数:国电", "库存可用天数:大唐", "库存可用天数:华能"};
    }

    /**
     * 获取港口库存港口名称列表
     */
    public static String[] getPortStockPortNames() {
        return new String[] {"秦皇岛港", "曹妃甸港", "广州港集团", "国投京唐港"};
    }



}
