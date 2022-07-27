package module.crack.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 架构枚举
 */
public enum ArchEnum {

    X86_32("i386", "i686", "x86"),
    X86_64("amd64", "x86_64"),
    ARMv7("arm"),
    ARMv8("aarch64", "arm64");

    private final Set<String> patterns;

    private ArchEnum(final String... patterns) {
        this.patterns = new HashSet<String>(Arrays.asList(patterns));
    }

    public boolean isCurrent() {
        final String osArch = System.getProperty("os.arch");
        return patterns.contains(osArch);
    }
    
    
    
}
