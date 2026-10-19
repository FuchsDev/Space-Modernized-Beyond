"""Monolith Keeper (Ike): schwebender schwarzer Obelisk mit violetten Runen, Schlitzauge, Steinhaenden, Splittern und Halo."""
import math
from model import B, C
from paint import mat
from specs import BOSSES

mat('mk_rune', 'runes', (34, 30, 46), glow_col=(196, 120, 255))
mat('mk_obs', 'obsidian', (24, 20, 32), (130, 96, 180))
mat('mk_stone', 'rock', (104, 98, 116))
mat('mk_stone_d', 'rock', (62, 58, 74))
mat('mk_trim', 'plate', (128, 104, 66), (206, 176, 110))
mat('mk_crys', 'crystal', (110, 56, 176), (230, 184, 255))
mat('mk_glow', 'glow', (120, 36, 210), (246, 214, 255))
mat('mk_eye', 'eye', (70, 0, 110), (255, 226, 255))


def M(s, x, w): return x if s > 0 else -x - w


# ── Haende: schwebende Steinpanzerhandschuhe mit Runenstulpe, Bronzemanschette und Krallenfingern ──
def mk_hand(s, k):
    t = 'r' if s < 0 else 'l'
    fingers = []
    for i in range(4):
        fx = M(s, -3.75 + i * 2.5, 0) if s > 0 else -(-3.75 + i * 2.5)
        fingers.append(B('finger_%s%d' % (t, i), (fx, 5, -1.5), (-22 - i * 3, 0, s * (6 - i * 4)), [
            C(-1, 0, -1, 2, 6, 2, 'mk_stone'), C(-1, 5.5, -2.5, 2, 5, 2, 'mk_stone_d'), C(-0.5, 9.5, -3.5, 1, 2, 1, 'mk_crys')]))
    thumb = B('thumb_' + t, (s * -4.5, 2, -2), (-30, 0, s * 35), [C(-1, 0, -1, 2, 5, 2, 'mk_stone'), C(-1, 4.5, -2, 2, 3, 2, 'mk_stone_d')])
    return B('hand_' + t, (s * 25, -12, -5), (-12, 0, s * -8), [
        C(-4, -9, -3.5, 8, 4, 7, 'mk_trim'), C(-3, -11, -2.5, 6, 2, 5, 'mk_stone_d'),
        C(-5, -5, -4, 10, 10, 7, 'mk_stone'), C(-5.5, -4, -5, 11, 4, 2, 'mk_rune'), C(-5.5, 3, -4.5, 11, 2, 2, 'mk_trim'),
        C(-4, -3, 3, 8, 7, 1, 'mk_rune'), C(-1.5, -3, -5.5, 3, 2, 1, 'mk_glow')],
        children=fingers + [thumb], role='hand', param=k)


# ── Halo: gebrochener Ring aus Steinbloecken hinter dem Kopf ──
def mk_halo():
    kids = []
    for i in range(12):
        if i in (2, 3, 8): continue
        a = math.radians(i * 30 - 90)
        r = 19 + (2 if i == 4 else 0)
        rune = i % 3 == 0
        cubes = [C(-2.5, -4, -2, 5, 8, 4, 'mk_rune' if rune else 'mk_stone')]
        if i % 3 == 1: cubes.append(C(-3, -1, -1.5, 1, 2, 3, 'mk_glow'))
        kids.append(B('halo_%d' % i, (r * math.cos(a), r * math.sin(a), 0), (0, 0, i * 30 - 90 + (14 if i == 4 else 0) - (10 if i == 9 else 0)), cubes))
    return B('halo', (0, -32, 10), (6, 0, 0), [], children=kids)


# ── Oberer Koerper (Kopf): Mittelstueck mit Schlitzauge, schwebende Schulterplatten, Spitze, Pyramidion ──
mk_cap = B('cap', (0, -19, 0), (0, -12, 0), [
    C(-4.5, -4, -4.5, 9, 4, 9, 'mk_obs'), C(-3, -7, -3, 6, 3, 6, 'mk_obs'), C(-1.5, -9, -1.5, 3, 2, 3, 'mk_obs'),
    C(-1, -12, -1, 2, 2, 2, 'mk_glow')])
