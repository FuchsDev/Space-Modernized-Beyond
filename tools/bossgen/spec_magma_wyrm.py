"""Magma Wyrm (Moho): fliegende Lavaschlange - gehoernter Kopf mit Kiefer und Glutaugen, kleine Fledermausfluegel am
Hals, neun sich verjuengende Krustensegmente mit Rueckenstacheln und Klingen-Schwanzspitze."""
from model import B, C
from paint import mat
from specs import BOSSES

mat('mw_crust', 'crust', (52, 32, 28), glow_col=(255, 120, 20))
mat('mw_crust_d', 'crust', (36, 24, 24), glow_col=(255, 76, 10))
mat('mw_scale', 'scale', (64, 38, 34), (150, 74, 44))
mat('mw_belly', 'scale', (126, 62, 30), (236, 150, 64))
mat('mw_obsidian', 'obsidian', (30, 22, 36), (130, 90, 170))
mat('mw_horn', 'bone', (122, 98, 80))
mat('mw_teeth', 'bone', (236, 220, 190))
mat('mw_membrane', 'flesh', (78, 26, 20), (240, 100, 30))
mat('mw_glow', 'glow', (255, 80, 10), (255, 236, 130))
mat('mw_eye', 'eye', (140, 30, 0), (255, 250, 150))

# ── Kopf ──
def horn(side):
    n = 'r' if side < 0 else 'l'
    tip = B('horn2_' + n, (0, -7, 0), (-35, 0, side * 12), [
        C(-1, -7, -1, 2, 7, 2, 'mw_horn'), C(-0.5, -9, -0.5, 1, 2, 1, 'mw_glow')])
    return B('horn_' + n, (side * 4, -6, -5), (-45, 0, side * 30), [
        C(-1.5, -7, -1.5, 3, 7, 3, 'mw_horn'), C(-2, -2, -2, 4, 2, 4, 'mw_obsidian')], children=[tip])

def cheek(side):
    n = 'r' if side < 0 else 'l'
    return B('cheek_' + n, (side * 5.5, -1, -4), (0, side * -35, 0), [
        C(-0.5 if side > 0 else -0.5, -2, 0, 1, 3, 7, 'mw_obsidian'), C(-0.5, -4, 2, 1, 2, 5, 'mw_obsidian')])

mw_jaw = B('jaw', (0, 1.5, -2), (0, 0, 0), [
    C(-4.5, 0, -15.5, 9, 3, 15, 'mw_scale'), C(-4, 3, -14, 8, 1, 12, 'mw_belly'), C(-3.5, -0.5, -14, 7, 1, 11, 'mw_glow'),
    C(-4, -2, -15, 1, 2, 1, 'mw_teeth'), C(3, -2, -15, 1, 2, 1, 'mw_teeth'), C(-4, -1.5, -11, 1, 1.5, 1, 'mw_teeth', infl=0.1),
    C(3, -1.5, -11, 1, 1.5, 1, 'mw_teeth', infl=0.1), C(-1.5, 3, -6, 3, 3, 4, 'mw_obsidian')],
    role='jaw')

mw_head = B('head', (0, -2, -5), (0, 0, 0), [
    C(-6, -6, -10, 12, 8, 11, 'mw_scale'), C(-6.5, -7, -11, 13, 2, 6, 'mw_obsidian'),            # Schaedel, Brauenwulst
    C(-4.5, -4.5, -19, 9, 5, 9, 'mw_scale'), C(-2, -5.5, -18, 4, 1, 8, 'mw_obsidian'), C(-6.5, -2, -8, 13, 3, 7, 'mw_crust'),             # Schnauze, Nasengrat
    C(-3.5, -3.5, -19.5, 1, 1, 1, 'mw_glow'), C(2.5, -3.5, -19.5, 1, 1, 1, 'mw_glow'),           # Nuestern
    C(-3.5, 0.5, -18, 7, 1, 13, 'mw_glow'),                                                      # Gaumenglut
    C(-4.5, 0.5, -18.5, 1, 2, 1, 'mw_teeth'), C(3.5, 0.5, -18.5, 1, 2, 1, 'mw_teeth'),
    C(-4.5, 0.5, -14, 1, 2, 1, 'mw_teeth'), C(3.5, 0.5, -14, 1, 2, 1, 'mw_teeth'),
    C(-6.6, -4, -9, 1, 2, 3, 'mw_eye'), C(5.6, -4, -9, 1, 2, 3, 'mw_eye'),
    C(-1, -9, -8, 2, 3, 6, 'mw_obsidian')],                                                      # Scheitelkamm
    children=[mw_jaw, horn(-1), horn(1), cheek(-1), cheek(1)], role='head')

