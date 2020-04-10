package com.judysocute;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wayne on 2020/4/10
 */
public class ResizeInfoFactory {
    /**
     * @param sizeArr - 接收到使用者指定的圖片尺寸陣列
     * @return - 沒有的話回傳 Optional.empty()
     */
    public static Optional<List<ResizeInfo>> getResizeInfo(String[] sizeArr) {
        /**
         * 例外判斷
         */
        if (sizeArr == null || sizeArr[0].isEmpty()) {
            return Optional.empty();
        }
        List<String> sizeList = Arrays.asList(sizeArr); // 字串轉為 List 結構
        List<ResizeInfo> resizeInfoList = getResizeInfo$(sizeList);
        return Optional.of(resizeInfoList);
    }

    public static List<ResizeInfo> defaultResizeInfo() {
        List<String> defaultSizeList = Arrays.asList("100x100", "250x250", "600x600");
        return getResizeInfo$(defaultSizeList);
    }

    /**
     * 物件內部的轉換方法，不公開給外部使用
     * $ 符號代表直接取得，不處理例外
     * @return 處理陣列字串轉為陣列 ResizeInfo 物件
     */
    private static List<ResizeInfo> getResizeInfo$(List<String> sizeList) {
        return sizeList.stream()
                .map(sizeItem -> new ResizeInfo(sizeItem))
                .collect(Collectors.toList())
                ;
    }
}