mk_spire = B('spire', (0, -20, 0), (0, 9, 0), [
    C(-5, -18, -4.5, 10, 18, 9, 'mk_rune'), C(-5.5, -2, -5, 11, 2, 10, 'mk_trim'), C(-1, -15, -5, 2, 10, 1, 'mk_glow'),
    C(-5.5, -19, -5, 11, 1, 10, 'mk_obs')],
    children=[mk_cap])
mk_slabs = [B('slab_' + t, (s * 10, -12, 0), (0, s * -8, s * -14), [
    C(M(s, -1, 3), -8, -6, 3, 14, 12, 'mk_stone'), C(M(s, 1.5, 1), -5, -4, 1, 8, 8, 'mk_rune'), C(M(s, -1.5, 4), -9, -6.5, 4, 2, 13, 'mk_trim')])
    for t, s in (('r', -1), ('l', 1))]
mk_head = B('head', (0, -6, 0), (0, 0, 0), [
    C(-6.5, -18, -6, 13, 17, 12, 'mk_rune'), C(-5.5, -12, -6.5, 11, 4, 1, 'mk_obs'), C(-4.5, -11, -7, 9, 2, 1, 'mk_eye'),
    C(-7.5, -15, -8, 15, 2, 4, 'mk_trim'), C(-7, -19.5, -6.5, 14, 2, 13, 'mk_obs')],
    children=[mk_spire, mk_halo()] + mk_slabs, role='head')

# ── Kreisende Splitter (Rolle orbit) ──
mk_shards = []
for k in range(6):
    a = k * 2 * math.pi / 6
    big = k % 2 == 0
    cubes = [C(-1.5, -4, -1.5, 3, 8, 3, 'mk_stone' if big else 'mk_stone_d'), C(-1, -6, -1, 2, 2, 2, 'mk_crys')]
    if big: cubes.append(C(-0.5, -2.5, -2, 1, 5, 1, 'mk_glow'))
    mk_shards.append(B('shard_%d' % k, (18 * math.cos(a), 10 + (3 if big else -2), 18 * math.sin(a)),
                       (25 - k * 9, math.degrees(2 * a) % 360, 15 + k * 7), cubes, role='orbit', param=k))

# ── Truemmer unter dem Sockel (taumeln) ──
mk_debris = [B('debris_%d' % i, p, r, [C(-w / 2, -w / 2, -w / 2, w, w, w, m)], role='tentacle', param=i * 1.7)
             for i, (p, r, w, m) in enumerate([((-3, 14, -1), (20, 10, 30), 4, 'mk_stone_d'), ((4, 15.5, 2), (35, 0, 15), 3, 'mk_stone'),
                                                 ((-1, 18, 3), (10, 40, 45), 3, 'mk_rune'), ((2, 17, -4), (0, 25, 20), 2, 'mk_stone_d')])]

mk_root = B('monolith', (0, 2, 0), (0, 0, 0), [
    C(-7.5, -2, -6.5, 15, 9, 13, 'mk_rune'), C(-8, -3, -7, 16, 2, 14, 'mk_trim'), C(-4.5, -6, -4, 9, 4, 8, 'mk_glow'),
    C(-6, 7, -5, 8, 3, 9, 'mk_stone_d'), C(1, 7, -3, 5, 4, 6, 'mk_stone_d'), C(-3, 6, 1, 4, 2, 5, 'mk_stone')],
    children=[mk_head, mk_hand(-1, 0), mk_hand(1, 1)] + mk_shards + mk_debris, role='bob')

