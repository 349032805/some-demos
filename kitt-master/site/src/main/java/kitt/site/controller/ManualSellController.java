package kitt.site.controller;

import com.google.common.collect.Maps;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.SMS;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.BeanValidators;
import kitt.site.service.Session;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by xiangyang on 14-12-17.
 */
@Controller
public class ManualSellController extends JsonController {
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private ManualSellMapper manualSellMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ValidMapper validMapper;
    @Autowired
    private SMS sendMessageService;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private Session session;

    private static Map<String, String> rangeTypes = Maps.newLinkedHashMap();

    static {
        rangeTypes.put("thirty_day", "30天以内");
        rangeTypes.put("three_month", "3个月以内");
        rangeTypes.put("half_year", "半年以内");
        rangeTypes.put("all", "全部");
    }


    @RequestMapping(value = "/manualsell_in", method = RequestMethod.GET)
    public String manualSella(Model model) {
        return manualSell(1, model);
    }

    @RequestMapping(value = "/manualsell_out", method = RequestMethod.GET)
    public String manualSellb(Model model) {
        return manualSell(0, model);
    }

    //显示表单页面
    public String manualSell(int type, Model model) {
        //所有省份区划
        List<Areaport> areas = areaportMapper.getAllArea();
        //提货方式
        List<Dictionary> deliverymodeList = dictionaryMapper.getDeliverymodes();
        //省份
        List<Areaport> provincesList = areaportMapper.getProcvincesOrPortsByParentid(areas.get(0).getId());
        //港口信息
        List<Areaport> portsList = areaportMapper.getProcvincesOrPortsByParentid(provincesList.get(0).getId());
        //煤炭种类
        List<Dictionary> cocaltypeList = dictionaryMapper.getAllCoalTypes();
        Areaport areaport = new Areaport("其它", "other");
        portsList.add(areaport);
        model.addAttribute("areaList", areas);
        model.addAttribute("deliverymodeList", deliverymodeList);
        model.addAttribute("provincesList", provincesList);
        model.addAttribute("portsList", portsList);
        model.addAttribute("delegationType", type);
        model.addAttribute("coalTypeList", cocaltypeList);
        if (session.getUser() != null) {
            model.addAttribute("company", companyMapper.getCompanyByUserid(session.getUser().getId()));
            model.addAttribute("userPhone", session.getUser().getSecurephone());
        }

        return "manualsell";
    }

    @RequestMapping(value = "/manualsell/save", method = RequestMethod.POST)
    @ResponseBody
    public Object saveManualSell(@Valid ManualSell form,BindingResult bindingResult) {
        //进行后端数据校验。如果有错误。前台弹框显示
         BeanValidators.validateWithException(form);
      Phonevalidator phonevalidator = validMapper.findVerifyCode(form.getPhone(), form.getVerifyCode(),ValidateType.manualsell);
        //不需要验证码
        if (StringUtils.isBlank(form.getVerifyCode())) {
            save(form);
        } else if (phonevalidator==null) {
          bindingResult.rejectValue("verifyCode", "codeerror", "验证码错误");
        } else if (phonevalidator.getExpiretime().isBefore(LocalDateTime.now())) {
          bindingResult.rejectValue("verifyCode", "codeover", "验证码已过期");
        } else {
            save(form);
            validMapper.modifyValidatedAndTime(form.getPhone(),form.getVerifyCode());
        }
        return json(bindingResult);
    }

