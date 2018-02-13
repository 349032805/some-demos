package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.service.EditorMethod;
import kitt.admin.service.Session;
import kitt.core.domain.AuthenticationRole;
import kitt.core.editor.*;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liuxinjie on 15/11/12.
 */
@Controller
@RequestMapping(value = "/editor")
public class EditorController {
    @Autowired
    private EditorMethod editorMethod;
    @Autowired
    private Session session;

    /**
     * editor(编辑器) 入口
     */
    @RequestMapping(value = "/index")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public void doInitUeditorPic(@RequestParam(value = "action", required = true)String action,
                                 HttpServletRequest request,  HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        try {
            String exec = doUploadEditorImageMainMethod(action, request);
            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            if(!StringUtils.isEmpty(e.getMessage()) && e.getMessage().toString().startsWith("文章图片不能超过")){
                throw new BusinessException(e.getMessage().toString());
            } else {
                e.printStackTrace();
            }
        }

    }

    /**
     * 上传图片方法-总方法
     * @param actiontype               action类型
     */
    public String doUploadEditorImageMainMethod(String actiontype, HttpServletRequest request) throws FileUploadException, IOException, org.apache.tomcat.util.http.fileupload.FileUploadException {
        String callbackName = request.getParameter("callback");
        if (callbackName == null){
            return invoke(request, actiontype);
        } else {
            if (callbackName.matches("^[a-zA-Z_]+[\\w0-9_]*$")) {
                return callbackName + "(" + this.invoke(request, actiontype) + ");";
            } else {
                return (new EditorBaseState(false, 401)).toJSONEditorString();
            }
        }
    }

    /**
     * 分析action类型, 根据action类型, 做不同处理
     * actionCode 为 2, 涂鸦文件, 文件类型是isBase64
     * actionCode 位 5, 抓取远程图片,此功能暂时没有实现
     * @param actionType           action类型
     * @return
     */
    public String invoke(HttpServletRequest request, String actionType) throws IOException, FileUploadException {
        if(actionType == null) return (new EditorBaseState(false, 101)).toJSONEditorString();
        EditorState editorState = null;
        switch(EnumActionType.valueOf(actionType).value()) {
            case 0:
                return editorMethod.getConfigFileContentJSON().toString();
            case 2:
                editorState = editorMethod.doUploadImageMethod(actionType, true, request);
                break;
            case 1: case 3: case 4:
                editorState = editorMethod.doUploadImageMethod(actionType, false, request);
                break;
            case 5:
                String[] list = request.getParameterValues(EditorConfigContent.fileName);
                editorState = editorMethod.capture(session.getAdmin().getId(), list);
                break;
            case 6:
                editorState = editorMethod.listFile(Integer.parseInt(request.getParameter("start")), EditorConfigContent.fileAllowFiles, EditorConfigContent.fileManagerListSize);
                break;
            case 7:
                editorState = editorMethod.listFile(Integer.parseInt(request.getParameter("start")), EditorConfigContent.imageAllowFiles, EditorConfigContent.imageManagerListSize);
                break;
            default:
                return (new EditorBaseState(false, 101)).toJSONEditorString();
        }
        return editorState.toJSONEditorString();
    }

}
