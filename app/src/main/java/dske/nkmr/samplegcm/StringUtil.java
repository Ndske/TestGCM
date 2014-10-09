package dske.nkmr.samplegcm;

/**
 * Stringに関するユーティリティクラス.
 */
public class StringUtil {

    private StringUtil() {
    }

    public static final String EMPTY = "";

    public static boolean isNullOrEmpty(String tgt){
        if(tgt == null){
            return true;
        }
        return tgt.isEmpty();
    }

    public static String stringCat(String... str){
        StringBuilder sb = new StringBuilder();
        for (String s : str){
            sb.append(s);
        }
        return new String(sb);
    }
}