    public void save(ManualSell manualSell) {
        //首先判断用户是否登录
        User user = session.getUser();
        manualSell.setClienttype(1);
        manualSell.setCreateDatetime(LocalDate.now());
        user = userMapper.getUserByPhone(manualSell.getPhone());
        //不是易煤网的用户,自动注册为易煤网用户，发送短信告知用户注册信息
        if (user == null) {
            String phoneNum = manualSell.getPhone();
            //用户名为手机号，密码为手机号后6位
            user = new User(manualSell.getPhone(), DigestUtils.md5Hex(phoneNum.substring(5)), LocalDateTime.now(), true, 1);
            userMapper.addUser(user);
            String content = "恭喜您已经成功注册为易煤网用户，用户名为您的手机号，密码为您手机号的后6位。";
            String signature = "【易煤网】";
            try {
                //sendMessageService.send(phoneNum, content, "", signature);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        manualSell.setUserId(user.getId());
        manualSellMapper.save(manualSell);
    }


    //人工销售
    @RequestMapping(value = "/manualsell/list_manualsel_out")
    @LoginRequired
    public String showSellList(PageQueryParam param, @RequestParam(value = "dateRange", required = false, defaultValue = "thirty_day") String dateRange, Model model) {
        return show(param, false, dateRange, model);
    }

    //人工找货
    @RequestMapping(value = "/manualsell/list_manualsel_in")
    @LoginRequired
    public String showlookupList(PageQueryParam param, @RequestParam(value = "dateRange", required = false, defaultValue = "thirty_day") String dateRange, Model model) {
        return show(param, true, dateRange, model);
    }

    public String show(PageQueryParam param, boolean manualsellType, String dateRange, Model model) {
        //分页大小
        User currentUser = session.getUser();
        LocalDate searchDate = getDateByCode(dateRange);
        int totalCount = manualSellMapper.count(currentUser.getId(), manualsellType, searchDate, null);
        param.setCount(totalCount);
        List<ManualSell> manualSellLists = manualSellMapper.list(session.getUser().getId(), manualsellType, searchDate, param.getPagesize(), param.getIndexNum(), null);
        model.addAttribute("rangeTypes", rangeTypes);
        model.addAttribute("manualsellList", manualSellLists);
        model.addAttribute("manualsellCount", totalCount);
        model.addAttribute("pageNumber", param.getPage());
        model.addAttribute("dateRange", dateRange);
        model.addAttribute("manualsellType", manualsellType);
        model.addAttribute("manualsellPagesize", param.getPagesize());
        return "individualCenter";

    }

    @RequestMapping(value = "/manualsell/load/{id}")
    @LoginRequired
    public String findOne(@PathVariable String id, Model model) {
        ManualSell manualSell = manualSellMapper.loadByUserIdandManualId(session.getUser().getId(), id);
        if (manualSell == null) {
            throw new NotFoundException();
        }
        model.addAttribute("manualsell", manualSell);
        return "individualCenter";
    }

    @RequestMapping(value = "/manualsell/delete/{manualsellId}")
    @ResponseBody
    @LoginRequired
    public String delete(@PathVariable String manualsellId) {
        if (null == manualSellMapper.loadByManualId(manualsellId)) {
            throw new NotFoundException();
        }
        manualSellMapper.deleteManualSellById(manualsellId, session.getUser().getId());
        return "true";
    }

    @RequestMapping(value = "/manualsell/sendVerifyCode", method = RequestMethod.POST)
    @ResponseBody
    public Object doSendSmsCode(@Valid AuthController.PhoneForm phoneForm, HttpServletRequest request, BindingResult bindingResult) throws Exception {
        if (!bindingResult.hasErrors()) {
            if (!checkPhonenum(phoneForm.securephone)) {
                bindingResult.rejectValue("securephone", "phoneillegal", "请输入正确的手机号");
            } else  {
              //verifyCodeService.generateVerifyCode(phoneForm.getSecurephone(),ValidateType.manualsell, request);
            }
        }
        return json(bindingResult);
    }



    private static boolean checkPhonenum(String securephone) {
        return Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$").matcher(securephone).matches();
    }

    private static LocalDate getDateByCode(String rangeType) {
        LocalDate nowDate = LocalDate.now();
        LocalDate newDate = null;
        switch (rangeType) {
            case "thirty_day":
                newDate = nowDate.minusDays(30);
                break;
            case "three_month":
                newDate = nowDate.minusMonths(2);
                break;
            case "half_year":
                newDate = nowDate.minusMonths(5);
                break;
        }
        return newDate;
    }


}


