package module.crack.factory.impl;

import module.crack.factory.GetElementService;
import module.crack.module.CrackBO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 〈〉
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2022/7/27
 */
public class GetElementSJ21ServiceImpl implements GetElementService {
    
    @Override
    public CrackBO getElement(WebDriver driver) {
        // 页面默认为微信登录, 点击切换
        driver.findElement(By.className("login-method__tab")).click();
        WebElement loginFrame = driver.findElement(By.className("J_LoginTabContent"));
        if (loginFrame == null) {
            loginFrame = driver.findElement(By.className("J_LoginTabContent"));
        }
        WebElement userElement = loginFrame.findElement(By.id("user-name"));
        WebElement pwdElement = loginFrame.findElement(By.id("user-pwd"));
        WebElement codeElement = loginFrame.findElement(By.className("btn-submit"));

        CrackBO crackBO = new CrackBO();
        crackBO.setUserElement(userElement);
        crackBO.setPwdElement(pwdElement);
        crackBO.setCodeElement(codeElement);

        return crackBO;
    }
}        
