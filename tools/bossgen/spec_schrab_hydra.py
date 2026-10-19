"""Schrab Hydra (Eve): vierbeinige Hydra - schwerer tuerkiser Schuppenleib mit violettem Bauch, drei lange Haelse mit
Kammkoepfen (Kiefer, cyan leuchtende Augen), Schrabidium-Kristallgruppen auf dem Ruecken und ein dicker Schwanz mit Kristallkeule."""
from model import B, C
from paint import mat
from specs import BOSSES

mat('sh_scale', 'scale', (40, 112, 104), (100, 200, 180))
mat('sh_scale_d', 'scale', (26, 66, 76), (60, 128, 136))
mat('sh_belly', 'scale', (112, 72, 136), (180, 138, 204))
mat('sh_crystal', 'crystal', (28, 116, 138), (130, 255, 245))
mat('sh_crystal_p', 'crystal', (90, 44, 136), (214, 156, 255))
mat('sh_bone', 'bone', (214, 206, 226))
mat('sh_rock', 'rock', (62, 54, 76))
mat('sh_eye', 'eye', (0, 60, 72), (160, 255, 255))
mat('sh_glow', 'glow', (20, 150, 160), (196, 255, 250))
mat('sh_flesh', 'flesh', (110, 50, 100), (60, 220, 200))

# ── Kristallgruppen ──
def cluster(name, pos, rot, h, purple=False):
    m = 'sh_crystal_p' if purple else 'sh_crystal'
    m2 = 'sh_crystal' if purple else 'sh_crystal_p'
    side_a = B(name + '_a', (-2, 0, 0.5), (10, 0, -34), [C(-1.5, -h * 0.6, -1.5, 3, h * 0.6, 3, m)])
    side_b = B(name + '_b', (2, 0, -0.5), (-15, 20, 30), [C(-1, -h * 0.5, -1, 2, h * 0.5, 2, m2)])
    return B(name, pos, rot, [C(-2, -h, -2, 4, h, 4, m), C(-3, -1, -3, 6, 2, 6, 'sh_rock')], children=[side_a, side_b])

# ── Koepfe/Haelse ──
def head(n, rz=0):
    jaw = B('jaw_' + n, (0, 1, -1), (0, 0, 0), [
        C(-3.5, 0, -12, 7, 2.5, 12, 'sh_scale_d'), C(-3, 2.5, -11, 6, 1, 9, 'sh_belly'), C(-2.5, -0.5, -11, 5, 1, 9, 'sh_glow'),
        C(-3, -1.5, -11.5, 1, 1.5, 1, 'sh_bone', infl=0.1), C(2, -1.5, -11.5, 1, 1.5, 1, 'sh_bone', infl=0.1)], role='jaw')
    crest = B('crest_' + n, (0, -6, -2), (-35, 0, 0), [
        C(-0.5, -8, -1, 1, 8, 4, 'sh_crystal'), C(-1, -4, 2, 2, 4, 5, 'sh_crystal_p')])
    return B('head_' + n, (0, -10, 0), (12, 0, rz), [
        C(-4.5, -6, -6, 9, 7, 9, 'sh_scale'), C(-5, -7, -7, 10, 2, 5, 'sh_scale_d'),               # Schaedel, Brauen
        C(-3.5, -4, -13, 7, 4, 7, 'sh_scale'),                                                      # Schnauze
        C(-3, 0, -13, 6, 1, 11, 'sh_glow'),                                                         # Rachenglut
        C(-4.8, -4.5, -6, 1, 2, 3, 'sh_eye'), C(3.8, -4.5, -6, 1, 2, 3, 'sh_eye'),
        C(-5.5, -3, -2, 1, 4, 5, 'sh_bone'), C(4.5, -3, -2, 1, 4, 5, 'sh_bone')],                   # Wangenschilde
        children=[jaw, crest], role='head')

NECK = [(7, 48), (6, -22), (6, -30)]   # (Breite, rx)

def neck(n, pos, splay, ph):
    child = head(n, -splay * 0.3)
    for i in reversed(range(3)):
        w, rx = NECK[i]
        rz = [splay, -splay * 0.35, -splay * 0.35][i]
        ry = [-splay * 0.4, 0, 0][i]
        cubes = [C(-w / 2, -10, -w / 2, w, 10 + (1 if i < 2 else 0), w, 'sh_scale' if i % 2 == 0 else 'sh_scale_d'),
                 C(-w / 2 + 1, -9, -w / 2 - 1, w - 2, 8, 1, 'sh_belly')]
        if i < 2: cubes.append(C(-0.5, -8, w / 2, 1, 6, 2, 'sh_crystal'))
        child = B('neck_%s%d' % (n, i + 1), pos if i == 0 else (0, -10, 0), (rx, ry, rz), cubes, children=[child],
                  role='neck', param=ph + i * 0.6)
    return child

