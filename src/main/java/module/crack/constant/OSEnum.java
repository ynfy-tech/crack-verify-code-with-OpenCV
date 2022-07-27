package module.crack.constant;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public enum OSEnum {
    OSX("^[Mm]ac OS X$"),
    LINUX("^[Ll]inux$"),
    WINDOWS("^[Ww]indows.*");

    private final Set<Pattern> patterns;

    private OSEnum(final String... patterns) {
        this.patterns = new HashSet<Pattern>();

        for (final String pattern : patterns) {
            this.patterns.add(Pattern.compile(pattern));
        }
    }

    private boolean is(final String id) {
        for (final Pattern pattern : patterns) {
            if (pattern.matcher(id).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是当前系统
     * @param id
     * @return
     */
    public boolean isCurrent() {
        final String osName = System.getProperty("os.name");
        return is(osName);
    }
}