# ── Fluegel ──
def wing(side):
    n = 'r' if side < 0 else 'l'
    sx = side   # Spiegeln: x -> sx * x
    def cx(x, w): return x if sx < 0 else -x - w
    tip = B('wtip_' + n, (sx * 13, 0, 0), (0, side * 25, side * -20), [
        C(cx(-11, 11), -1, -1, 11, 2, 2, 'mw_obsidian'), C(cx(-11, 11), -0.5, 1, 11, 1, 6, 'mw_membrane'),
        C(cx(-5, 5), -0.5, 7, 5, 1, 3, 'mw_membrane'), C(cx(-13, 2), -1.5, -1.5, 2, 3, 3, 'mw_horn')])
    return B('wing_' + n, (sx * 6.5, -3, 0), (0, side * 15, side * -15), [
        C(cx(-13, 13), -1.5, -1.5, 13, 3, 3, 'mw_obsidian'), C(cx(-13, 13), -0.5, 1, 13, 1, 9, 'mw_membrane'),
        C(cx(-8, 8), -0.5, 10, 8, 1, 3, 'mw_membrane'), C(cx(-13, 1), -1, 1, 1, 1, 11, 'mw_obsidian')],
        children=[tip], role='wing_' + n)

# ── Rumpfkette ──
SEG = [  # (Breite, Hoehe, Laenge, rx, ry)
    (13, 12, 10, -12, 14), (12, 11, 10, -8, 10), (11, 10, 9, 2, -12), (10, 9, 9, 10, -20), (9, 8, 8, 10, -14),
    (8, 7, 8, 6, 12), (7, 6, 7, 0, 22), (6, 5, 7, -6, 16), (5, 4, 6, -8, -8)]

def segment(i, child):
    w, h, l, rx, ry = SEG[i]
    cubes = [C(-w / 2, -h / 2, 0, w, h, l, 'mw_crust' if i % 2 == 0 else 'mw_crust_d'),
             C(-w / 2 + 1, -h / 2 - 1, 1, w - 2, 1, l - 2, 'mw_scale'),
             C(-w / 2 + 1, h / 2, 1, w - 2, 1, l - 2, 'mw_belly')]
    if w >= 9:   # seitliche Krustenplatten
        cubes += [C(-w / 2 - 1, -h / 2 + 1, 2, 1, h - 3, l - 4, 'mw_obsidian'), C(w / 2, -h / 2 + 1, 2, 1, h - 3, l - 4, 'mw_obsidian')]
    sl = max(3, int(h * 0.7)) + (1 if i % 2 == 0 else -1)
    spine = B('spine_%d' % (i + 1), (0, -h / 2 - 0.5, l * 0.4), (-40, 0, 0), [
        C(-1, -sl, -1, 2, sl, 2, 'mw_obsidian'), C(-0.5, -sl - 2, -0.5, 1, 2, 1, 'mw_glow')])
    kids = [spine] + ([child] if child else [])
    pz = SEG[i - 1][2] if i > 0 else 6
    return B('seg_%d' % (i + 1), (0, 0, pz), (rx, ry, 0), cubes, children=kids, role='chain', param=i + 1)

mw_tail = B('tail', (0, 0, 6), (-6, -10, 0), [
    C(-1.5, -1.5, 0, 3, 3, 6, 'mw_crust'), C(-5, -0.5, 4, 10, 1, 5, 'mw_obsidian'), C(-3, -0.5, 9, 6, 1, 3, 'mw_obsidian'),
    C(-1, -0.5, 12, 2, 1, 2, 'mw_glow'), C(-0.5, -4, 3, 1, 3, 6, 'mw_obsidian'), C(-4, -1, 3.5, 8, 2, 1, 'mw_glow')],
    role='chain', param=10)

chain = mw_tail
for i in reversed(range(len(SEG))):
    chain = segment(i, chain)

mw_collar = B('collar', (0, -6, -2), (-35, 0, 0), [
    C(-1.5, -9, -1.5, 3, 9, 3, 'mw_obsidian'), C(-1, -11, -1, 2, 2, 2, 'mw_glow'),
    C(-5.5, -6, 0, 2, 6, 2, 'mw_obsidian'), C(3.5, -6, 0, 2, 6, 2, 'mw_obsidian')])

mw_body = B('body', (0, 8, 4), (0, 0, 0), [
    C(-7, -6.5, -6, 14, 13, 12, 'mw_crust'), C(-6, -7.5, -5, 12, 1, 10, 'mw_scale'), C(-5.5, 6.5, -5, 11, 1, 10, 'mw_belly'),
    C(-7.5, -4, -4, 1, 8, 8, 'mw_obsidian'), C(6.5, -4, -4, 1, 8, 8, 'mw_obsidian'), C(-3, 3, -6.5, 6, 3, 1, 'mw_glow')],
    children=[mw_head, wing(-1), wing(1), mw_collar, chain], role='body')

_segs = ['seg_%d' % i for i in range(1, 10)]

def _k(t, bones, r):
    return [(t, b, r[0], r[1], r[2]) for b in bones]