# ── Beine ──
def leg(n, x, z, role):
    s = -1 if 'r' in n else 1
    claws = [C(-3.5 + k * 2.5, 1, -7, 2, 2, 2, 'sh_bone') for k in range(3)]
    foot = B('foot_' + n, (0, 9, -1), (-6, 0, 0), [C(-4, 0, -5, 8, 3, 8, 'sh_scale_d')] + claws)
    shin = B('shin_' + n, (0, 8, 0), (16, 0, 0), [
        C(-3, 0, -3, 6, 9, 6, 'sh_scale')], children=[foot])
    return B('leg_' + n, (x, 4, z), (-10, 0, s * -4), [
        C(-4, -3, -4.5, 8, 11, 9, 'sh_scale_d'), C(-4.5 + s * 0.5, -4, -5, 9, 6, 10, 'sh_scale')], children=[shin], role=role)

# ── Schwanz ──
TAIL = [(12, 10, 10, -18, 10), (10, 8, 9, -8, 14), (8, 6, 9, 6, 16), (5, 4, 8, 10, 12)]
sh_tail = B('tail_tip', (0, 0, 8), (10, 10, 0), [C(-2, -2, 0, 4, 4, 4, 'sh_scale_d')],
            children=[cluster('tail_spike', (0, 0, 3), (-80, 0, 0), 7, purple=True)], role='chain', param=5)
for i in reversed(range(4)):
    w, h, l, rx, ry = TAIL[i]
    cubes = [C(-w / 2, -h / 2, 0, w, h, l, 'sh_scale' if i % 2 == 0 else 'sh_scale_d'),
             C(-1, -h / 2 - 2, 2, 2, 2, l - 4, 'sh_crystal')]
    sh_tail = B('tail_%d' % (i + 1), (0, -2, 17) if i == 0 else (0, 0, TAIL[i - 1][2]), (rx, ry, 0), cubes, children=[sh_tail],
                role='chain', param=i + 1)

sh_body = B('body', (0, 2, 2), (0, 0, 0), [
    C(-12, -10, -16, 24, 18, 33, 'sh_scale'), C(-13, -13, -18, 26, 10, 14, 'sh_scale_d'), C(-12.5, -12, 5, 25, 9, 12, 'sh_scale_d'),
    C(-10, 8, -14, 20, 2, 28, 'sh_belly'), C(-7, -6, -19, 14, 12, 3, 'sh_belly'),
    C(-13, -5, -12, 1, 10, 26, 'sh_scale_d'), C(12, -5, -12, 1, 10, 26, 'sh_scale_d'), C(-3, -14, -14, 6, 2, 28, 'sh_belly')],
    children=[neck('c', (0, -10, -15), 0, 0.0), neck('r', (-8.5, -8, -14), -34, 2.1), neck('l', (8.5, -8, -14), 34, 4.2),
              cluster('crys_1', (-6, -13, -7), (-10, 20, -22), 13), cluster('crys_2', (6, -13, -3), (8, -30, 20), 16, purple=True),
              cluster('crys_3', (-6, -12, 8), (14, 45, -18), 12, purple=True), cluster('crys_4', (6, -12, 12), (18, 10, 24), 13),
              cluster('crys_5', (0, -15, 2), (-6, 0, 0), 20), sh_tail],
    role='body')

def _k(t, bones, r):
    return [(t, b, r[0], r[1], r[2]) for b in bones]

N1 = ['neck_c1', 'neck_r1', 'neck_l1']
HD = ['head_c', 'head_r', 'head_l']
JW = ['jaw_c', 'jaw_r', 'jaw_l']

