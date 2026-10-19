"""Frost Wisp (Minmus): schwebender Eisgeist - leuchtender Kristallkern in einem Rippenkaefig aus Eis, Kopf mit
Eiskrone und Minzaugen, schwebende Eisklauen, kreisende Eissplitter und ein Schleier aus herabhaengenden Eiszacken."""
import math
from model import B, C
from paint import mat
from mobs import MOBS

mat('fw_ice', 'ice', (196, 255, 238), (96, 206, 186))
mat('fw_ice_d', 'ice', (100, 196, 190), (40, 112, 124))
mat('fw_snow', 'snow', (245, 250, 255))
mat('fw_crys', 'crystal', (130, 240, 220), (230, 255, 250))
mat('fw_core', 'glow', (40, 220, 170), (236, 255, 248))
mat('fw_eye', 'eye', (10, 110, 90), (210, 255, 240))
mat('fw_dark', 'obsidian', (24, 60, 70), (120, 220, 210))


def X(s, x, w): return x if s > 0 else -x - w


def spike(name, pivot, rot, h, w, m='fw_crys', down=False, role=None, param=0.0):
    """Spitz zulaufender Eiszacken, waechst vom Pivot nach oben (-y) bzw. unten (down)."""
    tiers = []
    for ww, hh in [(w, round(h * 0.5)), (max(1, w - 1), round(h * 0.3)), (max(1, w - 2), h - round(h * 0.5) - round(h * 0.3))]:
        if tiers and tiers[-1][0] == ww: tiers[-1] = (ww, tiers[-1][1] + hh)     # gleiche Breite zusammenfassen
        else: tiers.append((ww, hh))
    cubes, y = [], 0
    for ww, hh in tiers:
        if hh < 1: continue
        if down: cubes.append(C(-ww / 2, y, -ww / 2, ww, hh, ww, m)); y += hh
        else: cubes.append(C(-ww / 2, y - hh, -ww / 2, ww, hh, ww, m)); y -= hh
    return B(name, pivot, rot, cubes, role=role, param=param)


# ── Kopf: Eismaske mit Stirnwulst, Minzaugen, Eiskrone ──
fw_head = B('head', (0, -1.5, 0), (0, 0, 0), [
    C(-3.5, -7, -3, 7, 7, 6, 'fw_ice'), C(-4, -6, -2.5, 8, 5, 5, 'fw_ice_d'), C(-4, -7.5, -3.5, 8, 2, 3, 'fw_snow'),
    C(-3, -5, -3.5, 2, 1, 1, 'fw_dark'), C(1, -5, -3.5, 2, 1, 1, 'fw_dark'), C(-3, -4, -3.4, 2, 2, 1, 'fw_eye'), C(1, -4, -3.4, 2, 2, 1, 'fw_eye'),
    C(-1.5, -2, -3.5, 3, 2, 1, 'fw_ice_d'), C(-0.5, -1, -4, 1, 2, 1, 'fw_crys')],
    children=[spike('crown_c', (0, -7, 0), (-10, 0, 0), 6, 3), spike('crown_r', (-2.5, -6.5, 0.5), (-15, 0, -35), 5, 2),
              spike('crown_l', (2.5, -6.5, 0.5), (-15, 0, 35), 5, 2), spike('crown_b', (0, -6, 2.5), (-55, 0, 0), 5, 2)],
    role='head')

# ── Kern im Rippenkaefig ──
fw_core = B('core', (0, 2, -1), (0, 0, 45), [C(-2, -2, -2, 4, 4, 4, 'fw_core'), C(-1.5, -1.5, -2.5, 3, 3, 5, 'fw_crys')])
fw_ribs = [B('rib_' + t, (s * 2.5, 2, -2), (0, s * 20, 0), [
    C(X(s, 0, 2), -3.5, -1, 2, 7, 2, 'fw_ice'), C(X(s, -1, 2), -4, -1.5, 2, 1, 2, 'fw_snow'), C(X(s, -1, 2), 3, -1.5, 2, 1, 2, 'fw_snow')],
    children=[spike('shoulder_' + t, (X(s, 1, 0), -3.5, 1), (-10, 0, s * 35), 5, 2)]) for t, s in (('r', -1), ('l', 1))]
fw_spine = B('spine', (0, -0.5, 1.5), (0, 0, 0), [C(-1.5, -1, 0, 3, 7, 2, 'fw_ice_d'), C(-2.5, 0, -1, 5, 2, 3, 'fw_snow')])


