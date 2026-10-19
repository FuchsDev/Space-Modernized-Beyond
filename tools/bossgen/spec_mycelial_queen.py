"""Mycelial Queen (Sporos): schlanke Pilz-Matriarchin mit Hyphen-Robe, breitem Pilzhut mit leuchtenden Lamellen,
blassem Gesicht, langen Armen mit Rankenfingern, Rueckententakeln und Sporensaecken."""
import math
from model import B, C
from paint import mat
from specs import BOSSES

mat('mq_cap', 'cap', (148, 58, 38), (238, 222, 196))
mat('mq_cap_d', 'cap', (112, 42, 30), (214, 194, 164))
mat('mq_gill', 'gill', (226, 188, 130), (178, 130, 78), glow_col=(255, 186, 70))
mat('mq_fiber', 'fiber', (214, 204, 180))
mat('mq_fiber_d', 'fiber', (112, 92, 78))
mat('mq_robe', 'cloth', (96, 66, 72), (214, 160, 80))
mat('mq_robe2', 'cloth', (64, 46, 56), (150, 110, 70))
mat('mq_skin', 'flesh', (232, 224, 208), (184, 166, 172))
mat('mq_spore', 'glow', (220, 120, 20), (255, 228, 136))
mat('mq_eye', 'eye', (70, 30, 0), (255, 214, 96))
mat('mq_stalk', 'fiber', (238, 232, 216))
mat('mq_shelf', 'cap', (176, 124, 70), (222, 184, 120))


def _mc(cubes):
    return [C(-c.x - c.w, c.y, c.z, c.w, c.h, c.d, c.mat, c.infl) for c in cubes]


_ROLE_MIRROR = {'wing_r': 'wing_l', 'arm_r': 'arm_l', 'leg_r': 'leg_l'}


def _mirror(b, a='_r', z='_l'):
    rx, ry, rz = (math.degrees(v) for v in b.rot)
    return B(b.name.replace(a, z), (-b.pivot[0], b.pivot[1], b.pivot[2]), (rx, -ry, -rz), _mc(b.cubes),
             [_mirror(ch, a, z) for ch in b.children], role=_ROLE_MIRROR.get(b.role, b.role), param=b.param)


# ── Pilzhut-Krone mit Lamellen, Hutrand, Kronenpilzen und Schleier ──────────────
def _minishroom(name, pivot, rot, h, glow):
    return B(name, pivot, rot, [C(-0.5, -h, -0.5, 1, h, 1, 'mq_stalk'), C(-1.5, -h - 1.5, -1.5, 3, 2, 3, 'mq_spore' if glow else 'mq_cap'),
                                C(-1, -h - 2.5, -1, 2, 1, 2, 'mq_cap_d' if glow else 'mq_spore')])


mq_crown = [_minishroom('crown%d' % i, (math.sin(a) * 5.5, -8.5, -math.cos(a) * 5.5), (-math.cos(a) * 14, 0, math.sin(a) * 14),
                        h, i % 2 == 0) for i, (a, h) in enumerate((math.radians(d), h) for d, h in ((0, 5), (60, 3), (120, 4), (180, 3), (240, 4), (300, 3)))]
mq_rims = [
    B('rim_f', (0, -2, -12), (18, 0, 0), [C(-11, -1, -4, 22, 2, 4, 'mq_cap'), C(-10, 1, -3, 20, 1, 3, 'mq_gill')]),
    B('rim_b', (0, -2, 12), (-32, 0, 0), [C(-11, -1, 0, 22, 2, 4, 'mq_cap'), C(-10, 1, 0, 20, 1, 3, 'mq_gill')]),
    B('rim_r', (-12, -2, 0), (0, 0, -32), [C(-4, -1, -11, 4, 2, 22, 'mq_cap'), C(-3, 1, -10, 3, 1, 20, 'mq_gill')]),
    B('rim_l', (12, -2, 0), (0, 0, 32), [C(0, -1, -11, 4, 2, 22, 'mq_cap'), C(0, 1, -10, 3, 1, 20, 'mq_gill')]),
]


def _veil(name, x, z, h, p):
    return B(name, (x, 0, z), (0, 0, 0), [C(-0.5, 0, -0.5, 1, h, 1, 'mq_fiber'), C(-0.5, h, -0.5, 1, 1, 1, 'mq_spore')], role='tentacle', param=p)


