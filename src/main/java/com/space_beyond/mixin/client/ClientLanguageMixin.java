package com.space_beyond.mixin.client;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.logging.LogUtils;
import com.space_beyond.BeyondMod;

import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Ueberschreibungen bestehender Uebersetzungen anderer Mods ({@code assets/space_beyond/lang_override/<sprache>.json}),
 * z.B. die echten Namen der Himmelskoerper (Kerbol -> Sun, Duna -> Mars). Gewoehnliche Lang-Dateien verschiedener
 * Namensraeume werden in keiner festen Reihenfolge zusammengefuehrt; diese Datei wird immer zuletzt angewandt.
 */
@Mixin(ClientLanguage.class)
public abstract class ClientLanguageMixin {

    @ModifyVariable(method = "loadFrom", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;", remap = false))
    private static Map<String, String> beyond$applyOverrides(Map<String, String> map, ResourceManager manager, List<String> filenames, boolean defaultRightToLeft) {
        for (String lang : filenames) {
            for (Resource resource : manager.getResourceStack(BeyondMod.id("lang_override/" + lang + ".json"))) {
                try (InputStream in = resource.open()) {
                    Language.loadFromJson(in, map::put);
                } catch (Exception e) {
                    LogUtils.getLogger().warn("[space_beyond] Language override {} unreadable: {}", lang, e.toString());
                }
            }
        }
        return map;
    }
}
