package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.service.Session;
import kitt.core.domain.AuthenticationRole;
import kitt.core.domain.SendMessage;
import kitt.core.persistence.SendMessageMapper;
import kitt.core.service.FileStore;
import kitt.core.service.MessageNotice;
import kitt.core.service.SMS;
import kitt.ext.mybatis.Where;
import me.chanjar.weixin.common.util.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lich on 15/12/30.
 */
@RestController
public class SendMessageController {
    @Autowired
    private SendMessageMapper sendMessageMapper;
    @Autowired
    private SMS sms;
    @Autowired
    private Session session;
    @Autowired
    private FileStore fileStore;

    /**
     * 获取短信列表
     * @param page                      page
     * @return
     */
    @RequestMapping("/message/listMessage")
    public Object getMessageList(
            @RequestParam(value = "oper", required = false, defaultValue = "")String oper,
            @RequestParam(value = "mobile", required = false, defaultValue = "")String mobile,
            @RequestParam(value = "page", required = false)int page){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("messageList",sendMessageMapper.getMessageList(Where.$like$(oper),Where.$like$(mobile),page, 10));
        map.put("mobile",mobile);
        map.put("oper",oper);
        return map;

    }

    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @RequestMapping(value = "/message/downloadTemplate")
    public void download(HttpServletResponse response) throws Exception {
    //    String s="15900000000#测试1\n15900000000#测试2";
    //    String fileName = "发送短信模版.txt";
    //    File dir=new File("../files/message/");
    //    if(!dir.exists()){
    //        dir.mkdir();
    //    }
    //    File file = new File("../files/message/"+fileName);
    //    FileWriter fw=null;
    //    if(!file.exists()){
    //        try{
    //            file.createNewFile();
    //        }catch(Exception e){
    //            e.printStackTrace();
    //        }
    //    }
    //    fw = new FileWriter(file);
    //    BufferedWriter out = new BufferedWriter(fw);
    //    out.write(s, 0, s.length()-1);
    //    out.close();
    //    //String path="./src-web/app/images/message/发送短信模版.txt";
    //    //this.getClass().getClassLoader().getResourceAsStream("message/发送短信模版.txt");
    //    HttpHeaders headers = new HttpHeaders();
    //    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    //    headers.setContentDispositionFormData("attachment",new String(file.getName().getBytes("utf-8"), "ISO8859-1"));
    //    return new HttpEntity<byte[]>(FileUtils.readFileToByteArray(file), headers);

            HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = wb.createSheet("发送短信模版");
    HSSFRow row = sheet.createRow((int) 0);
    HSSFCellStyle style = wb.createCellStyle();
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

    String[] excelHeader = {"手机号码", "短信内容"};
    for (int i = 0; i < excelHeader.length; i++) {
        HSSFCell cell = row.createCell(i);
        cell.setCellValue(excelHeader[i]);
        cell.setCellStyle(style);
        sheet.autoSizeColumn(i);
    }
        sheet.setColumnWidth(0, 3500);
        sheet.setColumnWidth(1, 5000);
        row = sheet.createRow(1);
        row.createCell(0).setCellValue("15900000000");
        row.createCell(1).setCellValue("短信内容");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/x-download");
    String filename = "发送短信模版"+ LocalDate.now();
    filename = URLEncoder.encode(filename, "UTF-8");
    response.addHeader("Content-Disposition", "attachment;filename="+ filename+".xls");
    OutputStream out = response.getOutputStream();
    wb.write(out);
    out.close();
    }


    @RequestMapping(value = "/message/addMessage", method = RequestMethod.POST)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object addDealer(@Valid SendMessage message,BindingResult result) throws Exception {
        Map map=new HashMap<String,Object>();
        if(result.hasErrors()){
            map.put("message",false);
            return  map;
        }else{
            message.setUserid(session.getAdmin().getId());
            message.setOperator(session.getAdmin().getUsername());
            message.setContent(message.getContent());
            sendMessageMapper.addMessage(message);
            MessageNotice.CommonMessage.noticeUser(message.getTelephone(), message.getContent());
            map.put("message",true);
           return map;
        }

    }