HANDS = ('hand_r', 'hand_l')
BOSSES['monolith_keeper'] = dict(java='monolithKeeper', tex=256, scale=1.1, hitbox=(2.6, 5.0), roots=[mk_root],
    doc='Monolith Keeper: schwebender schwarzer Obelisk mit violett leuchtenden Runen, Schlitzauge, zwei schwebenden Steinhandschuhen, kreisenden Splittern und gebrochenem Steinhalo.',
    # Angriffe: eye_beam(40), gravity(62), stone_rain(40), blink(1)
    poses={
        0: [(0, 'head', 16, 0, 0), (0, 'halo', 18, 0, 20), (0, 'hand_r', 30, 0, -12), (0, 'hand_l', 30, 0, 12),
            (0, 'monolith', 0, 0, 6), (0, 'spire', 0, -9, 4), (0, 'cap', 0, 12, -6)],
        1: [(0, 'head', 0, 0, 0), (8, 'head', -10, 0, 0), (20, 'head', 10, 0, 0), (37, 'head', 10, 0, 0), (40, 'head', 0, 0, 0),
            (0, 'halo', 0, 0, 0), (20, 'halo', 0, 0, 180), (40, 'halo', 0, 0, 360),
            (0, 'hand_r', 0, 0, 0), (10, 'hand_r', -25, 0, 40), (36, 'hand_r', -25, 0, 40), (40, 'hand_r', 0, 0, 0),
            (0, 'hand_l', 0, 0, 0), (10, 'hand_l', -25, 0, -40), (36, 'hand_l', -25, 0, -40), (40, 'hand_l', 0, 0, 0),
            (0, 'monolith', 0, 0, 0), (8, 'monolith', -6, 0, 0), (20, 'monolith', 6, 0, 0), (37, 'monolith', 6, 0, 0), (40, 'monolith', 0, 0, 0)],
        2: [(0, 'hand_r', 0, 0, 0), (12, 'hand_r', -165, 0, 25), (56, 'hand_r', -170, 0, 15), (60, 'hand_r', 35, 0, 0), (62, 'hand_r', 0, 0, 0),
            (0, 'hand_l', 0, 0, 0), (12, 'hand_l', -165, 0, -25), (56, 'hand_l', -170, 0, -15), (60, 'hand_l', 35, 0, 0), (62, 'hand_l', 0, 0, 0),
            (0, 'monolith', 0, 0, 0), (12, 'monolith', -10, 0, 0), (56, 'monolith', -12, 0, 0), (60, 'monolith', 14, 0, 0), (62, 'monolith', 0, 0, 0),
            (0, 'halo', 0, 0, 0), (56, 'halo', 0, 0, -720), (62, 'halo', 0, 0, -720),
            (0, 'spire', 0, 0, 0), (12, 'spire', 0, 45, 0), (56, 'spire', 0, 45, 0), (60, 'spire', 0, 0, 0)],
        3: [(0, 'head', 0, 0, 0), (8, 'head', -22, 0, 0), (34, 'head', -22, 0, 0), (40, 'head', 0, 0, 0),
            (0, 'hand_r', 0, 0, 0), (8, 'hand_r', -130, 0, 30), (34, 'hand_r', -130, 0, 30), (40, 'hand_r', 0, 0, 0),
            (0, 'hand_l', 0, 0, 0), (8, 'hand_l', -130, 0, -30), (34, 'hand_l', -130, 0, -30), (40, 'hand_l', 0, 0, 0),
            (0, 'halo', 0, 0, 0), (40, 'halo', 0, 0, 720), (0, 'cap', 0, 0, 0), (40, 'cap', 0, 360, 0)],
        4: [(0, 'monolith', 0, 0, 0), (1, 'monolith', 0, 45, 14), (8, 'monolith', 0, 0, 0),
            (0, 'hand_r', 0, 0, 0), (1, 'hand_r', -40, 0, 55), (8, 'hand_r', 0, 0, 0),
            (0, 'hand_l', 0, 0, 0), (1, 'hand_l', -40, 0, -55), (8, 'hand_l', 0, 0, 0),
            (0, 'halo', 0, 0, 0), (1, 'halo', 0, 0, 40), (8, 'halo', 0, 0, 0)],
    })
