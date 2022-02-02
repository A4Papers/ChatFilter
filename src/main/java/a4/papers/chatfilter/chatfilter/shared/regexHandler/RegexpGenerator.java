package a4.papers.chatfilter.chatfilter.shared.regexHandler;

public class RegexpGenerator {

    public static String generateRegexp(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String chars = String.valueOf(c);
            if (chars.equals(" ")) {
                String reg = "(%SPACE%+)+(\\W|_)*";
                stringBuilder.append(reg);
            }
            if (chars.equals("#")) {
                String reg = "#";
                stringBuilder.append(reg);
            } else {
                String reg = "(" + c + ")+(\\W|_)*";
                stringBuilder.append(reg);
            }
        }
        String regexString = toLeetSpeak(stringBuilder.toString().replaceAll("\\[|\\]|,|\\s", "").replaceAll("%SPACE%", " "));
        if (!regexString.contains("#")) {
            regexString = "(" + regexString + ")";
        } else if (regexString.startsWith("#") && regexString.endsWith("#")) {
            regexString = regexString.replace("#", "");
            regexString = "\\b(" + regexString + ")\\b";
        } else if (regexString.startsWith("#")) {
            regexString = regexString.replace("#", "\\b(");
            regexString = regexString + ")";
        } else if (regexString.endsWith("#")) {
            regexString = regexString.replace("#", ")\\b");
            regexString = "(" + regexString;
        }
        return regexString;
    }

    static String toLeetSpeak(String speak) {
        StringBuilder sb = new StringBuilder(speak.length());
        for (char c : speak.toCharArray()) {
            switch (c) {
                case 'a':
                    sb.append("@|a");
                    break;
                case 'b':
                    sb.append("8|b");
                    break;
                case 'c':
                    sb.append("\\(|c");
                    break;
                case 'e':
                    sb.append("3|e");
                    break;
                case 'g':
                    sb.append("6|g");
                    break;
                case 'h':
                    sb.append("h");
                    break;
                case 'i':
                    sb.append("!|i");
                    break;
                case 'l':
                    sb.append("1|l");
                    break;
                case 'o':
                    sb.append("0|o");
                    break;
                case 's':
                    sb.append("\\$|s");
                    break;
                case 't':
                    sb.append("7|t");
                    break;
                case 'z':
                    sb.append("2|z");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }
}




