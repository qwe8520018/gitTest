//package com.fr.io;
//
//
//import cn.hutool.http.HttpRequest;
//import com.fr.HttpUtil;
//import com.fr.decision.authority.data.User;
//import com.fr.decision.webservice.exception.user.UserNotExistException;
//import com.fr.decision.webservice.v10.login.LoginService;
//import com.fr.decision.webservice.v10.user.UserService;
//import com.fr.log.FineLoggerFactory;
//import com.fr.stable.StringUtils;
//import lombok.SneakyThrows;
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.HashMap;
//import java.util.Properties;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
///**
// * desc
// *
// * @author Anner
// * created on 2020-12-07
// */
//public class AuthFilter implements Filter {
//    private static final String params = "fine_request_param";
//    private static final String isLogout = "isLogout";
//    private static final String stateV = "zydddl";
//    private static HashMap<String, String> getUserInfoMap = null;
//    private static HashMap<String, String> getTokenMap = null;
//    private static String casIdpUrl;
//    private static String casAuthorizeUrl;
//    private static String casUserInfoUrl;
//    private static String client_id;
//    private static String client_secret;
//    private static String casTokenUrl;
//    private static String logoutUrl;
//
//    /**
//     * @param servletRequest
//     * @param servletResponse
//     * @param filterChain
//     * @throws IOException
//     * @throws ServletException 帆软单点登录过滤器，用于帆软平台集成oauth2单点登录
//     *                          1.判断用户是否登录
//     *                          2.如果没有登录则判断请求中是否有code
//     *                          2.1.如果没有code重定向到cas登录界面
//     *                          2.2.如果有code则根据code获取用户名进行登录
//     */
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
////        System.out.println(request.getMethod()+"---"+LoginService.getInstance().isLogged(request) +"---"+request.getRequestURL()+"?"+request.getQueryString());
//        /*
//            退出的时候会连续发送两个请求通过此过滤器。一条是post请求地址是/logout,一条是跳转到登录页面的，地址是/login.
//            1.logout请求发出后帆软系统用户成功退出。
//            2.跳转到login的请求再次被拦截到，因为此时cas还没有退出，会导致再次被登录。
//            此处拦截退出请求，给session中放入(isLogout, "t")，当再次拦截到前往login的请求时就跳转到cas登出页面。
//         */
//
//        if (request.getMethod().equals("POST") && request.getRequestURI().endsWith("logout")) {
//            request.getSession().setAttribute(isLogout, "t");
//        }
//
//        if (!LoginService.getInstance().isLogged(request) && request.getMethod().equals("GET")) {
//            String code = request.getParameter("code") + "";
//            String requestState = request.getParameter("state");
//            //为了屏蔽掉帆软的登录页面，去除掉/login就是帆软首页地址。
//            String requestUrl = request.getRequestURL().toString().replace("/login", "");
//            HttpSession session = request.getSession();
//            //如果session中有isLogout就判断为退出操作，重定向到cas退出界面。
//            if (session.getAttribute(isLogout) != null && session.getAttribute(isLogout).equals("t")) {
//                session.removeAttribute(isLogout);
//                response.sendRedirect(logoutUrl.replace("{}", requestUrl));
//                return;
//            }
//            //将请求参数先保存起来,如果"state=" + stateV就是从cas跳过来的，则忽略参数。
//            if (request.getQueryString() != null && !request.getQueryString().contains("state=" + stateV)) {
//                session.setAttribute(params, request.getQueryString());
//            }
//            //如果没有code重定向到cas登录界面
//            if (StringUtils.isEmpty(code) || StringUtils.isEmpty(requestState) || !stateV.equals(requestState)) {
//                response.sendRedirect(casAuthorizeUrl + "?redirect_uri=" + requestUrl + "&state=" + stateV + "&client_id=" + client_id + "&response_type=code");
//                return;
//            }
//            try {
//                //根据code获取用户名
//                String username = getUserNameByCode(code);
//                // 系统中无该用户则抛出异常
//                User user = UserService.getInstance().getUserByUserName(username);
//                FineLoggerFactory.getLogger().info("user=" + user);
//                if (user == null) {
//                    throw new UserNotExistException();
//                }
//                LoginService.getInstance().login(request, response, username);
//                //登录过后取出登录前保存的参数拼接到url上
//                if (session.getAttribute(params) != null) {
//                    requestUrl = requestUrl + "?" + session.getAttribute(params);
//                    session.removeAttribute(params);
//                }
//                response.sendRedirect(requestUrl);
//                return;
//            } catch (Exception e) {
//                FineLoggerFactory.getLogger().error(e.getMessage(), e);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    //根据code获取用户名
//    private String getUserNameByCode(String code) throws Exception {
//        String access_res = HttpUtil.doPost(casTokenUrl + "&code=" + code, getTokenMap);
//        if (access_res == null || !access_res.contains("access_token")) {
//            throw new Exception(access_res);
//        }
//        //返回值 {"access_token":"skiew234i3i4o6uy77b4k3b3v2j1vv53j" "expires_in":"1500", "refresh_token":"iewoer233422i34o2i34uio55iojhg6g" "uid":"admin" }
//        String access_token_sub = access_res.substring(access_res.indexOf("access_token") + 15);
//        String access_token = access_token_sub.substring(0, access_token_sub.indexOf("\""));
//        HashMap userInfoMap = new HashMap(getUserInfoMap);
//        userInfoMap.put("access_token", access_token);
//        String username_res = HttpUtil.doGet(casUserInfoUrl, userInfoMap);
//        if (username_res == null || !username_res.contains("loginName")) {
//            throw new Exception(access_res);
//        }
//        String loginName_sub = username_res.substring(username_res.indexOf("loginName") + 12);
//        String loginName = loginName_sub.substring(0, loginName_sub.indexOf("\""));
//        return loginName;
//    }
//
//    /**
//     * 初始化获取token和userInfo的参数，
//     * tokenMap中不包括code
//     * userInfoMap中不包括access_token
//     *
//     * @param filterConfig
//     * @throws ServletException
//     */
//    @SneakyThrows
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        //以下配置属性值来自于classpath,fr-cas.properties
//        String propertiesPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "fr-cas.properties";
//        FileReader reader = new FileReader(propertiesPath);
//        Properties properties = new Properties();
//        properties.load(reader);
//        casIdpUrl = properties.getProperty("casIdpUrl");
//        casAuthorizeUrl = casIdpUrl + "authorize";
//        casUserInfoUrl = casIdpUrl + "getUserInfo";
//        client_id = properties.getProperty("client_id");
//        client_secret = properties.getProperty("client_secret");
//        casTokenUrl = casIdpUrl + "getToken?client_id=" + client_id + "&client_secret=" + client_secret + "&grant_type=authorization_code";
//        logoutUrl = properties.getProperty("logoutUrl") + "?redirctToUrl={}&redirectToLogin=true&entityId=" + client_id;
//        getTokenMap = new HashMap<>();
//        getTokenMap.put("client_id", client_id);
//        getUserInfoMap = new HashMap<>();
//        getUserInfoMap.put("client_id", client_id);
//    }
//
//    @Override
//    public void destroy() {
//    }
//
//    public static void main(String[] args) {
//
//
//    }
//}