BOSSES['magma_wyrm'] = dict(java='magmaWyrm', tex=256, scale=1.25, hitbox=(2.2, 2.0), roots=[mw_body],
    doc='Magma Wyrm: fliegende Lavaschlange mit gehoerntem Kopf, Glutaugen und gluehendem Rachen, kleinen Fledermausfluegeln '
        'am Hals und neun sich verjuengenden Krustensegmenten mit Rueckenstacheln bis zur Klingen-Schwanzspitze.',
    # Angriffe: fire_breath(30), magma_volley(12), dive(bis 35), eruption(22)
    poses={
        0: [(0, 'body', 12, 0, 0), (0, 'head', 30, 0, 0), (0, 'jaw', -5, 0, 0), (0, 'wing_r', 0, 30, -45), (0, 'wing_l', 0, -30, 45),
            (0, 'wtip_r', 0, 40, 0), (0, 'wtip_l', 0, -40, 0)] + _k(0, _segs, (0, 28, 0)),
        1: [(0, 'body', 0, 0, 0), (6, 'body', -12, 0, 0), (10, 'body', 6, 0, 0), (28, 'body', 6, 0, 0), (34, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (6, 'head', -25, 0, 0), (10, 'head', 12, 0, 0), (28, 'head', 12, 0, 0), (34, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (6, 'jaw', 10, 0, 0), (10, 'jaw', 45, 0, 0), (28, 'jaw', 45, 0, 0), (34, 'jaw', 0, 0, 0),
            (0, 'wing_r', 0, 0, 0), (6, 'wing_r', 0, -10, 30), (28, 'wing_r', 0, -10, 30), (34, 'wing_r', 0, 0, 0),
            (0, 'wing_l', 0, 0, 0), (6, 'wing_l', 0, 10, -30), (28, 'wing_l', 0, 10, -30), (34, 'wing_l', 0, 0, 0),
            (0, 'seg_1', 0, 0, 0), (6, 'seg_1', 12, 0, 0), (10, 'seg_1', -6, 0, 0), (34, 'seg_1', 0, 0, 0)],
        2: [(0, 'body', 0, 0, 0), (6, 'body', -14, 0, 0), (8, 'body', 8, 0, 0), (14, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (6, 'head', -30, 0, 0), (8, 'head', 15, 0, 0), (14, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (6, 'jaw', 20, 0, 0), (8, 'jaw', 50, 0, 0), (14, 'jaw', 0, 0, 0),
            (0, 'seg_1', 0, 0, 0), (6, 'seg_1', 15, 0, 0), (8, 'seg_1', -8, 0, 0), (14, 'seg_1', 0, 0, 0),
            (0, 'wing_r', 0, 0, 0), (6, 'wing_r', 0, 0, 35), (8, 'wing_r', 0, 0, -10), (14, 'wing_r', 0, 0, 0),
            (0, 'wing_l', 0, 0, 0), (6, 'wing_l', 0, 0, -35), (8, 'wing_l', 0, 0, 10), (14, 'wing_l', 0, 0, 0)],
        3: [(0, 'body', 0, 0, 0), (10, 'body', -35, 0, 0), (15, 'body', -35, 0, 0), (19, 'body', 45, 0, 0), (32, 'body', 45, 0, 0), (38, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (10, 'head', -15, 0, 0), (19, 'head', 15, 0, 0), (32, 'head', 15, 0, 0), (38, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (10, 'jaw', 10, 0, 0), (19, 'jaw', 35, 0, 0), (32, 'jaw', 35, 0, 0), (38, 'jaw', 0, 0, 0),
            (0, 'wing_r', 0, 0, 0), (10, 'wing_r', 0, 0, 35), (19, 'wing_r', 0, 45, -40), (32, 'wing_r', 0, 45, -40), (38, 'wing_r', 0, 0, 0),
            (0, 'wing_l', 0, 0, 0), (10, 'wing_l', 0, 0, -35), (19, 'wing_l', 0, -45, 40), (32, 'wing_l', 0, -45, 40), (38, 'wing_l', 0, 0, 0)]
           + _k(0, _segs[:4], (0, 0, 0)) + _k(10, _segs[:4], (10, 0, 0)) + _k(19, _segs[:4], (-12, 0, 0))
           + _k(32, _segs[:4], (-12, 0, 0)) + _k(38, _segs[:4], (0, 0, 0)),
        4: [(0, 'body', 0, 0, 0), (8, 'body', -22, 0, 0), (18, 'body', -22, 0, 0), (21, 'body', 14, 0, 0), (26, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (8, 'head', -35, 0, 0), (18, 'head', -35, 0, 0), (21, 'head', 20, 0, 0), (26, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (8, 'jaw', 50, 0, 0), (18, 'jaw', 50, 0, 0), (21, 'jaw', 30, 0, 0), (26, 'jaw', 0, 0, 0),
            (0, 'wing_r', 0, 0, 0), (8, 'wing_r', 0, -15, 45), (18, 'wing_r', 0, -15, 45), (21, 'wing_r', 0, 0, -20), (26, 'wing_r', 0, 0, 0),
            (0, 'wing_l', 0, 0, 0), (8, 'wing_l', 0, 15, -45), (18, 'wing_l', 0, 15, -45), (21, 'wing_l', 0, 0, 20), (26, 'wing_l', 0, 0, 0)]
           + _k(0, _segs[:3], (0, 0, 0)) + _k(8, _segs[:3], (10, 0, 0)) + _k(21, _segs[:3], (-6, 0, 0)) + _k(26, _segs[:3], (0, 0, 0)),
    })
