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
public class GetElementQQ007ServiceImpl implements GetElementService {
    
    @Override
    public CrackBO getElement(WebDriver driver) {
        WebElement loginFrame = driver.findElement(By.className("wp-on-form"));
        WebElement userElement = loginFrame.findElement(By.id("acc"));
        WebElement pwdElement = loginFrame.findElement(By.id("pwd"));
        WebElement codeElement = loginFrame.findElement(By.id("code"));

        CrackBO crackBO = new CrackBO();
        crackBO.setUserElement(userElement);
        crackBO.setPwdElement(pwdElement);
        crackBO.setCodeElement(codeElement);
        
        return crackBO;
    }
} 
