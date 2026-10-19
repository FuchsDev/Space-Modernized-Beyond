"""Abyssal Leviathan (Laythe): Tiefsee-Anglerfisch-Leviathan mit Unterbiss, Leuchtkoeder, Biolumineszenz."""
import math
from model import B, C
from paint import mat
from specs import BOSSES

mat('al_scale', 'scale', (30, 58, 106), (84, 146, 200))
mat('al_scale_d', 'scale', (16, 30, 62), (44, 82, 132))
mat('al_belly', 'scale', (64, 92, 126), (150, 180, 206))
mat('al_mouth', 'flesh', (92, 18, 42), (168, 48, 84))
mat('al_tooth', 'bone', (238, 232, 210))
mat('al_spine', 'bone', (126, 150, 178))
mat('al_ray', 'bone', (70, 96, 134))
mat('al_fin', 'fiber', (40, 92, 146))
mat('al_stalk', 'flesh', (30, 52, 88), (70, 170, 210))
mat('al_glow', 'glow', (0, 120, 160), (160, 255, 250))
mat('al_lure', 'glow', (30, 190, 255), (240, 255, 255))
mat('al_eye', 'eye', (6, 30, 40), (140, 255, 235))
mat('al_gill', 'gill', (22, 40, 70), (10, 20, 40), glow_col=(60, 220, 255))
mat('al_bone', 'bone', (112, 128, 150))


def _mc(cubes):
    return [C(-c.x - c.w, c.y, c.z, c.w, c.h, c.d, c.mat, c.infl) for c in cubes]


_ROLE_MIRROR = {'wing_r': 'wing_l', 'arm_r': 'arm_l', 'leg_r': 'leg_l'}


def _mirror(b, a='_r', z='_l'):
    rx, ry, rz = (math.degrees(v) for v in b.rot)
    return B(b.name.replace(a, z), (-b.pivot[0], b.pivot[1], b.pivot[2]), (rx, -ry, -rz), _mc(b.cubes),
             [_mirror(ch, a, z) for ch in b.children], role=_ROLE_MIRROR.get(b.role, b.role), param=b.param)


def _ch(x, y, z, w, h, d, m, k=1):
    """Gefaster Block aus drei Wuerfeln (runde Silhouette statt Kiste)."""
    return [C(x, y + k, z + k, w, h - 2 * k, d - 2 * k, m), C(x + k, y, z + k, w - 2 * k, h, d - 2 * k, m),
            C(x + k, y + k, z, w - 2 * k, h - 2 * k, d, m)]


def _dots(pts, m='al_glow'):
    """Leuchtpunkte, links/rechts gespiegelt (x = Aussenflaeche rechts)."""
    out = []
    for x, y, z in pts: out += [C(x - 0.6, y, z, 1, 1, 1, m), C(-x - 0.4, y, z, 1, 1, 1, m)]
    return out


# ── Koeder: langer Stiel in drei Gliedern, Leuchtkugel mit Kaefig und Faeden ────
al_bulb = B('lure_bulb', (0, -6, 0), (-75, 0, 0), [
    C(-2, -2, -2, 4, 4, 4, 'al_lure', 0.6), C(-1.5, -4, -1.5, 3, 1, 3, 'al_stalk'),
    C(-3, -1, -1, 1, 2, 2, 'al_glow'), C(2, -1, -1, 1, 2, 2, 'al_glow'), C(-1, -1, 2.2, 2, 2, 1, 'al_glow'),
    C(-0.5, 2.5, -0.5, 1, 4, 1, 'al_stalk'), C(-1, 6.5, -1, 2, 2, 2, 'al_glow'), C(-2, 2.5, 0.5, 1, 3, 1, 'al_stalk')])
al_lure_tip = B('lure_tip', (0, -10, 0), (50, 0, 0), [
    C(-0.5, -6, -0.5, 1, 6, 1, 'al_stalk', 0.15), C(-1, -1, -1, 2, 1, 2, 'al_spine')], children=[al_bulb], role='tentacle', param=2.0)
