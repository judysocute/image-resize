package com.judysocute;

/**
 * 要輸出的圖片寬高資訊
 */
class ResizeInfo {
    // 要輸出的寬
    public int m_width;
    // 要輸出的高
    public int m_height;

    /**
     *
     * @param sSize {String} - 字串通常是 60x60 120x200 這種格式
     */
    ResizeInfo(String sSize) {
        this(sSize.split("x"));
    }

    ResizeInfo(String[] spSize) {
        m_width = Integer.parseInt(spSize[0]);
        m_height = Integer.parseInt(spSize[1]);
    }
}
