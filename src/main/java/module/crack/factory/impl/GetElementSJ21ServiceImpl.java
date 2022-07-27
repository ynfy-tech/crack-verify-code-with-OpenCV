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
        WebElement loginFrame = driver.findElement(By.id("J_LoginTabContent"));
        driver.switchTo().frame(loginFrame);
        WebElement userElement = driver.findElement(By.id("user-name"));
        WebElement pwdElement = driver.findElement(By.id("user-pwd"));
        WebElement codeElement = driver.findElement(By.className("btn-submit"));

        CrackBO crackBO = new CrackBO();
        crackBO.setUserElement(userElement);
        crackBO.setPwdElement(pwdElement);
        crackBO.setCodeElement(codeElement);

        return crackBO;
    }
}        
