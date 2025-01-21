package com.shop.server.utils;

public class CodeUtils {

    /**
     * @param input: 'Phạm Thị Lan Anh'
     * @return res: 'anhptl'
     */
    public static String convertStringCode(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String[] words = input.split(" ");
        String lastName = removeAccents(words[words.length - 1].toLowerCase());
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            initials.append(removeAccents(words[i].substring(0, 1).toLowerCase()));
        }
        return lastName + initials;
    }

    private static String removeAccents(String str) {
        if (str == null) {
            return "";
        }
        String normalized = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replace("đ", "d")
                .replace("Đ", "D");
    }

    public static void main(String[] args) {
        String input = "Phạm Thị Lan Anh";
        String result = convertStringCode(input);
        System.out.println(result);
    }
}
