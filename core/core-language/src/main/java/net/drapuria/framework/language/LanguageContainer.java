package net.drapuria.framework.language;

import jdk.internal.foreign.ResourceScopeImpl;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class LanguageContainer {

    private static final Pattern LANG_FILE = Pattern.compile("^[^-]{2,3}-[^-]{2,3}(-[^-]{2,3})?$");

    private final LanguageHolder<?> holder;

    public LanguageContainer(LanguageHolder<?> holder) {
        this.holder = holder;
    }
    private void initContainer() {
        final File languageFolder = this.holder.languageFolder();
        if (!languageFolder.exists())
            languageFolder.mkdirs();

        Arrays.stream(languageFolder.listFiles())
                .filter(file -> !file.isDirectory()
                        && file.getName().endsWith(".properties")
                        && LANG_FILE.matcher(file.getName()).matches())
                .forEach(file -> {

                });
    }

    private void copyResources() {

    }
}