al_lure_mid = B('lure_mid', (0, -9, 0), (38, 0, 0), [
    C(-1, -10, -1, 2, 10, 2, 'al_stalk'), C(-1.5, -1, -1.5, 3, 1, 3, 'al_spine'), C(-0.5, -7, 1, 1, 3, 1, 'al_spine'),
    C(-0.5, -4, 1, 1, 2, 1, 'al_glow')], children=[al_lure_tip], role='tentacle', param=1.0)
al_lure = B('lure_base', (0, -14, -10), (14, 0, 0), [
    C(-1.5, -9, -1.5, 3, 9, 3, 'al_stalk'), C(-2.5, -1, -2.5, 5, 2, 5, 'al_scale_d'), C(-0.5, -6, 1.5, 1, 4, 2, 'al_ray')],
    children=[al_lure_mid], role='neck', param=0.5)

# ── Unterkiefer (Unterbiss) mit Reisszaehnen ────────────────────────────────────
al_teeth_lf = B('teeth_lf', (0, 0, -19), (6, 0, 0),
                [C(x - (1 if h >= 6 else 0.5), -h, 0, 2 if h >= 6 else 1, h, 2 if h >= 6 else 1, 'al_tooth')
                 for x, h in ((-10.5, 5), (-7.5, 7), (-4, 4), (-1.5, 6), (1.5, 6), (4, 4), (7.5, 7), (10.5, 5))])
al_teeth_lr = B('teeth_lr', (-11.5, 0, -10), (0, 0, -14),
                [C(-0.5, -5, -7, 1, 5, 1, 'al_tooth'), C(-0.5, -3, -3.5, 1, 3, 1, 'al_tooth'), C(-0.5, -4, 0, 1, 4, 1, 'al_tooth')])
al_barbel_r = B('barbel_r', (-5, 9, -17), (15, 0, 12), [C(-0.5, 0, -0.5, 1, 6, 1, 'al_stalk'), C(-1, 6, -1, 2, 2, 2, 'al_glow')],
                role='tentacle', param=0.3)
al_jaw = B('jaw', (0, 1, -3), (12, 0, 0),
    _ch(-12.5, 0, -20, 25, 7, 19, 'al_scale') + _ch(-11, 6, -18, 22, 4, 16, 'al_belly') + [
    C(-10.5, -1, -18.5, 21, 1, 16, 'al_mouth'),               # Mundboden
    C(-3, -1.5, -15, 6, 1, 9, 'al_mouth', 0.25),              # Zunge
    C(-8, 1, -21.5, 16, 5, 2, 'al_scale_d'),                  # vorstehendes Kinn
    C(-4, 6, -20, 8, 3, 3, 'al_scale_d'),
] + _dots([(-12.5, 3, -16), (-12.5, 4, -11), (-12.5, 3, -6)]) + [C(-0.5, 2.5, -22, 1, 1, 1, 'al_glow')],
    children=[al_teeth_lf, al_teeth_lr, _mirror(al_teeth_lr, '_lr', '_ll'), al_barbel_r, _mirror(al_barbel_r)], role='jaw')

# ── Schaedel ────────────────────────────────────────────────────────────────────
al_brow_r = B('brow_r', (-9, -7, -13), (0, 18, -20), [C(-2, -2, -4, 4, 2, 8, 'al_bone'), C(-1.5, -3, 2, 3, 1, 4, 'al_bone')])
al_cheek_r = B('cheek_r', (-11, -2, -7), (0, 40, 12), [
    C(-1, -1, 0, 2, 2, 7, 'al_spine'), C(-0.5, -0.5, 7, 1, 1, 4, 'al_spine'), C(-0.5, 2, 1, 1, 1, 6, 'al_spine')])
