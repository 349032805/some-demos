package kitt.core.domain;

import org.springframework.util.StringUtils;

/**
 * Created by xiangyang on 16/1/7.
 */
public class BrokerTeam {

    private Integer id;
    private String teamName;
    private String teamLeader;
    private String dealerName;
    private boolean status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = StringUtils.isEmpty(teamName)==true?null:teamName;
    }

    public String getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(String teamLeader) {
        this.teamLeader =  StringUtils.isEmpty(teamLeader)==true?null:teamLeader;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }
}