mq_cap = B('cap', (0, -11, 1), (-3, 0, 0), [
    C(-12, -1, -12, 24, 1, 24, 'mq_gill'),                    # Lamellen (leuchten)
    C(-12, -3, -12, 24, 2, 24, 'mq_cap'),                     # Hut als Kuppel
    C(-11.5, -5, -11.5, 23, 2, 23, 'mq_cap'),
    C(-10.5, -6, -10.5, 21, 1, 21, 'mq_cap'),
    C(-9, -7, -9, 18, 1, 18, 'mq_cap'),
    C(-7, -8, -7, 14, 1, 14, 'mq_cap_d'),
    C(-4.5, -9, -4.5, 9, 1, 9, 'mq_cap_d'),
    C(-2.5, 0, 0, 5, 2, 4, 'mq_stalk'),                       # Stielansatz hinter dem Gesicht
], children=mq_crown + mq_rims + [
    _veil('veil0', -8, -8, 7, 0.0), _veil('veil1', 8, -8, 7, 1.1), _veil('veil2', -11, 0, 9, 2.2), _veil('veil3', 11, 0, 9, 3.3),
    _veil('veil4', -7, 9, 8, 4.4), _veil('veil5', 7, 9, 8, 5.5), _veil('veil6', 0, 11, 10, 0.7)])

# ── Kopf: blasses langes Gesicht, Hyphen-Haar ───────────────────────────────────
mq_head = B('head', (0, -15, -0.5), (0, 0, 0), [
    C(-1.5, -3, -1.5, 3, 3, 3, 'mq_skin'),                    # Hals
    C(-3, -10, -3.5, 6, 6, 6, 'mq_skin'),                     # Gesicht
    C(-2, -4.5, -3.2, 4, 2, 5, 'mq_skin'),                    # schmales Kinn
    C(-3.2, -8.5, -3.8, 6, 1, 1, 'mq_fiber_d'),               # Brauenschatten
    C(-2.5, -7.5, -3.9, 2, 1, 1, 'mq_spore'), C(0.5, -7.5, -3.9, 2, 1, 1, 'mq_spore'),
    C(-2.6, -6.5, -3.7, 1, 2, 1, 'mq_fiber_d'), C(1.6, -6.5, -3.7, 1, 2, 1, 'mq_fiber_d'),   # Traenenspuren
    C(-0.5, -5, -3.5, 1, 1, 1, 'mq_fiber_d'),                 # Mund
    C(-3.5, -10, -0.5, 7, 10, 4, 'mq_fiber'),                 # Hyphen-Haar hinten
    C(-3.6, -9, -2.5, 1, 6, 3, 'mq_fiber'), C(2.6, -9, -2.5, 1, 6, 3, 'mq_fiber'),
], children=[mq_cap], role='head')


# ── Arme: lang und duenn, Glockenaermel, Rankenfinger ──────────────────────────
def _finger(name, x, z, rot, p):
    return B(name, (x, 2, z), rot, [C(-0.5, 0, -0.5, 1, 4, 1, 'mq_skin'), C(-0.5, 3.5, -1.2, 1, 3, 1, 'mq_fiber'),
                                    C(-0.5, 6, -2, 1, 2, 1, 'mq_fiber'), C(-0.5, 7.5, -2.6, 1, 1, 1, 'mq_spore')], role='tentacle', param=p)


mq_hand_r = B('hand_r', (0, 10, 0), (0, 0, 0), [C(-1.5, 0, -1.5, 3, 3, 3, 'mq_skin'), C(-2, 0, -2, 4, 1, 4, 'mq_fiber_d')], children=[
    _finger('finger_r0', -1, -1, (-10, 0, 18), 0.0), _finger('finger_r1', 0.5, -1, (-18, 0, -8), 0.8),
    _finger('finger_r2', -1, 1, (12, 0, 14), 1.6), _finger('finger_r3', 1, 1, (8, 0, -16), 2.4)])
mq_fore_r = B('fore_r', (0, 11, 0), (-22, 0, 0), [
    C(-1, 0, -1, 2, 10, 2, 'mq_skin'), C(-1.5, 7, -1.5, 3, 2, 3, 'mq_fiber'), C(-1.5, 1, -1.5, 3, 1, 3, 'mq_fiber_d')], children=[mq_hand_r])
mq_sleeve_r = B('sleeve_r', (0, 1, 0), (12, 0, 10), [C(-3, 0, -3, 6, 9, 6, 'mq_robe'), C(-2.5, -1, -2.5, 5, 1, 5, 'mq_robe2'),
                                                    C(-3, 9, 1, 2, 2, 2, 'mq_fiber'), C(1, 9, -3, 2, 3, 1, 'mq_fiber')])
