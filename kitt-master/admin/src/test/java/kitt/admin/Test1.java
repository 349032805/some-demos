package kitt.admin;

import java.util.Map;

/**
 * Created by lich on 16/2/25.
 */
public class Test1 {

    private static final String suo = "hahahaha";

    public void test1(Map<String, Object> map) {
        map.put("aaa", "aaa");
    }

    public  void test2(int i) {
        synchronized (suo) {}
    }
}
