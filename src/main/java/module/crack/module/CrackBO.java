package module.crack.module;

import org.openqa.selenium.WebElement;

/**
 * 〈〉
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2022/7/27
 */
public class CrackBO {

    /**
     * 用户名栏
     */
    private WebElement userElement;

    /**
     * 密码栏
     */
    private WebElement pwdElement;

    /**
     * 登录按钮
     */
    private WebElement codeElement;

    public WebElement getUserElement() {
        return userElement;
    }

    public void setUserElement(WebElement userElement) {
        this.userElement = userElement;
    }

    public WebElement getPwdElement() {
        return pwdElement;
    }

    public void setPwdElement(WebElement pwdElement) {
        this.pwdElement = pwdElement;
    }

    public WebElement getCodeElement() {
        return codeElement;
    }

    public void setCodeElement(WebElement codeElement) {
        this.codeElement = codeElement;
    }
}
