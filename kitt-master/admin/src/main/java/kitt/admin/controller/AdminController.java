package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Session;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.util.PageQueryParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by xiangyang on 15-3-5.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private OperateMapper operateMapper;

    @Autowired
    private Session session;

    @RequestMapping(value = "/loadAllRoles", method = RequestMethod.POST)
    public Object loadAllRoles() {
        return roleMapper.list();
    }

    @RequestMapping(value = "/findRoleByUserId", method = RequestMethod.POST)
    public Object loadAllRoles(@RequestParam("userId") int userId) {
        return roleMapper.findByUserId(userId);
    }

    //添加用户
    @RequestMapping(value = "/addAdmin", method = RequestMethod.POST)
    @Authority(role = AuthenticationRole.Admin)
    public boolean addAdmin(Admin admin, @RequestParam(value = "roleIds", required = false) Integer[] roleIds) {
        //添加用户
        admin.setPassword(DigestUtils.md5Hex("123456"));
        adminMapper.addAdmin(admin);
        admin.setJobnum("YMW" + admin.getId());
        adminMapper.updateJobNumByid(admin);
        //添加用户关联角色
        if (roleIds != null) {
            for (Integer roleId : roleIds) {
                userRoleMapper.insertUserRole(new UserRole(roleId, admin.getId()));
            }
        }
        return true;
    }

    //修改用户的角色
    @RequestMapping(value = "/updateRole", method = RequestMethod.POST)
    @Authority(role = AuthenticationRole.Admin)
    public boolean updateRole(@RequestParam("userId") int userId, @RequestParam(value = "userRoles", required = false) Integer[] roleIds) {
        Admin admin = adminMapper.getAdminById(userId);
        if (admin == null) throw new NotFoundException();
        if (StringUtils.isNullOrEmpty(admin.getJobnum())) {
            admin.setJobnum("YMW" + admin.getId());
            adminMapper.updateJobNumByid(admin);
        }
        List<Integer> oldRoleIds = roleMapper.findByUserId(userId);
        List<Integer> newRoleIds = roleIds == null ? new ArrayList<Integer>() : Arrays.asList(roleIds);
        //删除角色信息
        List<Integer> deleteIds = getSubSet(oldRoleIds, newRoleIds);
        //添加角色信息
        List<Integer> addIds = getSubSet(newRoleIds, oldRoleIds);
        if (deleteIds.size() > 0) {
            roleMapper.deleteUserRoles(userId, deleteIds);
        }
        if (addIds.size() > 0) {
            roleMapper.addUserRoles(userId, addIds);
        }
        return true;
    }

    @RequestMapping("/checkUsernameExist")
    public boolean checkuserNameIsExist(@RequestParam("username") String username) {
        if (adminMapper.getByUsername(username) != null) {
            return false;
        }
        return true;
    }

    @RequestMapping("/checkphoneExist")
    public boolean checkphoneIsExist(@RequestParam("phone") String phone) {
        if (adminMapper.countByPhone(phone) >= 1) {
            return false;
        }
        return true;
    }

    @RequestMapping("/loadDealerIsactive")
    public Admin loadDealer(@RequestParam("phone") String phone) {
        return adminMapper.loadDealerIsactive(phone);

    }

    @RequestMapping("/checkRoleExist")
    public boolean checkuserNameIsExist(Role role) {
        if (roleMapper.isExists(role) > 0) {
            return false;
        }
        return true;
    }

    @RequestMapping("/showRoles")
    public Object showRoles(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page) {
        return roleMapper.pageAllRoles(page, 10);
    }

    @RequestMapping("/addRole")
    @Authority(role = AuthenticationRole.Admin)
    public Object addRole(Role role) {
        if (roleMapper.save(role) == 1) {
            return true;
        }
        return false;
    }

    @RequestMapping("/load/{id}")
    public Object load(@PathVariable("id") int id) {
        return roleMapper.queryById(id);
    }

    @RequestMapping(value = "/updateSystemRole", method = RequestMethod.POST)
    @Authority(role = AuthenticationRole.Admin)
    public Object updateRole(Role role) {
        if (roleMapper.update(role) == 1) {
            return true;
        }
        return false;
    }

    @RequestMapping("/deleteRole/{id}")
    @Authority(role = AuthenticationRole.Admin)
    public Object deleteRole(@PathVariable("id") int id) {
        if (roleMapper.delete(id) == 1) {
            return true;
        }
        return false;
    }

    //获取菜单和相关的权限集合
    @RequestMapping("/getMenusAuth")
    public Object getMenusAuth(int roleid) {
        //获取所有选项
        List<Menu> parentMenusList = menuMapper.getAllParentMenus();
        List<Menu> childMenusList = menuMapper.getAllChildMenus();
        List<Operateauth> operateauthList = operateMapper.getAllOperateauth();

        //获取用户的数据
        List<RoleMenu> userMenusList = menuMapper.getRoleMenuListByRoleid(roleid);
        List<RoleOperate> userOperateList = operateMapper.getRoleOperateByRoleid(roleid);
        Map map = new HashMap<>();
        map.put("parentMenusList", parentMenusList);
        map.put("childMenusList", childMenusList);
        map.put("operateauthList", operateauthList);
        map.put("userMenusList", userMenusList);
        map.put("userOperateList", userOperateList);
        return map;
    }

    //保存菜单权限配置
    @RequestMapping("/saveRoleMenuAuth")
    public boolean saveRoleMenuAuth(int roleid, String menuidArr, String operatecodeArr) {
        //先删除用户已有的菜单和权限,然后再添加
        menuMapper.deleteRoleMenuByRoleid(roleid);
        operateMapper.deleteRoleOperateByRoleid(roleid);

        if (menuidArr != null && menuidArr != "" && !menuidArr.equals(",")) {
            String menuidList[] = menuidArr.split(",");

            for (int i = 0; i < menuidList.length; i++) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleid(roleid);
                roleMenu.setMenuid(Integer.parseInt(menuidList[i]));
                menuMapper.addRoleMenu(roleMenu);
            }
        }

        if (operatecodeArr != null && operatecodeArr != "" && !operatecodeArr.equals(",")) {
            String operatecodeList[] = operatecodeArr.split(",");
            for (int i = 0; i < operatecodeList.length; i++) {
                RoleOperate roleOperate = new RoleOperate();
                roleOperate.setRoleid(roleid);
                roleOperate.setOperatecode(operatecodeList[i]);
                operateMapper.addRoleOperate(roleOperate);
            }
        }

        return true;
    }

    //获取所有用户
    @RequestMapping("/adminList")
    public Object adminList(@RequestParam Map<String, Object> params) {
        Map map = new HashMap();
        int page = Integer.valueOf((String) params.get("page"));
        map.put("roleId", params.get("roleId"));
        map.put("name", params.get("name"));
        map.put("phone", params.get("phone"));
        map.put("jobnum", params.get("jobnum"));
        map.put("admin", adminMapper.pageAllAdmins(params, page, 10));

        return map;
    }


    //禁用/启用用户
    @RequestMapping("/changeStatus")
    public boolean changeStatus(int id, int isActive) {
        if (isActive == 0) {
            adminMapper.modifyIsactive(id, false, EnumAdmin.EndJob);
        } else {
            adminMapper.modifyIsactive(id, true, EnumAdmin.OnJob);
        }
        return true;
    }

    //重置密码
    @RequestMapping("/initPassword")
    public boolean initPassword(int id) {
        adminMapper.initPassword(id, DigestUtils.md5Hex("123456"));
        return true;
    }

    //获取用户权限
    @RequestMapping("/getUserAuth")
    public Object getUserAuth() {
        Admin admin = session.getAdmin();
        List operatecodeList = new ArrayList<>();
        if (admin != null) {
            List<UserRole> userRoleList = userRoleMapper.getUserRolesByUserid(admin.getId());
            for (UserRole userRole : userRoleList) {
                List<RoleOperate> roleOperateList = operateMapper.getRoleOperateByRoleid(userRole.getRoleid());
                if (roleOperateList != null && roleOperateList.size() > 0) {
                    for (RoleOperate roleOperate : roleOperateList) {
                        operatecodeList.add(roleOperate.getOperatecode());
                    }
                }
            }
        }
        Map map = new HashMap<>();
        map.put("operatecodeList", operatecodeList);
        return map;
    }

    //得到两个集合的差集
    private List<Integer> getSubSet(List<Integer> c1, List<Integer> c2) {
        List<Integer> result = new ArrayList<Integer>();
        result.addAll(c1);
        result.removeAll(c2);
        return result;
    }

    @Autowired
    private BrokerTeamMapper brokerTeamMapper;
    @Autowired
    private DealerMapper dealerMapper;

    @RequestMapping(value = "createBrokerTeam", method = RequestMethod.POST,produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> createBrokerTeam(@RequestBody  BrokerTeam brokerTeam) {
        if (brokerTeamMapper.isTeamNameExists(brokerTeam.getTeamName()) > 0) {
            return new ResponseEntity<>("teamNameExists",HttpStatus.BAD_REQUEST);
        }
        brokerTeam.setStatus(false);
        brokerTeamMapper.add(brokerTeam);
        return new ResponseEntity<>("success",HttpStatus.OK);
    }


    @RequestMapping(value = "loadBrokerTeam/{teamId}", method = RequestMethod.GET)
    public ResponseEntity<?> loadTeamById(@PathVariable(value = "teamId") Integer  teamId) {

        Map<String,Object> params= new HashMap<String,Object>();
        //当前团队
        params.put("currentTeam",brokerTeamMapper.loadTeamById(teamId));
        //当前团队的交易员
        params.put("dealerInTeam", dealerMapper.findAdminByTeamId(teamId));
        //不是当前团队的交易员
        params.put("allDealer",dealerMapper.findWithoutAdminByTeamIds(teamId));

        return new ResponseEntity<>(params,HttpStatus.OK);
    }

    @RequestMapping(value = "/loadTeamInfo/{teamId}", method = RequestMethod.GET)
    public ResponseEntity<?> loadTeamInfoById(@PathVariable(value = "teamId") Integer  teamId) {
        Map<String,Object> params= new HashMap<String,Object>();
        BrokerTeam team = brokerTeamMapper.loadTeamById(teamId);
        if(team==null){
            throw  new NotFoundException();
        }
        params.put("currentTeam",team);
        return new ResponseEntity<>(params,HttpStatus.OK);
    }
    @RequestMapping(value = "updateBrokerTeam", method = RequestMethod.POST,produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> updateBrokerTeam(@RequestBody  BrokerTeam brokerTeam) {
        if (brokerTeamMapper.isEqualsTeamNameExists(brokerTeam)>0) {
            return new ResponseEntity<>("teamNameExists",HttpStatus.BAD_REQUEST);
        }
        brokerTeamMapper.update(brokerTeam);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @RequestMapping(value = "/brokerteam", method = RequestMethod.POST)
    public PageQueryParam<Admin> userList(PageQueryParam pageParam, BrokerTeam brokerTeam) {
        int totalCount = brokerTeamMapper.count(brokerTeam);
        List<BrokerTeam> teams = brokerTeamMapper.list(pageParam,brokerTeam);
        pageParam.setTotalCount(totalCount);
        pageParam.setList(teams);
        int totalPage = totalCount / pageParam.getPagesize();
        totalPage = totalCount % pageParam.getPagesize() == 0 ? totalPage : totalPage + 1;
        pageParam.setTotalCount(totalCount);
        pageParam.setTotalPage(totalPage);
        return pageParam;
    }







}
