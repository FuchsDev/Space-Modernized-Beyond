"""Tomb Warden (Ike): Statuen-Golem aus dunklem Stein mit violetten Runen, Obelisk-Helm mit Schlitzauge,
Bronzebaendern, Steinhammer in der Rechten und Grabstein-Schild an der Linken."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('tw_stone', 'rock', (82, 76, 94))
mat('tw_stone_d', 'rock', (54, 50, 64))
mat('tw_obs', 'obsidian', (30, 26, 40), (134, 98, 184))
mat('tw_rune', 'runes', (46, 42, 58), glow_col=(196, 120, 255))
mat('tw_trim', 'plate', (122, 100, 66), (204, 174, 112))
mat('tw_glow', 'glow', (122, 38, 212), (246, 214, 255))
mat('tw_eye', 'eye', (72, 0, 112), (255, 226, 255))


def M(s, x, w): return x if s > 0 else -x - w    # x spiegeln (s=+1 links = Modell +x)


# ── Beine: Steinsaeulen mit Bronze-Knieschild, Runenschienbein, breiter Sockelfuss ──
def tw_leg(s):
    t = 'r' if s < 0 else 'l'
    shin = B('shin_' + t, (0, 7, 0), (0, 0, 0), [
        C(-3, 0, -3, 6, 5, 6, 'tw_rune'), C(-3.5, 0, -3.8, 7, 4, 1, 'tw_stone_d'), C(-4, 5, -5, 8, 3, 9, 'tw_stone_d'),
        C(-3.5, 6, -6, 7, 2, 1, 'tw_trim'), C(M(s, 2.5, 1.5), 5.5, -4, 1.5, 2, 6, 'tw_stone')])
    return B('leg_' + t, (s * 4.2, 9, 0), (0, 0, s * -2), [
        C(-3, -1, -3, 6, 8, 6, 'tw_stone'), C(-3.5, 5.5, -4.2, 7, 3, 2, 'tw_trim'), C(M(s, 2.5, 1), 0, -2, 1, 5, 4, 'tw_rune')],
        children=[shin], role='leg_' + t)


# ── Kopf: Obelisk-Helm (Pyramidion mit Leuchtspitze), Visier mit Schlitzauge, Bronzeband ──
tw_head = B('head', (0, -16, -1.5), (-8, 0, 0), [
    C(-3.5, -7, -3.5, 7, 7, 7, 'tw_obs'), C(-3, -5, -4, 6, 4, 1, 'tw_stone_d'), C(-2.5, -4, -4.5, 5, 1, 1, 'tw_eye'),
    C(-4, -1.5, -4, 8, 2, 8, 'tw_trim'), C(-2.5, -10, -2.5, 5, 3, 5, 'tw_rune'), C(-1.5, -12, -1.5, 3, 2, 3, 'tw_obs'),
    C(-0.5, -13, -0.5, 1, 1, 1, 'tw_glow'), C(-4, -6, -2, 1, 4, 4, 'tw_stone_d'), C(3, -6, -2, 1, 4, 4, 'tw_stone_d'),
    C(-2.6, -10.5, -2.6, 5, 1, 5, 'tw_trim')], role='head')

# ── Steinhammer (rechts): Runenstiel mit Bronzewicklungen, Kopf quer zur Blickrichtung ──
tw_hammer = B('hammer', (0, 10, -0.5), (48, 0, 0), [
    C(-1, -3, -1, 2, 11, 2, 'tw_stone_d'), C(-1.5, -4, -1.5, 3, 1, 3, 'tw_trim'), C(-1.5, 3, -1.5, 3, 1, 3, 'tw_trim'),
    C(-3.5, 7.5, -6, 7, 6, 12, 'tw_stone'), C(-4, 7, -6.5, 8, 7, 2, 'tw_trim'), C(-4, 7, 4.5, 8, 7, 2, 'tw_trim'),
    C(-4, 8.5, -3, 1, 4, 6, 'tw_rune'), C(3, 8.5, -3, 1, 4, 6, 'tw_rune'), C(-1.5, 9, -7, 3, 3, 1, 'tw_glow')])
tw_fore_r = B('fore_r', (-1.5, 8, 0), (-50, 0, 6), [
    C(-3.5, 0, -3.5, 7, 8, 7, 'tw_rune'), C(-4, 0, -4, 8, 2, 8, 'tw_trim'), C(-3, 7.5, -3, 6, 4, 6, 'tw_stone'),
    C(-3.5, 8, -3.5, 1, 2, 3, 'tw_stone_d')], children=[tw_hammer])
tw_arm_r = B('arm_r', (-10.5, -12, 0), (0, 0, 3), [
    C(-5, -3, -4.5, 7, 5, 9, 'tw_stone'), C(-5.5, 1.5, -5, 8, 1, 10, 'tw_trim'), C(-5.5, -2, -3, 1, 3, 6, 'tw_rune'),
    C(-4, 1, -3, 5, 8, 6, 'tw_stone_d')], children=[tw_fore_r], role='arm_r')

# ── Grabstein-Schild (links) ──
tw_shield = B('shield', (3, 3, -0.5), (0, 0, 2), [
    C(0, -2, -5, 2, 13, 10, 'tw_stone'), C(0, -4, -4, 2, 2, 8, 'tw_stone'), C(0, -5, -2.5, 2, 1, 5, 'tw_stone'),
    C(-0.5, -1.5, -5.5, 1, 13, 11, 'tw_trim'), C(2, 0, -3.5, 1, 9, 7, 'tw_rune'), C(2.5, 2, -1.5, 1, 4, 3, 'tw_glow'),
    C(2.5, 3, -2.5, 1, 2, 1, 'tw_glow'), C(2.5, 3, 1.5, 1, 2, 1, 'tw_glow'), C(1.5, 10.5, -5.5, 2, 1, 11, 'tw_obs')])
tw_fore_l = B('fore_l', (1.5, 8, 0), (-24, 0, -4), [
    C(-3.5, 0, -3.5, 7, 8, 7, 'tw_rune'), C(-4, 0, -4, 8, 2, 8, 'tw_trim'), C(-3, 7.5, -3, 6, 4, 6, 'tw_stone')],
    children=[tw_shield])
tw_arm_l = B('arm_l', (10.5, -12, 0), (0, 0, -3), [
    C(-2, -3, -4.5, 7, 5, 9, 'tw_stone'), C(-2.5, 1.5, -5, 8, 1, 10, 'tw_trim'), C(4.5, -2, -3, 1, 3, 6, 'tw_rune'),
    C(-1, 1, -3, 5, 8, 6, 'tw_stone_d')], children=[tw_fore_l], role='arm_l')

# ── Rumpf: breite Steinbrust mit gluehendem Runenkern in Bronzefassung, Runen-Rueckenplatte ──
tw_torso = B('torso', (0, -3, 0), (8, 0, 0), [
    C(-5.5, -4, -4, 11, 5, 8, 'tw_stone_d'), C(-9, -15, -5.5, 18, 11, 11, 'tw_stone'), C(-3.5, -13, -6, 7, 7, 1, 'tw_trim'),
    C(-2.5, -12, -6.4, 5, 5, 1, 'tw_glow'), C(-8.5, -14, -6, 4, 7, 1, 'tw_rune'), C(4.5, -14, -6, 4, 7, 1, 'tw_rune'),
    C(-7, -15, 5, 14, 10, 2, 'tw_rune'), C(-6, -16, -4, 12, 2, 8, 'tw_obs'), C(-9.5, -6, -6, 19, 2, 12, 'tw_trim'),
    C(-1, -5, -4.6, 2, 2, 1, 'tw_glow')],
    children=[tw_head, tw_arm_r, tw_arm_l], role='body')

# ── Becken mit Bronzeguertel und Steinlatz ──
tw_tabard = B('tabard', (0, 0.5, -4.5), (-4, 0, 0), [
    C(-2.5, 0, -1, 5, 8, 1, 'tw_rune'), C(-3, 0, -1.3, 6, 1, 1, 'tw_trim'), C(-2, 7, -1.2, 4, 1, 1, 'tw_obs')])
tw_hips = B('hips', (0, 9, 0), (0, 0, 0), [
    C(-6.5, -3, -4, 13, 4, 8, 'tw_stone_d'), C(-7, -1.5, -4.5, 14, 2, 9, 'tw_trim'), C(-2, 0.5, 4, 4, 5, 1, 'tw_rune')],
    children=[tw_torso, tw_tabard])

MOBS['tomb_warden'] = dict(java='tombWarden', tex=256, scale=1.0, roots=[tw_hips, tw_leg(-1), tw_leg(1)],
    doc='Tomb Warden: Statuen-Golem aus dunklem Stein mit violetten Runen, Obelisk-Helm, gluehendem Runenkern, Steinhammer und Grabstein-Schild.',
    poses={
        1: [(0, 'arm_r', 0, 0, 0), (4, 'arm_r', -125, 0, 15), (7, 'arm_r', -30, 0, -10), (10, 'arm_r', 0, 0, 0),
            (0, 'fore_r', 0, 0, 0), (4, 'fore_r', -15, 0, 0), (7, 'fore_r', 25, 0, 0), (10, 'fore_r', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (4, 'torso', -8, 20, 0), (7, 'torso', 14, -18, 0), (10, 'torso', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (4, 'arm_l', -35, 0, -10), (7, 'arm_l', -15, 0, 0), (10, 'arm_l', 0, 0, 0)],
        3: [(0, 'arm_r', 0, 0, 0), (12, 'arm_r', -170, 0, 12), (14, 'arm_r', -40, 0, 0), (19, 'arm_r', -40, 0, 0), (22, 'arm_r', 0, 0, 0),
            (0, 'fore_r', 0, 0, 0), (12, 'fore_r', 10, 0, 0), (14, 'fore_r', 30, 0, 0), (19, 'fore_r', 30, 0, 0), (22, 'fore_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (12, 'arm_l', -150, 0, -12), (14, 'arm_l', -40, 0, 0), (19, 'arm_l', -40, 0, 0), (22, 'arm_l', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (12, 'torso', -18, 0, 0), (14, 'torso', 26, 0, 0), (19, 'torso', 26, 0, 0), (22, 'torso', 0, 0, 0),
            (0, 'head', 0, 0, 0), (12, 'head', -12, 0, 0), (14, 'head', 10, 0, 0), (19, 'head', 10, 0, 0), (22, 'head', 0, 0, 0),
            (0, 'leg_l', 0, 0, 0), (12, 'leg_l', -35, 0, 0), (14, 'leg_l', 0, 0, 0), (22, 'leg_l', 0, 0, 0),
            (0, 'tabard', 0, 0, 0), (12, 'tabard', -20, 0, 0), (14, 'tabard', 15, 0, 0), (22, 'tabard', 0, 0, 0)],
    })
