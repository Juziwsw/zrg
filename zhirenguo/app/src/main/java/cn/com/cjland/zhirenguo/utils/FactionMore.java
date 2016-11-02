package cn.com.cjland.zhirenguo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/11/25.
 */
public class FactionMore {
    /**
     * @param mobile
     * @return 中国移动134.135.136.137.138.139.150.151.152.157.158.159.182.183.187.188
     *         ,147(数据卡不验证)
     *         中国联通130.131.132.155.156.185.186
     *         中国电信133.153.177.180.181.189
     *         CDMA 133,153 手机号码验证 适合目前所有的手机
     */
    public static boolean checkMobile(String mobile) {
        String regex = "^1(3[0-9]|5[012356789]|7[0123789]|8[01236789])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mobile);
        return m.find();
    }
}
