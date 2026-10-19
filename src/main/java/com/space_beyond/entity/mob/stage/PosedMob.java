package com.space_beyond.entity.mob.stage;

/**
 * Zustand fuer das gemeinsame Posen-Modell ({@code StageBossModel}): Stufen-Bosse und Dungeon-Mobs.
 */
public interface PosedMob {

    /** Wach (Kopf folgt dem Blick, keine Ruhepose). */
    boolean awake();

    /** Laufende Animation (0 = keine; Index der Schluesselposen). */
    int anim();

    /** Ticks seit Beginn der aktuellen Animation. */
    float animTime(float pt);

    /** Schnellere Grundbewegung (z. B. Wut). */
    boolean phaseTwo();

    boolean phaseThree();
}
