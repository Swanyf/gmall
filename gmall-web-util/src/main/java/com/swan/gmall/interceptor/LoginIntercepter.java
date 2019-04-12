package com.swan.gmall.interceptor;

import com.swan.gmall.annotation.LoginRequest;
import com.swan.gmall.util.CookieUtil;
import com.swan.gmall.util.HttpClientUtil;
import com.swan.gmall.util.JwtUtil;
import com.swan.gmall.util.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class LoginIntercepter extends HandlerInterceptorAdapter {


    // hanlder 要请求的RequestMapping 的方法名
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequest annotation = handlerMethod.getMethodAnnotation(LoginRequest.class);

        String token = "";
        // 获取页面的请求参数
        String newToken = request.getParameter("newToken");
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);

        /*
            如果token是空就直接重定向到登录页面
         */
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }

        // 如果当前注解不为空
        if (annotation != null) {
            // 初始化验证的结果
            boolean loginResult = false;

            // 获取请求的参数token
            if (StringUtils.isNotBlank(token)) {
                // 获取springmvc内部请求校验
                // 获取进入verify方法之前的requestUrl的ip
                String ip = request.getHeader("request-forwared-for");
                if (StringUtils.isBlank(ip)) {
                    ip = request.getRemoteAddr();
                }
                String userId = HttpClientUtil.doGet("http://passport.gmall.com:8085/verify?token=" + token + "&requestId=" + ip);
                if (userId != null) {
                    loginResult = true;
                    // 验证结果通过需要把新的token转为旧的token数据
                    CookieUtil.setCookie(request,response,"oldToken",token,60*60*24,true);
//                    Map map = JwtUtil.decode("gmallkey", token, MD5Utils.md5(ip));
//                    String userId = (String) map.get("userId");
                    request.setAttribute("userId",userId);
                }
            }

            // 只有用户要做登录操作，且校验结果未通过才重定向到passport登录页面
            if (loginResult == false && annotation.isNeedLogin() == true) {
                response.sendRedirect("http://passport.gmall.com:8085/index?returnUrl=" + request.getRequestURL());
                return false;
            }
        }

        return true;
    }
}
