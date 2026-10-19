package com.space_beyond.machine.misc.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.hbm_m.network.NBTControlPacket;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.space_m.SpaceMod;
import com.space_beyond.celestial.AsteroidBelts;
import com.space_m.celestial.AstronomyUtil;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem;
import com.space_beyond.celestial.StarSystem;
import com.space_m.celestial.trait.CBT_Impact;
import com.space_m.celestial.trait.CBT_Lights;
import com.space_m.client.sky.AtmosphereRenderUtil;
import com.space_m.client.sky.SkyGL;
import com.space_m.client.sky.SkyHooks;
import com.space_m.client.sky.SkyShader;
import com.space_m.client.sky.SkyShaders;
import com.space_m.client.sky.SkyTess;
import com.space_m.dim.SpaceDimensions;
import com.space_m.item.SpaceMaterialItems;
import com.space_m.machine.misc.ContainerStardar;
import com.space_m.machine.misc.TileEntityMachineStardar;
import com.space_m.rocket.ItemVOTVdrive;
import com.space_m.rocket.SpaceRocketItems;
import com.space_m.satellite.ClientSatellite;
import com.space_m.satellite.SatelliteClientData;
import com.space_m.satellite.SatelliteOrbit;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * 1:1 NTM Space {@code GUIMachineStardar} (176x256, {@code gui_stardar.png}).
 *
 * <p><b>Systemkarte</b> (158x108 bei 9,9 auf {@code starmap3.png}, isometrisch {@code ISO_X 0,70 / ISO_Y 0,35}):
 * Sonnensystem um {@code kerbol} mit Keplerbahnen (Planetenbahnen ganz, Mondbahnen als Vorder-/Rueckhaelfte und nur bei
 * fokussiertem Planeten bzw. Mond), Koerper mit Ringen, Atmosphaeren-Leuchtrand, Drehung bzw. U-Versatz, Shader
 * (Atmosphaere, Sichel samt Mondfinsternis, emissive Atmosphaere, Nachtlichter, Blitze, Nuke-Schock), Einschlag-Lava,
 * Beschriftungen (Monde nur im Fokus-Planetensystem, Skalierung 0,6), kuenstliche Satelliten des aktuellen Koerpers
 * (Bahnhaelften, Symbol {@code sat_scanner}, Tooltip Besitzer/Geschwindigkeit/Hoehe/Inklination/Phase).
 * Ziehen verschiebt die Karte, Mausrad zoomt (0,5-180, Faktor 1,1, im Fokus begrenzt auf die Mondbahnen bzw. die
 * Detailstufe), Klick auf einen Koerper fokussiert ihn (Kamerafahrt 500 ms, folgt dem Koerper), Rechtsklick zurueck zum
 * Elternkoerper bzw. zur Gesamtansicht (420 ms), Klick auf einen Satelliten folgt ihm.
 * Zweiter Klick auf den fokussierten, landbaren Koerper mit leerem Laufwerk -> Landemodus.
 *
 * <p><b>Landemodus</b>: das Laufwerk wird auf den Koerper geschrieben ({@code pid}, auf dem eigenen Koerper mit den
 * StarDar-Koordinaten), danach zeigt die Karte die 256x256-Hoehenkarte des Servers (2 Pixel je Block, Gruen = Hoehe
 * mod 16) zum Verschieben; die 5x5-Umgebung unter der Maus muss eben sein, Klick setzt die Landekoordinaten
 * ({@code px}/{@code pz}). Ein beschriebenes, landbares Laufwerk im Slot oeffnet den Landemodus selbst, Entnahme
 * schliesst ihn; Rechtsklick verlaesst ihn.
 *
 * <p>Knoepfe: (129,123) aktuellen Koerper fokussieren, (129,143) neue Orbitalstation auf das Laufwerk,
 * (149,143) aktuellen Koerper (mit StarDar-Koordinaten) auf das Laufwerk.
 *
 * <p>Abweichungen (technisch): GL11/Tessellator ueber {@link SkyGL}/{@link SkyTess} (Shader {@link SkyShaders}),
 * Scissor ueber {@link GuiGraphics#enableScissor}, {@code Mouse.isButtonDown(0)} ueber GLFW, LWJGL2-Mausereignisse
 * ({@code handleMouseInput}) auf die einzelnen Ereignisse der Screen-API verteilt (Reihenfolge wie im Original: erst
 * {@code mouseMovedOrUp}/{@code mouseClickMove}, dann die Landemodus-Begrenzung), Dimensionsnummern als Dimensions-ID.
 * Die Bodentextur wird beim Schliessen freigegeben (Original: nie).
 *
 * <p><b>Erweiterung Sternsysteme</b> (kein Original): das System des aktuellen Koerpers liegt wie bisher in der
 * Kartenmitte, alle weiteren {@link StarSystem}e liegen entsprechend ihrer Galaxie-Position daneben
 * ({@code +X} = rechts, {@link #STARDAR_LY_TO_MAP} Karteneinheiten je Lichtjahr), mit Namen am Stern. Sterne ausserhalb
 * des Ausschnitts erscheinen als Randmarke (Name + Abstand); Klick auf Randmarke oder fremden Stern fokussiert ihn.
 * Koerper anderer Systeme zeigen "Interstellar gesperrt" und koennen nicht auf ein Laufwerk geschrieben werden.
 * <p><b>Modernisierung</b> (kein Original, Funktionen unveraendert): Vollbild-Karte mit prozeduralem Nebel und
 * funkelndem Sternfeld (Parallaxe), leuchtenden Sternen (Korona), Bahnen mit Leuchtband und Schweif hinter dem Koerper,
 * Beschriftungen ohne Ueberlappung, Auswahlrahmen und "Hier"-Marke, Kopfzeile mit klickbaren Brotkrumen, Navigator
 * (Systeme/Planeten/Monde, Klick = Fokus), Infotafel je Koerper (Sonnenlicht, Durchmesser, Schwerkraft, Tag, Atmosphaere,
 * Umlauf, Erze, Bedrock-Erze; unbesuchte Koerper nur "???", s. {@link com.space_beyond.celestial.VisitedBodies}),
 * Statusleiste mit den Hinweisen, aufklappbares Laufwerk-Dock (Slot + Inventar), weiches Zoomen, Ziehen 1:1,
 * Tasten (WASD/Pfeile, +/-, Backspace, H, Tab, I).
 * Original-Klasse: GUIMachineStardar
 */
public class GUIBeyondStarMap extends AbstractContainerScreen<ContainerStardar> {

    public static final ResourceLocation texture = SpaceMod.id("textures/gui/machine/gui_stardar.png");
    private static final ResourceLocation ringTexture = SpaceMod.id("textures/misc/space/rings.png");
    private static final ResourceLocation impactTexture = SpaceMod.id("textures/misc/space/impact.png");
    private static final ResourceLocation defaultMask = SpaceMod.id("textures/misc/space/default_mask.png");
    private static final ResourceLocation satelliteTextureDefault = ResourceLocation.fromNamespaceAndPath("hbm_m", "textures/item/sat_scanner.png");
    private static final ResourceLocation[] citylights = new ResourceLocation[] {
        SpaceMod.id("textures/misc/space/citylights_0.png"),
        SpaceMod.id("textures/misc/space/citylights_1.png"),
        SpaceMod.id("textures/misc/space/citylights_2.png"),
        SpaceMod.id("textures/misc/space/citylights_3.png"),
    };

    /** Kartenflaeche auf dem Bildschirm (Vollbild, rechts die Infotafel; s. {@link #layout()}). */
    private int mapX = 0;
    private int mapY = 0;
    private int mapW = 158;
    private int mapH = 108;

    private static final float MAP_TEX_W = 1024F;
    private static final float MAP_TEX_H = 1024F;
    private static final float SYSTEM_LAYOUT_REF_TEX = 256F;

    private static final float MIN_ZOOM = 0.5F;
    private static final float MAX_ZOOM = 180.0F;
    private static final float ZOOM_STEP = 1.2F;

    private static final float ISO_X = 0.70F;
    private static final float ISO_Y = 0.35F;
    private static final float STARDAR_BODY_SCALE = 0.45F;
    private static final float STARDAR_MOON_VISUAL_SCALE = 0.82F;
    private static final float STARDAR_SYSTEM_RADIUS_FRACTION = 0.50F;
    private static final float SATELLITE_ICON_BODY_SCALE = 0.75F;
    private static final float SATELLITE_ORBIT_RADIUS_SCALE = 1.5F;
    private static final long SATELLITE_CYCLE_MS = 600L * 50L;

    /** Erweiterung: Karteneinheiten je Lichtjahr (Sporos bei 5 Lj -> 300 in U und V, ~3x die Bahn des aeussersten Planeten). */
    private static final float STARDAR_LY_TO_MAP = 60F;
    /** Systemmitten bleiben so weit vom Kartenrand weg, dass ihre Planetenbahnen (128 Einheiten) noch hineinpassen. */
    private static final float SYSTEM_EDGE_MARGIN = 128F;

    @Nullable private final TileEntityMachineStardar star;
    private final DynamicTexture groundTexture;
    private final ResourceLocation groundMap;
    private final Map<ResourceLocation, Boolean> textureAlphaCache = new HashMap<>();

    private final List<BodyRenderInfo> renderedBodies = new ArrayList<>();
    private final List<BodyLabelRenderInfo> renderedBodyLabels = new ArrayList<>();
    private final List<SatelliteRenderInfo> renderedSatellites = new ArrayList<>();
    private final List<SystemLayout> systemLayouts = new ArrayList<>();
    private final List<SystemIndicator> systemIndicators = new ArrayList<>();
    private final BodyPosition trackedBodyPosition = new BodyPosition();
    private final BodyPosition trackedSatellitePosition = new BodyPosition();

    private final CelestialBody currentBody;
    private CelestialBody focusedBody;
    private CelestialBody focusAnimationBody;
    private BodyRenderInfo hoveredBody;
    private SatelliteRenderInfo hoveredSatellite;
    private SatelliteRenderInfo focusedSatellite;
    private Integer focusedSatelliteFrequency;
    private CelestialBody landingBody;

    private boolean landingMode;
    private boolean hadFullDrive;
    private int lastFullDriveMeta = -1;

    private boolean draggingMap;
    private int dragLastX;
    private int dragLastY;

    private int mX;
    private int mY;
    private int lX;
    private int lY;
    private int sX;
    private int sY;
    private boolean draggingSurface;

    private float surfaceX;
    private float surfaceY;
    private float velocityX;
    private float velocityY;

    private float mapU = 0F;
    private float mapV = 0F;
    private float mapZoom = 1.0F;

    private boolean focusAnimationActive = false;
    private boolean focusAnimationToBody = false;
    private float focusAnimStartCenterU = 0F;
    private float focusAnimStartCenterV = 0F;
    private float focusAnimStartZoom = 1.0F;
    private float focusAnimTargetCenterU = 0F;
    private float focusAnimTargetCenterV = 0F;
    private float focusAnimTargetZoom = 1.0F;
    private boolean focusAnimTrackingInitialized = false;
    private float focusAnimTrackingOffsetU = 0F;
    private float focusAnimTrackingOffsetV = 0F;
    private long focusAnimStartTimeMs = 0L;
    private int focusAnimDurationMs = 500;

    private int lastMouseX;
    private int lastMouseY;

    /** Aktuelle Zeichenflaeche (nur waehrend {@link #render}). */
    private GuiGraphics g;

    public GUIBeyondStarMap(ContainerStardar menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.star = menu.stardar;

        this.imageWidth = 176;
        this.imageHeight = 256;

        this.currentBody = CelestialBody.getBody(star != null && star.getLevel() != null ? star.getLevel() : playerInventory.player.level());
        this.focusedBody = null;

        groundTexture = new DynamicTexture(256, 256, true);
        groundMap = net.minecraft.client.Minecraft.getInstance().getTextureManager().register("stardar_ground_map", groundTexture);

        setViewFromCenter(MAP_TEX_W * 0.5F, MAP_TEX_H * 0.5F, mapZoom);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateLandingModeFromDrive();
    }

    @Override
    public void removed() {
        super.removed();
        minecraft.getTextureManager().release(groundMap);
    }

    // ── Neue Oberflaeche: Vollbild-Karte, Kopfzeile, Navigator, Infotafel, Statusleiste, Laufwerk-Dock ─────────────

    /** Breite der Infotafel rechts. */
    private static final int CARD_W = 172;
    /** Breite des Navigators links. */
    private static final int NAV_W = 112;
    private static final int ACCENT = 0xFF5FE0FF;
    private static final int PANEL = 0xE00A0E18;
    private static final int PANEL_EDGE = 0x5560C8FF;
    private static final int TEXT = 0xFFE8F0FF;
    private static final int TEXT_DIM = 0xFF8C9AB4;

    /** Knopf/Klickflaeche der Oberflaeche (jedes Bild neu aufgebaut). */
    private record UiButton(int x, int y, int w, int h, Runnable action, @Nullable List<Component> tooltip) {
        boolean contains(int mx, int my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    /** Klickflaechen und Tafeln des letzten Bildes (fuer Maus) und die im aktuellen Bild gesammelten. */
    private final List<UiButton> uiButtons = new ArrayList<>();
    private final List<int[]> uiBlocks = new ArrayList<>();
    private final List<UiButton> newButtons = new ArrayList<>();
    private final List<int[]> newBlocks = new ArrayList<>();
    private final List<String> hintTexts = new ArrayList<>();
    private final List<Integer> hintColors = new ArrayList<>();
    private final List<float[]> placedLabels = new ArrayList<>();
    @Nullable private List<Component> pendingTooltip;
    private ItemStack pendingItemTooltip = ItemStack.EMPTY;
    @Nullable private CelestialBody navHoverBody;
    private boolean dockOpen;
    private boolean navOpen = true;
    private boolean viewInitialized;
    private long lastFrameNs;
    private float frameDt = 0.016F;
    private float uiTime;
    private float zoomTarget = -1F;
    /** Verschiebung der Kartenmitte nach rechts, solange die Koerperliste offen ist (weich nachgefuehrt). */
    private float viewShiftX;
    /** Zeitraffer der Karte: Pause, Echtzeit (wahre Stellungen), 100x, 1000x, 10000x. Bleibt zwischen Oeffnungen erhalten. */
    private static final int[] TIME_SPEEDS = {0, 1, 100, 1000, 10000};
    private static int timeSpeedIndex = 3;
    /** Kartenzeit in Spielticks (NaN = noch nicht gesetzt); bei Echtzeit = Spielzeit. */
    private double mapTicks = Double.NaN;
    private double realTicks;
    private int zoomAnchorX;
    private int zoomAnchorY;
    private double currentWorldTicks;

    @Override
    protected void init() {
        super.init();
        layout();
        if (!viewInitialized) {
            viewInitialized = true;
            viewShiftX = viewShiftTarget();
            setViewFromCenter(MAP_TEX_W * 0.5F, MAP_TEX_H * 0.5F, overviewZoom());
        }
    }

    /** Karte fuellt den Bildschirm links der Infotafel; Laufwerk + Inventar (Slots der alten GUI ab y 118) unten links. */
    private void layout() {
        mapX = 0;
        mapY = 0;
        mapW = Math.max(120, width - CARD_W - 8);
        mapH = height;
        leftPos = dockOpen ? 8 : -10000; // geschlossen: Slots ausserhalb des Bildschirms
        topPos = height - 258;
    }

    /** Zoom der Gesamtansicht: die aeusserste Bahn fuellt die freie Flaeche zwischen Koerperliste und Infotafel. */
    private float overviewZoom() {
        float[] ext = systemExtentAt1x();
        float freeW = mapW - (navOpen ? NAV_W + 6 : 0);
        if (ext[0] <= 0F || ext[1] <= 0F) {
            float byW = freeW * 0.80F / (256F * ISO_X * 1.42F);
            float byH = mapH * 0.80F / (256F * ISO_Y * 1.42F);
            return clampF(Math.max(1.0F, Math.min(byW, byH)), MIN_ZOOM, MAX_ZOOM);
        }
        float byW = (freeW * 0.5F - 14F) / ext[0];
        float byH = (mapH * 0.5F - 28F) / ext[1];
        return clampF(Math.min(byW, byH), MIN_ZOOM, MAX_ZOOM);
    }

    /** Halbe Bildschirmausdehnung (x, y bei Zoom 1) aller Planetenbahnen des eigenen Systems um den Stern. */
    private float[] systemExtentAt1x() {
        StarSystem system = StarSystem.of(currentBody);
        if (system == null) system = StarSystem.home();
        if (system == null || system.star == null || system.star.satellites == null) return new float[] {0F, 0F};
        CelestialBody starBody = system.star;
        float scale = getOrbitScalePxPerKm(starBody);
        float ex = getBodySizePxAt1x(starBody) * 0.5F, ey = ex;
        for (CelestialBody child : starBody.satellites) {
            if (child == null || child.semiMajorAxisKm <= 0F) continue;
            for (int i = 0; i < 64; i++) {
                float[] o = calculateOrbitOffsetPx(starBody, child, 2D * Math.PI * i / 64D, scale);
                ex = Math.max(ex, Math.abs((o[0] - o[1]) * ISO_X));
                ey = Math.max(ey, Math.abs((o[0] + o[1]) * ISO_Y));
            }
        }
        return new float[] {ex, ey};
    }

    private float viewShiftTarget() {
        return navOpen && !landingMode ? (NAV_W + 6) * 0.5F : 0F;
    }

    /** Bildschirm-x der Kartenmitte (Projektionszentrum). */
    private float mapCenterX() {
        return mapX + mapW * 0.5F + viewShiftX;
    }

    private void toggleDock() {
        playButton();
        dockOpen = !dockOpen;
        layout();
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        return false; // Klicks auf die Karte werfen keine Gegenstaende weg
    }

    /** Liegt der Punkt auf einem Bedienelement (Tafel, Navigator, Dock ...)? */
    private boolean isOverUi(int mx, int my) {
        for (int[] r : uiBlocks) if (mx >= r[0] && mx < r[0] + r[2] && my >= r[1] && my < r[1] + r[3]) return true;
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.g = graphics;
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        long now = System.nanoTime();
        frameDt = lastFrameNs == 0L ? 0.016F : Math.min(0.1F, (now - lastFrameNs) / 1.0e9F);
        lastFrameNs = now;
        uiTime = (System.currentTimeMillis() % 3_600_000L) / 1000F;
        pendingTooltip = null;
        pendingItemTooltip = ItemStack.EMPTY;

        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
        drawSatelliteTooltip(mouseX, mouseY);
        if (!pendingItemTooltip.isEmpty()) graphics.renderTooltip(font, pendingItemTooltip, mouseX, mouseY);
        else if (pendingTooltip != null) graphics.renderComponentTooltip(font, pendingTooltip, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        this.g = graphics;
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        newButtons.clear();
        newBlocks.clear();
        hintTexts.clear();
        hintColors.clear();
        if (landingMode) navHoverBody = null;

        graphics.fill(0, 0, width, height, 0xFF03040A);
        graphics.flush();

        SkyGL.begin(graphics.pose().last().pose());
        drawSpaceBackground();
        pushScissor(0, 0, width, height);
        if (landingMode) {
            drawSurfaceMap();
        } else {
            drawSystemMap(partialTicks);
        }
        popScissor();
        SkyGL.reset();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.flush();

        // Oberflaeche (Reihenfolge = Klickvorrang von hinten nach vorn: Bloecke zuerst sammeln)
        if (landingMode) collectLandingHints();
        else {
            drawSelectionMarkers();
            collectMapHints();
        }
        drawInfoCard();
        drawHeader();
        if (!landingMode) drawTimeBar();
        if (!landingMode) drawNavigator();
        drawDock();
        drawStatusBar();
        uiButtons.clear();
        uiButtons.addAll(newButtons);
        uiBlocks.clear();
        uiBlocks.addAll(newBlocks);
        for (UiButton b : uiButtons) {
            if (b.tooltip != null && b.contains(mouseX, mouseY)) pendingTooltip = b.tooltip;
        }
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // alles wird in renderBg in Bildschirmkoordinaten gezeichnet
    }

    /** Scissor in Bildschirmkoordinaten. */
    private void pushScissor(int x, int y, int width, int height) {
        g.enableScissor(x, y, x + width, y + height);
    }

    private void popScissor() {
        g.disableScissor();
    }

    private void hint(String text, int color) {
        hintTexts.add(text);
        hintColors.add(0xFF000000 | color);
    }

    private static String tr(String key, Object... args) {
        return Component.translatable(key, args).getString();
    }

    private ItemStack slotStack() {
        return menu.getSlot(0).getItem();
    }

    @Nullable
    private int[] heightmap() {
        return star != null ? star.heightmap : null;
    }

    /** Hinweise der Karte (Original {@code drawGuiContainerForegroundLayer}, jetzt in der Statusleiste). */
    private void collectMapHints() {
        if (focusedSatelliteFrequency != null) {
            hint(tr("gui.stardar.label.focus_satellite"), 0x7CFFB0);
            hint(tr("gui.stardar.label.unfocus_hint"), 0x7CFFB0);
            return;
        }

        CelestialBody activeFocus = getRenderFocusBody();
        CelestialBody displayBody = hoveredBody != null ? hoveredBody.body : activeFocus;

        if (displayBody == null) {
            hint(tr("gui.stardar.label.drag_map"), 0x7CFFB0);
            hint(tr("gui.stardar.label.click_body_focus"), 0x7CFFB0);
            return;
        }

        hint(tr("gui.stardar.label.focus_body", getBodyDisplayName(displayBody)), 0x7CFFB0);
        if (activeFocus != null && displayBody == activeFocus) {
            if (!isReachableFromHere(displayBody)) {
                hint(tr("gui.stardar.label.interstellar_locked"), 0xFF6060);
            } else if (!displayBody.canLand) {
                hint(tr("gui.stardar.label.cant_land"), 0xFF6060);
            } else {
                ItemStack slotStack = slotStack();
                if (!slotStack.isEmpty() && slotStack.getItem() == SpaceMaterialItems.HARD_DRIVE.get()) {
                    hint(tr("gui.stardar.label.click_again_land"), 0x7CFFB0);
                } else {
                    hint(tr("gui.stardar.label.insert_drive"), 0xFFD060);
                }
            }
        } else {
            hint(tr("gui.stardar.label.click_body_focus"), 0x7CFFB0);
        }
    }

    /** Hinweise und Zielmarke im Landemodus. */
    private void collectLandingHints() {
        if (heightmap() == null) {
            hint(tr("gui.stardar.label.loading_surface_map"), 0x7CFFB0);
            return;
        }

        int sx = lastMouseX - ((lastMouseX + (int) surfaceX) % 2);
        int sy = lastMouseY - ((lastMouseY + (int) surfaceY) % 2);

        if (lastMouseX < mapX || lastMouseX >= mapX + mapW || lastMouseY < mapY || lastMouseY >= mapY + mapH || isOverUi(lastMouseX, lastMouseY)) {
            hint(tr("gui.stardar.label.select_landing_zone"), 0x7CFFB0);
            return;
        }

        int hx = (lastMouseX - (int) surfaceX - mapX + 256) / 2;
        int hz = (lastMouseY - (int) surfaceY - mapY + 256) / 2;
        String info = landingInfo(hx, hz);
        int altitude = altitude(hx, hz);
        boolean canLand = info == null;

        pushScissor(mapX, mapY, mapW, mapH);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        g.blit(texture, sx - 6, sy - 6, imageWidth + (canLand ? 14 : 0), 28, 14, 14);
        popScissor();

        hint(canLand ? tr("gui.stardar.label.valid_location") : info, canLand ? 0x7CFFB0 : 0xFF6060);
        if (altitude > 0) hint(tr("gui.stardar.label.target_altitude", altitude), 0x7CFFB0);
    }

    // ── Bausteine der Oberflaeche ──

    /** Glas-Tafel mit hellem Rand und Akzentlinie oben. */
    private void panel(int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, PANEL);
        g.fill(x, y, x + w, y + 1, PANEL_EDGE);
        g.fill(x, y + h - 1, x + w, y + h, 0x33000000);
        g.fill(x, y, x + 1, y + h, 0x2260C8FF);
        g.fill(x + w - 1, y, x + w, y + h, 0x2260C8FF);
        g.fillGradient(x + 1, y + 1, x + w - 1, y + 10, 0x1A60C8FF, 0x0060C8FF);
        newBlocks.add(new int[] {x, y, w, h});
    }

    private void text(String s, int x, int y, int color, float scale) {
        if (scale == 1F) {
            g.drawString(font, s, x, y, color, false);
            return;
        }
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1F);
        g.drawString(font, s, 0, 0, color, false);
        g.pose().popPose();
    }

    private int textW(String s, float scale) {
        return Math.round(font.width(s) * scale);
    }

    private String clip(String s, int maxW, float scale) {
        if (textW(s, scale) <= maxW) return s;
        while (s.length() > 1 && textW(s + "…", scale) > maxW) s = s.substring(0, s.length() - 1);
        return s + "…";
    }

    /** Flacher Knopf mit Text; {@code active} = hervorgehoben. */
    private void button(int x, int y, int w, int h, String label, boolean active, Runnable action, @Nullable String tooltip) {
        boolean hover = lastMouseX >= x && lastMouseX < x + w && lastMouseY >= y && lastMouseY < y + h;
        g.fill(x, y, x + w, y + h, active ? 0x6060C8FF : hover ? 0x40FFFFFF : 0x26FFFFFF);
        g.fill(x, y + h - 1, x + w, y + h, active || hover ? ACCENT : 0x4460C8FF);
        String l = clip(label, w - 6, 0.75F);
        text(l, x + (w - textW(l, 0.75F)) / 2, y + (h - 6) / 2, hover || active ? 0xFFFFFFFF : TEXT, 0.75F);
        newButtons.add(new UiButton(x, y, w, h, action, tooltip == null ? null : List.of(Component.literal(tooltip))));
    }

    /** Eckklammern um einen Punkt (Auswahlrahmen). */
    private void corners(float cx, float cy, float r, int color) {
        int x0 = Math.round(cx - r), y0 = Math.round(cy - r), x1 = Math.round(cx + r), y1 = Math.round(cy + r);
        int l = Math.max(2, Math.round(r * 0.45F));
        g.fill(x0, y0, x0 + l, y0 + 1, color);
        g.fill(x0, y0, x0 + 1, y0 + l, color);
        g.fill(x1 - l + 1, y0, x1 + 1, y0 + 1, color);
        g.fill(x1, y0, x1 + 1, y0 + l, color);
        g.fill(x0, y1, x0 + l, y1 + 1, color);
        g.fill(x0, y1 - l + 1, x0 + 1, y1 + 1, color);
        g.fill(x1 - l + 1, y1, x1 + 1, y1 + 1, color);
        g.fill(x1, y1 - l + 1, x1 + 1, y1 + 1, color);
    }

    /** Auswahlrahmen fuer fokussierten/ueberfahrenen Koerper und Marke "Hier" am aktuellen Koerper. */
    private void drawSelectionMarkers() {
        CelestialBody focus = getRenderFocusBody();
        CelestialBody hover = hoveredBody != null ? hoveredBody.body : navHoverBody;
        for (BodyRenderInfo info : renderedBodies) {
            boolean f = info.body == focus, h = info.body == hover, here = info.body == currentBody;
            if (!f && !h && !here) continue;
            float x = mapToScreenX(info.mapU, info.mapV), y = mapToScreenY(info.mapU, info.mapV);
            if (x < -20 || x > mapX + mapW + 20 || y < -20 || y > height + 20) continue;
            float base = Math.max(5F, info.drawSize * 0.5F) + 4F;
            if (f) corners(x, y, base + 1.5F + 1.2F * Mth.sin(uiTime * 4F), ACCENT);
            else if (h) corners(x, y, base, 0xCCFFFFFF);
            if (here) {
                int tx = Math.round(x), ty = Math.round(y - base - 5 - 1.5F * Mth.sin(uiTime * 3F));
                for (int i = 0; i < 3; i++) g.fill(tx - 2 + i, ty + i, tx + 3 - i, ty + i + 1, 0xFF7CFFB0);
                String s = tr("gui.stardar.ui.here");
                text(s, tx - textW(s, 0.6F) / 2, ty - 6, 0xFF7CFFB0, 0.6F);
            }
        }
    }

    // ── Kopfzeile: Titel, Standort, Brotkrumen des Fokus (klickbar) ──

    private void drawHeader() {
        int x = 6, y = 6, h = 16;
        List<CelestialBody> crumbs = new ArrayList<>();
        CelestialBody c = getRenderFocusBody();
        if (c == null && !systemLayouts.isEmpty()) c = systemLayouts.get(0).star;
        while (c != null && crumbs.size() < 8) {
            crumbs.add(0, c);
            c = c.parent;
        }
        String title = tr("gui.stardar.ui.title");
        String loc = tr("gui.stardar.ui.location", getBodyDisplayName(currentBody));
        int w = 10 + textW(title, 1F) + 8 + textW(loc, 0.75F) + 10;
        for (CelestialBody b : crumbs) w += textW(getBodyDisplayName(b), 0.75F) + 12;
        w = Math.min(w, mapW - 12 - (landingMode ? 0 : timeBarWidth() + 6));
        panel(x, y, w, h);
        g.fill(x, y, x + 2, y + h, ACCENT);
        int cx = x + 7;
        g.drawString(font, title, cx, y + 4, ACCENT, false);
        cx += textW(title, 1F) + 6;
        g.fill(cx, y + 4, cx + 1, y + h - 4, 0x5560C8FF);
        cx += 5;
        text(loc, cx, y + 5, TEXT_DIM, 0.75F);
        cx += textW(loc, 0.75F) + 6;
        for (int i = 0; i < crumbs.size(); i++) {
            CelestialBody b = crumbs.get(i);
            String n = getBodyDisplayName(b);
            int nw = textW(n, 0.75F);
            if (cx + nw > x + w - 4) break;
            text("›", cx, y + 5, TEXT_DIM, 0.75F);
            cx += 6;
            boolean last = i == crumbs.size() - 1;
            boolean hover = lastMouseX >= cx - 1 && lastMouseX < cx + nw + 1 && lastMouseY >= y && lastMouseY < y + h;
            text(n, cx, y + 5, last ? 0xFFFFFFFF : hover ? ACCENT : TEXT, 0.75F);
            final CelestialBody target = b;
            newButtons.add(new UiButton(cx - 1, y, nw + 2, h, () -> focusBody(target), null));
            cx += nw + 6;
        }
    }

    // ── Navigator: Systeme, Planeten, Monde des fokussierten Planeten ──

    private void drawNavigator() {
        int x = 6, y = 26, rowH = 10;
        navHoverBody = null;
        if (!navOpen) {
            button(x, y, 16, 12, "≡", false, () -> {
                playButton();
                navOpen = true;
            }, tr("gui.stardar.ui.nav_open"));
            return;
        }
        List<CelestialBody> rows = new ArrayList<>();
        List<Integer> depth = new ArrayList<>();
        CelestialBody focusPlanet = getTopLevelPlanetForBody(getRenderFocusBody());
        for (SystemLayout layout : systemLayouts) {
            rows.add(layout.star);
            depth.add(0);
            if (layout.star.satellites == null) continue;
            for (CelestialBody p : layout.star.satellites) {
                rows.add(p);
                depth.add(1);
                if (p.satellites != null && (p == focusPlanet || p == currentBody || (currentBody != null && currentBody.parent == p))) {
                    for (CelestialBody m : p.satellites) {
                        rows.add(m);
                        depth.add(2);
                    }
                }
            }
        }
        int maxRows = Math.max(3, (height - y - 60) / rowH);
        int n = Math.min(rows.size(), maxRows);
        int h = 14 + n * rowH + 4;
        panel(x, y, NAV_W, h);
        text(tr("gui.stardar.ui.systems").toUpperCase(java.util.Locale.ROOT), x + 6, y + 4, ACCENT, 0.6F);
        button(x + NAV_W - 13, y + 2, 11, 9, "‹", false, () -> {
            playButton();
            navOpen = false;
        }, tr("gui.stardar.ui.nav_close"));
        CelestialBody focus = getRenderFocusBody();
        CelestialBody mapHover = hoveredBody != null ? hoveredBody.body : null;
        java.util.Set<String> visited = com.space_beyond.celestial.VisitedBodies.client();
        for (int i = 0; i < n; i++) {
            CelestialBody b = rows.get(i);
            int d = depth.get(i);
            int ry = y + 14 + i * rowH;
            boolean hover = lastMouseX >= x && lastMouseX < x + NAV_W && lastMouseY >= ry && lastMouseY < ry + rowH;
            if (hover) navHoverBody = b;
            boolean sel = b == focus;
            if (sel) g.fill(x + 1, ry, x + NAV_W - 1, ry + rowH, 0x4060C8FF);
            else if (hover || b == mapHover) g.fill(x + 1, ry, x + NAV_W - 1, ry + rowH, 0x22FFFFFF);
            if (sel) g.fill(x + 1, ry, x + 2, ry + rowH, ACCENT);
            int ix = x + 6 + d * 7;
            int col = colorArrayToRgb(b.color);
            if (d == 0) {
                g.fill(ix, ry + 3, ix + 4, ry + 7, col);
            } else {
                g.fill(ix + 1, ry + 3, ix + 4, ry + 7, col);
                g.fill(ix, ry + 4, ix + 5, ry + 6, col);
            }
            String name = getBodyDisplayName(b);
            if (d == 0 && !systemLayouts.isEmpty() && b != systemLayouts.get(0).star) {
                StarSystem here = systemLayouts.get(0).system;
                SystemLayout l = layoutFor(b);
                if (l != null) name = tr("gui.stardar.label.system_distance", name, String.format(java.util.Locale.ROOT, "%.1f", here.distanceLightYears(l.system)));
            }
            int nameColor = d == 0 ? 0xFFFFE6A0 : (b == currentBody ? 0xFF7CFFB0 : sel ? 0xFFFFFFFF : TEXT);
            text(clip(name, NAV_W - (ix - x) - 18, 0.75F), ix + 8, ry + 2, nameColor, 0.75F);
            if (d > 0 && b.canLand) {
                boolean known = b == currentBody || visited.contains(b.name);
                g.fill(x + NAV_W - 8, ry + 4, x + NAV_W - 5, ry + 7, known ? 0xFF7CFFB0 : 0x55FFFFFF);
            }
            final CelestialBody target = b;
            newButtons.add(new UiButton(x, ry, NAV_W, rowH, () -> focusBody(target), null));
        }
    }

    // ── Statusleiste: Hinweise + Steuerung ──

    private void drawStatusBar() {
        int x = dockOpen ? leftPos + 186 : 6;
        int lines = Math.min(2, hintTexts.size());
        String controls = tr(landingMode ? "gui.stardar.ui.controls_landing" : "gui.stardar.ui.controls");
        int w = Math.max(textW(controls, 0.6F), 0);
        for (int i = 0; i < lines; i++) w = Math.max(w, textW(hintTexts.get(i), 0.8F));
        w = Math.min(w + 14, mapW - x - 6);
        int h = 6 + lines * 10 + 9;
        int y = height - h - 6;
        if (dockOpen && x + w > mapW) return;
        panel(x, y, w, h);
        for (int i = 0; i < lines; i++) {
            g.fill(x + 4, y + 5 + i * 10, x + 6, y + 11 + i * 10, hintColors.get(i));
            text(clip(hintTexts.get(i), w - 14, 0.8F), x + 10, y + 4 + i * 10, hintColors.get(i), 0.8F);
        }
        text(clip(controls, w - 10, 0.6F), x + 5, y + h - 8, TEXT_DIM, 0.6F);
    }

    // ── Laufwerk-Dock (Slot + Inventar der alten GUI) ──

    private void drawDock() {
        if (!dockOpen) return;
        int x = leftPos - 4, y = topPos + 112, w = 184, h = 146;
        panel(x, y, w, h);
        text(tr("gui.stardar.ui.drive").toUpperCase(java.util.Locale.ROOT), leftPos + 4, topPos + 118, ACCENT, 0.6F);
        text(tr("gui.stardar.ui.inventory").toUpperCase(java.util.Locale.ROOT), leftPos + 4, topPos + 164, ACCENT, 0.6F);
        String drive = slotStack().isEmpty() ? tr("gui.stardar.ui.drive_empty") : slotStack().getHoverName().getString();
        text(clip(drive, 120, 0.75F), leftPos + 4, topPos + 130, TEXT, 0.75F);
        for (net.minecraft.world.inventory.Slot s : menu.slots) {
            int sx = leftPos + s.x - 1, sy = topPos + s.y - 1;
            g.fill(sx, sy, sx + 18, sy + 18, 0xFF0E1320);
            g.fill(sx, sy, sx + 18, sy + 1, 0x33000000);
            g.fill(sx, sy + 17, sx + 18, sy + 18, 0x3360C8FF);
        }
        int dx = leftPos + menu.getSlot(0).x - 2, dy = topPos + menu.getSlot(0).y - 2;
        g.fill(dx, dy, dx + 20, dy + 1, ACCENT);
        g.fill(dx, dy + 19, dx + 20, dy + 20, ACCENT);
        button(x + w - 14, y + 3, 11, 9, "×", false, this::toggleDock, null);
    }

    // ── Infotafel ──

    @Nullable
    private CelestialBody cardBody() {
        if (navHoverBody != null) return navHoverBody;
        if (hoveredBody != null) return hoveredBody.body;
        CelestialBody focus = getRenderFocusBody();
        return focus != null ? focus : currentBody;
    }

    private void drawInfoCard() {
        int x = width - CARD_W - 4, y = 4, w = CARD_W, h = height - 8;
        panel(x, y, w, h);
        int bottom = y + h;

        // Aktionen unten
        int by = bottom - 64;
        g.fill(x + 6, by - 4, x + w - 6, by - 3, 0x3360C8FF);
        String drive = slotStack().isEmpty() ? tr("gui.stardar.ui.drive_empty") : tr("gui.stardar.ui.drive_label", slotStack().getHoverName().getString());
        text(clip(drive, w - 12, 0.7F), x + 6, by, slotStack().isEmpty() ? TEXT_DIM : 0xFF7CFFB0, 0.7F);
        button(x + 6, by + 9, w - 12, 12, tr("gui.stardar.ui.btn_dock"), dockOpen, this::toggleDock, tr("gui.stardar.ui.btn_dock_tip"));
        int bw = (w - 16) / 2;
        button(x + 6, by + 24, bw, 12, tr("gui.stardar.ui.btn_station"), false, this::actionProgramStation, tr("gui.stardar.tooltip.program_new_station"));
        button(x + 10 + bw, by + 24, bw, 12, tr("gui.stardar.ui.btn_body"), false, this::actionProgramCurrentBody, tr("gui.stardar.tooltip.program_current_body"));
        button(x + 6, by + 39, w - 12, 12, tr("gui.stardar.ui.btn_here"), false, this::actionFocusCurrentBody, tr("gui.stardar.tooltip.focus_current_body"));

        CelestialBody b = cardBody();
        if (b == null) return;
        boolean star = b.parent == null;
        boolean moon = isMoon(b);
        java.util.Set<String> visited = com.space_beyond.celestial.VisitedBodies.client();
        boolean known = star || !b.canLand || b == currentBody || visited.contains(b.name);

        // Kopf: Bild, Name, Art
        int ix = x + 8, iy = y + 10, is = 30;
        g.fill(ix - 2, iy - 2, ix + is + 2, iy + is + 2, 0x55000000);
        if (b.texture != null) {
            RenderSystem.enableBlend();
            g.blit(b.texture, ix, iy, is, is, 0F, 0F, 16, 16, 16, 16);
        } else {
            g.fill(ix + 4, iy + 4, ix + is - 4, iy + is - 4, colorArrayToRgb(b.color));
        }
        int tx = ix + is + 7;
        String name = getBodyDisplayName(b);
        text(clip(name, x + w - tx - 4, 1.25F), tx, iy + 1, 0xFFFFFFFF, 1.25F);
        String kind = star ? tr("gui.stardar.ui.star") : moon ? tr("gui.stardar.ui.moon", getBodyDisplayName(b.parent)) : tr("gui.stardar.ui.planet");
        text(clip(kind, x + w - tx - 4, 0.7F), tx, iy + 14, TEXT_DIM, 0.7F);
        // Kennzeichen
        int bx = tx;
        int byy = iy + 23;
        if (b == currentBody) bx = badge(bx, byy, tr("gui.stardar.ui.here"), 0xFF7CFFB0);
        if (!star && b.canLand) bx = badge(bx, byy, tr(known ? "gui.stardar.ui.visited" : "gui.stardar.ui.unknown"), known ? 0xFF5FE0FF : 0xFFB08CFF);
        if (!star && !isReachableFromHere(b)) badge(bx, byy, tr("gui.stardar.ui.interstellar"), 0xFFFF8060);

        int cy = iy + is + 8;
        g.fill(x + 6, cy, x + w - 6, cy + 1, 0x3360C8FF);
        cy += 5;

        // Werte (2 Spalten x 3 Zeilen)
        String q = "???";
        String[] vals = new String[6];
        String[] labels = {tr("gui.stardar.stat.light"), tr("gui.stardar.stat.diameter"), tr("gui.stardar.stat.gravity"), tr("gui.stardar.stat.day"),
                tr("gui.stardar.stat.atmosphere"), tr(star ? "gui.stardar.stat.planets" : "gui.stardar.stat.orbit")};
        net.minecraft.world.item.Item[] icons = {net.minecraft.world.item.Items.GLOWSTONE_DUST, net.minecraft.world.item.Items.COMPASS,
                net.minecraft.world.item.Items.ANVIL, net.minecraft.world.item.Items.CLOCK, net.minecraft.world.item.Items.GLASS_BOTTLE,
                star ? net.minecraft.world.item.Items.NETHER_STAR : net.minecraft.world.item.Items.ENDER_EYE};
        if (known) {
            vals[0] = star ? "—" : fmtPercent(StardarBodyInfo.sunPercent(b));
            vals[1] = String.format(java.util.Locale.ROOT, "%,d km", Math.round(b.radiusKm * 2F));
            vals[2] = star ? "—" : String.format(java.util.Locale.ROOT, "%.2f g", StardarBodyInfo.gravityG(b));
            vals[3] = star ? "—" : fmtDay(StardarBodyInfo.dayMinutes(b));
            double p = star ? 0 : StardarBodyInfo.pressure(b);
            vals[4] = star ? "—" : p < 0.001 ? tr("gui.stardar.stat.vacuum") : String.format(java.util.Locale.ROOT, "%.2f atm", p);
            vals[5] = star ? Integer.toString(StardarBodyInfo.moons(b)) : fmtYear(StardarBodyInfo.yearDays(b));
        } else {
            java.util.Arrays.fill(vals, q);
        }
        int colW = (w - 12) / 2;
        for (int i = 0; i < 6; i++) {
            int sx = x + 6 + (i % 2) * colW, sy = cy + (i / 2) * 14;
            g.pose().pushPose();
            g.pose().translate(sx, sy, 0);
            g.pose().scale(0.625F, 0.625F, 1F);
            g.renderItem(new ItemStack(icons[i]), 0, 0);
            g.pose().popPose();
            text(clip(vals[i], colW - 16, 0.8F), sx + 13, sy + 2, known ? TEXT : 0xFFB08CFF, 0.8F);
            newButtons.add(new UiButton(sx, sy, colW, 12, () -> { }, List.of(Component.literal(labels[i]))));
        }
        cy += 3 * 14 + 2;
        if (!known) {
            String hint = tr("gui.stardar.ui.unexplored_hint");
            for (String line : wrap(hint, w - 14, 0.7F)) {
                text(line, x + 7, cy, 0xFFB08CFF, 0.7F);
                cy += 8;
            }
            cy += 2;
        }

        // Erze
        if (!star && b.canLand) {
            StardarBodyInfo.Ores ores = StardarBodyInfo.ores(b);
            int avail = by - 8;
            cy = oreGrid(x, cy, w, avail, tr("gui.stardar.ui.ores"), ores.normal(), known);
            cy = oreGrid(x, cy, w, avail, tr("gui.stardar.ui.bedrock"), ores.bedrock(), known);
        }
    }

    private int badge(int x, int y, String s, int color) {
        int w = textW(s, 0.6F) + 5;
        g.fill(x, y, x + w, y + 7, (color & 0x00FFFFFF) | 0x33000000);
        g.fill(x, y + 6, x + w, y + 7, color);
        text(s, x + 3, y + 1, color, 0.6F);
        return x + w + 3;
    }

    /** Raster aus Gegenstandssymbolen; unbekannt = Fragezeichen. */
    private int oreGrid(int x, int y, int w, int maxY, String title, List<ItemStack> items, boolean known) {
        if (y + 22 > maxY) return y;
        g.fill(x + 6, y, x + w - 6, y + 1, 0x3360C8FF);
        text(title.toUpperCase(java.util.Locale.ROOT), x + 7, y + 4, ACCENT, 0.6F);
        y += 11;
        int cell = 17, perRow = Math.max(1, (w - 12) / cell);
        int count = known ? items.size() : Math.min(perRow, Math.max(4, items.size()));
        if (known && items.isEmpty()) {
            text(tr("gui.stardar.ui.none"), x + 8, y + 2, TEXT_DIM, 0.7F);
            return y + 12;
        }
        int rows = (count + perRow - 1) / perRow;
        int maxRows = Math.max(1, (maxY - y) / cell);
        rows = Math.min(rows, maxRows);
        for (int i = 0; i < Math.min(count, rows * perRow); i++) {
            int cx = x + 6 + (i % perRow) * cell, cy = y + (i / perRow) * cell;
            boolean hover = lastMouseX >= cx && lastMouseX < cx + 16 && lastMouseY >= cy && lastMouseY < cy + 16;
            g.fill(cx, cy, cx + 16, cy + 16, hover ? 0x4460C8FF : 0x40000000);
            if (known) {
                g.renderItem(items.get(i), cx, cy);
                if (hover) pendingItemTooltip = items.get(i);
            } else {
                text("?", cx + 6, cy + 4, 0xFFB08CFF, 1F);
            }
        }
        return y + rows * cell + 4;
    }

    private List<String> wrap(String s, int maxW, float scale) {
        List<String> out = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : s.split(" ")) {
            String next = line.length() == 0 ? word : line + " " + word;
            if (textW(next, scale) > maxW && line.length() > 0) {
                out.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(next);
            }
        }
        if (line.length() > 0) out.add(line.toString());
        return out;
    }

    private static String fmtPercent(float p) {
        return p >= 10F ? String.format(java.util.Locale.ROOT, "%.0f%%", p) : String.format(java.util.Locale.ROOT, "%.1f%%", p);
    }

    private static String fmtDay(double minutes) {
        if (minutes <= 0) return "—";
        if (minutes < 120) return String.format(java.util.Locale.ROOT, "%.0f min", minutes);
        if (minutes < 60 * 48) return String.format(java.util.Locale.ROOT, "%.1f h", minutes / 60.0);
        return String.format(java.util.Locale.ROOT, "%.1f d", minutes / 1440.0);
    }

    private static String fmtYear(double days) {
        if (days <= 0) return "—";
        return days < 10 ? String.format(java.util.Locale.ROOT, "%.1f d", days) : String.format(java.util.Locale.ROOT, "%,.0f d", days);
    }

    // ── Aktionen (frueher die drei Knoepfe der alten GUI) ──

    private void actionProgramStation() {
        playButton();
        CompoundTag data = new CompoundTag();
        data.putString("pid", SpaceDimensions.ORBIT.location().toString());
        sendControl(data);
    }

    private void actionFocusCurrentBody() {
        playButton();
        CelestialBody body = currentBody;
        if (body != null && body.getStar() != null) {
            clearSatelliteFocus();
            if (landingMode) {
                exitLandingMode();
            }
            configureFocusAnimation(body, body, true, 420, 0F, 0F, getFocusZoomForBody(body));
        }
    }

    private void actionProgramCurrentBody() {
        playButton();
        if (star != null && star.getLevel() != null) {
            CompoundTag data = new CompoundTag();
            data.putString("pid", star.getLevel().dimension().location().toString());
            data.putInt("ix", star.getBlockPos().getX());
            data.putInt("iz", star.getBlockPos().getZ());
            sendControl(data);
        }
    }

    /** Fokus aus Navigator/Brotkrumen: Stern des eigenen Systems = Gesamtansicht, fremder Stern = System, sonst Koerper. */
    private void focusBody(@Nullable CelestialBody body) {
        if (body == null) return;
        if (landingMode) exitLandingMode();
        clearSatelliteFocus();
        if (body.parent == null) {
            SystemLayout layout = layoutFor(body);
            if (layout == null) return;
            if (layout == systemLayouts.get(0)) {
                playButton();
                configureFocusAnimation(null, null, false, 420, layout.centerU, layout.centerV, overviewZoom());
            } else {
                focusStar(layout);
            }
            return;
        }
        if (body == focusedBody) return;
        playButton();
        configureFocusAnimation(body, body, true, 500, 0F, 0F, getFocusZoomForBody(body));
    }

    // ── Tastatur ──

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        switch (key) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (landingMode) exitLandingMode();
                else if (focusedSatelliteFrequency != null) clearSatelliteFocus();
                else beginUnfocusAnimation();
                return true;
            }
            case GLFW.GLFW_KEY_H -> {
                actionFocusCurrentBody();
                return true;
            }
            case GLFW.GLFW_KEY_T -> {
                if (landingMode) return true;
                setTimeSpeed(timeSpeedIndex + ((mods & GLFW.GLFW_MOD_SHIFT) != 0 ? -1 : 1));
                return true;
            }
            case GLFW.GLFW_KEY_TAB -> {
                playButton();
                navOpen = !navOpen;
                return true;
            }
            case GLFW.GLFW_KEY_I -> {
                toggleDock();
                return true;
            }
            case GLFW.GLFW_KEY_EQUAL, GLFW.GLFW_KEY_KP_ADD -> {
                handleWheel(mapX + mapW / 2, mapY + mapH / 2, 1);
                return true;
            }
            case GLFW.GLFW_KEY_MINUS, GLFW.GLFW_KEY_KP_SUBTRACT -> {
                handleWheel(mapX + mapW / 2, mapY + mapH / 2, -1);
                return true;
            }
            default -> {
                return super.keyPressed(key, scan, mods);
            }
        }
    }

    private boolean keyDown(int key) {
        return GLFW.glfwGetKey(minecraft.getWindow().getWindow(), key) == GLFW.GLFW_PRESS;
    }

    /** WASD/Pfeiltasten verschieben die Gesamtansicht; weiches Zoomen zum Ziel. */
    private void updateKeyboardAndZoom() {
        viewShiftX += (viewShiftTarget() - viewShiftX) * (1F - (float) Math.exp(-frameDt * 12F));
        if (!isFocusContextActive() && !focusAnimationActive) {
            float speed = 220F * frameDt;
            float dx = 0F, dy = 0F;
            if (keyDown(GLFW.GLFW_KEY_A) || keyDown(GLFW.GLFW_KEY_LEFT)) dx += speed;
            if (keyDown(GLFW.GLFW_KEY_D) || keyDown(GLFW.GLFW_KEY_RIGHT)) dx -= speed;
            if (keyDown(GLFW.GLFW_KEY_W) || keyDown(GLFW.GLFW_KEY_UP)) dy += speed;
            if (keyDown(GLFW.GLFW_KEY_S) || keyDown(GLFW.GLFW_KEY_DOWN)) dy -= speed;
            if (dx != 0F || dy != 0F) {
                panByScreenDelta(Math.round(dx), Math.round(dy));
                clampMapUV();
            }
        }
        if (zoomTarget > 0F) {
            if (focusAnimationActive) {
                zoomTarget = -1F;
                return;
            }
            float old = mapZoom;
            float k = 1F - (float) Math.exp(-frameDt * 14F);
            float next = old + (zoomTarget - old) * k;
            if (Math.abs(next - zoomTarget) < zoomTarget * 0.002F) {
                next = zoomTarget;
                zoomTarget = -1F;
            }
            if (isFocusContextActive()) mapZoom = next;
            else zoomAtMouse(zoomAnchorX, zoomAnchorY, old, next);
        }
    }

    // ── Kartenzeit (Zeitraffer) ──

    private double advanceMapTicks(double gameTicks) {
        realTicks = gameTicks;
        int speed = TIME_SPEEDS[timeSpeedIndex];
        if (Double.isNaN(mapTicks) || speed == 1) mapTicks = gameTicks;
        else if (speed > 1) mapTicks += frameDt * 20D * speed;
        return mapTicks;
    }

    private void setTimeSpeed(int index) {
        playButton();
        timeSpeedIndex = Math.floorMod(index, TIME_SPEEDS.length);
    }

    private static String speedLabel(int speed) {
        return speed >= 1000 ? (speed / 1000) + "k×" : speed + "×";
    }

    private static String speedButtonLabel(int speed) {
        return speed == 0 ? "II" : speedLabel(speed);
    }

    private int speedButtonW(int speed) {
        return Math.max(16, textW(speedButtonLabel(speed), 0.75F) + 8);
    }

    /** Breite der Zeitleiste (fuer die Kopfzeile, damit beide nebeneinander passen). */
    private int timeBarWidth() {
        int w = 8 + textW(tr("gui.stardar.ui.time").toUpperCase(java.util.Locale.ROOT), 0.6F) + 4;
        for (int speed : TIME_SPEEDS) w += speedButtonW(speed) + 2;
        return w + 4 + textW("+0000 d", 0.7F) + 4;
    }

    /** Zeitleiste oben rechts auf der Karte: Pause, Echtzeit, Zeitraffer; daneben der Vorlauf gegenueber der Spielzeit. */
    private void drawTimeBar() {
        int w = timeBarWidth(), h = 16;
        int x = mapX + mapW - w - 6, y = 6;
        panel(x, y, w, h);
        int cx = x + 6;
        String title = tr("gui.stardar.ui.time").toUpperCase(java.util.Locale.ROOT);
        text(title, cx, y + 6, ACCENT, 0.6F);
        cx += textW(title, 0.6F) + 4;
        for (int i = 0; i < TIME_SPEEDS.length; i++) {
            int speed = TIME_SPEEDS[i];
            final int index = i;
            String tip = speed == 0 ? tr("gui.stardar.ui.time_pause") : speed == 1 ? tr("gui.stardar.ui.time_real") : tr("gui.stardar.ui.time_fast", speedLabel(speed));
            int bw = speedButtonW(speed);
            button(cx, y + 2, bw, 12, speedButtonLabel(speed), i == timeSpeedIndex, () -> setTimeSpeed(index), tip);
            cx += bw + 2;
        }
        double aheadDays = Double.isNaN(mapTicks) ? 0D : (mapTicks - realTicks) / (double) AstronomyUtil.TICKS_IN_DAY;
        if (Math.abs(aheadDays) >= 0.05D) {
            String s = String.format(java.util.Locale.ROOT, aheadDays >= 0 ? "+%,.1f d" : "%,.1f d", aheadDays);
            if (Math.abs(aheadDays) >= 100D) s = String.format(java.util.Locale.ROOT, aheadDays >= 0 ? "+%,.0f d" : "%,.0f d", aheadDays);
            text(clip(s, x + w - cx - 4, 0.7F), cx + 2, y + 5, 0xFFFFE6A0, 0.7F);
        }
    }

    // ── Asteroidenguertel ──

    /** Gesteinsbrocken der Guertel als kleine Quads (eigene Kepler-Bahnen, gleiche Stauchung wie die Planetenbahnen). */
    private void drawBelts(SystemLayout layout) {
        List<AsteroidBelts.Belt> belts = AsteroidBelts.of(layout.star);
        if (belts.isEmpty() || layout.orbitScale <= 0F) return;
        float fade = getRenderFocusBody() != null ? 0.45F : 0.85F;
        float right = mapX + mapW;
        SkyGL.disableTexture();
        SkyTess.startDrawingQuads();
        for (AsteroidBelts.Belt belt : belts) {
            for (AsteroidBelts.Rock rock : AsteroidBelts.rocks(belt, layout.star)) {
                double m = rock.meanAnomalyAt(currentWorldTicks);
                float[] o = calculateOrbitOffsetPx(layout.star, rock.semiMajorAxisKm(), rock.eccentricity(), rock.semiMinorAxisFactor(),
                    rock.argumentPeriapsis(), rock.inclination(), rock.ascendingNode(), m, layout.orbitScale);
                float sx = mapToScreenX(layout.centerU + o[0], layout.centerV + o[1]);
                float sy = mapToScreenY(layout.centerU + o[0], layout.centerV + o[1]);
                if (sx < mapX - 2 || sx > right + 2 || sy < mapY - 2 || sy > mapY + mapH + 2) continue;
                float tw = 0.8F + 0.2F * Mth.sin(uiTime * 2F + (float) rock.meanAnomaly0() * 7F);
                float s = rock.size();
                SkyTess.setColorRGBA_F(rock.r(), rock.g(), rock.b(), rock.brightness() * fade * tw);
                SkyTess.addVertex(sx - s, sy + s, 0);
                SkyTess.addVertex(sx + s, sy + s, 0);
                SkyTess.addVertex(sx + s, sy - s, 0);
                SkyTess.addVertex(sx - s, sy - s, 0);
            }
        }
        SkyTess.draw();
        SkyGL.enableTexture();
    }

    /** Namen der Guertel in der Gesamtansicht, am vorderen Rand der Guertelmitte (nur wenn frei). */
    private void drawBeltLabels() {
        if (getRenderFocusBody() != null || this.font == null) return;
        for (SystemLayout layout : systemLayouts) {
            for (AsteroidBelts.Belt belt : AsteroidBelts.of(layout.star)) {
                float a = (belt.innerKm() + belt.outerKm()) * 0.5F;
                float bestX = 0F, bestY = -Float.MAX_VALUE;
                for (int i = 0; i < 48; i++) {
                    float[] o = calculateOrbitOffsetPx(layout.star, a, 0F, 1D, 0F, 0F, 0F, 2D * Math.PI * i / 48D, layout.orbitScale);
                    float sy = mapToScreenY(layout.centerU + o[0], layout.centerV + o[1]);
                    if (sy > bestY) {
                        bestY = sy;
                        bestX = mapToScreenX(layout.centerU + o[0], layout.centerV + o[1]);
                    }
                }
                String label = tr(belt.langKey()).toUpperCase(java.util.Locale.ROOT);
                int w = textW(label, 0.6F), h = 6;
                float x = bestX - w * 0.5F, y = bestY + 3F;
                if (x < mapX || x + w > mapX + mapW || y + h > mapY + mapH || labelOverlaps(x, y, w, h)) continue;
                placedLabels.add(new float[] {x, y, x + w, y + h});
                float[] c = belt.colorA();
                int color = 0xB0000000 | colorArrayToRgb(new float[] {c[0] * 0.6F + 0.4F, c[1] * 0.6F + 0.4F, c[2] * 0.6F + 0.4F}) & 0xFFFFFF;
                text(label, Math.round(x), Math.round(y), color, 0.6F);
            }
        }
    }

    private void drawSystemMap(float partialTicks) {
        updateDragPanning(lastMouseX, lastMouseY);
        updateKeyboardAndZoom();

        renderedBodies.clear();
        renderedBodyLabels.clear();
        renderedSatellites.clear();
        hoveredSatellite = null;
        focusedSatellite = null;

        if (SolarSystem.kerbol == null) return;

        // Erweiterung: alle Sternsysteme, das eigene (Original: immer Kerbol) in der Mitte
        updateSystemLayouts();
        if (systemLayouts.isEmpty()) return;
        SystemLayout viewLayout = systemLayouts.get(0);
        CelestialBody root = viewLayout.star;

        double dayTicks = minecraft != null && minecraft.level != null ? (double) minecraft.level.getGameTime() + (double) partialTicks : 0D;
        // Bahnen laufen auf der Kartenzeit (Zeitraffer); Drehung/Wolken der Koerper bleiben auf Spielzeit
        double worldTicks = advanceMapTicks(dayTicks) * (double) AstronomyUtil.TIME_MULTIPLIER;
        currentWorldTicks = worldTicks;
        float centerU = viewLayout.centerU;
        float centerV = viewLayout.centerV;
        float systemOrbitScale = viewLayout.orbitScale;

        if (focusedBody != null && layoutFor(focusedBody) == null) {
            cancelFocusState();
        }
        if (focusAnimationBody != null && layoutFor(focusAnimationBody) == null) {
            cancelFocusState();
        }

        // glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_LINE_BIT)
        SkyGL.enableBlend();
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA);

        updateFocusView(root, centerU, centerV, systemOrbitScale, worldTicks);
        for (int i = systemLayouts.size() - 1; i >= 0; i--) {
            drawBelts(systemLayouts.get(i));
        }
        for (int i = systemLayouts.size() - 1; i >= 0; i--) {
            SystemLayout layout = systemLayouts.get(i);
            drawBodyTree(layout.star, layout.centerU, layout.centerV, layout.orbitScale, worldTicks, dayTicks, Float.NaN, Float.NaN);
        }
        drawQueuedBodyLabels();
        drawBeltLabels();
        drawSystemIndicators();

        // glPopAttrib
        SkyGL.reset();
        RenderSystem.enableDepthTest();
        RenderSystem.lineWidth(1F);

        boolean overUi = isOverUi(lastMouseX, lastMouseY);
        hoveredBody = overUi ? null : findBodyUnderCursor(lastMouseX, lastMouseY);
        hoveredSatellite = overUi ? null : findSatelliteUnderCursor(lastMouseX, lastMouseY);
    }

    private void drawSurfaceMap() {
        if (!isLeftMouseDown()) {
            velocityX *= 0.85F;
            velocityY *= 0.85F;
            surfaceX += velocityX;
            surfaceY += velocityY;
            surfaceX = clampSurface(surfaceX, mapW);
            surfaceY = clampSurface(surfaceY, mapH);
        }

        int[] heightmap = heightmap();
        if (heightmap == null) {
            return;
        }

        if (star.updateHeightmap) {
            NativeImage pixels = groundTexture.getPixels();
            if (pixels != null) {
                for (int i = 0; i < heightmap.length && i < 256 * 256; i++) {
                    int h = Math.floorMod(heightmap[i], 16) * 16;
                    pixels.setPixelRGBA(i % 256, i / 256, 255 << 24 | h << 8); // ARGB und ABGR: Gruen liegt gleich
                }
                groundTexture.upload();
            }
            star.updateHeightmap = false;
        }

        SkyGL.bind(groundMap);
        drawPartialTex(
            mapX + (int) surfaceX - 256,
            mapY + (int) surfaceY - 256,
            512,
            512,
            0F,
            0F,
            1F,
            1F
        );
    }

    // ── Weltraum-Hintergrund: Nebel (prozedural), Sternfeld mit Parallaxe und Funkeln, Leuchthoefe ─────────────────

    private static final int STAR_COUNT = 900;
    private static float[] starData;
    @Nullable private static ResourceLocation nebulaTex;
    @Nullable private static ResourceLocation glowTex;

    private static float[] stars() {
        if (starData == null) {
            java.util.Random r = new java.util.Random(0x5743A12L);
            starData = new float[STAR_COUNT * 7];
            for (int i = 0; i < STAR_COUNT; i++) {
                int o = i * 7;
                starData[o] = r.nextFloat();                       // x
                starData[o + 1] = r.nextFloat();                   // y
                starData[o + 2] = r.nextInt(3);                    // Ebene
                starData[o + 3] = 0.25F + r.nextFloat() * 0.75F;   // Helligkeit
                starData[o + 4] = r.nextFloat() * 6.283F;          // Phase
                float temp = r.nextFloat();                        // Farbtemperatur: blau - weiss - gelblich
                starData[o + 5] = temp;
                starData[o + 6] = r.nextFloat() < 0.06F ? 1F : 0F; // grosser Stern
            }
        }
        return starData;
    }

    private static float hash2(int x, int y, int seed) {
        int h = x * 374761393 + y * 668265263 + seed * 1442695041;
        h = (h ^ (h >>> 13)) * 1274126177;
        return ((h ^ (h >>> 16)) & 0xFFFFFF) / (float) 0x1000000;
    }

    private static float valueNoise(float x, float y, int seed, int period) {
        int x0 = (int) Math.floor(x), y0 = (int) Math.floor(y);
        float fx = x - x0, fy = y - y0;
        fx = fx * fx * (3 - 2 * fx);
        fy = fy * fy * (3 - 2 * fy);
        int x1 = x0 + 1, y1 = y0 + 1;
        x0 = Math.floorMod(x0, period); x1 = Math.floorMod(x1, period); y0 = Math.floorMod(y0, period); y1 = Math.floorMod(y1, period);
        float a = hash2(x0, y0, seed), b = hash2(x1, y0, seed), c = hash2(x0, y1, seed), d = hash2(x1, y1, seed);
        return (a + (b - a) * fx) * (1 - fy) + (c + (d - c) * fx) * fy;
    }

    private static float fbm(float x, float y, int seed) {
        float sum = 0, amp = 0.5F, norm = 0;
        int period = 4;
        for (int o = 0; o < 5; o++) {
            sum += valueNoise(x * period / 4F, y * period / 4F, seed + o * 31, period) * amp;
            norm += amp;
            amp *= 0.5F;
            period *= 2;
        }
        return sum / norm;
    }

    /** Nebeltextur (nahtlos, 256 px): zwei Farbwolken (Violett/Petrol) mit Filamenten. */
    private ResourceLocation nebulaTexture() {
        if (nebulaTex == null) {
            int n = 256;
            NativeImage img = new NativeImage(NativeImage.Format.RGBA, n, n, false);
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    float u = x / (float) n * 4F, v = y / (float) n * 4F;
                    float d1 = fbm(u, v, 11), d2 = fbm(u + 1.7F, v + 0.3F, 47), fil = fbm(u * 2F, v * 2F, 93);
                    float a1 = Math.max(0F, d1 - 0.45F) * 2.2F, a2 = Math.max(0F, d2 - 0.48F) * 2.4F;
                    float ridge = 1F - Math.abs(fil - 0.5F) * 2F;
                    ridge = ridge * ridge * ridge * 0.35F;
                    float r = 0.42F * a1 + 0.10F * a2 + 0.20F * ridge;
                    float gr = 0.12F * a1 + 0.38F * a2 + 0.18F * ridge;
                    float b = 0.62F * a1 + 0.55F * a2 + 0.30F * ridge;
                    float a = Mth.clamp(Math.max(a1, a2) * 0.75F + ridge * 0.5F, 0F, 1F);
                    int ir = Mth.clamp((int) (r * 255), 0, 255), ig = Mth.clamp((int) (gr * 255), 0, 255), ib = Mth.clamp((int) (b * 255), 0, 255);
                    int ia = Mth.clamp((int) (a * 255), 0, 255);
                    img.setPixelRGBA(x, y, ia << 24 | ib << 16 | ig << 8 | ir); // ABGR
                }
            }
            nebulaTex = minecraft.getTextureManager().register("stardar_nebula", new DynamicTexture(img));
        }
        return nebulaTex;
    }

    /** Weicher runder Lichthof (64 px, weiss, Alpha faellt nach aussen ab). */
    private ResourceLocation glowTexture() {
        if (glowTex == null) {
            int n = 64;
            NativeImage img = new NativeImage(NativeImage.Format.RGBA, n, n, false);
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    float dx = (x + 0.5F) / n * 2F - 1F, dy = (y + 0.5F) / n * 2F - 1F;
                    float d = Math.min(1F, (float) Math.sqrt(dx * dx + dy * dy));
                    float a = (float) Math.pow(1F - d, 2.4F);
                    img.setPixelRGBA(x, y, Mth.clamp((int) (a * 255), 0, 255) << 24 | 0xFFFFFF);
                }
            }
            glowTex = minecraft.getTextureManager().register("stardar_glow", new DynamicTexture(img));
        }
        return glowTex;
    }

    /** Hintergrund: Verlauf, Nebel (leicht verschoben mit der Karte), drei Sternebenen mit Parallaxe und Funkeln. */
    private void drawSpaceBackground() {
        float camX = landingMode ? 0F : mapToScreenX(MAP_TEX_W * 0.5F, MAP_TEX_H * 0.5F) - mapCenterX();
        float camY = landingMode ? 0F : mapToScreenY(MAP_TEX_W * 0.5F, MAP_TEX_H * 0.5F) - (mapY + mapH * 0.5F);

        SkyGL.enableBlend();
        SkyGL.disableTexture();
        SkyTess.startDrawingQuads();
        SkyTess.setColorRGBA_F(0.010F, 0.012F, 0.035F, 1F);
        SkyTess.addVertex(0, height, 0);
        SkyTess.addVertex(width, height, 0);
        SkyTess.setColorRGBA_F(0.030F, 0.020F, 0.070F, 1F);
        SkyTess.addVertex(width, 0, 0);
        SkyTess.addVertex(0, 0, 0);
        SkyTess.draw();

        // Nebel, additiv
        SkyGL.enableTexture();
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE);
        SkyGL.bind(nebulaTexture());
        float span = Math.max(width, height) * 1.6F;
        float ox = -((camX * 0.06F) % span + span) % span, oy = -((camY * 0.06F) % span + span) % span;
        SkyGL.color(1F, 1F, 1F, 0.75F);
        for (int tx = 0; tx < 2; tx++) for (int ty = 0; ty < 2; ty++) {
            drawPartialTex(ox + tx * span, oy + ty * span, span, span, 0F, 0F, 1F, 1F);
        }
        SkyGL.color(1F, 1F, 1F, 1F);

        // Sterne
        SkyGL.disableTexture();
        float[] s = stars();
        float[] par = {0.015F, 0.04F, 0.09F};
        SkyTess.startDrawingQuads();
        for (int i = 0; i < STAR_COUNT; i++) {
            int o = i * 7;
            int layer = (int) s[o + 2];
            float px = s[o] * width + camX * par[layer], py = s[o + 1] * height + camY * par[layer];
            px = ((px % width) + width) % width;
            py = ((py % height) + height) % height;
            float tw = 0.65F + 0.35F * Mth.sin(uiTime * (1.2F + s[o + 3] * 2.5F) + s[o + 4]);
            float a = s[o + 3] * tw * (0.45F + layer * 0.25F);
            float t = s[o + 5];
            float r = t < 0.33F ? 0.70F : t < 0.85F ? 0.95F : 1F, gr = t < 0.33F ? 0.82F : t < 0.85F ? 0.95F : 0.88F, b = t < 0.33F ? 1F : t < 0.85F ? 1F : 0.70F;
            float size = (s[o + 6] > 0F ? 1.6F : 0.8F) * (0.7F + layer * 0.25F);
            SkyTess.setColorRGBA_F(r, gr, b, a);
            SkyTess.addVertex(px - size, py + size, 0);
            SkyTess.addVertex(px + size, py + size, 0);
            SkyTess.addVertex(px + size, py - size, 0);
            SkyTess.addVertex(px - size, py - size, 0);
            if (s[o + 6] > 0F) {
                // Glanzkreuz
                SkyTess.setColorRGBA_F(r, gr, b, a * 0.35F);
                float l = size * 3.2F, th = 0.35F;
                SkyTess.addVertex(px - l, py + th, 0);
                SkyTess.addVertex(px + l, py + th, 0);
                SkyTess.addVertex(px + l, py - th, 0);
                SkyTess.addVertex(px - l, py - th, 0);
                SkyTess.addVertex(px - th, py + l, 0);
                SkyTess.addVertex(px + th, py + l, 0);
                SkyTess.addVertex(px + th, py - l, 0);
                SkyTess.addVertex(px - th, py - l, 0);
            }
        }
        SkyTess.draw();
        SkyGL.enableTexture();
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA);
    }

    /** Additiver Lichthof in Koerperfarbe. */
    private void drawGlow(float x, float y, float size, float[] color, float alpha) {
        float r = color != null && color.length >= 3 ? color[0] : 1F;
        float gr = color != null && color.length >= 3 ? color[1] : 1F;
        float b = color != null && color.length >= 3 ? color[2] : 1F;
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE);
        SkyGL.bind(glowTexture());
        SkyGL.color(Math.min(1F, r * 1.1F + 0.1F), Math.min(1F, gr * 1.1F + 0.1F), Math.min(1F, b * 1.1F + 0.1F), alpha);
        drawTexturedQuad(x, y, size, 0F);
        SkyGL.color(1F, 1F, 1F, 1F);
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA);
    }

    /** Bahnlinie mit Leuchtband: Punkte {x, y, staerke}. */
    private void drawOrbitPath(List<float[]> pts, float r, float gr, float b, float alpha, float glowWidth) {
        if (pts.size() < 2) return;
        SkyGL.disableTexture();
        if (glowWidth > 0F) {
            SkyTess.startDrawingQuads();
            for (int i = 1; i < pts.size(); i++) {
                float[] p0 = pts.get(i - 1), p1 = pts.get(i);
                float dx = p1[0] - p0[0], dy = p1[1] - p0[1];
                float len = (float) Math.sqrt(dx * dx + dy * dy);
                if (len < 1e-3F) continue;
                float nx = -dy / len * glowWidth, ny = dx / len * glowWidth;
                SkyTess.setColorRGBA_F(r, gr, b, 0F);
                SkyTess.addVertex(p0[0] + nx, p0[1] + ny, 0);
                SkyTess.addVertex(p1[0] + nx, p1[1] + ny, 0);
                SkyTess.setColorRGBA_F(r, gr, b, alpha * 0.30F * p1[2]);
                SkyTess.addVertex(p1[0], p1[1], 0);
                SkyTess.setColorRGBA_F(r, gr, b, alpha * 0.30F * p0[2]);
                SkyTess.addVertex(p0[0], p0[1], 0);
                SkyTess.addVertex(p0[0], p0[1], 0);
                SkyTess.setColorRGBA_F(r, gr, b, alpha * 0.30F * p1[2]);
                SkyTess.addVertex(p1[0], p1[1], 0);
                SkyTess.setColorRGBA_F(r, gr, b, 0F);
                SkyTess.addVertex(p1[0] - nx, p1[1] - ny, 0);
                SkyTess.addVertex(p0[0] - nx, p0[1] - ny, 0);
            }
            SkyTess.draw();
        }
        SkyTess.startDrawing(SkyTess.LINE_STRIP);
        for (float[] p : pts) {
            SkyTess.setColorRGBA_F(r, gr, b, alpha * p[2]);
            SkyTess.addVertex(p[0], p[1], 0);
        }
        SkyTess.draw();
        SkyGL.enableTexture();
        SkyGL.color(1F, 1F, 1F, 1F);
    }

    /** Helligkeit eines Bahnpunkts: hell direkt hinter dem Koerper, verblassend (Schweif). */
    private static float trail(double anomalyAtPoint, double bodyAnomaly) {
        double d = ((bodyAnomaly - anomalyAtPoint) % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI);
        float f = (float) (1.0 - d / (2 * Math.PI));
        return 0.18F + 0.82F * f * f * f;
    }

    /** Grundhelligkeit einer Bahn: hervorgehoben, normal oder gedimmt (anderer Koerper im Fokus). */
    private float orbitAlpha(CelestialBody body) {
        CelestialBody focus = getRenderFocusBody();
        CelestialBody hover = hoveredBody != null ? hoveredBody.body : navHoverBody;
        if (body == focus || body == hover) return 0.95F;
        if (focus != null && focus.parent != null && body != focus && body.parent != focus && focus.parent != body.parent) return 0.30F;
        return 0.55F;
    }

    /** Bodenkarte (512 px) bedeckt die Karte; ist die Karte groesser, steht sie mittig. */
    private static float clampSurface(float v, int size) {
        if (size >= 512) return 256F + (size - 512) / 2F;
        return clampF(v, -256 + size, 256);
    }

    private boolean isLeftMouseDown() {
        return GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
    }

    private void drawBodyTree(CelestialBody body, float bodyMapU, float bodyMapV, float systemOrbitScalePxPerKm, double worldTicks, double dayTicks, float parentMapU, float parentMapV) {
        if (body == null) return;

        List<ChildRenderState> childStates = new ArrayList<>();
        float parentScreenY = mapToScreenY(bodyMapU, bodyMapV);

        if (body.satellites != null) {
            for (CelestialBody child : body.satellites) {
                if (child == null) continue;

                float childScale = getChildOrbitScalePxPerKm(body, systemOrbitScalePxPerKm);
                double meanAnomaly = calculateMeanAnomaly(child, worldTicks);
                float[] offset = calculateOrbitOffsetPx(body, child, meanAnomaly, childScale);

                boolean moonChild = body.parent != null;
                float childMapU = bodyMapU + offset[0];
                float childMapV = bodyMapV + offset[1];
                boolean drawInFrontOfParent = !moonChild || mapToScreenY(childMapU, childMapV) > parentScreenY;

                childStates.add(new ChildRenderState(child, childScale, offset[0], offset[1], moonChild, drawInFrontOfParent));
            }
        }

        for (ChildRenderState childState : childStates) {
            if (!shouldDrawChildOrbit(body, childState.childBody)) {
                continue;
            }

            if (childState.moonChild) {
                drawOrbitHalf(bodyMapU, bodyMapV, body, childState.childBody, childState.orbitScalePxPerKm, false);
            } else {
                drawOrbit(bodyMapU, bodyMapV, body, childState.childBody, childState.orbitScalePxPerKm);
            }
        }

        for (ChildRenderState childState : childStates) {
            if (!childState.moonChild || childState.drawInFrontOfParent) {
                continue;
            }

            drawBodyTree(
                childState.childBody,
                bodyMapU + childState.orbitOffsetU,
                bodyMapV + childState.orbitOffsetV,
                systemOrbitScalePxPerKm,
                worldTicks,
                dayTicks,
                bodyMapU,
                bodyMapV
            );
        }

        drawArtificialSatellites(body, bodyMapU, bodyMapV, false);
        drawBody(body, bodyMapU, bodyMapV, dayTicks, parentMapU, parentMapV);
        drawArtificialSatellites(body, bodyMapU, bodyMapV, true);

        for (ChildRenderState childState : childStates) {
            if (!childState.moonChild || !shouldDrawChildOrbit(body, childState.childBody)) {
                continue;
            }

            drawOrbitHalf(bodyMapU, bodyMapV, body, childState.childBody, childState.orbitScalePxPerKm, true);
        }

        for (ChildRenderState childState : childStates) {
            if (childState.moonChild && !childState.drawInFrontOfParent) {
                continue;
            }

            drawBodyTree(
                childState.childBody,
                bodyMapU + childState.orbitOffsetU,
                bodyMapV + childState.orbitOffsetV,
                systemOrbitScalePxPerKm,
                worldTicks,
                dayTicks,
                bodyMapU,
                bodyMapV
            );
        }
    }

    private boolean shouldDrawChildOrbit(CelestialBody parent, CelestialBody child) {
        if (parent == null || child == null) return false;

        if (parent.parent == null) {
            return true;
        }

        if (!isMoon(child)) {
            return true;
        }

        // Moon orbit lines are visible only when the parent body (or one of its moons) is focused.
        CelestialBody activeFocus = getOrbitRuleFocusBody();
        return activeFocus != null && (activeFocus == parent || activeFocus.parent == parent);
    }

    private void drawOrbit(float parentMapU, float parentMapV, CelestialBody parent, CelestialBody body, float orbitScalePxPerKm) {
        if (body == null || body.semiMajorAxisKm <= 0F) return;

        float[] color = body.color != null && body.color.length >= 3 ? body.color : null;
        float r = color != null ? color[0] : 0.8F;
        float gr = color != null ? color[1] : 0.8F;
        float b = color != null ? color[2] : 0.8F;
        float alpha = orbitAlpha(body);
        if (alpha > 0.9F) {
            r = r * 0.7F + 0.3F;
            gr = gr * 0.7F + 0.3F;
            b = b * 0.7F + 0.3F;
        }
        double bodyAnomaly = calculateMeanAnomaly(body, currentWorldTicks);

        List<float[]> pts = new ArrayList<>(161);
        for (int i = 0; i <= 160; i++) {
            double meanAnomaly = 2D * Math.PI * ((double) i / 160D);
            float[] offset = calculateOrbitOffsetPx(parent, body, meanAnomaly, orbitScalePxPerKm);
            float orbitMapU = parentMapU + offset[0];
            float orbitMapV = parentMapV + offset[1];
            pts.add(new float[] {mapToScreenX(orbitMapU, orbitMapV), mapToScreenY(orbitMapU, orbitMapV), trail(meanAnomaly, bodyAnomaly)});
        }
        drawOrbitPath(pts, r, gr, b, alpha, alpha > 0.9F ? 2.4F : 1.6F);
    }

    /** Vorder- bzw. Rueckhaelfte einer Mondbahn (Schnitt an der Hoehe des Planeten), mit Schweif. */
    private void drawOrbitHalf(float parentMapU, float parentMapV, CelestialBody parent, CelestialBody body, float orbitScalePxPerKm, boolean frontHalf) {
        if (body == null || body.semiMajorAxisKm <= 0F) {
            return;
        }

        float[] color = body.color != null && body.color.length >= 3 ? body.color : null;
        float r = color != null ? color[0] : 0.8F;
        float gr = color != null ? color[1] : 0.8F;
        float b = color != null ? color[2] : 0.8F;
        float alpha = orbitAlpha(body) * (frontHalf ? 1F : 0.7F);
        double bodyAnomaly = calculateMeanAnomaly(body, currentWorldTicks);
        float crossY = mapToScreenY(parentMapU, parentMapV);

        List<float[]> segment = new ArrayList<>();
        float[] prev = null;
        for (int i = 0; i <= 128; i++) {
            double meanAnomaly = 2D * Math.PI * ((double) i / 128D);
            float[] offset = calculateOrbitOffsetPx(parent, body, meanAnomaly, orbitScalePxPerKm);
            float orbitMapU = parentMapU + offset[0];
            float orbitMapV = parentMapV + offset[1];
            float[] cur = {mapToScreenX(orbitMapU, orbitMapV), mapToScreenY(orbitMapU, orbitMapV), trail(meanAnomaly, bodyAnomaly)};
            boolean curIn = (cur[1] >= crossY) == frontHalf;
            if (prev != null) {
                boolean prevIn = (prev[1] >= crossY) == frontHalf;
                if (prevIn != curIn) {
                    float dy = cur[1] - prev[1];
                    float tc = clampF(dy == 0F ? 0.5F : (crossY - prev[1]) / dy, 0F, 1F);
                    float[] cross = {prev[0] + (cur[0] - prev[0]) * tc, crossY, prev[2] + (cur[2] - prev[2]) * tc};
                    if (prevIn) {
                        segment.add(cross);
                        drawOrbitPath(segment, r, gr, b, alpha, 1.2F);
                        segment = new ArrayList<>();
                    } else {
                        segment.add(cross);
                    }
                }
            }
            if (curIn) segment.add(cur);
            prev = cur;
        }
        drawOrbitPath(segment, r, gr, b, alpha, 1.2F);
    }

    private void drawBody(CelestialBody body, float bodyMapU, float bodyMapV, double dayTicks, float parentMapU, float parentMapV) {
        if (body == null) return;

        float drawSize = getBodySizePxAtZoom(body);
        float bodyScreenX = mapToScreenX(bodyMapU, bodyMapV);
        float bodyScreenY = mapToScreenY(bodyMapU, bodyMapV);
        float half = drawSize * 0.5F;
        float ringHalfWidth = 0F;
        float ringHalfHeight = 0F;

        float minX = bodyScreenX - half;
        float maxX = bodyScreenX + half;
        float minY = bodyScreenY - half;
        float maxY = bodyScreenY + half;

        if (body.hasRings) {
            ringHalfWidth = drawSize * 0.5F * Math.max(1F, body.ringSize);
            float ringTiltSin = Math.abs(Mth.sin((float) Math.toRadians(body.ringTilt)));
            ringTiltSin = Math.max(0.08F, ringTiltSin);
            ringHalfHeight = Math.max(0.5F, ringHalfWidth * ringTiltSin);

            minX = Math.min(minX, bodyScreenX - ringHalfWidth);
            maxX = Math.max(maxX, bodyScreenX + ringHalfWidth);
            minY = Math.min(minY, bodyScreenY - ringHalfHeight);
            maxY = Math.max(maxY, bodyScreenY + ringHalfHeight);
        }

        if (maxX < mapX || minX > mapX + mapW ||
            maxY < mapY || minY > mapY + mapH) {
            return;
        }

        // Lichthoefe: Sterne gluehen (Korona, pulsierend), Fokus/Maus-Koerper bekommen einen Schimmer
        if (body.parent == null) {
            float s = Math.max(drawSize, 6F);
            drawGlow(bodyScreenX, bodyScreenY, s * 4.2F, body.color, 0.50F + 0.08F * Mth.sin(uiTime * 1.7F));
            drawGlow(bodyScreenX, bodyScreenY, s * 1.9F, body.color, 0.65F);
        } else if (body == getRenderFocusBody() || (hoveredBody != null && hoveredBody.body == body) || body == navHoverBody) {
            drawGlow(bodyScreenX, bodyScreenY, drawSize * 2.2F + 8F, body.color, 0.30F + 0.06F * Mth.sin(uiTime * 3F));
        } else if (isTopLevelPlanet(body)) {
            drawGlow(bodyScreenX, bodyScreenY, drawSize * 2.6F + 7F, body.color, 0.16F);
        }

        if (body.hasRings) {
            drawBodyRingHalf(body, bodyScreenX, bodyScreenY, ringHalfWidth, ringHalfHeight, false);
        }

        if (body.texture != null) {
            SkyGL.bind(body.texture);
            if (body.parent == null) {
                drawTexturedQuad(bodyScreenX, bodyScreenY, drawSize, 0F);
            } else {
                AtmosphereRenderUtil.renderAtmosphereGlow2D(body, bodyScreenX, bodyScreenY, drawSize, 1.0F);
                SkyGL.bind(body.texture);

                float phase = getBodyRotationPhase(body, dayTicks);
                float bodyRotationAngle = phase * 360F;
                boolean rotateBody = hasTransparentPixels(body.texture);
                float textureUOffset = 0F;
                if (rotateBody) {
                    drawTexturedQuadRotating(bodyScreenX, bodyScreenY, drawSize, bodyRotationAngle);
                } else {
                    drawTexturedQuad(bodyScreenX, bodyScreenY, drawSize, phase);
                    textureUOffset = phase;
                }
                drawBodyOverlays(body, bodyScreenX, bodyScreenY, drawSize, bodyMapU, bodyMapV, parentMapU, parentMapV, rotateBody, bodyRotationAngle, dayTicks, textureUOffset);
            }
        } else {
            int color = colorArrayToRgb(body.color);
            g.fill((int) (bodyScreenX - half), (int) (bodyScreenY - half), (int) (bodyScreenX + half), (int) (bodyScreenY + half), color);
        }

        if (body.hasRings) {
            drawBodyRingHalf(body, bodyScreenX, bodyScreenY, ringHalfWidth, ringHalfHeight, true);
        }

        float labelAnchorExtentX = half;
        float labelAnchorExtentY = half;
        if (body.parent == null) {
            float starLabelHalfExtent = (8F * mapZoom) * 0.5F;
            labelAnchorExtentX = Math.min(labelAnchorExtentX, starLabelHalfExtent);
            labelAnchorExtentY = Math.min(labelAnchorExtentY, starLabelHalfExtent);
        }
        renderedBodyLabels.add(new BodyLabelRenderInfo(body, bodyScreenX + labelAnchorExtentX, bodyScreenY - labelAnchorExtentY));

        renderedBodies.add(new BodyRenderInfo(body, bodyMapU, bodyMapV, drawSize));
    }

    private void drawArtificialSatellites(CelestialBody body, float bodyMapU, float bodyMapV, boolean frontHalf) {
        if (!shouldDrawArtificialSatellites(body)) {
            return;
        }

        Map<Integer, ClientSatellite> satellites = SatelliteClientData.getClientSats();
        if (satellites == null || satellites.isEmpty()) {
            return;
        }

        float baseOrbitRadiusMapPx = getBodySizePxAt1x(body) * SATELLITE_ORBIT_RADIUS_SCALE;
        if (baseOrbitRadiusMapPx <= 0F) {
            return;
        }

        if (shouldDrawArtificialSatelliteOrbitLines(body)) {
            for (ClientSatellite satellite : satellites.values()) {
                if (satellite == null) {
                    continue;
                }
                drawArtificialSatelliteOrbitHalf(bodyMapU, bodyMapV, baseOrbitRadiusMapPx, satellite.orbit, frontHalf);
            }
        }

        float iconSize = clampF(getBodySizePxAtZoom(body) * SATELLITE_ICON_BODY_SCALE * 0.25F, 0.2F, 4.0F);
        float iconHalf = iconSize * 0.5F;
        float mapLeft = mapX;
        float mapTop = mapY;
        float mapRight = mapLeft + mapW;
        float mapBottom = mapTop + mapH;
        double angle = getArtificialSatelliteAngle();

        SkyGL.color(1F, 1F, 1F, 1F);
        for (Map.Entry<Integer, ClientSatellite> entry : satellites.entrySet()) {
            Integer frequency = entry.getKey();
            ClientSatellite satellite = entry.getValue();
            if (frequency == null || satellite == null) {
                continue;
            }

            SatelliteOrbit orbit = satellite.orbit;
            float satelliteAngle = SatelliteOrbit.applyPhaseOffsetToOrbitAngle(orbit.phaseOffset, orbit.altitude, angle, (float) (2D * Math.PI));
            SatelliteOrbitPoint orbitPoint = getArtificialSatelliteOrbitPoint(orbit, satelliteAngle, baseOrbitRadiusMapPx);
            float screenX = mapToScreenX(bodyMapU + orbitPoint.offsetU, bodyMapV + orbitPoint.offsetV);
            float screenY = mapToScreenY(bodyMapU + orbitPoint.offsetU, bodyMapV + orbitPoint.offsetV);
            boolean inFrontHalf = orbitPoint.depth >= 0F;
            if (inFrontHalf != frontHalf) {
                continue;
            }
            if (screenX + iconHalf < mapLeft || screenX - iconHalf > mapRight || screenY + iconHalf < mapTop || screenY - iconHalf > mapBottom) {
                continue;
            }

            SkyGL.bind(satelliteTextureDefault);
            drawPartialTex(screenX - iconHalf, screenY - iconHalf, iconSize, iconSize, 0F, 0F, 1F, 1F);

            SatelliteRenderInfo renderInfo = new SatelliteRenderInfo(frequency.intValue(), orbit, bodyMapU + orbitPoint.offsetU, bodyMapV + orbitPoint.offsetV, iconSize);
            renderedSatellites.add(renderInfo);
            if (focusedSatelliteFrequency != null && focusedSatelliteFrequency.intValue() == renderInfo.frequency) {
                focusedSatellite = renderInfo;
            }
        }
    }

    private boolean shouldDrawArtificialSatellites(CelestialBody body) {
        return body != null && currentBody != null && body == currentBody;
    }

    private void drawArtificialSatelliteOrbitHalf(float bodyMapU, float bodyMapV, float baseRadiusMapPx, SatelliteOrbit satellite, boolean frontHalf) {
        float r = clampF(satellite.colorR, 0F, 1F);
        float gr = clampF(satellite.colorG, 0F, 1F);
        float b = clampF(satellite.colorB, 0F, 1F);

        SkyGL.disableTexture();
        RenderSystem.lineWidth(1F);

        boolean hasPrev = false;
        float prevX = 0F;
        float prevY = 0F;
        float prevDepth = 0F;
        boolean prevInFrontHalf = false;
        boolean drawing = false;

        for (int i = 0; i <= 64; i++) {
            float angle = (float) (2D * Math.PI * ((double) i / 64D));
            SatelliteOrbitPoint orbitPoint = getArtificialSatelliteOrbitPoint(satellite, angle, baseRadiusMapPx);
            float currX = mapToScreenX(bodyMapU + orbitPoint.offsetU, bodyMapV + orbitPoint.offsetV);
            float currY = mapToScreenY(bodyMapU + orbitPoint.offsetU, bodyMapV + orbitPoint.offsetV);
            float currDepth = orbitPoint.depth;
            boolean currInFrontHalf = currDepth >= 0F;

            if (!hasPrev) {
                prevX = currX;
                prevY = currY;
                prevDepth = currDepth;
                prevInFrontHalf = currInFrontHalf;
                hasPrev = true;
                continue;
            }

            boolean prevSelected = prevInFrontHalf == frontHalf;
            boolean currSelected = currInFrontHalf == frontHalf;

            if (prevSelected && currSelected) {
                if (!drawing) {
                    SkyTess.startDrawing(SkyTess.LINE_STRIP);
                    SkyTess.setColorRGBA_F(r, gr, b, 0.25F);
                    SkyTess.addVertex(prevX, prevY, 0);
                    drawing = true;
                }
                SkyTess.addVertex(currX, currY, 0);
            } else if (prevSelected != currSelected) {
                float depthDelta = currDepth - prevDepth;
                float t = depthDelta == 0F ? 0.5F : -prevDepth / depthDelta;
                t = clampF(t, 0F, 1F);
                float crossX = prevX + (currX - prevX) * t;
                float crossPointY = prevY + (currY - prevY) * t;

                if (prevSelected) {
                    if (!drawing) {
                        SkyTess.startDrawing(SkyTess.LINE_STRIP);
                        SkyTess.setColorRGBA_F(r, gr, b, 0.25F);
                        SkyTess.addVertex(prevX, prevY, 0);
                        drawing = true;
                    }
                    SkyTess.addVertex(crossX, crossPointY, 0);
                    SkyTess.draw();
                    drawing = false;
                } else {
                    SkyTess.startDrawing(SkyTess.LINE_STRIP);
                    SkyTess.setColorRGBA_F(r, gr, b, 0.25F);
                    SkyTess.addVertex(crossX, crossPointY, 0);
                    SkyTess.addVertex(currX, currY, 0);
                    drawing = true;
                }
            }

            prevX = currX;
            prevY = currY;
            prevDepth = currDepth;
            prevInFrontHalf = currInFrontHalf;
        }

        if (drawing) {
            SkyTess.draw();
        }

        SkyGL.enableTexture();
        SkyGL.color(1F, 1F, 1F, 1F);
    }

    private SatelliteOrbitPoint getArtificialSatelliteOrbitPoint(@Nullable SatelliteOrbit satellite, float angle, float baseRadiusMapPx) {
        float altitude = satellite != null ? satellite.altitude : SatelliteOrbit.DEFAULT_ALTITUDE_KM;
        double inclination = Math.toRadians(satellite != null ? satellite.inclination : SatelliteOrbit.DEFAULT_INCLINATION);
        double radiusMapPx = baseRadiusMapPx * (altitude / SatelliteOrbit.DEFAULT_ALTITUDE_KM);

        double x = radiusMapPx * Mth.cos(angle);
        double y = radiusMapPx * Mth.sin(angle);
        double z = Math.sin(inclination) * y;
        y = Math.cos(inclination) * y;

        y -= z * 0.35D;
        y *= 0.8D;

        return new SatelliteOrbitPoint((float) x, (float) y, (float) z);
    }

    private double getArtificialSatelliteAngle() {
        long cycle = SATELLITE_CYCLE_MS;
        double progress = (double) System.currentTimeMillis() / (double) cycle;
        return -progress * 2D * Math.PI;
    }

    /** Beschriftungen mit Vorrang (Fokus, Maus, Standort, Sterne, Planeten, Monde) und ohne Ueberlappung. */
    private void drawQueuedBodyLabels() {
        if (renderedBodyLabels.isEmpty() || this.font == null) {
            return;
        }

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        placedLabels.clear();

        List<BodyLabelRenderInfo> sortedLabels = new ArrayList<>(renderedBodyLabels);
        // gleicher Rang: groessere Koerper zuerst (Jupiter vor Ceres)
        sortedLabels.sort(Comparator.comparingInt(this::labelPriority).thenComparingDouble(l -> -l.body.radiusKm));
        for (BodyLabelRenderInfo labelInfo : sortedLabels) {
            if (labelInfo == null || labelInfo.body == null) {
                continue;
            }
            drawBodyLabel(labelInfo.body, labelInfo.topRightAnchorScreenX, labelInfo.topRightAnchorScreenY);
        }

        RenderSystem.depthMask(true);
    }

    private int labelPriority(BodyLabelRenderInfo info) {
        CelestialBody b = info.body;
        if (b == getRenderFocusBody()) return 0;
        if (hoveredBody != null && hoveredBody.body == b) return 1;
        if (b == currentBody) return 2;
        return 3 + getBodyHierarchyDepth(b);
    }

    private void drawBodyLabel(CelestialBody body, float anchorScreenX, float anchorScreenY) {
        if (body == null || this.font == null) {
            return;
        }
        boolean important = body == getRenderFocusBody() || (hoveredBody != null && hoveredBody.body == body) || body == currentBody;
        if (!important && !shouldShowBodyLabel(body)) {
            return;
        }

        String label = getBodyDisplayName(body);
        if (label.isEmpty()) {
            return;
        }

        float scale = isMoon(body) ? 0.62F : 0.75F;
        int w = Math.round(this.font.width(label) * scale) + 6;
        int h = Math.round(this.font.lineHeight * scale) + 2;
        // Platz suchen: rechts oben, links oben, rechts unten, links unten; wichtige Namen notfalls ueberlappend
        float[][] spots = {
            {anchorScreenX + 2F, anchorScreenY - h},
            {anchorScreenX - w - 8F, anchorScreenY - h},
            {anchorScreenX + 2F, anchorScreenY + 4F},
            {anchorScreenX - w - 8F, anchorScreenY + 4F}
        };
        float x = Float.NaN, y = 0F;
        for (float[] spot : spots) {
            if (spot[0] + w > mapX + mapW - 2) continue;
            if (!labelOverlaps(spot[0], spot[1], w, h)) {
                x = spot[0];
                y = spot[1];
                break;
            }
        }
        if (Float.isNaN(x)) {
            if (!important) return;
            x = spots[0][0] + w > mapX + mapW - 2 ? spots[1][0] : spots[0][0];
            y = spots[0][1];
        }
        placedLabels.add(new float[] {x, y, x + w, y + h});

        int color = colorArrayToRgb(body.color);
        int ix = Math.round(x), iy = Math.round(y);
        g.fill(ix, iy, ix + w, iy + h, important ? 0xC0081018 : 0x8C05080F);
        g.fill(ix, iy, ix + 1, iy + h, color);
        if (important) g.fill(ix, iy + h - 1, ix + w, iy + h, (color & 0x00FFFFFF) | 0x88000000);
        int textColor = important ? 0xFFFFFFFF : (0xFF000000 | ((((color >> 16) & 255) + 255) / 2) << 16 | ((((color >> 8) & 255) + 255) / 2) << 8 | (((color & 255) + 255) / 2));
        g.pose().pushPose();
        g.pose().translate(ix + 3, iy + 1.5F, 0F);
        g.pose().scale(scale, scale, 1F);
        g.drawString(this.font, label, 0, 0, textColor, false);
        g.pose().popPose();
        SkyGL.color(1F, 1F, 1F, 1F);
    }

    private boolean labelOverlaps(float x, float y, int w, int h) {
        for (float[] r : placedLabels) {
            if (x < r[2] && x + w > r[0] && y < r[3] && y + h > r[1]) return true;
        }
        return false;
    }

    private boolean shouldShowBodyLabel(CelestialBody body) {
        if (body == null) {
            return false;
        }

        if (body.parent == null) {
            // Original: Sterne ohne Namen; Erweiterung: Sterne anderer Systeme mit Namen
            return !systemLayouts.isEmpty() && body != systemLayouts.get(0).star;
        }

        if (!isMoon(body)) {
            return true;
        }

        CelestialBody focusedTopLevelPlanet = getTopLevelPlanetForBody(getRenderFocusBody());
        if (focusedTopLevelPlanet == null) {
            return false;
        }
        return getTopLevelPlanetForBody(body) == focusedTopLevelPlanet;
    }

    private void drawBodyRingHalf(CelestialBody body, float bodyScreenX, float bodyScreenY, float ringHalfWidth, float drawH, boolean frontHalf) {
        if (body == null || ringHalfWidth <= 0F || drawH <= 0F) {
            return;
        }

        float[] ringColor = body.ringColor != null && body.ringColor.length >= 3 ? body.ringColor : null;
        float r = ringColor != null ? ringColor[0] : 0.5F;
        float gr = ringColor != null ? ringColor[1] : 0.5F;
        float b = ringColor != null ? ringColor[2] : 0.5F;
        float a = ringColor != null && ringColor.length >= 4 ? ringColor[3] : 1F;

        float drawX = bodyScreenX - ringHalfWidth;
        float drawY = frontHalf ? bodyScreenY : bodyScreenY - drawH;
        float drawW = ringHalfWidth * 2F;
        float v1 = frontHalf ? 0.5F : 0F;
        float v2 = frontHalf ? 1F : 0.5F;

        SkyGL.color(r, gr, b, a);
        SkyGL.bind(ringTexture);
        drawPartialTex(drawX, drawY, drawW, drawH, 0F, v1, 1F, v2);
        SkyGL.color(1F, 1F, 1F, 1F);
    }

    private void drawPartialTex(float x, float y, float w, float h, float u1, float v1, float u2, float v2) {
        SkyTess.startDrawingQuads();
        SkyTess.addVertexWithUV(x, y + h, 0, u1, v2);
        SkyTess.addVertexWithUV(x + w, y + h, 0, u2, v2);
        SkyTess.addVertexWithUV(x + w, y, 0, u2, v1);
        SkyTess.addVertexWithUV(x, y, 0, u1, v1);
        SkyTess.draw();
    }

    private void drawTexturedQuad(float x, float y, float size, float uOffset) {
        float half = size * 0.5F;
        float minU = uOffset;
        float maxU = 1F + uOffset;
        SkyTess.startDrawingQuads();
        SkyTess.addVertexWithUV(x - half, y + half, 0, minU, 1);
        SkyTess.addVertexWithUV(x + half, y + half, 0, maxU, 1);
        SkyTess.addVertexWithUV(x + half, y - half, 0, maxU, 0);
        SkyTess.addVertexWithUV(x - half, y - half, 0, minU, 0);
        SkyTess.draw();
    }

    private void drawTexturedQuadRotating(float x, float y, float size, float angle) {
        SkyGL.push();
        SkyGL.translate(x, y, 0F);
        SkyGL.rotate(angle, 0F, 0F, 1F);
        drawTexturedQuad(0F, 0F, size, 0F);
        SkyGL.pop();
    }

    /** {@code if(rotateBody) drawTexturedQuadRotating(...) else drawTexturedQuad(..., uOffset)}. */
    private void drawBodyQuad(float x, float y, float size, boolean rotateBody, float angle, float uOffset) {
        if (rotateBody) {
            drawTexturedQuadRotating(x, y, size, angle);
        } else {
            drawTexturedQuad(x, y, size, uOffset);
        }
    }

    private void drawBodyOverlays(CelestialBody body, float bodyScreenX, float bodyScreenY, float drawSize, float bodyMapU, float bodyMapV, float parentMapU, float parentMapV, boolean rotateBody, float bodyRotationAngle, double dayTicks, float textureUOffset) {
        if (body == null || body.parent == null || body.texture == null) {
            return;
        }

        float phase = calculateHorizontalCrescentPhase(body, bodyMapU, bodyMapV, parentMapU, parentMapV);
        CBT_Impact impact = body.getTrait(CBT_Impact.class);
        CBT_Lights light = body.getTrait(CBT_Lights.class);
        List<SkyHooks.ShockStatus> nukeShocks = SkyHooks.get().clientShocks(body);
        double impactTime = impact != null ? dayTicks - impact.time : 0.0D;
        float impactAnimationTime = impact != null ? (float) impactTime : -1.0F;
        int lightIntensity = light != null && impactTime < 40.0D ? Mth.clamp(light.getIntensity(), 0, citylights.length - 1) : 0;
        int activeBlackouts = Math.max(0, Math.min((int) (impactTime / 8.0D), 5));
        float atmosphereAlpha = AtmosphereRenderUtil.getAtmosphereSurfaceAlpha(body);
        float atmosphereDensity = AtmosphereRenderUtil.getAtmosphereDensity(body);
        Vec3 atmosphereColor = AtmosphereRenderUtil.getBodyAtmosphereColor(body);
        Vec3 cloudColor = AtmosphereRenderUtil.getBodyCloudColor(body);
        float cloudTintStrength = AtmosphereRenderUtil.getBodyCloudTintStrength(body);
        float weatherPartialTicks = (float) (dayTicks - Math.floor(dayTicks));
        float cloudStormDarkness = AtmosphereRenderUtil.getBodyCloudStormDarkness(body, weatherPartialTicks);
        float cloudLightningStrength = AtmosphereRenderUtil.getBodyCloudLightningStrength(body, weatherPartialTicks);
        int atmosphereStyle = AtmosphereRenderUtil.getAtmosphereStyle(body);
        float atmosphereTime = (float) (dayTicks / 20.0D);
        float atmospherePatternOffset = textureUOffset;
        if (!rotateBody && body != null) {
            double period = body.getRotationalPeriod();
            if (period > 0D && !Double.isNaN(period) && !Double.isInfinite(period)) {
                atmospherePatternOffset = (float) (dayTicks / period);
            }
        }

        if (atmosphereAlpha > 0.001F) {
            SkyGL.enableBlend();
            SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA);
            SkyGL.color(1F, 1F, 1F, 1F);

            SkyShader atmosphereShader = SkyShaders.ATMOSPHERE;
            if (atmosphereShader.use()) {
                atmosphereShader.setUniform1f("offset", textureUOffset);
                atmosphereShader.setUniform1f("patternOffset", atmospherePatternOffset);
                atmosphereShader.setUniform1i("bodyTex", 0);
                atmosphereShader.setUniform1i("useBodyAlphaMask", 1);
                atmosphereShader.setUniform1f("atmosphereColorR", (float) atmosphereColor.x);
                atmosphereShader.setUniform1f("atmosphereColorG", (float) atmosphereColor.y);
                atmosphereShader.setUniform1f("atmosphereColorB", (float) atmosphereColor.z);
                atmosphereShader.setUniform1f("cloudColorR", (float) cloudColor.x);
                atmosphereShader.setUniform1f("cloudColorG", (float) cloudColor.y);
                atmosphereShader.setUniform1f("cloudColorB", (float) cloudColor.z);
                atmosphereShader.setUniform1f("cloudTintStrength", cloudTintStrength);
                atmosphereShader.setUniform1f("cloudStormDarkness", cloudStormDarkness);
                atmosphereShader.setUniform1f("atmosphereAlpha", atmosphereAlpha);
                atmosphereShader.setUniform1f("atmosphereTime", atmosphereTime);
                atmosphereShader.setUniform1i("atmosphereStyle", atmosphereStyle);
                atmosphereShader.setUniform1f("impactTime", impactAnimationTime);
                AtmosphereRenderUtil.applyNukeShockUniforms(atmosphereShader, nukeShocks, dayTicks);

                SkyGL.bind(body.texture);
                drawBodyQuad(bodyScreenX, bodyScreenY, drawSize, rotateBody, bodyRotationAngle, 0F);

                atmosphereShader.stop();
            }
        }

        SkyGL.enableBlend();
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA);
        SkyGL.color(1F, 1F, 1F, 1F);

        SkyShader crescentShader = SkyShaders.CRESCENT;
        // unter 6 px nur schwarze Punkte: Schatten erst bei groesseren Scheiben
        if (drawSize >= 6F && crescentShader.use()) {
            crescentShader.setUniform1f("phase", phase);
            crescentShader.setUniform1f("offset", textureUOffset);
            crescentShader.setUniform1i("bodyTex", 0);
            crescentShader.setUniform1i("useBodyAlphaMask", 1);

            SkyGL.bind(body.texture);
            drawBodyQuad(bodyScreenX, bodyScreenY, drawSize, rotateBody, bodyRotationAngle, 0F);

            crescentShader.stop();
        }

        if (lightIntensity > 0
                && atmosphereDensity > 0.001F
                && (atmosphereStyle == AtmosphereRenderUtil.ATMOSPHERE_STYLE_CLOUDS || atmosphereStyle == AtmosphereRenderUtil.ATMOSPHERE_STYLE_HAZE)) {
            SkyGL.enableBlend();
            SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE);
            SkyGL.color(1F, 1F, 1F, 1F);

            SkyShader atmosphereEmissiveShader = SkyShaders.ATMOSPHERE_EMISSIVE;
            if (atmosphereEmissiveShader.use()) {
                atmosphereEmissiveShader.setUniform1f("phase", phase);
                atmosphereEmissiveShader.setUniform1f("offset", textureUOffset);
                atmosphereEmissiveShader.setUniform1f("atmosphereDensity", atmosphereDensity);
                atmosphereEmissiveShader.setUniform1f("patternOffset", atmospherePatternOffset);
                atmosphereEmissiveShader.setUniform1f("atmosphereTime", atmosphereTime);
                atmosphereEmissiveShader.setUniform1i("atmosphereStyle", atmosphereStyle);
                atmosphereEmissiveShader.setUniform1f("impactTime", impactAnimationTime);
                AtmosphereRenderUtil.applyNukeShockUniforms(atmosphereEmissiveShader, nukeShocks, dayTicks);
                atmosphereEmissiveShader.setUniform1i("bodyTex", 0);
                atmosphereEmissiveShader.setUniform1i("lights", 1);
                atmosphereEmissiveShader.setUniform1i("cityMask", 2);
                atmosphereEmissiveShader.setUniform1i("blackouts", activeBlackouts);
                atmosphereEmissiveShader.setUniform1i("useBodyAlphaMask", 1);

                SkyGL.bind(body.texture);
                SkyGL.bindUnit(1, citylights[lightIntensity]);
                SkyGL.bindUnit(2, body.cityMask != null ? body.cityMask : defaultMask);
                drawBodyQuad(bodyScreenX, bodyScreenY, drawSize, rotateBody, bodyRotationAngle, 0F);

                atmosphereEmissiveShader.stop();
            }
        }

        if (lightIntensity > 0) {
            SkyGL.enableBlend();
            SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE);
            SkyGL.color(1F, 1F, 1F, 1F);

            SkyShader nightLightsShader = SkyShaders.NIGHT_LIGHTS;
            if (nightLightsShader.use()) {
                nightLightsShader.setUniform1f("phase", phase);
                nightLightsShader.setUniform1f("offset", textureUOffset);
                nightLightsShader.setUniform1f("atmosphereDensity", atmosphereDensity);
                nightLightsShader.setUniform1f("patternOffset", atmospherePatternOffset);
                nightLightsShader.setUniform1f("atmosphereTime", atmosphereTime);
                nightLightsShader.setUniform1i("atmosphereStyle", atmosphereStyle);
                nightLightsShader.setUniform1f("impactTime", impactAnimationTime);
                AtmosphereRenderUtil.applyNukeShockUniforms(nightLightsShader, nukeShocks, dayTicks);
                nightLightsShader.setUniform1i("bodyTex", 0);
                nightLightsShader.setUniform1i("lights", 1);
                nightLightsShader.setUniform1i("cityMask", 2);
                nightLightsShader.setUniform1i("blackouts", activeBlackouts);
                nightLightsShader.setUniform1i("useBodyAlphaMask", 1);

                SkyGL.bind(body.texture);
                SkyGL.bindUnit(1, citylights[lightIntensity]);
                SkyGL.bindUnit(2, body.cityMask != null ? body.cityMask : defaultMask);
                drawBodyQuad(bodyScreenX, bodyScreenY, drawSize, rotateBody, bodyRotationAngle, 0F);

                nightLightsShader.stop();
            }
        }

        if (atmosphereAlpha > 0.001F && cloudLightningStrength > 0.001F
                && (atmosphereStyle == AtmosphereRenderUtil.ATMOSPHERE_STYLE_CLOUDS || atmosphereStyle == AtmosphereRenderUtil.ATMOSPHERE_STYLE_HAZE)) {
            SkyGL.enableBlend();
            SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE);
            SkyGL.color(1F, 1F, 1F, 1F);

            SkyShader lightningShader = SkyShaders.LIGHTNING;
            if (lightningShader.use()) {
                lightningShader.setUniform1f("phase", phase);
                lightningShader.setUniform1f("offset", textureUOffset);
                lightningShader.setUniform1f("patternOffset", atmospherePatternOffset);
                lightningShader.setUniform1i("bodyTex", 0);
                lightningShader.setUniform1i("cityMask", 1);
                lightningShader.setUniform1i("useBodyAlphaMask", 1);
                lightningShader.setUniform1f("cloudTintStrength", cloudTintStrength);
                lightningShader.setUniform1f("cloudLightningStrength", cloudLightningStrength);
                lightningShader.setUniform1f("atmosphereAlpha", atmosphereAlpha);
                lightningShader.setUniform1f("atmosphereTime", atmosphereTime);
                lightningShader.setUniform1f("eveFlashStrength", AtmosphereRenderUtil.getBodyEveFlashStrength(body, atmosphereTime));
                lightningShader.setUniform1i("atmosphereStyle", atmosphereStyle);
                lightningShader.setUniform1i("lightningMode", AtmosphereRenderUtil.getBodyLightningMode(body));
                lightningShader.setUniform1f("impactTime", impactAnimationTime);
                AtmosphereRenderUtil.applyNukeShockUniforms(lightningShader, nukeShocks, dayTicks);

                SkyGL.bind(body.texture);
                SkyGL.bindUnit(1, body.cityMask != null ? body.cityMask : defaultMask);
                drawBodyQuad(bodyScreenX, bodyScreenY, drawSize, rotateBody, bodyRotationAngle, 0F);

                lightningShader.stop();
            }
        }

        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA);

        if (impact != null) {
            float lavaAlpha = (float) Math.min(impactTime * 0.1D, 1.0D);
            if (lavaAlpha > 0F) {
                SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE);
                SkyGL.color(1F, 1F, 1F, lavaAlpha);
                SkyGL.bind(impactTexture);
                drawBodyQuad(bodyScreenX, bodyScreenY, drawSize, rotateBody, bodyRotationAngle, textureUOffset);
                SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA);
            }
        }

        if (!nukeShocks.isEmpty()) {
            renderNukeImpactOverlay(bodyScreenX, bodyScreenY, drawSize, rotateBody, bodyRotationAngle, phase, nukeShocks, dayTicks);
            SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA);
        }

        SkyGL.color(1F, 1F, 1F, 1F);
    }

    private void renderNukeImpactOverlay(float bodyScreenX, float bodyScreenY, float drawSize, boolean rotateBody, float bodyRotationAngle, float phase, List<SkyHooks.ShockStatus> nukeShocks, double currentShockTime) {
        SkyGL.enableBlend();
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE);
        SkyGL.color(1F, 1F, 1F, 1F);

        SkyShader nukeShader = SkyShaders.NUKE;
        if (!nukeShader.use()) return;
        nukeShader.setUniform1f("phase", phase);
        AtmosphereRenderUtil.applyNukeShockUniforms(nukeShader, nukeShocks, currentShockTime);
        drawBodyQuad(bodyScreenX, bodyScreenY, drawSize, rotateBody, bodyRotationAngle, 0F);

        nukeShader.stop();
    }

    // yeah no, radius checks and new trait just don't work. drop them transparency checks, planets won't have it anyway, how bad it can be!
    private boolean hasTransparentPixels(ResourceLocation tex) {
        if (tex == null || minecraft == null || minecraft.getResourceManager() == null) {
            return false;
        }

        Boolean cached = textureAlphaCache.get(tex);
        if (cached != null) {
            return cached;
        }

        boolean hasAlpha = false;
        Optional<Resource> resource = minecraft.getResourceManager().getResource(tex);
        if (resource.isPresent()) {
            try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
                // Bilder ohne Alphakanal (Original getColorModel().hasAlpha() == false): getPixelRGBA wirft -> false
                for (int y = 0; y < image.getHeight() && !hasAlpha; y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        if (((image.getPixelRGBA(x, y) >>> 24) & 255) < 255) {
                            hasAlpha = true;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        textureAlphaCache.put(tex, hasAlpha);
        return hasAlpha;
    }

    private float calculateHorizontalCrescentPhase(CelestialBody body, float bodyMapU, float bodyMapV, float parentMapU, float parentMapV) {
        float sunMapU = MAP_TEX_W * 0.5F;
        float sunMapV = MAP_TEX_H * 0.5F;
        float bodyProjX = projectMapToIsoX(bodyMapU, bodyMapV);
        float bodyProjY = projectMapToIsoY(bodyMapU, bodyMapV);
        float sunProjX = projectMapToIsoX(sunMapU, sunMapV);
        float sunProjY = projectMapToIsoY(sunMapU, sunMapV);

        float dx = bodyProjX - sunProjX;
        float dy = bodyProjY - sunProjY;
        float lengthSq = dx * dx + dy * dy;
        if (lengthSq <= 0.0001F) {
            return 0F;
        }

        float length = Mth.sqrt(lengthSq);
        float verticalFactor = dy / length;
        float phaseMagnitude = clampF((verticalFactor + 1F) * 0.5F, 0F, 1F);

        phaseMagnitude = applyMoonEclipseDarkening(body, bodyMapU, bodyMapV, parentMapU, parentMapV, sunMapU, sunMapV, phaseMagnitude);

        float phaseSign = dx <= 0F ? 1F : -1F;
        return clampF(phaseMagnitude * phaseSign, -1F, 1F);
    }

    private float applyMoonEclipseDarkening(CelestialBody body, float moonMapU, float moonMapV,
                                            float parentMapU, float parentMapV, float sunMapU, float sunMapV, float phaseMagnitude) {
        if (!isMoon(body) || body.parent == null) {
            return phaseMagnitude;
        }
        if (Float.isNaN(parentMapU) || Float.isNaN(parentMapV)) {
            return phaseMagnitude;
        }

        float moonProjX = projectMapToIsoX(moonMapU, moonMapV);
        float moonProjY = projectMapToIsoY(moonMapU, moonMapV);
        float parentProjX = projectMapToIsoX(parentMapU, parentMapV);
        float parentProjY = projectMapToIsoY(parentMapU, parentMapV);
        float sunProjX = projectMapToIsoX(sunMapU, sunMapV);
        float sunProjY = projectMapToIsoY(sunMapU, sunMapV);

        float sunToParentX = parentProjX - sunProjX;
        float sunToParentY = parentProjY - sunProjY;
        float parentToMoonX = moonProjX - parentProjX;
        float parentToMoonY = moonProjY - parentProjY;

        float sunParentLenSq = sunToParentX * sunToParentX + sunToParentY * sunToParentY;
        if (sunParentLenSq <= 0.0001F) {
            return phaseMagnitude;
        }

        float sunParentLen = Mth.sqrt(sunParentLenSq);
        float alongShadowAxis = (sunToParentX * parentToMoonX + sunToParentY * parentToMoonY) / sunParentLen;
        float lineDistance = Math.abs(sunToParentX * parentToMoonY - sunToParentY * parentToMoonX) / sunParentLen;
        float eclipseRadius = Math.max(1.5F, getBodySizePxAt1x(body.parent) * 0.45F);
        float penumbraRadius = eclipseRadius * 2F;
        float behindFade = Math.max(0.15F, eclipseRadius * 0.12F);

        if (lineDistance >= penumbraRadius || alongShadowAxis <= -behindFade) {
            return phaseMagnitude;
        }

        float behindFactor = alongShadowAxis >= 0F ? 1F : smoothstep(alongShadowAxis, -behindFade, 0F);
        if (lineDistance <= eclipseRadius) {
            return clampF(phaseMagnitude + (1F - phaseMagnitude) * behindFactor, 0F, 1F);
        }

        float penumbraFactor = 1F - smoothstep(lineDistance, eclipseRadius, penumbraRadius);
        float eclipseFactor = behindFactor * penumbraFactor;
        return clampF(phaseMagnitude + (1F - phaseMagnitude) * eclipseFactor, 0F, 1F);
    }

    /** Original {@code Library.smoothstep(t, edge0, edge1)}. */
    private static float smoothstep(float t, float edge0, float edge1) {
        t = clampF((t - edge0) / (edge1 - edge0), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    /** Original {@code MathHelper.clamp_float} (bei NaN-Grenzen bleibt der Wert, anders als {@code Mth.clamp}). */
    private static float clampF(float v, float min, float max) {
        return v < min ? min : (v > max ? max : v);
    }

    private float projectMapToIsoX(float mapU, float mapV) {
        return (mapU - mapV) * ISO_X;
    }

    private float projectMapToIsoY(float mapU, float mapV) {
        return (mapU + mapV) * ISO_Y;
    }

    private float mapToScreenX(float mapCoordU, float mapCoordV) {
        float centerU = mapU + (mapW / mapZoom) * 0.5F;
        float centerV = mapV + (mapH / mapZoom) * 0.5F;
        float relU = mapCoordU - centerU;
        float relV = mapCoordV - centerV;
        return mapCenterX() + (relU - relV) * mapZoom * ISO_X;
    }

    private float mapToScreenY(float mapCoordU, float mapCoordV) {
        float centerU = mapU + (mapW / mapZoom) * 0.5F;
        float centerV = mapV + (mapH / mapZoom) * 0.5F;
        float relU = mapCoordU - centerU;
        float relV = mapCoordV - centerV;
        return mapY + mapH * 0.5F + (relU + relV) * mapZoom * ISO_Y;
    }

    private BodyRenderInfo findBodyUnderCursor(int mouseX, int mouseY) {
        BodyRenderInfo nearest = null;
        float nearestDistSq = Float.MAX_VALUE;

        for (int i = renderedBodies.size() - 1; i >= 0; i--) {
            BodyRenderInfo bodyInfo = renderedBodies.get(i);
            float bodyX = mapToScreenX(bodyInfo.mapU, bodyInfo.mapV);
            float bodyY = mapToScreenY(bodyInfo.mapU, bodyInfo.mapV);
            float clickableRadius = Math.max(5F, bodyInfo.drawSize * 0.5F);

            float dx = mouseX - bodyX;
            float dy = mouseY - bodyY;
            float distanceSq = dx * dx + dy * dy;

            if (distanceSq <= clickableRadius * clickableRadius && distanceSq < nearestDistSq) {
                nearestDistSq = distanceSq;
                nearest = bodyInfo;
            }
        }

        return nearest;
    }

    private SatelliteRenderInfo findSatelliteUnderCursor(int mouseX, int mouseY) {
        SatelliteRenderInfo nearest = null;
        float nearestDistSq = Float.MAX_VALUE;

        for (int i = renderedSatellites.size() - 1; i >= 0; i--) {
            SatelliteRenderInfo satelliteInfo = renderedSatellites.get(i);
            float satelliteX = mapToScreenX(satelliteInfo.mapU, satelliteInfo.mapV);
            float satelliteY = mapToScreenY(satelliteInfo.mapU, satelliteInfo.mapV);
            float clickableRadius = Math.max(4F, satelliteInfo.drawSize * 0.75F);
            float dx = mouseX - satelliteX;
            float dy = mouseY - satelliteY;
            float distanceSq = dx * dx + dy * dy;

            if (distanceSq <= clickableRadius * clickableRadius && distanceSq < nearestDistSq) {
                nearestDistSq = distanceSq;
                nearest = satelliteInfo;
            }
        }

        return nearest;
    }

    // ── Maus (Original handleMouseInput / mouseClicked / mouseClickMove / mouseMovedOrUp) ──

    //? if < 1.21.1 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        super.mouseScrolled(mouseX, mouseY, delta);
        handleWheel((int) mouseX, (int) mouseY, delta);
        return true;
    }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
        handleWheel((int) mouseX, (int) mouseY, deltaY);
        return true;
    }
    *///?}

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        if (landingMode) handleLandingMouseInput(-1, false);
    }

    /** Original {@code handleMouseInput}, Mausrad-Zweig. */
    private void handleWheel(int mouseX, int mouseY, double scroll) {
        if (landingMode) {
            handleLandingMouseInput(-1, false);
            return;
        }

        if (this.minecraft == null) return;

        int wheel = scroll > 0 ? 1 : scroll < 0 ? -1 : 0;
        if (wheel == 0) return;

        if (mouseX < mapX || mouseX >= mapX + mapW || mouseY < mapY || mouseY >= mapY + mapH || focusAnimationActive) return;
        if (isOverUi(mouseX, mouseY)) return;
        if (isMoon(focusedBody)) return;

        float oldZoom = zoomTarget > 0F ? zoomTarget : mapZoom;
        float minZoom = MIN_ZOOM;
        float maxZoom = MAX_ZOOM;

        if (focusedBody != null) {
            float[] bounds = new float[2];
            if (calculateFocusedChildZoomBounds(focusedBody, bounds)) {
                minZoom = bounds[0];
                maxZoom = Math.max(bounds[1], getBodyDetailZoomCap(focusedBody));
            }
        }

        float newZoom = oldZoom;
        if (wheel > 0) newZoom = Math.min(maxZoom, oldZoom * ZOOM_STEP);
        if (wheel < 0) newZoom = Math.max(minZoom, oldZoom / ZOOM_STEP);

        if (newZoom != oldZoom) {
            // weich: Ziel setzen, updateKeyboardAndZoom naehert sich je Bild an
            zoomTarget = newZoom;
            zoomAnchorX = mouseX;
            zoomAnchorY = mouseY;
        }
    }

    /**
     * Original {@code handleLandingMouseInput} (nach jedem Mausereignis im Landemodus). Wie im Original laeuft es nach
     * {@code mouseMovedOrUp}, das {@code draggingSurface} beim Loslassen schon zuruecksetzt.
     */
    private void handleLandingMouseInput(int button, boolean buttonState) {
        float cx = clampSurface(surfaceX, mapW), cy = clampSurface(surfaceY, mapH);
        if (cx != surfaceX) {
            velocityX = 0;
            surfaceX = cx;
        }
        if (cy != surfaceY) {
            velocityY = 0;
            surfaceY = cy;
        }

        if (draggingSurface && button == 0 && !buttonState) {
            velocityX = (mX - lX) * 0.8F;
            velocityY = (mY - lY) * 0.8F;
            draggingSurface = false;
        }
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
        super.mouseDragged(x, y, button, dragX, dragY);
        mouseClickMove((int) x, (int) y, button);
        if (landingMode) handleLandingMouseInput(button, true);
        return true;
    }

    /** Original {@code mouseClickMove}. */
    private void mouseClickMove(int mouseX, int mouseY, int button) {
        if (!landingMode || !draggingSurface) return;

        int deltaX = mouseX - mX;
        int deltaY = mouseY - mY;
        surfaceX += deltaX;
        surfaceY += deltaY;

        lX = mX;
        lY = mY;
        mX = mouseX;
        mY = mouseY;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        super.mouseClicked(x, y, button);
        onMouseClicked((int) x, (int) y, button);
        if (landingMode) handleLandingMouseInput(button, true);
        return true;
    }

    private void playButton() {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private void sendControl(CompoundTag data) {
        if (star == null) return;
        NBTControlPacket.sendToServer(star.getBlockPos(), data);
    }

    /** Original {@code mouseClicked}. */
    private void onMouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            for (int k = uiButtons.size() - 1; k >= 0; k--) {
                UiButton b = uiButtons.get(k);
                if (b.contains(mouseX, mouseY)) {
                    draggingMap = false;
                    b.action.run();
                    return;
                }
            }
        }
        if (isOverUi(mouseX, mouseY)) {
            draggingMap = false;
            draggingSurface = false;
            return;
        }

        if (mouseX < mapX || mouseX >= mapX + mapW || mouseY < mapY || mouseY >= mapY + mapH) {
            draggingMap = false;
            draggingSurface = false;
            return;
        }

        if (landingMode) {
            if (button == 1) {
                exitLandingMode();
                return;
            }
            if (button == 0) {
                draggingSurface = true;
                sX = mX = lX = mouseX;
                sY = mY = lY = mouseY;
            }
            return;
        }

        if (button == 1) {
            if (focusedSatelliteFrequency != null) {
                clearSatelliteFocus();
                draggingMap = false;
                return;
            }
            beginUnfocusAnimation();
            draggingMap = false;
            return;
        }

        if (button != 0) {
            return;
        }

        SystemIndicator clickedIndicator = findSystemIndicatorUnderCursor(mouseX, mouseY);
        if (clickedIndicator != null) {
            focusStar(clickedIndicator.layout);
            draggingMap = false;
            return;
        }

        SatelliteRenderInfo clickedSatellite = findSatelliteUnderCursor(mouseX, mouseY);
        if (clickedSatellite != null) {
            focusSatellite(clickedSatellite);
            draggingMap = false;
            return;
        }

        BodyRenderInfo clickedBody = findBodyUnderCursor(mouseX, mouseY);
        if (clickedBody != null) {
            CelestialBody activeFocusBody = getRenderFocusBody();
            if (activeFocusBody != null && clickedBody.body == activeFocusBody) {
                handleFocusedBodySecondClick(clickedBody.body);
                draggingMap = false;
                return;
            }

            if (clickedBody.body.parent != null) {
                startFocusAnimation(clickedBody);
                draggingMap = false;
                return;
            }

            // Erweiterung: Stern eines anderen Systems -> fokussieren (der eigene Stern bleibt wie im Original Ziehflaeche)
            SystemLayout starLayout = layoutFor(clickedBody.body);
            if (starLayout != null && starLayout != systemLayouts.get(0) && clickedBody.body == starLayout.star) {
                focusStar(starLayout);
                draggingMap = false;
                return;
            }
        }

        if (isFocusContextActive()) {
            draggingMap = false;
            return;
        }

        draggingMap = true;
        dragLastX = mouseX;
        dragLastY = mouseY;
    }

    private void handleFocusedBodySecondClick(CelestialBody body) {
        if (body == null) return;
        if (!isReachableFromHere(body)) {
            return; // Erweiterung: Ziel in anderem Sternsystem -> interstellarer Antrieb noetig (gesperrt)
        }
        if (!body.canLand) {
            return;
        }

        ItemStack slotStack = slotStack();
        if (!slotStack.isEmpty() && slotStack.getItem() == SpaceMaterialItems.HARD_DRIVE.get()) {
            enterLandingMode(body);
        }
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        super.mouseReleased(x, y, button);
        mouseMovedOrUp((int) x, (int) y, button);
        if (landingMode) handleLandingMouseInput(button, false);
        return true;
    }

    /** Original {@code mouseMovedOrUp} (Loslassen einer Taste). */
    private void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        if (which == 0) {
            draggingMap = false;
            draggingSurface = false;
        }

        if (!landingMode || which == -1 || which == 1) return;

        if (Math.abs(sX - mouseX) > 2 || Math.abs(sY - mouseY) > 2) return;
        if (mouseX < mapX || mouseX >= mapX + mapW || mouseY < mapY || mouseY >= mapY + mapH || isOverUi(mouseX, mouseY)) return;
        if (heightmap() == null) return;

        int hx = (mouseX - (int) surfaceX - mapX + 256) / 2;
        int hz = (mouseY - (int) surfaceY - mapY + 256) / 2;
        if (landingInfo(hx, hz) != null) return;

        playButton();
        CompoundTag data = new CompoundTag();
        data.putInt("px", hx);
        data.putInt("pz", hz);
        sendControl(data);
    }

    private void updateDragPanning(int mouseX, int mouseY) {
        if (!draggingMap) {
            return;
        }

        if (isFocusContextActive() || !isLeftMouseDown()) {
            draggingMap = false;
            return;
        }

        int dx = mouseX - dragLastX;
        int dy = mouseY - dragLastY;
        if (dx != 0 || dy != 0) {
            panByScreenDelta(dx, dy);
            clampMapUV();
        }

        dragLastX = mouseX;
        dragLastY = mouseY;
    }

    private void panByScreenDelta(int dx, int dy) {
        float zoom = Math.max(0.0001F, mapZoom);
        float speed = 1F; // Karte folgt dem Mauszeiger exakt

        float a = -dx / (zoom * ISO_X);
        float b = -dy / (zoom * ISO_Y);
        float deltaU = 0.5F * (a + b);
        float deltaV = 0.5F * (b - a);

        mapU += deltaU * speed;
        mapV += deltaV * speed;
    }

    private void startFocusAnimation(BodyRenderInfo bodyInfo) {
        if (bodyInfo == null || bodyInfo.body == null || bodyInfo.body.parent == null) {
            return;
        }
        if (bodyInfo.body == focusedBody) {
            return;
        }
        if (focusAnimationActive && focusAnimationToBody && bodyInfo.body == focusAnimationBody) {
            return;
        }

        clearSatelliteFocus();
        playButton();
        configureFocusAnimation(bodyInfo.body, bodyInfo.body, true, 500, bodyInfo.mapU, bodyInfo.mapV, getFocusZoomForBody(bodyInfo.body));
    }

    private void beginUnfocusAnimation() {
        if (!hasBodyFocusContext()) {
            return;
        }

        CelestialBody activeFocus = focusedBody != null ? focusedBody : focusAnimationBody;
        CelestialBody parentFocus = activeFocus != null ? activeFocus.parent : null;
        if (parentFocus != null && parentFocus.parent != null) {
            configureFocusAnimation(parentFocus, parentFocus, true, 420, 0F, 0F, getFocusZoomForBody(parentFocus));
            return;
        }

        // Erweiterung: zur Mitte des Systems des fokussierten Koerpers (Heimat = Kartenmitte wie im Original)
        SystemLayout layout = layoutFor(activeFocus);
        float targetU = layout != null ? layout.centerU : MAP_TEX_W * 0.5F;
        float targetV = layout != null ? layout.centerV : MAP_TEX_H * 0.5F;
        configureFocusAnimation(null, null, false, 420, targetU, targetV, overviewZoom());
    }

    private void configureFocusAnimation(CelestialBody newFocusedBody, CelestialBody animationBody, boolean animationToBody,
                                         int durationMs, float targetCenterU, float targetCenterV, float targetZoom) {
        focusedBody = newFocusedBody;
        focusAnimationBody = animationBody;
        focusAnimationToBody = animationToBody;
        focusAnimationActive = true;
        focusAnimDurationMs = durationMs;
        focusAnimStartTimeMs = System.currentTimeMillis();
        focusAnimStartCenterU = mapU + (mapW / mapZoom) * 0.5F;
        focusAnimStartCenterV = mapV + (mapH / mapZoom) * 0.5F;
        focusAnimStartZoom = mapZoom;
        focusAnimTargetCenterU = targetCenterU;
        focusAnimTargetCenterV = targetCenterV;
        focusAnimTargetZoom = targetZoom;
        focusAnimTrackingInitialized = false;
        focusAnimTrackingOffsetU = 0F;
        focusAnimTrackingOffsetV = 0F;
    }

    private void updateFocusView(CelestialBody starBody, float starCenterU, float starCenterV, float systemOrbitScalePxPerKm, double worldTicks) {
        if (updateFocusedSatelliteView(starBody, starCenterU, starCenterV, systemOrbitScalePxPerKm, worldTicks)) {
            return;
        }

        if (!hasBodyFocusContext()) {
            return;
        }

        CelestialBody trackingBody = focusedBody != null ? focusedBody : focusAnimationBody;
        boolean hasTrackingBodyPosition = false;
        if (trackingBody != null) {
            // Erweiterung: Koerper im jeweils eigenen Sternsystem suchen (Original: nur Kerbol-Baum)
            SystemLayout trackingLayout = layoutFor(trackingBody);
            if (trackingLayout != null && trackingLayout.star != starBody) {
                hasTrackingBodyPosition = findBodyMapPosition(trackingLayout.star, trackingLayout.centerU, trackingLayout.centerV, trackingLayout.orbitScale, worldTicks, trackingBody, trackedBodyPosition);
            } else {
                hasTrackingBodyPosition = findBodyMapPosition(starBody, starCenterU, starCenterV, systemOrbitScalePxPerKm, worldTicks, trackingBody, trackedBodyPosition);
            }
        }

        if (focusAnimationActive) {
            long now = System.currentTimeMillis();
            double duration = Math.max(1D, focusAnimDurationMs);
            float t = clampF((float) ((now - focusAnimStartTimeMs) / duration), 0F, 1F);
            float eased = t * t * (3F - 2F * t);

            float centerU;
            float centerV;
            if (focusAnimationToBody && hasTrackingBodyPosition) {
                if (!focusAnimTrackingInitialized) {
                    focusAnimTrackingOffsetU = focusAnimStartCenterU - trackedBodyPosition.mapU;
                    focusAnimTrackingOffsetV = focusAnimStartCenterV - trackedBodyPosition.mapV;
                    focusAnimTrackingInitialized = true;
                }
                float remaining = 1F - eased;
                centerU = trackedBodyPosition.mapU + focusAnimTrackingOffsetU * remaining;
                centerV = trackedBodyPosition.mapV + focusAnimTrackingOffsetV * remaining;
            } else {
                centerU = focusAnimStartCenterU + (focusAnimTargetCenterU - focusAnimStartCenterU) * eased;
                centerV = focusAnimStartCenterV + (focusAnimTargetCenterV - focusAnimStartCenterV) * eased;
            }

            float zoom = focusAnimStartZoom + (focusAnimTargetZoom - focusAnimStartZoom) * eased;
            setViewFromCenter(centerU, centerV, zoom);

            if (t >= 1F) {
                focusAnimationActive = false;
                focusAnimationBody = null;
                focusAnimTrackingInitialized = false;
            }
            return;
        }

        if (focusedBody != null && hasTrackingBodyPosition) {
            setViewFromCenter(trackedBodyPosition.mapU, trackedBodyPosition.mapV, mapZoom);
            return;
        }

        if (focusedBody != null) {
            cancelFocusState();
        }
    }

    private boolean updateFocusedSatelliteView(CelestialBody starBody, float starCenterU, float starCenterV, float systemOrbitScalePxPerKm, double worldTicks) {
        if (focusedSatelliteFrequency == null) {
            return false;
        }

        if (!findSatelliteMapPosition(starBody, starCenterU, starCenterV, systemOrbitScalePxPerKm, worldTicks, focusedSatelliteFrequency.intValue(), trackedSatellitePosition)) {
            clearSatelliteFocus();
            return false;
        }

        setViewFromCenter(trackedSatellitePosition.mapU, trackedSatellitePosition.mapV, mapZoom);
        return true;
    }

    private boolean findBodyMapPosition(CelestialBody body, float bodyMapU, float bodyMapV, float systemOrbitScalePxPerKm,
                                        double worldTicks, CelestialBody targetBody, BodyPosition outPosition) {
        if (body == targetBody) {
            outPosition.mapU = bodyMapU;
            outPosition.mapV = bodyMapV;
            return true;
        }

        if (body.satellites == null) {
            return false;
        }

        float childOrbitScale = getChildOrbitScalePxPerKm(body, systemOrbitScalePxPerKm);
        for (CelestialBody childBody : body.satellites) {
            if (childBody == null) continue;
            double meanAnomaly = calculateMeanAnomaly(childBody, worldTicks);
            float[] orbitOffset = calculateOrbitOffsetPx(body, childBody, meanAnomaly, childOrbitScale);
            if (findBodyMapPosition(childBody, bodyMapU + orbitOffset[0], bodyMapV + orbitOffset[1], systemOrbitScalePxPerKm, worldTicks, targetBody, outPosition)) {
                return true;
            }
        }

        return false;
    }

    private boolean findSatelliteMapPosition(CelestialBody starBody, float starCenterU, float starCenterV, float systemOrbitScalePxPerKm, double worldTicks, int frequency, BodyPosition outPosition) {
        if (currentBody == null || outPosition == null) {
            return false;
        }

        Map<Integer, ClientSatellite> satellites = SatelliteClientData.getClientSats();
        if (satellites == null || satellites.isEmpty()) {
            return false;
        }

        ClientSatellite satellite = satellites.get(frequency);
        if (satellite == null) {
            return false;
        }

        if (!findBodyMapPosition(starBody, starCenterU, starCenterV, systemOrbitScalePxPerKm, worldTicks, currentBody, trackedBodyPosition)) {
            return false;
        }

        float baseOrbitRadiusMapPx = getBodySizePxAt1x(currentBody) * SATELLITE_ORBIT_RADIUS_SCALE;
        if (baseOrbitRadiusMapPx <= 0F) {
            return false;
        }

        SatelliteOrbit orbit = satellite.orbit;
        float satelliteAngle = SatelliteOrbit.applyPhaseOffsetToOrbitAngle(orbit.phaseOffset, orbit.altitude, getArtificialSatelliteAngle(), (float) (2D * Math.PI));
        SatelliteOrbitPoint orbitPoint = getArtificialSatelliteOrbitPoint(orbit, satelliteAngle, baseOrbitRadiusMapPx);
        outPosition.mapU = trackedBodyPosition.mapU + orbitPoint.offsetU;
        outPosition.mapV = trackedBodyPosition.mapV + orbitPoint.offsetV;
        return true;
    }

    private void zoomAtMouse(int mouseX, int mouseY, float oldZoom, float newZoom) {
        float minZoom = MIN_ZOOM;
        if (oldZoom <= 0F || newZoom <= 0F) {
            mapZoom = clampF(newZoom, minZoom, MAX_ZOOM);
            clampMapUV();
            return;
        }

        float mapCenterScreenX = mapCenterX();
        float mapCenterScreenY = mapY + mapH * 0.5F;

        float oldCenterU = mapU + (mapW / oldZoom) * 0.5F;
        float oldCenterV = mapV + (mapH / oldZoom) * 0.5F;

        float oldA = (mouseX - mapCenterScreenX) / (oldZoom * ISO_X);
        float oldB = (mouseY - mapCenterScreenY) / (oldZoom * ISO_Y);
        float targetMapU = oldCenterU + 0.5F * (oldA + oldB);
        float targetMapV = oldCenterV + 0.5F * (oldB - oldA);

        float newA = (mouseX - mapCenterScreenX) / (newZoom * ISO_X);
        float newB = (mouseY - mapCenterScreenY) / (newZoom * ISO_Y);
        float newCenterU = targetMapU - 0.5F * (newA + newB);
        float newCenterV = targetMapV - 0.5F * (newB - newA);

        mapZoom = clampF(newZoom, minZoom, MAX_ZOOM);
        float viewW = mapW / mapZoom;
        float viewH = mapH / mapZoom;
        mapU = newCenterU - viewW * 0.5F;
        mapV = newCenterV - viewH * 0.5F;
        clampMapUV();
    }

    private CelestialBody getRenderFocusBody() {
        if (focusedBody != null) {
            return focusedBody;
        }
        if (focusAnimationActive && focusAnimationToBody) {
            return focusAnimationBody;
        }
        return null;
    }

    private CelestialBody getLocalOrbitParent() {
        CelestialBody activeFocus = getRenderFocusBody();
        if (activeFocus == null) {
            return null;
        }
        if (isTopLevelPlanet(activeFocus)) {
            return activeFocus;
        }
        if (activeFocus.parent != null) {
            return activeFocus.parent;
        }
        return null;
    }

    private CelestialBody getTopLevelPlanetForBody(CelestialBody body) {
        if (body == null) {
            return null;
        }

        CelestialBody current = body;
        int steps = 0;
        while (current.parent != null && current.parent.parent != null && steps <= 64) {
            current = current.parent;
            steps++;
        }
        return isTopLevelPlanet(current) ? current : null;
    }

    private boolean isTopLevelPlanet(CelestialBody body) {
        return body != null && body.parent != null && body.parent.parent == null;
    }

    private boolean isMoon(CelestialBody body) {
        return body != null && body.parent != null && body.parent.parent != null;
    }

    private void cancelFocusState() {
        focusedBody = null;
        focusAnimationBody = null;
        focusAnimationActive = false;
        focusAnimationToBody = false;
        focusAnimTrackingInitialized = false;
    }

    private void clearSatelliteFocus() {
        focusedSatelliteFrequency = null;
        focusedSatellite = null;
    }

    private void focusSatellite(SatelliteRenderInfo satelliteInfo) {
        if (satelliteInfo == null || satelliteInfo.satellite == null) {
            return;
        }
        if (focusedSatelliteFrequency != null && focusedSatelliteFrequency.intValue() == satelliteInfo.frequency) {
            return;
        }

        cancelFocusState();
        focusedSatelliteFrequency = satelliteInfo.frequency;
        focusedSatellite = satelliteInfo;
        setViewFromCenter(satelliteInfo.mapU, satelliteInfo.mapV, mapZoom);
        playButton();
    }

    private void drawSatelliteTooltip(int mouseX, int mouseY) {
        if (landingMode) {
            return;
        }

        boolean focusedTooltip = focusedSatelliteFrequency != null && focusedSatellite != null;
        if (!focusedTooltip) {
            if (!isMouseInsideMap(mouseX, mouseY)) {
                return;
            }
            if (!shouldDrawArtificialSatelliteOrbitLines(currentBody)) {
                return;
            }
        }

        SatelliteRenderInfo satelliteInfo = focusedTooltip ? focusedSatellite : hoveredSatellite;
        if (satelliteInfo == null || satelliteInfo.satellite == null) {
            return;
        }

        SatelliteOrbit satellite = satelliteInfo.satellite;
        String owner = satellite.owner != null && !satellite.owner.isEmpty() ? satellite.owner : SatelliteOrbit.DEFAULT_OWNER;
        List<Component> tooltip = new ArrayList<>(5);
        tooltip.add(Component.literal(ChatFormatting.YELLOW + tr("item.sat.desc.owner") + ": " + ChatFormatting.GOLD + owner));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + tr("item.sat.desc.speed") + ": " + ChatFormatting.GOLD + formatOrbitSpeed(satellite.altitude) + tr("gui.sat.settings.unit.km_per_second")));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + tr("item.sat.desc.altitude") + ": " + ChatFormatting.GOLD + formatValue(satellite.altitude) + tr("gui.sat.settings.unit.km")));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + tr("item.sat.desc.inclination") + ": " + ChatFormatting.GOLD + formatValue(satellite.inclination) + "°"));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + tr("item.sat.desc.phase") + ": " + ChatFormatting.GOLD + formatPhaseOffset(satellite.phaseOffset) + "°"));

        int tooltipX = mouseX;
        int tooltipY = mouseY;
        if (focusedTooltip) {
            float screenX = mapToScreenX(satelliteInfo.mapU, satelliteInfo.mapV);
            float screenY = mapToScreenY(satelliteInfo.mapU, satelliteInfo.mapV);
            float half = satelliteInfo.drawSize * 0.5F;
            tooltipX = Math.round(screenX + half);
            tooltipY = Math.round(screenY - half);
        }

        g.renderComponentTooltip(font, tooltip, tooltipX, tooltipY);
    }

    private boolean isMouseInsideMap(int mouseX, int mouseY) {
        return mouseX >= mapX && mouseX < mapX + mapW && mouseY >= mapY && mouseY < mapY + mapH && !isOverUi(mouseX, mouseY);
    }

    private boolean shouldDrawArtificialSatelliteOrbitLines(CelestialBody body) {
        CelestialBody activeFocus = getOrbitRuleFocusBody();
        return body != null && activeFocus != null && (activeFocus == body || activeFocus.parent == body);
    }

    private CelestialBody getOrbitRuleFocusBody() {
        CelestialBody activeFocus = getRenderFocusBody();
        if (activeFocus != null) {
            return activeFocus;
        }
        if (focusedSatelliteFrequency != null) {
            return currentBody;
        }
        return null;
    }

    private boolean hasBodyFocusContext() {
        return focusedBody != null || focusAnimationActive;
    }

    private boolean isFocusContextActive() {
        return hasBodyFocusContext() || focusedSatelliteFrequency != null;
    }

    private void setViewFromCenter(float centerU, float centerV, float zoom) {
        mapZoom = clampF(zoom, MIN_ZOOM, MAX_ZOOM);
        float viewW = mapW / mapZoom;
        float viewH = mapH / mapZoom;
        mapU = centerU - viewW * 0.5F;
        mapV = centerV - viewH * 0.5F;
        clampMapUV();
    }

    private float getFocusZoomForBody(CelestialBody body) {
        // If the focused body has satellites, start zoom so all of them are visible.
        if (body != null) {
            float[] focusZoomBounds = new float[2];
            if (calculateFocusedChildZoomBounds(body, focusZoomBounds)) {
                return focusZoomBounds[0];
            }
        }

        float bodySizeAt1x = isMoon(body) ? getMoonSizePxAt1x(body) : getBodySizePxAt1x(body);
        if (bodySizeAt1x <= 0F) {
            return clampF(mapZoom, MIN_ZOOM, MAX_ZOOM);
        }

        float targetBodySizePx = Math.min(mapW, mapH) * 0.88F;
        float targetZoom = targetBodySizePx / bodySizeAt1x;
        targetZoom /= 3.0F;
        targetZoom = Math.max(targetZoom, 4.0F);
        return clampF(targetZoom, MIN_ZOOM, MAX_ZOOM);
    }

    private boolean calculateFocusedChildZoomBounds(CelestialBody focusBody, float[] outBounds) {
        if (outBounds == null || outBounds.length < 2 || focusBody == null || focusBody.satellites == null || focusBody.satellites.isEmpty()) {
            return false;
        }

        CelestialBody starBody = focusBody.getStar();
        if (starBody == null) {
            return false;
        }

        float systemOrbitScale = getOrbitScalePxPerKm(starBody);
        if (systemOrbitScale <= 0F) {
            return false;
        }

        float childOrbitScale = getChildOrbitScalePxPerKm(focusBody, systemOrbitScale);
        if (childOrbitScale <= 0F) {
            return false;
        }

        float nearestProjectedDist = Float.MAX_VALUE;
        float furthestProjectedDist = 0F;
        for (CelestialBody childBody : focusBody.satellites) {
            if (childBody == null || childBody.semiMajorAxisKm <= 0F) {
                continue;
            }
            for (int i = 0; i <= 96; i++) {
                double meanAnomaly = 2D * Math.PI * ((double) i / 96D);
                float[] orbitOffset = calculateOrbitOffsetPx(focusBody, childBody, meanAnomaly, childOrbitScale);
                float projected = getProjectedOffsetDistanceAt1x(orbitOffset[0], orbitOffset[1]);
                if (projected <= 0F || Float.isNaN(projected) || Float.isInfinite(projected)) {
                    continue;
                }
                nearestProjectedDist = Math.min(nearestProjectedDist, projected);
                furthestProjectedDist = Math.max(furthestProjectedDist, projected);
            }
        }

        if (nearestProjectedDist == Float.MAX_VALUE || furthestProjectedDist <= 0F) {
            return false;
        }

        float availableRadiusPx = Math.max(1F, Math.min(mapW, mapH) * 0.5F - 10F);
        float minZoom = availableRadiusPx / furthestProjectedDist;
        float maxZoom = availableRadiusPx / nearestProjectedDist;

        minZoom = clampF(minZoom, MIN_ZOOM, MAX_ZOOM);
        maxZoom = clampF(maxZoom, MIN_ZOOM, MAX_ZOOM);
        if (maxZoom < minZoom) {
            float swap = minZoom;
            minZoom = maxZoom;
            maxZoom = swap;
        }

        outBounds[0] = minZoom;
        outBounds[1] = maxZoom;
        return true;
    }

    private float getProjectedOffsetDistanceAt1x(float offsetU, float offsetV) {
        float screenOffsetX = (offsetU - offsetV) * ISO_X;
        float screenOffsetY = (offsetU + offsetV) * ISO_Y;
        return Mth.sqrt(screenOffsetX * screenOffsetX + screenOffsetY * screenOffsetY);
    }

    private float getBodyDetailZoomCap(CelestialBody body) {
        if (body == null) {
            return MIN_ZOOM;
        }

        float bodySizeAt1x = isMoon(body) ? getMoonSizePxAt1x(body) : getBodySizePxAt1x(body);
        if (bodySizeAt1x <= 0F || Float.isNaN(bodySizeAt1x) || Float.isInfinite(bodySizeAt1x)) {
            return MIN_ZOOM;
        }

        float targetBodySizePx = Math.min(mapW, mapH) * 0.88F;
        float detailZoom = targetBodySizePx / bodySizeAt1x;
        return clampF(detailZoom, MIN_ZOOM, MAX_ZOOM);
    }

    private void clampMapUV() {
        float viewW = mapW / mapZoom;
        float viewH = mapH / mapZoom;
        float maxU = Math.max(0F, MAP_TEX_W - viewW);
        float maxV = Math.max(0F, MAP_TEX_H - viewH);
        mapU = clampF(mapU, 0F, maxU);
        mapV = clampF(mapV, 0F, maxV);
    }

    private float getOrbitScalePxPerKm(CelestialBody starBody) {
        float maxDistanceKm = getSystemMaxDistanceKm(starBody);
        if (maxDistanceKm <= 0F) {
            return 0F;
        }

        float starRadiusPxAt1x = getBodySizePxAt1x(starBody) * 0.5F;
        float availableRadiusPx = Math.max(0F, (SYSTEM_LAYOUT_REF_TEX * STARDAR_SYSTEM_RADIUS_FRACTION) - starRadiusPxAt1x);
        if (availableRadiusPx <= 0F) {
            return 0F;
        }

        return availableRadiusPx / maxDistanceKm;
    }

    private float getSystemMaxDistanceKm(CelestialBody starBody) {
        if (starBody == null || starBody.satellites == null) {
            return 0F;
        }

        float maxDistance = 0F;
        for (CelestialBody child : starBody.satellites) {
            if (child == null) {
                continue;
            }

            float apoapsisKm = child.semiMajorAxisKm * (1F + child.eccentricity);
            maxDistance = Math.max(maxDistance, Math.max(0F, apoapsisKm));
        }

        return maxDistance;
    }

    private float getChildOrbitScalePxPerKm(CelestialBody parent, float systemOrbitScalePxPerKm) {
        if (parent != null && parent.parent != null) {
            float moonOrbitScale = getMoonOrbitScalePxPerKm(parent, systemOrbitScalePxPerKm);
            if (moonOrbitScale > 0F) {
                return moonOrbitScale;
            }
        }
        return systemOrbitScalePxPerKm;
    }

    private float getMoonOrbitScalePxPerKm(CelestialBody parent, float systemOrbitScalePxPerKm) {
        if (parent == null || parent.satellites == null || parent.satellites.isEmpty()) {
            return 0F;
        }

        float maxMoonDistanceKm = 0F;
        for (CelestialBody moon : parent.satellites) {
            if (moon == null) continue;
            float apoapsisKm = moon.semiMajorAxisKm * (1F + moon.eccentricity);
            maxMoonDistanceKm = Math.max(maxMoonDistanceKm, Math.max(0F, apoapsisKm));
        }

        if (maxMoonDistanceKm <= 0F) {
            return 0F;
        }

        float parentRadiusPxAt1x = getBodySizePxAt1x(parent) * 0.5F;
        float moonOrbitRadiusPxAt1x = Math.max(10.0F, parentRadiusPxAt1x * 14F);

        float siblingGapPxAt1x = getNearestSiblingOrbitGapPx(parent, systemOrbitScalePxPerKm);
        if (siblingGapPxAt1x > 0F) {
            float maxAllowedMoonOrbitRadiusPxAt1x = siblingGapPxAt1x * 0.45F;
            if (maxAllowedMoonOrbitRadiusPxAt1x > 0F) {
                moonOrbitRadiusPxAt1x = Math.min(moonOrbitRadiusPxAt1x, maxAllowedMoonOrbitRadiusPxAt1x);
            }
        }

        return moonOrbitRadiusPxAt1x / maxMoonDistanceKm;
    }

    /** Abstand (px bei 1x, nach {@link #compressOrbitPx}) zur naechsten Nachbarbahn. */
    private float getNearestSiblingOrbitGapPx(CelestialBody body, float orbitScalePxPerKm) {
        if (body == null || body.parent == null || body.semiMajorAxisKm <= 0F || body.parent.satellites == null || orbitScalePxPerKm <= 0F) {
            return 0F;
        }

        float bodyPx = compressOrbitPx(body.parent, body.semiMajorAxisKm * orbitScalePxPerKm, orbitScalePxPerKm);
        float nearestGapPx = Float.MAX_VALUE;
        for (CelestialBody sibling : body.parent.satellites) {
            if (sibling == null || sibling == body || sibling.semiMajorAxisKm <= 0F) {
                continue;
            }
            float gapPx = Math.abs(compressOrbitPx(body.parent, sibling.semiMajorAxisKm * orbitScalePxPerKm, orbitScalePxPerKm) - bodyPx);
            if (gapPx > 0F) {
                nearestGapPx = Math.min(nearestGapPx, gapPx);
            }
        }

        return nearestGapPx == Float.MAX_VALUE ? 0F : nearestGapPx;
    }

    /**
     * Staucht Bahnabstaende radial (Potenz 0,38 bei Planeten, 0,7 bei Monden): die aeusserste Bahn bleibt wo sie ist,
     * innere Bahnen ruecken nach aussen. Sonst kleben Merkur bis Mars neben Eris bzw. die inneren Saturnmonde neben Iapetus.
     */
    private float compressOrbitPx(CelestialBody parent, float distancePx, float orbitScalePxPerKm) {
        if (parent == null || distancePx <= 0F || orbitScalePxPerKm <= 0F) return distancePx;
        float outerPx = getSystemMaxDistanceKm(parent) * orbitScalePxPerKm;
        if (outerPx <= 0F) return distancePx;
        double exponent = parent.parent == null ? 0.38D : 0.7D;
        return (float) (outerPx * Math.pow(distancePx / outerPx, exponent));
    }

    private float[] calculateOrbitOffsetPx(CelestialBody parent, CelestialBody body, double meanAnomaly, float orbitScalePxPerKm) {
        double semiMinorAxisFactor = body.semiMinorAxisFactor > 0 ? body.semiMinorAxisFactor : Math.sqrt(1D - (body.eccentricity * body.eccentricity));
        return calculateOrbitOffsetPx(parent, body.semiMajorAxisKm, body.eccentricity, semiMinorAxisFactor, body.argumentPeriapsis,
            body.inclination, body.ascendingNode, meanAnomaly, orbitScalePxPerKm);
    }

    /** Wie oben mit rohen Bahnelementen (Winkel in Bogenmass), z.B. fuer Asteroiden. */
    private float[] calculateOrbitOffsetPx(CelestialBody parent, float semiMajorAxisKm, float eccentricity, double semiMinorAxisFactor,
                                           float argumentPeriapsis, float inclination, float ascendingNode, double meanAnomaly, float orbitScalePxPerKm) {
        double eccentricAnomaly = calculateEccentricAnomaly(meanAnomaly, eccentricity);

        double x = semiMajorAxisKm * (Math.cos(eccentricAnomaly) - eccentricity);
        double y = semiMajorAxisKm * semiMinorAxisFactor * Math.sin(eccentricAnomaly);
        double z;

        double px = x;
        x = Math.cos(argumentPeriapsis) * px - Math.sin(argumentPeriapsis) * y;
        y = Math.sin(argumentPeriapsis) * px + Math.cos(argumentPeriapsis) * y;

        z = Math.sin(inclination) * y;
        y = Math.cos(inclination) * y;

        px = x;
        x = Math.cos(ascendingNode) * px - Math.sin(ascendingNode) * y;
        y = Math.sin(ascendingNode) * px + Math.cos(ascendingNode) * y;

        y -= z * 0.35D;
        y *= 0.8F;

        float mapX = (float) (x * orbitScalePxPerKm);
        float mapY = (float) (y * orbitScalePxPerKm);

        if (parent != null) {
            float linearPx = Mth.sqrt(mapX * mapX + mapY * mapY);
            if (linearPx > 0F) {
                float compressed = compressOrbitPx(parent, linearPx, orbitScalePxPerKm) / linearPx;
                mapX *= compressed;
                mapY *= compressed;
            }
            float parentVisualRadiusPx = getBodySizePxAt1x(parent) * 0.5F;
            float distanceFromParentPx = Mth.sqrt(mapX * mapX + mapY * mapY);
            if (distanceFromParentPx > 0F) {
                float radialScale = (distanceFromParentPx + parentVisualRadiusPx) / distanceFromParentPx;
                mapX *= radialScale;
                mapY *= radialScale;
            } else {
                mapX = parentVisualRadiusPx;
                mapY = 0F;
            }
        }

        return new float[] {mapX, mapY};
    }

    private double calculateMeanAnomaly(CelestialBody body, double worldTicks) {
        if (body == null) {
            return 0D;
        }

        double orbitPeriodTicks = getOrbitalPeriodTicks(body);
        if (orbitPeriodTicks <= 0D || Double.isNaN(orbitPeriodTicks) || Double.isInfinite(orbitPeriodTicks)) {
            return 0D;
        }

        return 2D * Math.PI * (worldTicks / orbitPeriodTicks);
    }

    private double getOrbitalPeriodTicks(CelestialBody body) {
        if (body == null || body.parent == null || body.semiMajorAxisKm <= 0F) {
            return 0D;
        }

        double orbitPeriodDays = body.getOrbitalPeriod();
        if (orbitPeriodDays <= 0D || Double.isNaN(orbitPeriodDays) || Double.isInfinite(orbitPeriodDays)) {
            return 0D;
        }

        return orbitPeriodDays * (double) AstronomyUtil.TICKS_IN_DAY;
    }

    private double calculateEccentricAnomaly(double meanAnomaly, float eccentricity) {
        double eccentricAnomaly = meanAnomaly;
        for (int i = 0; i < 4; i++) {
            eccentricAnomaly = meanAnomaly + eccentricity * Math.sin(eccentricAnomaly);
        }
        return eccentricAnomaly;
    }

    private float getBodyRotationPhase(CelestialBody body, double dayTicks) {
        if (body == null) {
            return 0F;
        }

        double period = body.getRotationalPeriod();
        if (period <= 0D || Double.isNaN(period) || Double.isInfinite(period)) {
            return 0F;
        }

        return (float) ((dayTicks % period) / period);
    }

    private float getBodySizePxAtZoom(CelestialBody body) {
        float bodySize = Math.max(getBodySizePxAt1x(body) * mapZoom, minBodySizePx(body));
        CelestialBody renderFocusBody = getRenderFocusBody();
        if (renderFocusBody != null) {
            CelestialBody localOrbitParent = getLocalOrbitParent();
            boolean isLocalMoon = localOrbitParent != null && body.parent == localOrbitParent;
            if (isLocalMoon) {
                float moonSizeAt1x = getMoonSizePxAt1x(body);
                bodySize = Math.max(moonSizeAt1x * mapZoom, minBodySizePx(body));
            }
        }
        return bodySize;
    }

    /** Planeten und Monde bleiben auch in der Gesamtansicht als Punkte sichtbar (Original: unter 1 px). */
    private float minBodySizePx(CelestialBody body) {
        if (body == null || body.parent == null) return 0F;
        return isMoon(body) ? 1.5F : 3.5F;
    }

    private float getBodySizePxAt1x(CelestialBody body) {
        if (body == null) {
            return 1F;
        }
        if (body.parent == null) {
            return 36F * STARDAR_BODY_SCALE;
        }
        if (isMoon(body)) {
            return getMoonSizePxAt1x(body);
        }
        float bodySizeAt1x = body.radiusKm * (36F / 261_600F) * 2.6F;
        bodySizeAt1x = clampF(bodySizeAt1x, 0.8F, 2.0F);
        return bodySizeAt1x * STARDAR_BODY_SCALE;
    }

    private float getMoonSizePxAt1x(CelestialBody moon) {
        if (moon == null) {
            return 0.2F * STARDAR_BODY_SCALE * STARDAR_MOON_VISUAL_SCALE;
        }
        float moonRadiusKm = Math.max(0F, moon.radiusKm);
        float radiusRangeKm = 500F - 65F;
        float t = (moonRadiusKm - 65F) / radiusRangeKm;
        t = clampF(t, 0F, 1F);
        return (0.2F + (0.5F - 0.2F) * t) * STARDAR_BODY_SCALE * STARDAR_MOON_VISUAL_SCALE;
    }

    private void enterLandingMode(CelestialBody body) {
        landingMode = true;
        landingBody = body;
        surfaceX = clampSurface(mapW * 0.5F, mapW);
        surfaceY = clampSurface(mapH * 0.5F, mapH);
        velocityX = 0;
        velocityY = 0;

        playButton();

        CompoundTag data = new CompoundTag();
        if (body.dimension != null) data.putString("pid", body.dimension.location().toString());

        if (star != null && star.getLevel() != null && body == currentBody && !CelestialBody.inOrbit(star.getLevel())) {
            data.putInt("ix", star.getBlockPos().getX());
            data.putInt("iz", star.getBlockPos().getZ());
        }

        sendControl(data);
    }

    private void exitLandingMode() {
        landingMode = false;
        landingBody = null;
        draggingSurface = false;
    }

    private void updateLandingModeFromDrive() {
        ItemStack slotStack = slotStack();

        if (slotStack.isEmpty()) {
            hadFullDrive = false;
            lastFullDriveMeta = -1;
            if (landingMode) {
                exitLandingMode();
            }
            return;
        }

        if (slotStack.getItem() != SpaceRocketItems.FULL_DRIVE.get()) {
            hadFullDrive = false;
            lastFullDriveMeta = -1;
            if (landingMode && slotStack.getItem() != SpaceMaterialItems.HARD_DRIVE.get()) {
                exitLandingMode();
            }
            return;
        }

        int meta = ItemVOTVdrive.getBody(slotStack).ordinal();
        if (hadFullDrive && meta == lastFullDriveMeta) return;

        hadFullDrive = true;
        lastFullDriveMeta = meta;

        CelestialBody body = ItemVOTVdrive.getBody(slotStack).getBody();
        if (landingMode && landingBody == body) return;

        if (body != null && body.canLand) {
            enterLandingMode(body);
        } else if (landingMode) {
            exitLandingMode();
        }
    }

    private String landingInfo(int x, int z) {
        int[] heightmap = heightmap();
        if (heightmap == null) return tr("gui.stardar.landing.no_heightmap");
        if (x < 3 || x > 252 || z < 3 || z > 252) return tr("gui.stardar.landing.outside_bounds");

        for (int ox = x - 2; ox <= x + 2; ox++) {
            for (int oz = z - 2; oz <= z + 2; oz++) {
                if (heightmap[256 * oz + ox] != heightmap[256 * z + x]) {
                    return tr("gui.stardar.landing.area_not_flat");
                }
            }
        }

        return null;
    }

    private int altitude(int x, int z) {
        int[] heightmap = heightmap();
        if (heightmap == null) return -1;
        if (x < 0 || x > 255 || z < 0 || z > 255) return -1;
        return heightmap[256 * z + x];
    }

    private String getBodyDisplayName(CelestialBody body) {
        if (body == null || body.name == null) return tr("gui.stardar.body.unknown");
        String key = "body." + body.name;
        String translated = I18n.get(key);
        if (translated != null && !translated.equals(key)) return translated;
        if (body.name.length() <= 1) return body.name.toUpperCase();
        return body.name.substring(0, 1).toUpperCase() + body.name.substring(1);
    }

    private static String formatValue(float value) {
        return value == (int) value ? Integer.toString((int) value) : Float.toString(value);
    }

    private static String formatOrbitSpeed(float altitude) {
        return formatValue(Math.round(SatelliteOrbit.getOrbitSpeedKmPerSecond(altitude) * 10.0F) / 10.0F);
    }

    private static String formatPhaseOffset(float phaseOffset) {
        float rounded = Math.round(SatelliteOrbit.normalizePhaseOffset(phaseOffset) * 10.0F) / 10.0F;
        return formatValue(rounded);
    }

    private int colorArrayToRgb(float[] color) {
        if (color == null || color.length < 3) {
            return 0xFFFFFFFF;
        }
        int r = Mth.clamp((int) (color[0] * 255F), 0, 255);
        int gr = Mth.clamp((int) (color[1] * 255F), 0, 255);
        int b = Mth.clamp((int) (color[2] * 255F), 0, 255);
        return 0xFF000000 | r << 16 | gr << 8 | b;
    }

    private int getBodyHierarchyDepth(CelestialBody body) {
        if (body == null) {
            return 0;
        }

        int depth = 0;
        CelestialBody current = body;
        while (current.parent != null && depth < 64) {
            depth++;
            current = current.parent;
        }
        return depth;
    }

    // ── Erweiterung: mehrere Sternsysteme ─────────────────────────────────────────────────────────

    /** Platziert alle Sternsysteme: das des aktuellen Koerpers in die Mitte, die anderen nach Galaxie-Position. */
    private void updateSystemLayouts() {
        systemLayouts.clear();
        StarSystem here = StarSystem.of(currentBody);
        if (here == null) here = StarSystem.home();
        if (here == null) return;

        addLayout(here, MAP_TEX_W * 0.5F, MAP_TEX_H * 0.5F);
        for (StarSystem system : StarSystem.all()) {
            if (system == here) continue;
            float dx = (float) (system.galaxyX - here.galaxyX);
            float dy = (float) (system.galaxyY - here.galaxyY);
            // isometrisch: Bildschirm-x ~ (U - V), Bildschirm-y ~ (U + V) -> +X rechts, +Y oben
            float u = MAP_TEX_W * 0.5F + (dx - dy) * STARDAR_LY_TO_MAP;
            float v = MAP_TEX_H * 0.5F + (-dx - dy) * STARDAR_LY_TO_MAP;
            addLayout(system, clampF(u, SYSTEM_EDGE_MARGIN, MAP_TEX_W - SYSTEM_EDGE_MARGIN), clampF(v, SYSTEM_EDGE_MARGIN, MAP_TEX_H - SYSTEM_EDGE_MARGIN));
        }
    }

    private void addLayout(StarSystem system, float centerU, float centerV) {
        systemLayouts.add(new SystemLayout(system, centerU, centerV, getOrbitScalePxPerKm(system.star)));
    }

    @Nullable
    private SystemLayout layoutFor(@Nullable CelestialBody body) {
        if (body == null) return null;
        CelestialBody bodyStar = body.getStar();
        for (SystemLayout layout : systemLayouts) if (layout.star == bodyStar) return layout;
        return null;
    }

    /** Liegt der Koerper im System des aktuellen Koerpers bzw. erlaubt der interstellare Antrieb die Reise? */
    private boolean isReachableFromHere(CelestialBody body) {
        return currentBody == null || StarSystem.isReachable(currentBody, body);
    }

    private void focusStar(SystemLayout layout) {
        if (layout == null || layout.star == focusedBody) return;
        clearSatelliteFocus();
        playButton();
        configureFocusAnimation(layout.star, layout.star, true, 500, layout.centerU, layout.centerV, getFocusZoomForBody(layout.star));
    }

    /** Randmarken fuer Sterne ausserhalb des Kartenausschnitts (Farbe des Sterns, Name, Abstand in Lichtjahren). */
    private void drawSystemIndicators() {
        systemIndicators.clear();
        if (this.font == null || systemLayouts.size() < 2) return;

        float left = mapX;
        float top = mapY;
        float cx = mapCenterX();
        float cy = top + mapH * 0.5F;
        StarSystem here = systemLayouts.get(0).system;

        for (SystemLayout layout : systemLayouts) {
            float sx = mapToScreenX(layout.centerU, layout.centerV);
            float sy = mapToScreenY(layout.centerU, layout.centerV);
            if (sx >= left && sx < left + mapW && sy >= top && sy < top + mapH) continue;

            float dx = sx - cx;
            float dy = sy - cy;
            float halfW = (dx >= 0F ? left + mapW - cx : cx - left) - 5F;
            float halfH = mapH * 0.5F - 5F;
            float t = Math.min(Math.abs(dx) > 1e-3F ? halfW / Math.abs(dx) : Float.MAX_VALUE, Math.abs(dy) > 1e-3F ? halfH / Math.abs(dy) : Float.MAX_VALUE);
            int ix = Math.round(cx + dx * t);
            int iy = Math.round(cy + dy * t);

            int color = colorArrayToRgb(layout.star.color);
            g.fill(ix - 3, iy - 3, ix + 3, iy + 3, 0xFF000000);
            g.fill(ix - 2, iy - 2, ix + 2, iy + 2, color);

            String label = getBodyDisplayName(layout.star);
            if (layout.system != here) {
                label = tr("gui.stardar.label.system_distance", label, String.format(java.util.Locale.ROOT, "%.1f", here.distanceLightYears(layout.system)));
            }
            int labelW = Math.round(this.font.width(label) * 0.6F);
            int labelH = Math.round(this.font.lineHeight * 0.6F);
            int lx = dx >= 0 ? ix - 5 - labelW : ix + 5;
            int ly = Mth.clamp(iy - labelH / 2, Math.round(top) + 1, Math.round(top + mapH) - labelH - 1);

            g.pose().pushPose();
            g.pose().scale(0.6F, 0.6F, 1F);
            g.drawString(this.font, label, Math.round(lx / 0.6F), Math.round(ly / 0.6F), color, true);
            g.pose().popPose();
            SkyGL.color(1F, 1F, 1F, 1F);

            int minX = Math.min(ix - 4, lx - 1);
            int maxX = Math.max(ix + 4, lx + labelW + 1);
            int minY = Math.min(iy - 4, ly - 1);
            int maxY = Math.max(iy + 4, ly + labelH + 1);
            systemIndicators.add(new SystemIndicator(layout, minX, minY, maxX, maxY));
        }
    }

    @Nullable
    private SystemIndicator findSystemIndicatorUnderCursor(int mouseX, int mouseY) {
        for (SystemIndicator indicator : systemIndicators) {
            if (mouseX >= indicator.minX && mouseX <= indicator.maxX && mouseY >= indicator.minY && mouseY <= indicator.maxY) return indicator;
        }
        return null;
    }

    private static class SystemLayout {
        final StarSystem system;
        final CelestialBody star;
        final float centerU;
        final float centerV;
        final float orbitScale;

        SystemLayout(StarSystem system, float centerU, float centerV, float orbitScale) {
            this.system = system;
            this.star = system.star;
            this.centerU = centerU;
            this.centerV = centerV;
            this.orbitScale = orbitScale;
        }
    }

    private static class SystemIndicator {
        final SystemLayout layout;
        final int minX;
        final int minY;
        final int maxX;
        final int maxY;

        SystemIndicator(SystemLayout layout, int minX, int minY, int maxX, int maxY) {
            this.layout = layout;
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    private static class BodyPosition {
        float mapU;
        float mapV;
    }

    private static class ChildRenderState {
        final CelestialBody childBody;
        final float orbitScalePxPerKm;
        final float orbitOffsetU;
        final float orbitOffsetV;
        final boolean moonChild;
        final boolean drawInFrontOfParent;

        ChildRenderState(CelestialBody childBody, float orbitScalePxPerKm, float orbitOffsetU, float orbitOffsetV, boolean moonChild, boolean drawInFrontOfParent) {
            this.childBody = childBody;
            this.orbitScalePxPerKm = orbitScalePxPerKm;
            this.orbitOffsetU = orbitOffsetU;
            this.orbitOffsetV = orbitOffsetV;
            this.moonChild = moonChild;
            this.drawInFrontOfParent = drawInFrontOfParent;
        }
    }

    private static class SatelliteOrbitPoint {
        final float offsetU;
        final float offsetV;
        final float depth;

        SatelliteOrbitPoint(float offsetU, float offsetV, float depth) {
            this.offsetU = offsetU;
            this.offsetV = offsetV;
            this.depth = depth;
        }
    }

    private static class BodyRenderInfo {
        final CelestialBody body;
        final float mapU;
        final float mapV;
        final float drawSize;

        BodyRenderInfo(CelestialBody body, float mapU, float mapV, float drawSize) {
            this.body = body;
            this.mapU = mapU;
            this.mapV = mapV;
            this.drawSize = drawSize;
        }
    }

    /** Original {@code SatelliteRenderInfo} (Satellit = Bahndaten {@link SatelliteOrbit}, Original {@code SatelliteBase}). */
    private static class SatelliteRenderInfo {
        final int frequency;
        final SatelliteOrbit satellite;
        final float mapU;
        final float mapV;
        final float drawSize;

        SatelliteRenderInfo(int frequency, SatelliteOrbit satellite, float mapU, float mapV, float drawSize) {
            this.frequency = frequency;
            this.satellite = satellite;
            this.mapU = mapU;
            this.mapV = mapV;
            this.drawSize = drawSize;
        }
    }

    private static class BodyLabelRenderInfo {
        final CelestialBody body;
        final float topRightAnchorScreenX;
        final float topRightAnchorScreenY;

        BodyLabelRenderInfo(CelestialBody body, float topRightAnchorScreenX, float topRightAnchorScreenY) {
            this.body = body;
            this.topRightAnchorScreenX = topRightAnchorScreenX;
            this.topRightAnchorScreenY = topRightAnchorScreenY;
        }
    }
}
