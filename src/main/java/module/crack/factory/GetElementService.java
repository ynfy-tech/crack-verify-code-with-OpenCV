package module.crack.factory;

import module.crack.module.CrackBO;
import org.openqa.selenium.WebDriver;

/**
 * 〈〉
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2022/7/27
 */
public interface GetElementService {
    
    CrackBO getElement(WebDriver driver);

}
