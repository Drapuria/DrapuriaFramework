package net.drapuria.framework.language;


import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.logging.log4j.core.util.FileUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
  This container holds every information about the provided languages by the holder (eg a plugin/module)
 */
public class LanguageContainer {

    private static final Pattern LANG_FILE = Pattern.compile("^[^-]{2,3}-[^-]{2,3}(-[^-]{2,3})?$");

    private final LanguageHolder<?> holder;
    private final Set<LanguageFile> languageFiles = new HashSet<>();

    public LanguageContainer(LanguageHolder<?> holder) {
        this.holder = holder;
        this.initContainer();
    }

    public LanguageHolder<?> getHolder() {
        return holder;
    }

    public Optional<LanguageFile> findLanguageFileByIsoCode(final String isoCode) {
        return this.languageFiles.stream()
                .filter(languageFile -> languageFile.getIsoCode().equals(isoCode))
                .findFirst();
    }

    private void initContainer() {
        final File languageFolder = this.holder.languageFolder();
        if (!languageFolder.exists())
            languageFolder.mkdirs();

        Arrays.stream(languageFolder.listFiles())
                .filter(file -> !file.isDirectory()
                        && file.getName().endsWith(".properties")
                        && LANG_FILE.matcher(file.getName().split(".")[0]).matches())
                .forEach(file -> this.languageFiles.add(new LanguageFile(file.getName().split(".")[0], file)));
        try {
            this.migrateLanguageResources();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void migrateLanguageResources() throws IOException {
        final List<File> resources = this.findLanguageResources();
        for (final File languageResource : resources) {
            final String isoCode = languageResource.getName().split(".")[0];
            final Optional<LanguageFile> savedLanguage = this.findLanguageFileByIsoCode(isoCode);
            if (!savedLanguage.isPresent()) {
                this.copyResourceAndCreateLanguageFile(languageResource, isoCode);
            } else {
                this.addMissingStrings(languageResource, savedLanguage.get());
            }
        }
    }

    private void copyResourceAndCreateLanguageFile(final File languageResource, final String isoCode) {
        final File generatedLanguageFile = new File(this.holder.languageFolder(), isoCode + ".properties");
        try {
            Files.copy(languageResource.toPath(), new FileOutputStream(generatedLanguageFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.languageFiles.add(new LanguageFile(isoCode, generatedLanguageFile));
    }

    private void addMissingStrings(final File languageResource, final LanguageFile savedLanguageFile) throws IOException {
        final Properties resourceProperties = new Properties();
        final Properties savedProperties = new Properties();
        resourceProperties.load(new FileInputStream(languageResource.getAbsolutePath()));
        savedProperties.load(new FileInputStream(savedLanguageFile.getFile().getAbsolutePath()));
        boolean edited = false;
        for (String resourceKey : resourceProperties.keySet().stream().map(o -> (String) o).collect(Collectors.toSet())) {
            if (savedProperties.keySet().stream()
                    .map(o -> (String) o)
                    .noneMatch(s -> s.equals(resourceKey))) {
                savedProperties.put(resourceKey, resourceProperties.getProperty(resourceKey));
                edited = true;
            }
        }
        if (edited) {
            savedProperties.store(new FileOutputStream(savedLanguageFile.getFile()), savedLanguageFile.getIsoCode());
        }
        resourceProperties.clear();
        languageFiles.clear();
    }

    private List<File> findLanguageResources() {
        try {
            final File file = new File(this.holder.holder().getClass().getResource("").toURI());
            if (file == null)
                throw new FileNotFoundException("Cannot find resources.");
            return Arrays.stream(file.listFiles())
                    .filter(listedFile -> listedFile.getName().endsWith(".properties"))
                    .filter(listedFile -> !listedFile.isDirectory())
                    .filter(listedFile -> LANG_FILE.matcher(listedFile.getName().split(".")[0]).matches())
                    .collect(Collectors.toList());
        } catch (URISyntaxException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}