al_forehead = B('forehead', (0, -6, -17), (-52, 0, 0), _ch(-9, -10, -1, 18, 10, 4, 'al_scale_d'))
al_head = B('head', (0, 0, -5), (-6, 0, 0),
    _ch(-11, -10, -10, 22, 11, 10, 'al_scale') + _ch(-11, -7, -17, 22, 8, 9, 'al_scale') + _ch(-8.5, -13, -10, 17, 4, 9, 'al_scale_d') + [
    C(-11.5, 0, -18, 23, 2, 16, 'al_scale_d'),                # Oberkieferrand
    C(-10, 2, -17, 20, 1, 14, 'al_mouth'),                    # Gaumen
    C(-11.6, -5, -14, 1, 2, 2, 'al_eye'), C(10.6, -5, -14, 1, 2, 2, 'al_eye'),     # kleine Leuchtaugen
    C(-11.3, -6, -15, 1, 4, 4, 'al_scale_d'), C(10.3, -6, -15, 1, 4, 4, 'al_scale_d'),
] + _dots([(-11.5, -2, -11), (-11.5, -4, -6), (-9, -10, -5)])
  + [C(x - 0.5, 2, -17.5, 1, h, 1, 'al_tooth') for x, h in ((-8.5, 3), (-5.5, 4), (-2.5, 3), (2.5, 3), (5.5, 4), (8.5, 3))]
  + [C(-10.5, 2, -13, 1, 3, 1, 'al_tooth'), C(9.5, 2, -13, 1, 3, 1, 'al_tooth')],
    children=[al_jaw, al_forehead, al_lure, al_brow_r, _mirror(al_brow_r), al_cheek_r, _mirror(al_cheek_r)], role='head')


# ── Rueckenstacheln mit Flossenhaut ─────────────────────────────────────────────
def _dorsal(name, pivot, rot, hs, step):
    cubes = []
    for i, h in enumerate(hs):
        z = i * step
        cubes.append(C(-0.5, -h, z, 1, h, 1, 'al_spine'))
        if i < len(hs) - 1:
            hm = min(h, hs[i + 1]) - 2
            if hm >= 1: cubes.append(C(-0.5, -hm, z + 1, 1, hm, step - 1, 'al_fin'))
        if i % 2 == 0: cubes.append(C(-1, -h - 1, z - 0.5, 2, 1, 2, 'al_glow'))
    return B(name, pivot, rot, cubes)


al_dorsal = _dorsal('dorsal', (0, -12, -3), (-38, 0, 0), [10, 12, 10, 8, 5], 3)

# ── Brustflossen (flattern), Bauchflossen ───────────────────────────────────────
al_fin_r = B('fin_r', (-10.5, 4, -1), (0, 28, -22), [
    C(-3, -2, -2, 3, 4, 6, 'al_scale_d'),                       # Flossenansatz
    C(-14, -0.5, -1, 11, 1, 8, 'al_fin'), C(-19, -0.5, 0, 5, 1, 5, 'al_fin'), C(-22, -0.5, 1, 3, 1, 2, 'al_fin'),
    C(-20, -1, 0, 17, 1, 1, 'al_ray'), C(-18, -1, 3, 15, 1, 1, 'al_ray'), C(-12, -1, 6, 9, 1, 1, 'al_ray'),
    C(-23, -1, 0.5, 2, 2, 2, 'al_glow')], role='wing_r')
al_pelvic_r = B('pelvic_r', (-6, 8, 2), (25, 20, -35), [
    C(-1, 0, -1, 1, 6, 3, 'al_fin'), C(-1.5, 0, -1.5, 1, 7, 1, 'al_ray')], role='tentacle', param=1.5)

# ── Schwanz (Kette) ─────────────────────────────────────────────────────────────
al_caudal = B('caudal', (0, 0, 5), (0, 0, 0), [C(-1.5, -2, -1, 3, 4, 4, 'al_scale_d'), C(-0.5, -1.5, 2, 1, 3, 9, 'al_fin')], children=[
    B('caudal_up', (0, -1, 1), (-38, 0, 0), [C(-0.5, -1, 0, 1, 3, 12, 'al_fin'), C(-1, -1.5, 0, 2, 1, 13, 'al_ray'), C(-1, -2, 12.5, 2, 2, 2, 'al_glow')]),
    B('caudal_dn', (0, 1, 1), (34, 0, 0), [C(-0.5, -2, 0, 1, 3, 10, 'al_fin'), C(-1, 0.5, 0, 2, 1, 11, 'al_ray'), C(-1, 0, 10.5, 2, 2, 2, 'al_glow')]),
], role='chain', param=4)
al_tail3 = B('tail3', (0, 0.5, 6), (3, 0, 0), [
    C(-2.5, -2.5, 0, 5, 5, 6, 'al_scale'), C(-1.5, -3.5, 0, 3, 1, 5, 'al_scale_d'), C(-0.5, -6, 1, 1, 3, 4, 'al_fin')],
    children=[al_caudal], role='chain', param=3)
