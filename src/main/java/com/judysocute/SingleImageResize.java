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

/**
 * @author wayne on 2020/4/3
 */

@MultipartConfig() // 這個是為了要使用 getPart() 方法一定要加的哦！
@WebServlet(
        name = "SingleFileResize",
        urlPatterns = {"/single-file-resize"}, // servlet entry url
        loadOnStartup = 1
)
public class SingleImageResize extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String size = req.getParameter("resize"); // 接收使用者指定的圖片尺寸
        ResizeInfo resizeInfo = new ResizeInfo(size); // 字串格式是 60x60 120x200 這種格式，轉為 ResizeInfo 物件

        Part imagePart = req.getPart("imageFile");

        String fileName = imagePart.getSubmittedFileName(); // 使用者上傳的檔名
        String formatName = fileName.substring(fileName.lastIndexOf(".") + 1); // 副檔名（拿原圖的副檔名就好）

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

        // Servlet 輸出流，用於輸出檔案給用戶
        ServletOutputStream out = resp.getOutputStream();

        // 設定回應格式 "image/jpeg" or "image/png" ...
        resp.setContentType("image/" + formatName);
        ImageIO.write(exportBFImage, formatName, out);
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // "/" 開頭帶表從我們 Smart Tomcat 定義的 Deployment Directory 開始
        // 也就是 webapp 那個目錄找檔案
        RequestDispatcher view = req.getRequestDispatcher("/SingleImageResize.html");

        // 轉回去給使用者
        view.forward(req, resp);
    }
}