mq_arm_r = B('arm_r', (-6.5, -13, 0), (0, 0, 8), [
    C(-1, 0, -1, 2, 11, 2, 'mq_skin'), C(-3, -2, -3, 5, 3, 6, 'mq_robe')], children=[mq_sleeve_r, mq_fore_r], role='arm_r')
mq_arm_l = _mirror(mq_arm_r)


# ── Rueckententakel ─────────────────────────────────────────────────────────────
def _tendril(s):
    n = 'r' if s < 0 else 'l'
    t4 = B('tendril_%s4' % n, (0, -5, 0), (0, 0, s * -18), [C(-0.5, -5, -0.5, 1, 5, 1, 'mq_fiber'), C(-1, -7, -1, 2, 2, 2, 'mq_spore')], role='tentacle', param=3.0)
    t3 = B('tendril_%s3' % n, (0, -6, 0), (0, 0, s * -20), [C(-1, -5, -1, 2, 5, 2, 'mq_fiber'), C(-1.5, -3, -0.5, 1, 1, 1, 'mq_spore')], children=[t4], role='tentacle', param=2.0)
    t2 = B('tendril_%s2' % n, (0, -6, 0), (0, 0, s * -22), [C(-1, -6, -1, 2, 6, 2, 'mq_fiber_d'), C(-1.5, -2, -1.5, 3, 1, 3, 'mq_fiber')], children=[t3], role='tentacle', param=1.0)
    return B('tendril_' + n, (s * 3, -11, 3.5), (-28, 0, s * 62), [
        C(-1.5, -6, -1.5, 3, 6, 3, 'mq_fiber_d'), C(-2, -1, -2, 4, 2, 4, 'mq_robe2'), C(-1, -4, 1.2, 1, 1, 1, 'mq_spore')],
        children=[t2], role='tentacle', param=0.0)


# ── Oberkoerper ─────────────────────────────────────────────────────────────────
def _sacs(s):
    return [C(s * 7.5 - 2, -19, -2, 4, 4, 4, 'mq_spore'), C(s * 6 - 1.5, -20.5, 0.5, 3, 3, 3, 'mq_spore'), C(s * 8.5 - 1, -16.5, 1.5, 2, 2, 2, 'mq_spore'),
            C(s * 7.5 - 2.5, -16, -2.5, 5, 1, 5, 'mq_fiber_d'), C(s * 6 - 1, -21, 1, 2, 1, 2, 'mq_fiber')]


mq_torso = B('torso', (0, 0, 0), (0, 0, 0), [
    C(-3, -6, -2.5, 6, 6, 5, 'mq_fiber_d'),                   # Taille (Korsett)
    C(-3.5, -5, -3, 7, 1, 6, 'mq_robe2'), C(-3.5, -3, -3, 7, 1, 6, 'mq_robe2'),
    C(-4.5, -14, -3, 9, 8, 6, 'mq_robe'),                     # Mieder
    C(-3.5, -14, -3.6, 1, 8, 1, 'mq_fiber'), C(2.5, -14, -3.6, 1, 8, 1, 'mq_fiber'),   # Hyphen-Rippen
    C(-2.5, -9, -3.6, 5, 1, 1, 'mq_fiber'),
    C(-1.5, -13, -4, 3, 3, 1, 'mq_spore'),                    # Sporenherz
    C(-2, -13.5, -3.7, 4, 4, 1, 'mq_fiber_d'),
    C(-7, -16, -3.5, 14, 3, 7, 'mq_robe'),                    # Schulterumhang
    C(-5.5, -17, -3, 11, 2, 6, 'mq_robe2'),
    C(-5, -15, 3, 10, 12, 2, 'mq_robe'),                      # Rueckenumhang
] + _sacs(-1) + _sacs(1), children=[mq_head, mq_arm_r, mq_arm_l, _tendril(-1), _tendril(1)], role='body')


# ── Robe: Kegel aus Bahnen, vordere Bahnen schwingen beim Gehen ─────────────────
def _tatters(x0, x1, y, z, m='mq_fiber'):
    return [C(x, y, z, 1, 1 + (i % 2), 1, m) for i, x in enumerate(range(int(x0), int(x1), 3))]


mq_robe_r = B('robe_r', (-3, 0, -3.5), (-9, 0, 7), [
    C(-3.5, 0, -1, 6, 22, 2, 'mq_robe'), C(-3, 4, -1.5, 1, 16, 1, 'mq_fiber_d')] + _tatters(-3.5, 2.5, 22, -0.5), role='leg_r')