al_tail2 = B('tail2', (0, 0.5, 7), (4, 0, 0), _ch(-4, -4, 0, 8, 8, 7, 'al_scale') + [
    C(-3, -5, 0, 6, 1, 6, 'al_scale_d'), C(-0.5, -8, 1, 1, 4, 1, 'al_spine'), C(-0.5, -7, 2, 1, 2, 4, 'al_fin')]
    + _dots([(-4, -1, 2), (-4, 1, 5)]), children=[al_tail3], role='chain', param=2)
al_tail1 = B('tail1', (0, 1, 10), (4, 0, 0), _ch(-6.5, -6.5, -1, 13, 12, 9, 'al_scale') + [
    C(-5, -8, 0, 10, 2, 7, 'al_scale_d'), C(-5, 5, 0, 10, 2, 7, 'al_belly'),
    C(-0.5, -12, 1, 1, 5, 1, 'al_spine'), C(-0.5, -11, 2, 1, 3, 4, 'al_fin')]
    + _dots([(-6.5, -2, 1), (-6.5, 1, 4), (-6.5, -4, 6)]), children=[al_tail2], role='chain', param=1)

# ── Rumpf ───────────────────────────────────────────────────────────────────────
al_body = B('body', (0, 4, 0), (0, 0, 0),
    _ch(-11, -11, -7, 22, 20, 17, 'al_scale', 2) + _ch(-9, 7, -6, 18, 5, 14, 'al_belly') + [
    C(-7, -13, -4, 14, 3, 12, 'al_scale_d'),                   # Ruecken
    C(-8, -8, 9, 16, 14, 2, 'al_scale_d'),                     # Ruecken-Ende
    C(-11.5, -6, -6, 1, 8, 4, 'al_gill'), C(10.5, -6, -6, 1, 8, 4, 'al_gill'),   # Kiemen
] + _dots([(-11, -2, 0), (-11, 1, 3), (-11, 4, 6), (-10.5, -6, 4), (-9, 9, -2)]),
    children=[al_head, al_dorsal, al_fin_r, _mirror(al_fin_r), al_pelvic_r, _mirror(al_pelvic_r), al_tail1], role='body')

