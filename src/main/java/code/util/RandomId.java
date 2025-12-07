package code.util;

import java.security.SecureRandom;

public class RandomId {
    private static final String totalChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRoomId(int length) {
        StringBuilder stringIdRandom = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // stringIdRandom.append(totalChar.charAt(random.nextInt(totalChar.length())));
            // Đầu tiên là lấy vị trí ngẫu nhiên trong chuỗi totalChar
            int index = random.nextInt(totalChar.length());
            // Sau đó lấy kí tự ngay tại vị trí ngẫu nhiên đó
            char randomChar = totalChar.charAt(index);
            // Cuối cùng là gán vào stringRandomId
            stringIdRandom.append(randomChar);
        }
        return stringIdRandom.toString();
    }
}
