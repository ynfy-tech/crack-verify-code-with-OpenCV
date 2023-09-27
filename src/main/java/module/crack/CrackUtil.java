package module.crack;

import com.google.common.collect.Lists;
import io.github.bonigarcia.wdm.WebDriverManager;
import module.crack.constant.ArchEnum;
import module.crack.constant.CrackFactoryEnum;
import module.crack.constant.OSEnum;
import module.crack.module.CrackBO;
import module.crack.module.CrackDTO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 〈〉
 *
 * @author Hsiong
 * @version 1.0.0
 * @since 2022/7/26
 */
public class CrackUtil {

    /**
     * 初始化
     */
    static {

        Integer javaVersion = null;
        try {
            String versionStr = System.getProperty("java.version");
            String[] versions = versionStr.split("\\.");
            javaVersion = Integer.valueOf(versions[0]);
        } catch (Exception e) {
            javaVersion = 8;
        }
        
        /**
         * 加载OpenCV本地库
         * linux: .so
         * windows: .dll
         * mac: .dylib
         */
        Boolean ifMacAppleSilicon = OSEnum.OSX.isCurrent() && ArchEnum.ARMv8.isCurrent();
        if (ifMacAppleSilicon) {
            if (javaVersion > 8) {
                throw new IllegalArgumentException(" M1 Only Support java 8 ");
            }
            // M1
            // cmake opencv in java11 or java19 but can not execute the dylib file, i don't know the reason
            // only performed preferably in java 8
            String opencvLocation = "opencv/m1/470/libopencv_java470.dylib";
            URL url = ClassLoader.getSystemResource(opencvLocation);
            System.load(url.getPath());
        } else if (javaVersion > 11) {
            // Other PlateForm & Java Version > 11
            // In Java 11+ loadShared() is not available. Use loadLocally() instead
            nu.pattern.OpenCV.loadLocally();
        } else {
            // Other PlateForm & Java Version <= 11
            nu.pattern.OpenCV.loadShared();
        }



        // chrome
        if (OSEnum.LINUX.isCurrent()) {
            String chromeLocation = "chrome/linux64/chromedriver";
            URL url = ClassLoader.getSystemResource(chromeLocation);
            System.setProperty("webdriver.chrome.driver", url.getPath());
        } else {
            WebDriverManager.chromedriver().setup();
        }

    }

