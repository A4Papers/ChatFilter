package a4.papers.chatfilter.chatfilter.shared.regexHandler;

import a4.papers.chatfilter.chatfilter.ChatFilter;

public class RegexpGenerator {
    static ChatFilter chatFilter;

    public RegexpGenerator(ChatFilter instance) {
        chatFilter = instance;
    }

    public String generateRegexp(String s) {
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

    String toLeetSpeak(String speak) {
        StringBuilder sb = new StringBuilder(speak.length());
        if (chatFilter.enableLeetSpeak)
            for (char c : speak.toCharArray()) {
                switch (c) {
                    case 'a':
                        sb.append("@|a|4");
                        break;
                    case 'b':
                        sb.append("8|b");
                        break;
                    case 'c':
                        sb.append("\\(|c");
                        break;
                    case 'd':
                        sb.append("d");
                        break;
                    case 'e':
                        sb.append("3|e");
                        break;
                    case 'f':
                        sb.append("f");
                        break;
                    case 'g':
                        sb.append("6|g");
                        break;
                    case 'h':
                        sb.append("h");
                        break;
                    case 'i':
                        sb.append("!|i|1");
                        break;
                    case 'j':
                        sb.append("j");
                        break;
                    case 'k':
                        sb.append("k");
                        break;
                    case 'l':
                        sb.append("1|l");
                        break;
                    case 'm':
                        sb.append("m");
                        break;
                    case 'n':
                        sb.append("n");
                        break;
                    case 'o':
                        sb.append("0|o");
                        break;
                    case 'p':
                        sb.append("p");
                        break;
                    case 'q':
                        sb.append("q");
                        break;
                    case 'r':
                        sb.append("r");
                        break;
                    case 's':
                        sb.append("\\$|s|5");
                        break;
                    case 't':
                        sb.append("7|t");
                        break;
                    case 'u':
                        sb.append("u");
                        break;
                    case 'v':
                        sb.append("v");
                        break;
                    case 'w':
                        sb.append("w");
                        break;
                    case 'x':
                        sb.append("x");
                        break;
                    case 'y':
                        sb.append("y");
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