# ── Schwebende Eisklauen ──
def fw_hand(s):
    t = 'r' if s < 0 else 'l'
    claws = [spike('claw_%s%d' % (t, i), (s * 0.5 + (i - 1) * 1.2, 1.5, -0.5 + (i - 1) * 0.8), (-15, 0, s * (10 - i * 12)), 4, 2, 'fw_crys', down=True)
             for i in range(3)]
    return B('hand_' + t, (s * 8, 1, -1), (0, 0, s * -10), [
        C(-1.5, -2, -1.5, 3, 4, 3, 'fw_ice'), C(-2, -3, -2, 4, 2, 4, 'fw_snow')],
        children=claws + [spike('cuff_' + t, (s * 1, -2.5, 0), (0, 0, s * 40), 4, 2, 'fw_ice')], role='hand', param=0 if s < 0 else 2)


# ── Schleier: Ring herabhaengender Eiszacken ──
fw_veil = [spike('veil_%d' % i, (math.sin(a) * 2.6, 5.5, math.cos(a) * 2.2), (math.cos(a) * 20, 0, -math.sin(a) * 20), h, w, m,
                 down=True, role='tentacle', param=i * 0.9)
           for i, (a, h, w, m) in enumerate([(math.radians(d), h, w, m) for d, h, w, m in
                                              [(0, 6, 3, 'fw_ice'), (60, 6, 3, 'fw_crys'), (120, 7, 3, 'fw_ice'), (180, 7, 3, 'fw_crys'),
                                               (240, 7, 3, 'fw_ice'), (300, 6, 3, 'fw_crys')]])]
fw_veil.append(spike('veil_c', (0, 6.5, 0), (0, 0, 0), 6, 3, 'fw_ice_d', down=True, role='tentacle', param=2.5))

# ── Kreisende Splitter (Rolle orbit, Phase = param) ──
fw_shards = [B('shard_%d' % i, (math.cos(i * math.pi / 3) * 14, 2 + (i % 2) * 2 - 1, math.sin(i * math.pi / 3) * 14), (0, 0, 25), [
    C(-1, -2.5, -1, 2, 5, 2, 'fw_crys'), C(-0.5, -3.5, -0.5, 1, 1, 1, 'fw_ice'), C(-0.5, 2.5, -0.5, 1, 2, 1, 'fw_ice')],
    role='orbit', param=float(i)) for i in range(6)]

fw_wisp = B('wisp', (0, 12, 0), (0, 0, 0), [
    C(-3, -1, -2, 6, 7, 4, 'fw_ice_d'), C(-3.5, -1.5, -2.5, 7, 2, 5, 'fw_snow'), C(-2.5, 4, -2, 5, 3, 4, 'fw_ice')],
    children=[fw_head, fw_core, fw_spine, fw_hand(-1), fw_hand(1)] + fw_ribs + fw_veil + fw_shards, role='bob')

MOBS['frost_wisp'] = dict(java='frostWisp', tex=128, scale=0.72, roots=[fw_wisp],
    doc='Frost Wisp: schwebender Eisgeist mit leuchtendem Kristallkern, Eiskrone, Minzaugen, kreisenden Splittern und Eiszacken-Schleier.',
    poses={
        2: [(0, 'head', 0, 0, 0), (5, 'head', -18, 0, 0), (8, 'head', 12, 0, 0), (14, 'head', 0, 0, 0),
            (0, 'hand_r', 0, 0, 0), (5, 'hand_r', -40, 0, 30), (8, 'hand_r', -85, -20, 0), (14, 'hand_r', 0, 0, 0),
            (0, 'hand_l', 0, 0, 0), (5, 'hand_l', -40, 0, -30), (8, 'hand_l', -85, 20, 0), (14, 'hand_l', 0, 0, 0),
            (0, 'core', 0, 0, 0), (5, 'core', 0, 0, 45), (8, 'core', 0, 0, 90), (14, 'core', 0, 0, 0),
            (0, 'wisp', 0, 0, 0), (5, 'wisp', -10, 0, 0), (8, 'wisp', 8, 0, 0), (14, 'wisp', 0, 0, 0),
            (0, 'rib_r', 0, 0, 0), (5, 'rib_r', 0, -35, 0), (11, 'rib_r', 0, 0, 0),
            (0, 'rib_l', 0, 0, 0), (5, 'rib_l', 0, 35, 0), (11, 'rib_l', 0, 0, 0)],
    })