BOSSES['abyssal_leviathan'] = dict(java='abyssalLeviathan', tex=256, scale=1.4, pscale=4, hitbox=(3.5, 3.0), roots=[al_body],
    doc='Abyssal Leviathan: Tiefsee-Anglerfisch mit riesigem Unterbiss voller Knochenzaehne, leuchtendem Koeder am Stirnstiel, '
        'biolumineszenten Punkten, Brustflossen, Rueckenstacheln und schlaengelndem Schwanz.',
    # Angriffe: lure_flash, vortex, water_jet, bite_dash (Reihenfolge wie in Bosses.AbyssalLeviathan)
    poses={
        # ruhend: Koeder gesenkt, Maul fast zu, Flossen angelegt, Schwanz eingerollt
        0: [(0, 'body', 8, 0, 0), (0, 'lure_base', 40, 0, 0), (0, 'lure_mid', 25, 0, 0), (0, 'jaw', -10, 0, 0),
            (0, 'fin_r', 0, -10, -25), (0, 'fin_l', 0, 10, 25), (0, 'tail1', 0, 25, 0), (0, 'tail2', 0, 25, 0), (0, 'tail3', 0, 20, 0)],
        # lure_flash (22): Koeder hoch und zurueck aufladen, bei 20 nach vorn schnellen (Blitz)
        1: [(0, 'lure_base', 0, 0, 0), (8, 'lure_base', -30, 0, 0), (18, 'lure_base', -35, 0, 0), (20, 'lure_base', 25, 0, 0), (22, 'lure_base', 0, 0, 0),
            (0, 'lure_mid', 0, 0, 0), (8, 'lure_mid', -25, 0, 0), (18, 'lure_mid', -28, 0, 0), (20, 'lure_mid', 15, 0, 0), (22, 'lure_mid', 0, 0, 0),
            (0, 'head', 0, 0, 0), (8, 'head', -12, 0, 0), (18, 'head', -14, 0, 0), (20, 'head', 6, 0, 0), (22, 'head', 0, 0, 0),
            (0, 'fin_r', 0, 0, 0), (8, 'fin_r', 0, 0, 30), (20, 'fin_r', 0, 0, 30), (22, 'fin_r', 0, 0, 0),
            (0, 'fin_l', 0, 0, 0), (8, 'fin_l', 0, 0, -30), (20, 'fin_l', 0, 0, -30), (22, 'fin_l', 0, 0, 0)],
        # vortex (52): Rollen um die Laengsachse, Schwanz peitscht, bei 50 Maul auf (Druckwelle)
        2: [(0, 'body', 0, 0, 0), (6, 'body', 0, 0, 25), (16, 'body', 0, 0, -25), (26, 'body', 0, 0, 25), (36, 'body', 0, 0, -25), (46, 'body', 0, 0, 0), (52, 'body', 0, 0, 0),
            (0, 'tail1', 0, 0, 0), (6, 'tail1', 0, 35, 0), (16, 'tail1', 0, -35, 0), (26, 'tail1', 0, 35, 0), (36, 'tail1', 0, -35, 0), (46, 'tail1', 0, 0, 0),
            (0, 'tail2', 0, 0, 0), (6, 'tail2', 0, 30, 0), (16, 'tail2', 0, -30, 0), (26, 'tail2', 0, 30, 0), (36, 'tail2', 0, -30, 0), (46, 'tail2', 0, 0, 0),
            (0, 'fin_r', 0, 0, 0), (6, 'fin_r', 0, -20, 40), (46, 'fin_r', 0, -20, 40), (52, 'fin_r', 0, 0, 0),
            (0, 'fin_l', 0, 0, 0), (6, 'fin_l', 0, 20, -40), (46, 'fin_l', 0, 20, -40), (52, 'fin_l', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (6, 'jaw', 15, 0, 0), (44, 'jaw', 0, 0, 0), (50, 'jaw', 40, 0, 0), (52, 'jaw', 0, 0, 0)],
        # water_jet (14): Kopf zurueck, bei 12 Maul weit auf und Rueckstoss
        3: [(0, 'head', 0, 0, 0), (8, 'head', -18, 0, 0), (12, 'head', 8, 0, 0), (14, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (8, 'jaw', 30, 0, 0), (12, 'jaw', 45, 0, 0), (14, 'jaw', 0, 0, 0),
            (0, 'body', 0, 0, 0), (8, 'body', -6, 0, 0), (12, 'body', 6, 0, 0), (14, 'body', 0, 0, 0),
            (0, 'fin_r', 0, 0, 0), (8, 'fin_r', 0, 30, 20), (14, 'fin_r', 0, 0, 0), (0, 'fin_l', 0, 0, 0), (8, 'fin_l', 0, -30, -20), (14, 'fin_l', 0, 0, 0)],
        # bite_dash (22): Maul aufreissen, Vorstoss ab 8, bei 14 zuschnappen
        4: [(0, 'jaw', 0, 0, 0), (8, 'jaw', 40, 0, 0), (12, 'jaw', 40, 0, 0), (14, 'jaw', -10, 0, 0), (22, 'jaw', 0, 0, 0),
            (0, 'head', 0, 0, 0), (8, 'head', -15, 0, 0), (14, 'head', 10, 0, 0), (22, 'head', 0, 0, 0),
            (0, 'body', 0, 0, 0), (8, 'body', -8, 0, 0), (14, 'body', 10, 0, 0), (22, 'body', 0, 0, 0),
            (0, 'lure_base', 0, 0, 0), (8, 'lure_base', -25, 0, 0), (14, 'lure_base', 20, 0, 0), (22, 'lure_base', 0, 0, 0),
            (0, 'tail1', 0, 0, 0), (8, 'tail1', 0, 30, 0), (14, 'tail1', 0, -30, 0), (22, 'tail1', 0, 0, 0),
            (0, 'fin_r', 0, 0, 0), (8, 'fin_r', 0, 35, 15), (22, 'fin_r', 0, 0, 0), (0, 'fin_l', 0, 0, 0), (8, 'fin_l', 0, -35, -15), (22, 'fin_l', 0, 0, 0)],
    })
