package a4.papers.chatfilter.chatfilter.shared;

public class LowerCaseReplace
{
    public static String replace(String source, String target, String replacement)
    {
        if (source == null || target == null || replacement == null || target.isEmpty()) {
            return source;
        }
        
        String searchString = target.toLowerCase();
        String sourceLower = source.toLowerCase();
        
        int idx = sourceLower.indexOf(searchString);
        if (idx == -1) {
            return source;
        }
        
        StringBuilder sbSource = new StringBuilder(source.length());
        int lastIdx = 0;
        
        while (idx != -1) {
            sbSource.append(source, lastIdx, idx);
            sbSource.append(replacement);
            lastIdx = idx + searchString.length();
            idx = sourceLower.indexOf(searchString, lastIdx);
        }
        sbSource.append(source, lastIdx, source.length());
        
        return sbSource.toString();
    }

}
