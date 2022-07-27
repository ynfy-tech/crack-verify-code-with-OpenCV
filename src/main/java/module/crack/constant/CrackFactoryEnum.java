package module.crack.constant;

import module.crack.factory.GetElementService;
import module.crack.factory.impl.GetElementQQ007ServiceImpl;

/**
 * 破解工厂, 用于获取各个实际的元素
 */
public enum CrackFactoryEnum {
    QQ007("腾讯防火墙", "https://007.qq.com/online.html", GetElementQQ007ServiceImpl.class),
    
    ;
    
    private String name;

    private String url;
    
    private GetElementService getElementService;

    CrackFactoryEnum(String name, String url, Class <? extends GetElementService> getElementService) {
        this.name = name;
        this.url = url;
        try {
            this.getElementService = getElementService.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            this.getElementService = null;
        }
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public GetElementService getGetElementService() {
        return getElementService;
    }
}
