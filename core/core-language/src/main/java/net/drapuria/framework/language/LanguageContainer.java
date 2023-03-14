package net.drapuria.framework.language;


import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
  This container holds every information about the provided languages by the holder (eg a plugin/module)
 */
public class LanguageContainer {

    private static final Pattern LANG_FILE = Pattern.compile("[a-z]{2}_[A-Z]{2}");

    @Getter private final LanguageService service;
    private final ILanguageComponent<?> component;
    private final Set<LanguageFile> languageFiles = new HashSet<>();

    public LanguageContainer(final LanguageService service, ILanguageComponent<?> component) {
        this.component = component;
        this.service = service;
        System.out.println("loading language container for " + component.holder().getClass().getName());
        this.initContainer();
        this.service.getResourceRepository().loadContainer(this);
    }

    public ILanguageComponent<?> getComponent() {
        return component;
    }

    public Optional<LanguageFile> findLanguageFileByIsoCode(final String isoCode) {
        return this.languageFiles.stream()
                .filter(languageFile -> languageFile.getIsoCode().equals(isoCode))
                .findFirst();
    }

    private void initContainer() {
        final File languageFolder = this.component.languageFolder();
        if (!languageFolder.exists())
            languageFolder.mkdirs();

        Arrays.stream(languageFolder.listFiles())
                .filter(file -> !file.isDirectory()
                        && file.getName().endsWith(".properties")
                        && LANG_FILE.matcher(file.getName().split("\\.")[0]).matches())
                .forEach(file -> this.languageFiles.add(new LanguageFile(this, file.getName().split("\\.")[0], file)));
        try {
            this.migrateLanguageResources();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void migrateLanguageResources() throws IOException {
        final List<File> resources = this.findLanguageResources();
        for (final File languageResource : resources) {
            String isoCode = languageResource.getName().split("\\.")[0];
            if (component.langFilePrefix() != null && !component.langFilePrefix().isEmpty())
                isoCode = isoCode.replaceFirst(component.langFilePrefix(), "");
            final Optional<LanguageFile> savedLanguage = this.findLanguageFileByIsoCode(isoCode);
            if (!savedLanguage.isPresent()) {
                this.copyResourceAndCreateLanguageFile(languageResource, isoCode);
            } else {
                this.addMissingStrings(languageResource, savedLanguage.get());
            }
        }
    }

    private void copyResourceAndCreateLanguageFile(final File languageResource, final String isoCode) {
        final File generatedLanguageFile = new File(this.component.languageFolder(), isoCode + ".properties");
        System.out.println("jar path: " + getJarPath());
        System.out.println(languageResource.getPath().replaceFirst(getJarPath() + "/", ""));
        System.out.println("RESOURCE: " + component.getClass().getClassLoader().getResource(languageResource.getPath().replaceFirst(getJarPath() + "/", "")));
        try {
            Files.copy(Objects.requireNonNull(component.getClass().getClassLoader().getResourceAsStream(languageResource.getPath().replaceFirst(getJarPath() + "/", ""))), generatedLanguageFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.languageFiles.add(new LanguageFile(this, isoCode, generatedLanguageFile));
    }

    private void addMissingStrings(final File languageResource, final LanguageFile savedLanguageFile)
            throws IOException {
        final Properties resourceProperties = new Properties();
        final Properties savedProperties = new Properties();
        resourceProperties.load(Objects.requireNonNull(component.getClass().getClassLoader().getResourceAsStream(languageResource.getPath().replaceFirst(getJarPath() + "/", ""))));
        savedProperties.load(Files.newInputStream(Paths.get(savedLanguageFile.getFile().getAbsolutePath())));
        boolean edited = false;
        for (String resourceKey : resourceProperties.keySet().stream().map(o -> (String) o)
                .collect(Collectors.toSet())) {
            if (savedProperties.keySet().stream()
                    .map(o -> (String) o)
                    .noneMatch(s -> s.equals(resourceKey))) {
                savedProperties.put(resourceKey, resourceProperties.getProperty(resourceKey));
                edited = true;
            }
        }
        if (edited) {
            savedProperties.store(Files.newOutputStream(savedLanguageFile.getFile().toPath()), savedLanguageFile.getIsoCode());
        }
        resourceProperties.clear();
        savedProperties.clear();
    }

    private List<File> findLanguageResources() {
        try {
            final File file = new File(this.getJarPath());
            System.out.println(file.getPath());
            System.out.println("CHANGED");
            if (!file.exists())
                throw new FileNotFoundException("Cannot find resources.");
            final List<File> files = new ArrayList<>();
            final String resPath = this.getResPath();
            try (final JarFile jarFile = new JarFile(file)) {
                JarEntry entry;
                Enumeration<JarEntry> entries = jarFile.entries();
                while ((entry = entries.nextElement()) != null) {
                    final String[] splitted = entry.getName().split("/");
                    final String fileName = splitted[splitted.length - 1];
                    if (fileName.endsWith(".properties") && LANG_FILE.matcher(fileName).find()) {
                        files.add(new File(resPath.replaceFirst("file:",  ""), entry.getName()));
                    }
                    if (!entries.hasMoreElements())
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return files;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getJarPath() {
        String resPath = this.getResPath();
        return resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
    }

    private String getResPath() {
        final CodeSource codeSource = this.component.holder().getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        return resource.getPath().replace("%20", " ");
    }

    public Set<LanguageFile> getLanguageFiles() {
        return languageFiles;
    }
}