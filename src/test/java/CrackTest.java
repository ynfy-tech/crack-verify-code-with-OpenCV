import module.crack.CrackUtil;
import module.crack.constant.CrackFactoryEnum;
import module.crack.module.CrackDTO;
import org.junit.jupiter.api.Test;

/**
 * 〈〉
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2022/7/27
 */
public class CrackTest {
    
    @Test
    public void crack007Test() {
        CrackUtil crackUtil = new CrackUtil();
        CrackDTO crackDTO = new CrackDTO();
        crackDTO.setUserName("362715381");
        crackDTO.setPwd("1234r2we");
        crackDTO.setCrackFactoryEnum(CrackFactoryEnum.QQ007);
        crackUtil.crackVerifyCode(crackDTO);
    }
}