mq_robe_b = B('robe_b', (0, 0, 3.5), (10, 0, 0), [C(-6, 0, -1, 12, 22, 2, 'mq_robe'), C(-5, 0, -1.5, 10, 4, 1, 'mq_robe2'),
    C(-4, 10, 1, 4, 1, 3, 'mq_shelf'), C(1, 14, 1, 3, 1, 2, 'mq_shelf'), C(-1, 6, 0.6, 1, 14, 1, 'mq_fiber')] + _tatters(-6, 6, 22, -0.5))
mq_robe_sr = B('robe_sr', (-5, 0, 0), (0, 0, 10), [
    C(-1, 0, -4.5, 2, 22, 9, 'mq_robe'), C(-2, 8, -2, 1, 3, 4, 'mq_shelf'), C(-1.6, 13, 0, 1, 2, 3, 'mq_shelf')] + [
    C(-0.5, 22, z, 1, 1 + (i % 2), 1, 'mq_fiber') for i, z in enumerate((-4, -1, 2))])
mq_robe = B('robe', (0, 0, 0), (0, 0, 0), [
    C(-4.5, 0, -3.5, 9, 12, 7, 'mq_robe2'), C(-6, 12, -5, 12, 11, 10, 'mq_robe2'),     # Unterkleid
    C(-5, -1, -4, 10, 3, 8, 'mq_fiber_d'),                                             # Guertel
    C(-2, 1, -4.6, 4, 3, 1, 'mq_spore'),                                               # Guertelspore
    C(-0.5, 23, -13, 1, 1, 5, 'mq_fiber'), C(-1, 23, -15, 2, 1, 2, 'mq_spore'),        # Myzel am Boden
    C(-11, 23, -6, 4, 1, 1, 'mq_fiber'), C(7, 23, -6, 4, 1, 1, 'mq_fiber'),
    C(-12, 23, 3, 5, 1, 1, 'mq_fiber'), C(7, 23, 4, 5, 1, 1, 'mq_fiber'), C(-13, 23, 2.5, 1, 1, 2, 'mq_spore'),
    C(-3, 23, 7, 1, 1, 6, 'mq_fiber'), C(2, 23, 8, 1, 1, 4, 'mq_fiber'), C(1.5, 23, 12, 2, 1, 1, 'mq_spore'),
], children=[mq_torso, mq_robe_r, _mirror(mq_robe_r), mq_robe_b, mq_robe_sr, _mirror(mq_robe_sr, '_sr', '_sl')])

