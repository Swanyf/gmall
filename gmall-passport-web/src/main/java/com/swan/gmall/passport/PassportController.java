package com.swan.gmall.passport;

import com.alibaba.dubbo.config.annotation.Reference;
import com.swan.gmall.bean.UserAddress;
import com.swan.gmall.bean.UserInfo;
import com.swan.gmall.service.CartManagerService;
import com.swan.gmall.service.UserInfoService;
import com.swan.gmall.util.CookieUtil;
import com.swan.gmall.util.JwtUtil;
import com.swan.gmall.util.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    UserInfoService userInfoService;

    @Reference
    CartManagerService cartManagerService;

    @RequestMapping("index")
    public String toIndex(String returnUrl, Model model){
        model.addAttribute("originUrl",returnUrl);
        return "index";
    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request,String requestId,String token,Model model) {
        Map map = JwtUtil.decode("gmallkey", token, MD5Utils.md5(requestId));
        String userId = (String) map.get("userId");
        if (map != null) {
            return userId;
        }
        return null;
    }

    @RequestMapping("login")
    @ResponseBody
    public String doLogin(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response,Model model){
        // 根据登录的信息查询
        UserInfo user = userInfoService.getLoginInfoByRedis(userInfo);

        if (user == null) {
            return "erro";
        }
        /**
         * 生成token数据
         */
        String token = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("loginName",user.getLoginName());

        // 获取nginx中的ip
        String ip = request.getHeader("request-forwared-for");
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }

        token = JwtUtil.encode("gmallkey", map, MD5Utils.md5(ip));

        /*
            同步购物车数据
         */
        // 获取cookie中缓存的购物车数据
        String cartListCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);
        if (StringUtils.isNotBlank(cartListCookie)) {
            // 同步数据库数据,如果存在就更新数量和总价,
            cartManagerService.synchronizedDBCartInfo(user.getId(), cartListCookie);
            // 删除cookie中的数据
            CookieUtil.deleteCookie(request,response,"listCartCookie");
        } else {
            // 同步缓存中的数据
            cartManagerService.synchronizedRedisCartInfo(user.getId());
        }

        return token;
    }

}

interface A{

}

abstract class B implements A {


}
