package com.space_beyond.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;

import dev.architectury.platform.Platform;

/**
 * Einstellungen von Beyond ({@code config/space_beyond/common.json}, global wie bei Space Modernized).
 * Zugriff: {@code BeyondConfig.get().feld}. Fehlende Schluessel bekommen den Standardwert, die Datei wird danach
 * vollstaendig neu geschrieben.
 */
public class BeyondConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static BeyondConfig instance = new BeyondConfig();

    /**
     * Planeten muessen der Reihe nach freigeschaltet werden (Mun -> ... -> Wemburos): Raketen brauchen ein Triebwerk der
     * Zielstufe (Mk.N, gebaut aus dem Boss-Kern des vorherigen Planeten), Stationen den besiegten vorherigen Boss.
     */
    public boolean progressionLocks = true;

    public static BeyondConfig get() {
        return instance;
    }

    public static Path file() {
        return Platform.getConfigFolder().resolve("space_beyond").resolve("common.json");
    }

    public static void load() {
        Path f = file();
        if (Files.isRegularFile(f)) {
            try (Reader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
                BeyondConfig read = GSON.fromJson(r, BeyondConfig.class);
                if (read != null) instance = read;
            } catch (IOException | RuntimeException e) {
                LOGGER.warn("[space_beyond] Config {} unreadable, using defaults: {}", f, e.toString());
            }
        }
        save();
    }

    public static void save() {
        Path f = file();
        try {
            Files.createDirectories(f.getParent());
            try (Writer w = Files.newBufferedWriter(f, StandardCharsets.UTF_8)) {
                GSON.toJson(instance, w);
            }
        } catch (IOException e) {
            LOGGER.warn("[space_beyond] Config {} not writable: {}", f, e.toString());
        }
    }
}
