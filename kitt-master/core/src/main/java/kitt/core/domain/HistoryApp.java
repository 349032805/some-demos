package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by fanjun on 15-5-12.
 */
public class HistoryApp implements Serializable {
    private String appName;
    private String appPath;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }
}