BOSSES['mycelial_queen'] = dict(java='mycelialQueen', tex=256, scale=1.4, pscale=4, hitbox=(2.2, 5.4), roots=[mq_robe],
    doc='Mycelial Queen: schlanke Pilz-Matriarchin in einer Robe aus Hyphen mit breitem rotbraunem Pilzhut als Krone, '
        'bernsteinfarben leuchtenden Lamellen, blassem Gesicht, langen Rankenfingern, zwei Rueckententakeln und Sporensaecken.',
    # Angriffe: spore_storm, root_eruption, tendril_lash, spore_bolts, brood (Reihenfolge wie in Bosses.MycelialQueen)
    poses={
        # ruhend: Arme vor dem Leib verschraenkt, Tentakel angelegt, leicht vorgebeugt
        0: [(0, 'torso', 8, 0, 0), (0, 'arm_r', -35, 0, -28), (0, 'arm_l', -35, 0, 28), (0, 'fore_r', -45, 0, 0), (0, 'fore_l', -45, 0, 0),
            (0, 'tendril_r', 25, 0, 35), (0, 'tendril_l', 25, 0, -35)],
        # spore_storm (40): Arme weit nach oben ausbreiten, Kopf in den Nacken, Tentakel aufgerichtet
        1: [(0, 'arm_r', 0, 0, 0), (8, 'arm_r', -30, 0, 80), (32, 'arm_r', -30, 0, 80), (40, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (8, 'arm_l', -30, 0, -80), (32, 'arm_l', -30, 0, -80), (40, 'arm_l', 0, 0, 0),
            (0, 'fore_r', 0, 0, 0), (8, 'fore_r', 15, 0, 0), (32, 'fore_r', 15, 0, 0), (40, 'fore_r', 0, 0, 0),
            (0, 'fore_l', 0, 0, 0), (8, 'fore_l', 15, 0, 0), (32, 'fore_l', 15, 0, 0), (40, 'fore_l', 0, 0, 0),
            (0, 'head', 0, 0, 0), (8, 'head', -22, 0, 0), (32, 'head', -22, 0, 0), (40, 'head', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (8, 'torso', -8, 0, 0), (32, 'torso', -8, 0, 0), (40, 'torso', 0, 0, 0),
            (0, 'tendril_r', 0, 0, 0), (8, 'tendril_r', -20, 0, 25), (32, 'tendril_r', -20, 0, 25), (40, 'tendril_r', 0, 0, 0),
            (0, 'tendril_l', 0, 0, 0), (8, 'tendril_l', -20, 0, -25), (32, 'tendril_l', -20, 0, -25), (40, 'tendril_l', 0, 0, 0)],
        # root_eruption (20, Ausbruch bei 18): Arme langsam hoch beschwoeren, bei 18 nach unten stossen
        2: [(0, 'arm_r', 0, 0, 0), (12, 'arm_r', -35, 0, 115), (17, 'arm_r', -35, 0, 120), (18, 'arm_r', -40, 0, 25), (20, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (12, 'arm_l', -35, 0, -115), (17, 'arm_l', -35, 0, -120), (18, 'arm_l', -40, 0, -25), (20, 'arm_l', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (12, 'torso', -10, 0, 0), (17, 'torso', -10, 0, 0), (18, 'torso', 18, 0, 0), (20, 'torso', 0, 0, 0),
            (0, 'head', 0, 0, 0), (12, 'head', -15, 0, 0), (18, 'head', 15, 0, 0), (20, 'head', 0, 0, 0),
            (0, 'tendril_r', 0, 0, 0), (12, 'tendril_r', -35, 0, 30), (18, 'tendril_r', 30, 0, -10), (20, 'tendril_r', 0, 0, 0),
            (0, 'tendril_l', 0, 0, 0), (12, 'tendril_l', -35, 0, -30), (18, 'tendril_l', 30, 0, 10), (20, 'tendril_l', 0, 0, 0)],
        # tendril_lash (14, Rundumschlag bei 10): Tentakel zurueckreissen und nach vorn peitschen, Oberkoerper dreht mit
        3: [(0, 'tendril_r', 0, 0, 0), (6, 'tendril_r', -50, 0, 20), (10, 'tendril_r', 85, 0, -15), (14, 'tendril_r', 0, 0, 0),
            (0, 'tendril_l', 0, 0, 0), (6, 'tendril_l', -50, 0, -20), (10, 'tendril_l', 85, 0, 15), (14, 'tendril_l', 0, 0, 0),
            (0, 'tendril_r2', 0, 0, 0), (6, 'tendril_r2', -20, 0, 0), (10, 'tendril_r2', 40, 0, 0), (14, 'tendril_r2', 0, 0, 0),
            (0, 'tendril_l2', 0, 0, 0), (6, 'tendril_l2', -20, 0, 0), (10, 'tendril_l2', 40, 0, 0), (14, 'tendril_l2', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (6, 'torso', -5, 30, 0), (10, 'torso', 10, -35, 0), (14, 'torso', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (6, 'arm_r', 10, 0, 45), (10, 'arm_r', -70, 30, 10), (14, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (6, 'arm_l', -40, 0, -20), (10, 'arm_l', 20, 0, -40), (14, 'arm_l', 0, 0, 0)],
        # spore_bolts (12, Salve bei 8 ueber dem Kopf): Kopf/Hut nach vorn schleudern, Haende zeigen aufs Ziel
        4: [(0, 'head', 0, 0, 0), (5, 'head', -22, 0, 0), (8, 'head', 18, 0, 0), (12, 'head', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (5, 'torso', -10, 0, 0), (8, 'torso', 10, 0, 0), (12, 'torso', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (5, 'arm_r', -50, 0, 30), (8, 'arm_r', -95, 0, 5), (12, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (5, 'arm_l', -50, 0, -30), (8, 'arm_l', -95, 0, -5), (12, 'arm_l', 0, 0, 0)],
        # brood (sofort, ~24): tief verbeugen, Arme zum Boden gespreizt - Brut steigt auf
        5: [(0, 'torso', 0, 0, 0), (8, 'torso', 20, 0, 0), (18, 'torso', 20, 0, 0), (24, 'torso', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (8, 'arm_r', -45, 0, 35), (18, 'arm_r', -45, 0, 35), (24, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (8, 'arm_l', -45, 0, -35), (18, 'arm_l', -45, 0, -35), (24, 'arm_l', 0, 0, 0),
            (0, 'head', 0, 0, 0), (8, 'head', 5, 0, 0), (18, 'head', 5, 0, 0), (24, 'head', 0, 0, 0)],
    })