    /**
     * 核心方法, 破解验证码
     * @param dto
     */
    public void crackVerifyCode(CrackDTO dto) {
        /**
         * 通过 selenium 进入到验证码页面（以 QQ 007 为例）
         */
        WebDriver driver = new ChromeDriver();
        try {
            CrackFactoryEnum crackFactoryEnum = dto.getCrackFactoryEnum();

            driver.get(crackFactoryEnum.getUrl());
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            
            // 获取 用户名 密码 登录按钮 元素
            CrackBO crackBO = crackFactoryEnum.getGetElementService().getElement(driver);
            WebElement userElement = crackBO.getUserElement();
            WebElement pwdElement = crackBO.getPwdElement();
            WebElement codeElement = crackBO.getCodeElement();
            
            //切换登录页面所在iframe
            userElement.sendKeys(dto.getUserName());
            pwdElement.sendKeys(dto.getPwd());
            codeElement.click();

            /**
             * 滑块和背景是两张分开的图片，src 属性中保存的即为图片 URL 地址，所以我们可以通过 URL 将两者下载到本地
             */
            //切换到验证码所在的iframe
            WebElement tcaptchaFrame = driver.findElement(By.id("tcaptcha_iframe"));
            driver.switchTo().frame(tcaptchaFrame);

            //定位滑块图片
            WebElement slideBlock = driver.findElement(By.id("slideBlock"));

            //定位验证码背景图
            WebElement slideBg = driver.findElement(By.id("slideBg"));

            //获取图片Url链接
            String slideBlockUrl = slideBlock.getAttribute("src");
            String slideBgUrl = slideBg.getAttribute("src");

            //下载对应图片
            System.out.println("图片下载开始...");
            downloadImg(slideBlockUrl, "slideBlock.png");
            downloadImg(slideBgUrl, "slideBg.png");

            /**
             * 关键点在于获取滑块到滑动背景缺口图的横向距离，这里通过 OpenCV 的模板匹配技术 matchTemplate
             *
             * 然后再通过 selenium 的 Actions 类完成滑动，在滑动的时候需要注意不能直接从开始点滑到终止点（有些网站会判定脚本操作），其中 getMoveTrack 用于获取滑动轨迹，控制每阶段的滑动速度
             *
             */
            //获取滑块到滑动背景缺口图的横向距离
            double slideDistance = getSlideDistance(System.getProperty("user.dir") + File.separator + "slideBlock.png",
                                                    System.getProperty("user.dir") + File.separator + "slideBg.png");
            Actions actions = new Actions(driver);
            WebElement dragElement = driver.findElement(By.id("tcaptcha_drag_button"));

            // 获取style属性值，其中设置了滑块初始偏离值 style=left: 23px;
            // 需要注意的是网页前端图片和本地图片比例是不同的，需要进行换算
            slideDistance = slideDistance * 280 / 680 - 12;
            actions.clickAndHold(dragElement).perform();

            //根据滑动距离生成滑动轨迹，约定规则：开始慢->中间快->最后慢
            List<Integer> moveTrack = getMoveTrack((int) slideDistance);
            for (Integer index : moveTrack) {

                Thread.sleep(10);

                actions.moveByOffset(index, 0).perform();

            }
            actions.release().perform();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            try {
                // 休眠 10s 观察效果
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 关闭浏览器
            driver.close();
        }

    }

    /**
     * 通过 openCV 模板匹配, 返回匹配点的横向距离
     * @param slideBlockPicPath
     * @param slideBgPicPath
     * @return
     */
    private Double getSlideDistance(String slideBlockPicPath, String slideBgPicPath) {
        /**
         * 首先对滑块进行处理
         *
         * 1、灰度化
         *
         * 2、去除图片黑边
         *
         * 3、inRange 二值化转黑白图
         */
        //对滑块进行处理
        // UnsatisfiedLinkError: 'long org.opencv.imgcodecs.Imgcodecs.imread_1(java.lang.String)'，之中情况是opencv模块没有加载所以无法正确链接到相应的方法。
        Mat slideBlockMat = Imgcodecs.imread(slideBlockPicPath);

        //1、灰度化图片
        Imgproc.cvtColor(slideBlockMat, slideBlockMat, Imgproc.COLOR_BGR2GRAY);

        //2、去除周围黑边
        for (int row = 0; row < slideBlockMat.height(); row++) {

            for (int col = 0; col < slideBlockMat.width(); col++) {

                if (slideBlockMat.get(row, col)[0] == 0) {

                    slideBlockMat.put(row, col, 96);

                }

            }

        }

        //3、inRange二值化转黑白图
        Core.inRange(slideBlockMat, Scalar.all(96), Scalar.all(96), slideBlockMat);

        /**
         * 对滑动背景图进行处理
         *
         * 1、灰度化
         *
         * 2、二值化转黑白图
         */
        //对滑动背景图进行处理
        Mat slideBgMat = Imgcodecs.imread(slideBgPicPath);

        //1、灰度化图片
        Imgproc.cvtColor(slideBgMat, slideBgMat, Imgproc.COLOR_BGR2GRAY);

        //2、二值化
        Imgproc.threshold(slideBgMat, slideBgMat, 127, 255, Imgproc.THRESH_BINARY);
        Mat g_result = new Mat();

        /*

         * matchTemplate：在模板和输入图像之间寻找匹配,获得匹配结果图像

         * result：保存匹配的结果矩阵

         * TM_CCOEFF_NORMED标准相关匹配算法

         */
        Imgproc.matchTemplate(slideBgMat, slideBlockMat, g_result, Imgproc.TM_CCOEFF_NORMED);

        /* minMaxLoc：在给定的结果矩阵中寻找最大和最小值，并给出它们的位置

         * maxLoc最大值

         */
        Point matchLocation = Core.minMaxLoc(g_result).maxLoc;

        //返回匹配点的横向距离
        return matchLocation.x + slideBlockMat.width() / 2;
    }

    /**
     * 根据距离获取滑动轨迹
     *
     * @param distance 需要移动的距离
     * @return
     */
    private static List<Integer> getMoveTrack(int distance) {
        List<Integer> track = Lists.newArrayList();// 移动轨迹
        Random random = new Random();
        int current = 0;// 已经移动的距离
        int mid = distance * 4 / 5;// 减速阈值
        int a = 0;
        int move = 0;// 每次循环移动的距离
        while (true) {
            a = random.nextInt(10);
            if (current <= mid) {
                move += a;// 不断加速
            } else {
                move -= a;
            }
            if ((current + move) < distance) {
                track.add(move);
            } else {
                track.add(distance - current);
                break;
            }
            current += move;
        }
        return track;
    }

    /**
     * 下载图片
     * @param urlStr
     * @param name
     */
    private void downloadImg(String urlStr, String name) {

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流 
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);
            //文件保存位置
            File saveDir = new File(System.getProperty("user.dir"));
            if (!saveDir.exists()) { // 没有就创建该文件
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + name);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);

            fos.close();
            inputStream.close();
            //            System.out.println("the file: " + url + " download success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}
