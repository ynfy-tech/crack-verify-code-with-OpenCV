package module.crack.module;

import module.crack.constant.CrackFactoryEnum;

/**
 * 〈〉
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2022/7/27
 */
public class CrackDTO {
    
    private String userName;
    
    private String pwd;

    private CrackFactoryEnum crackFactoryEnum;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public CrackFactoryEnum getCrackFactoryEnum() {
        return crackFactoryEnum;
    }

    public void setCrackFactoryEnum(CrackFactoryEnum crackFactoryEnum) {
        this.crackFactoryEnum = crackFactoryEnum;
    }
}