    /**
     * 批量发送短信
     */
    @RequestMapping(value="/message/sendMessage", method= RequestMethod.POST)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object uploadAdvPic(@RequestParam("file") MultipartFile file,HttpServletResponse response) throws Exception {
        Map map = new HashMap<String, Object>();
        List<SendMessage> mlist=new ArrayList<SendMessage>();
        //Map info=new HashMap<String,Object>();
        System.out.println("filename==========="+file.getOriginalFilename());
        List<String> errPhoneList=new ArrayList<String>();
        if (file.getOriginalFilename().endsWith(".xls")) {
            if(file.isEmpty()){
                throw new BusinessException("文件内容为空");
            }else{
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(file.getInputStream());
                // 循环工作表Sheet
                for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                    HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                    if (hssfSheet != null) {
                    // 循环行Row
                    for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                        HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                        if (hssfRow != null) {
                            System.out.println("cell1:"+hssfRow.getCell(1));
                            if(hssfRow.getCell(0)!=null) {
                                hssfRow.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                                if (StringUtils.isNotBlank(hssfRow.getCell(0).getStringCellValue())) {
                                    if (hssfRow.getCell(1) != null) {
                                        hssfRow.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                                    if (StringUtils.isNotBlank(hssfRow.getCell(1).getStringCellValue())) {
                                        if (isMobileNO(hssfRow.getCell(0).getStringCellValue())) {
                                            if(hssfRow.getCell(1).getStringCellValue().length()<=195) {
                                                SendMessage message = new SendMessage();
                                                message.setTelephone(hssfRow.getCell(0).getStringCellValue());
                                                message.setContent(hssfRow.getCell(1).getStringCellValue());
                                              mlist.add(message);
                                            }else{
                                                //errPhoneList.add(hssfRow.getCell(0).getStringCellValue()+"(短信内容大于195个字符)");
                                                throw new BusinessException("手机号 "+hssfRow.getCell(0).getStringCellValue()+" 对应的短信内容大于195个字符");
                                            }
                                        } else {
                                            //不是正确的手机号
                                            //errPhoneList.add(hssfRow.getCell(0).getStringCellValue()+"(不是正确的手机号)");
                                            throw new BusinessException("手机号 "+hssfRow.getCell(0).getStringCellValue()+" 不是正确的手机号");
                                        }
                                    } else {
                                        //短信内容为空
                                        //errPhoneList.add(hssfRow.getCell(0).getStringCellValue()+"(短信内容为空)");
                                        throw new BusinessException("手机号 "+ hssfRow.getCell(0).getStringCellValue()+" 对应的短信内容为空");
                                    }
                                }else{
                                        //errPhoneList.add(hssfRow.getCell(0).getStringCellValue()+"(短信内容为空)");
                                        throw new BusinessException("手机号 "+ hssfRow.getCell(0).getStringCellValue()+" 对应的短信内容为空");
                                    }
                            }

                            }

                            if(hssfRow.getCell(1)!=null){
                                hssfRow.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                                if (StringUtils.isNotBlank(hssfRow.getCell(1).getStringCellValue())) {
                                    if (hssfRow.getCell(0) != null) {
                                        hssfRow.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                                        if (StringUtils.isBlank(hssfRow.getCell(0).getStringCellValue())) {
                                            throw new BusinessException("短信内容为 "+ hssfRow.getCell(1).getStringCellValue()+" 对应的手机号为空");
                                        }
                                    }else{
                                        //errPhoneList.add(hssfRow.getCell(0).getStringCellValue()+"(短信内容为空)");
                                        throw new BusinessException("短信内容为 "+ hssfRow.getCell(1).getStringCellValue()+" 对应的手机号为空");
                                    }
                                }
                            }

                        }
                        }

                    }
                }
               // String str="";
               //if(errPhoneList.size()>0){
               //    str="异常信息有:";
               //    for(String s:errPhoneList){
               //        str=str+s+",";
               //    }
               //}
               // map.put("error",str);
                for(SendMessage s:mlist){
                    SendMessage message = new SendMessage();
                    message.setTelephone(s.getTelephone());
                    message.setContent(s.getContent());
                    message.setUserid(session.getAdmin().getId());
                    message.setOperator(session.getAdmin().getUsername());
                    message.setContent(message.getContent());
                    sendMessageMapper.addMessage(message);
                    MessageNotice.CommonMessage.noticeUser(message.getTelephone(), message.getContent());
                }
                map.put("message","短信发送成功!");
                return map;
                }
            }else{
            throw new BusinessException("请上传以xls为后缀的excel文档");
        }

        }


    public  boolean isMobileNO(String mobiles){

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        System.out.println(m.matches()+"----------"+mobiles);

        return m.matches();

    }



}


