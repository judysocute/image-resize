package com.judysocute;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author wayne on 2020/4/5
 */
@MultipartConfig() // 這個是為了要使用 getPart() 方法一定要加的哦！
@WebServlet(
        name = "ZipImage",
        urlPatterns = {"/zip-image"}, // servlet entry url
        loadOnStartup = 1
)
public class ZipImage extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 設定回應格式
        resp.setContentType("application/zip");
        // Servlet 輸出流，用於輸出檔案給用戶
        ServletOutputStream out = resp.getOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(out); // 用檔案輸出流建立出 Zip 輸出流

        // 處理使用者傳入想要壓縮的 size
        String[] sizeArr = req.getParameterMap().get("resize"); // {接收使用者指定的圖片尺寸}，收到的會是陣列格式
        List<ResizeInfo> resizeInfoList = ResizeInfoFactory
                .getResizeInfo(sizeArr)
                .orElse(ResizeInfoFactory.defaultResizeInfo()) // 如果使用者未指定尺寸的話，回傳預設的切割 size
                ;

        Part imagePart = req.getPart("imageFile"); // 取得使用者上傳的圖片檔案

        String fileName = imagePart.getSubmittedFileName(); // 使用者上傳的檔名
        String formatName = fileName.substring(fileName.lastIndexOf(".") + 1); // 副檔名（拿原圖的副檔名就好）


        /**
         * 把所有 ResizeInfo 物件迭代一次
         */
        resizeInfoList.stream().forEach(resizeInfo -> {
            ZipEntry zipEntry = new ZipEntry(fileName + resizeInfo.toString() + "." + formatName);
            try {
                BufferedImage bufferedImage = ImageIO.read(imagePart.getInputStream());
                // 建立一個空白的 BufferedImage 物件，
                // 寬度、高度 為使用者輸入的
                // 輸出類型為使用者上傳的圖片類型
                BufferedImage exportBFImage = new BufferedImage(resizeInfo.m_width, resizeInfo.m_height, bufferedImage.getType());

                // 用剛才建立的空白 BufferedImage 物件來建立畫布
                Graphics2D g2d = exportBFImage.createGraphics();
                g2d.drawImage(
                        bufferedImage, // 把我們讀入的圖片畫上去
                        0, // x軸起始點
                        0, // y軸起始點
                        resizeInfo.m_width, // 要畫上去的寬度
                        resizeInfo.m_height, // 要畫上去的長度
                        null
                );
                g2d.dispose(); // g2d 就不再接受被寫入內容

                zipOut.putNextEntry(zipEntry);
                ImageIO.write(exportBFImage, formatName, zipOut);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 關閉輸出流
        zipOut.close();
        out.close();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // "/" 開頭帶表從我們 Smart Tomcat 定義的 Deployment Directory 開始
        // 也就是 webapp 那個目錄找檔案
        RequestDispatcher view = req.getRequestDispatcher("/ZipImage.html");

        // 轉回去給使用者
        view.forward(req, resp);
    }
}