BOSSES['schrab_hydra'] = dict(java='schrabHydra', tex=256, scale=1.1, hitbox=(2.8, 3.4),
    roots=[sh_body, leg('fr', -12, -9, 'leg_a'), leg('fl', 12, -9, 'leg_b'), leg('br', -12, 13, 'leg_b'), leg('bl', 12, 13, 'leg_a')],
    doc='Schrab Hydra: vierbeinige Hydra mit tuerkisem Schuppenleib, violettem Bauch, drei langen Haelsen mit Kammkoepfen und '
        'cyan leuchtenden Augen, Schrabidium-Kristallgruppen auf dem Ruecken und dickem Schwanz mit Kristallkeule.',
    # Angriffe: triple_beam(22), shards(10), acid(1), bite(12)
    poses={
        0: _k(0, N1, (40, 0, 0)) + _k(0, ['neck_c2', 'neck_r2', 'neck_l2'], (25, 0, 0)) + _k(0, HD, (15, 0, 0)) + _k(0, JW, (-5, 0, 0))
           + [(0, 'body', 5, 0, 0)] + _k(0, ['tail_1', 'tail_2', 'tail_3', 'tail_4'], (0, 22, 0)),
        1: _k(0, N1, (0, 0, 0)) + _k(8, N1, (-25, 0, 0)) + _k(16, N1, (-25, 0, 0)) + _k(19, N1, (12, 0, 0)) + _k(24, N1, (0, 0, 0))
           + _k(0, HD, (0, 0, 0)) + _k(8, HD, (-20, 0, 0)) + _k(16, HD, (-15, 0, 0)) + _k(19, HD, (5, 0, 0)) + _k(24, HD, (0, 0, 0))
           + _k(0, JW, (0, 0, 0)) + _k(8, JW, (20, 0, 0)) + _k(16, JW, (35, 0, 0)) + _k(19, JW, (45, 0, 0)) + _k(24, JW, (0, 0, 0))
           + [(0, 'body', 0, 0, 0), (8, 'body', -5, 0, 0), (19, 'body', 3, 0, 0), (24, 'body', 0, 0, 0)],
        2: [(0, 'body', 0, 0, 0), (4, 'body', -9, 0, 0), (6, 'body', 5, 0, 0), (12, 'body', 0, 0, 0)]
           + _k(0, N1, (0, 0, 0)) + _k(4, N1, (-20, 0, 0)) + _k(6, N1, (15, 0, 0)) + _k(12, N1, (0, 0, 0))
           + _k(0, HD, (0, 0, 0)) + _k(4, HD, (-30, 0, 0)) + _k(6, HD, (10, 0, 0)) + _k(12, HD, (0, 0, 0))
           + _k(0, JW, (0, 0, 0)) + _k(4, JW, (30, 0, 0)) + _k(6, JW, (40, 0, 0)) + _k(12, JW, (0, 0, 0)),
        3: _k(0, HD, (0, 0, 0)) + _k(3, HD, (25, 0, 0)) + _k(8, HD, (0, 0, 0))
           + _k(0, JW, (0, 0, 0)) + _k(3, JW, (40, 0, 0)) + _k(8, JW, (0, 0, 0))
           + _k(0, N1, (0, 0, 0)) + _k(3, N1, (10, 0, 0)) + _k(8, N1, (0, 0, 0)),
        4: [(0, 'neck_c1', 0, 0, 0), (5, 'neck_c1', -30, 0, 0), (8, 'neck_c1', 40, 0, 0), (13, 'neck_c1', 0, 0, 0),
            (0, 'neck_c2', 0, 0, 0), (5, 'neck_c2', -10, 0, 0), (8, 'neck_c2', 20, 0, 0), (13, 'neck_c2', 0, 0, 0),
            (0, 'head_c', 0, 0, 0), (5, 'head_c', -25, 0, 0), (8, 'head_c', 15, 0, 0), (13, 'head_c', 0, 0, 0),
            (0, 'jaw_c', 0, 0, 0), (5, 'jaw_c', 50, 0, 0), (8, 'jaw_c', 5, 0, 0), (13, 'jaw_c', 0, 0, 0),
            (0, 'neck_r1', 0, 0, 0), (5, 'neck_r1', -15, 0, -10), (8, 'neck_r1', 20, 0, 5), (13, 'neck_r1', 0, 0, 0),
            (0, 'neck_l1', 0, 0, 0), (5, 'neck_l1', -15, 0, 10), (8, 'neck_l1', 20, 0, -5), (13, 'neck_l1', 0, 0, 0),
            (0, 'jaw_r', 0, 0, 0), (5, 'jaw_r', 30, 0, 0), (8, 'jaw_r', 10, 0, 0), (13, 'jaw_r', 0, 0, 0),
            (0, 'jaw_l', 0, 0, 0), (5, 'jaw_l', 30, 0, 0), (8, 'jaw_l', 10, 0, 0), (13, 'jaw_l', 0, 0, 0),
            (0, 'body', 0, 0, 0), (5, 'body', -6, 0, 0), (8, 'body', 6, 0, 0), (13, 'body', 0, 0, 0)],
    })
