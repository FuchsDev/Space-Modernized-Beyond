"""Core Abomination (Dres): lebender RBMK-Reaktorkern - achteckiger Graphitkoerper, schraege Brenn-/Steuerstaebe,
tropfendes Corium, gluehender Schlund vorn, vier Kabel-Tentakel als Stuetzen, brodelnder Deckel mit Graphitbrocken."""
import math
from model import B, C
from paint import mat
from specs import BOSSES

mat('ca_graphite', 'graphite', (62, 64, 66))
mat('ca_graphite_l', 'graphite', (92, 94, 92))
mat('ca_frame', 'frame', (48, 50, 46))
mat('ca_steel', 'plate', (128, 134, 124), (184, 190, 176))
mat('ca_steel_d', 'plate', (78, 84, 78), (130, 138, 126))
mat('ca_rust', 'rust', (104, 98, 88), (150, 76, 34))
mat('ca_hazard', 'hazard', (230, 190, 30))
mat('ca_pipe', 'pipe', (140, 146, 132), (84, 90, 80))
mat('ca_cable', 'pipe', (54, 58, 54), (210, 170, 30))
mat('ca_corium', 'crust', (50, 42, 36), glow_col=(120, 255, 60))
mat('ca_corium_o', 'crust', (58, 40, 30), glow_col=(255, 140, 30))
mat('ca_glow', 'glow', (40, 170, 30), (215, 255, 160))
mat('ca_glow_o', 'glow', (210, 80, 10), (255, 220, 120))
mat('ca_eye', 'eye', (10, 60, 10), (190, 255, 120))
mat('ca_flesh', 'flesh', (96, 108, 64), (60, 210, 40))
mat('ca_teeth', 'bone', (190, 196, 170))

R = 16   # Apothem des Achtecks (Aussenflaeche der Saeulen)
H = 19   # Wandhoehe

def wall(k):
    """Seitenwand k (ry = k*45): Graphitsaeulen unterschiedlicher Hoehe, Corium-Rinnsale, Stutzen."""
    cubes = []
    if k != 0:
        hs = [(H, 0), (H + 3, 1), (H - 1, 0)] if k % 2 else [(H + 1, 0), (H - 1, 1), (H + 2, 0)]
        xs = [(-6.5, 5), (-1.5, 3), (1.5, 5)]
        for (x, w), (h, out) in zip(xs, hs):
            cubes.append(C(x, -h, -R - out, w, h, 5 + out, 'ca_graphite_l' if (k * 3 + int(x)) % 4 == 0 else 'ca_graphite'))
        if k % 2: cubes.append(C(-7, -7, -R - 1, 14, 2, 2, 'ca_rust'))          # Spannband
    if k in (2, 6):
        cubes.append(C(-5 if k == 2 else 3, -H, -R - 1.5, 2, 13, 1, 'ca_corium_o'))   # Rinnsal
        cubes.append(C(-3.5, -5, -R - 3, 7, 5, 3, 'ca_steel'))                        # Kuehlstutzen
    if k in (3, 5):   # Wucherungen zwischen den Saeulen
        cubes.append(C(-4, -16, -R - 3, 6, 5, 3, 'ca_flesh'))
        cubes.append(C(-2.5, -17.5, -R - 4, 3, 3, 2, 'ca_flesh', infl=0.3))
        cubes.append(C(-1.5, -14, -R - 4.5, 1, 1, 1, 'ca_eye', infl=0.3))
    if k == 4:
        cubes.append(C(-4, -H - 1, -R - 2, 3, 16, 1, 'ca_corium_o'))
        cubes.append(C(2, -H, -R - 1.5, 2, 9, 1, 'ca_corium'))
    children = []
    if k in (2, 4, 6):   # schraeg herausragender Brennstab
        children.append(B('wrod_%d' % k, (2 if k != 4 else -2, -H + 3, -R + 1), (55, 0, 10 if k == 2 else -10), [
            C(-1, -12, -1, 2, 12, 2, 'ca_pipe'), C(-1.5, -14, -1.5, 3, 2, 3, 'ca_glow')]))
    return B('wall_%d' % k, (0, 0, 0), (0, k * 45, 0), cubes, children)

def tentacle(k, side, front):
    """Kabel-Tentakel aus der Diagonalwand k; Segmente laufen lokal nach -z und stuetzen sich am Boden ab."""
    n = '%s%s' % ('f' if front else 'b', 'r' if side < 0 else 'l')
    a = math.radians(k * 45)
    px, pz = -math.sin(a) * (R - 1), -math.cos(a) * (R - 1)
    claw = B('claw_' + n, (0, 0, -7), (45, 0, 0), [
        C(-2.5, -2.5, -3, 5, 5, 3, 'ca_steel_d'), C(-1.5, -1.5, -4, 3, 3, 1, 'ca_glow'),
        C(-3, -1, -7, 1, 2, 4, 'ca_frame'), C(2, -1, -7, 1, 2, 4, 'ca_frame')])
    s3 = B('tent3_' + n, (0, 0, -8), (35, 0, 0), [C(-1.5, -1.5, -7, 3, 3, 7, 'ca_cable')],
           children=[claw], role='tentacle', param=k * 0.8 + 1.0)
    s2 = B('tent2_' + n, (0, 0, -9), (30, 0, 0), [C(-2, -2, -8, 4, 4, 8, 'ca_cable'), C(-2.5, -2.5, -1, 5, 5, 2, 'ca_steel_d')],
           children=[s3], role='tentacle', param=k * 0.8)
    return B('tent_' + n, (px, -9, pz), (-15, k * 45, 0), [
        C(-3.5, -3.5, -1, 7, 7, 3, 'ca_rust'), C(-2.5, -2.5, -9, 5, 5, 8, 'ca_pipe')], children=[s2])

# ── Schlund (vorn) ──
ca_jaw = B('jaw', (0, -3, -13), (0, 0, 0), [
    C(-7, 0, -7, 14, 3, 8, 'ca_steel'), C(-7.5, 1, -7.5, 15, 1, 4, 'ca_hazard'), C(-5, -1, -6, 10, 1, 5, 'ca_glow'),
    C(-6, -2, -6.5, 1, 2, 1, 'ca_teeth'), C(-3, -3, -6.5, 1, 3, 1, 'ca_teeth'), C(2, -3, -6.5, 1, 3, 1, 'ca_teeth'), C(5, -2, -6.5, 1, 2, 1, 'ca_teeth')],
    role='jaw')

ca_maw = B('maw', (0, 0, 0), (0, 0, 0), [
    C(-9, -17, -20, 18, 5, 8, 'ca_steel'), C(-9.5, -13, -20.5, 19, 1, 3, 'ca_hazard'),            # Oberlippe/Sturz
    C(-10, -12, -19, 3, 10, 6, 'ca_steel_d'), C(7, -12, -19, 3, 10, 6, 'ca_steel_d'),           # Wangen
    C(-7, -12, -14, 14, 10, 1, 'ca_glow'),                                                       # Glut im Rachen
    C(-5, -12, -18.5, 1, 4, 1, 'ca_teeth'), C(-2.5, -12, -18.5, 1, 5, 1, 'ca_teeth'), C(1.5, -12, -18.5, 1, 5, 1, 'ca_teeth'),
    C(4, -12, -18.5, 1, 4, 1, 'ca_teeth'),
    C(-6.5, -21, -18, 4, 3, 2, 'ca_eye'), C(2.5, -21, -18, 4, 3, 2, 'ca_eye'), C(-1.5, -23, -18.5, 3, 3, 2, 'ca_eye'),
    C(-7, -22, -17, 14, 5, 3, 'ca_graphite'),
    C(-10, -3, -20, 20, 3, 7, 'ca_corium_o')],                                                   # Corium-Kinn
    children=[ca_jaw])

# ── Deckel: brodelnder Kern, schraege Staebe (hebt sich, Rolle bob) ──
def rod(name, pos, rot, fuel=True, length=14):
    if fuel:
        cubes = [C(-1, -length, -1, 2, length, 2, 'ca_pipe'), C(-1.5, -length - 2, -1.5, 3, 2, 3, 'ca_glow')]
    else:
        cubes = [C(-1, -length, -1, 2, length, 2, 'ca_graphite_l'), C(-1.5, -length + 3, -1.5, 3, 2, 3, 'ca_hazard')]
    return B(name, pos, rot, cubes)

def chunk(i, size):
    a = i * 2 * math.pi / 6
    return B('chunk_%d' % i, (math.cos(a) * 18, -12, math.sin(a) * 18), (30 + i * 10, i * 25, 20), [
        C(-size / 2, -size / 2, -size / 2, size, size, size, 'ca_corium' if i == 2 else 'ca_graphite_l')], role='orbit', param=i)

ca_lid = B('lid', (0, -H - 1, 0), (0, 0, 0), [
    C(-13, -2, -6, 26, 3, 12, 'ca_steel'), C(-6, -2, -13, 12, 3, 26, 'ca_steel'), C(-10, -2, -10, 20, 3, 20, 'ca_steel_d'),
    C(-8, -3, -8, 16, 1, 16, 'ca_corium'), C(-6, -4, -6, 12, 1, 12, 'ca_glow'),
    C(-3, -7, -2, 4, 4, 4, 'ca_glow'), C(2, -6, -5, 3, 3, 3, 'ca_glow'), C(1, -5, 2, 2, 2, 2, 'ca_glow_o')],
    children=[rod('rod_0', (-9, -2, -5), (15, 0, -30), True, 15), rod('rod_1', (8, -2, -6), (25, 0, 25), True, 12),
              rod('rod_2', (-7, -2, 8), (-30, 0, -15), False, 16), rod('rod_3', (8, -2, 7), (-20, 0, 30), True, 17),
              rod('rod_4', (0, -2, 10), (-35, 0, 0), False, 11),
              chunk(0, 4), chunk(2, 3), chunk(4, 4)],
    role='bob')

ca_core = B('core', (0, 21, 0), (0, 0, 0), [
    C(-10, -H, -10, 20, H, 20, 'ca_frame')],
    children=[wall(k) for k in range(8)] + [ca_maw, ca_lid,
              tentacle(1, -1, True), tentacle(7, 1, True), tentacle(3, -1, False), tentacle(5, 1, False)],
    role='body')

# Coriumlache am Boden (statisch)
ca_pool = B('pool', (0, 24, 0), (0, 0, 0), [
    C(-17, -2, -16, 34, 2, 32, 'ca_corium'), C(-21, -1, -9, 5, 1, 16, 'ca_corium_o'), C(16, -1, -11, 5, 1, 14, 'ca_corium_o'),
    C(-10, -1, -22, 20, 1, 7, 'ca_corium_o')])

BOSSES['core_abomination'] = dict(java='coreAbomination', tex=256, scale=1.45, hitbox=(3.0, 3.2), roots=[ca_pool, ca_core],
    doc='Core Abomination: lebender RBMK-Reaktorkern aus Graphitsaeulen mit schraegen Brenn- und Steuerstaeben, '
        'tropfendem Corium, gluehendem Schlund, vier Kabel-Tentakeln und brodelndem Deckel mit kreisenden Graphitbrocken.',
    # Angriffe: rad_pulse(22), sludge(10), toxic_pool(1), meltdown(44)
    poses={
        0: [(0, 'core', 6, 0, 0), (0, 'lid', 4, 0, 5), (0, 'jaw', -6, 0, 0),
            (0, 'tent_fr', 10, 0, 0), (0, 'tent_fl', 10, 0, 0), (0, 'tent_br', 4, 0, 0), (0, 'tent_bl', 4, 0, 0),
            (0, 'tent3_fr', 30, 0, 0), (0, 'tent3_fl', 30, 0, 0), (0, 'tent3_br', 30, 0, 0), (0, 'tent3_bl', 30, 0, 0)],
        1: [(0, 'core', 0, 0, 0), (14, 'core', -8, 0, 0), (19, 'core', -8, 0, 0), (21, 'core', 6, 0, 0), (28, 'core', 0, 0, 0),
            (0, 'tent_fr', 0, 0, 0), (14, 'tent_fr', -45, 0, 0), (19, 'tent_fr', -45, 0, 0), (21, 'tent_fr', 15, 0, 0), (28, 'tent_fr', 0, 0, 0),
            (0, 'tent_fl', 0, 0, 0), (14, 'tent_fl', -45, 0, 0), (19, 'tent_fl', -45, 0, 0), (21, 'tent_fl', 15, 0, 0), (28, 'tent_fl', 0, 0, 0),
            (0, 'tent_br', 0, 0, 0), (14, 'tent_br', -45, 0, 0), (19, 'tent_br', -45, 0, 0), (21, 'tent_br', 15, 0, 0), (28, 'tent_br', 0, 0, 0),
            (0, 'tent_bl', 0, 0, 0), (14, 'tent_bl', -45, 0, 0), (19, 'tent_bl', -45, 0, 0), (21, 'tent_bl', 15, 0, 0), (28, 'tent_bl', 0, 0, 0),
            (0, 'lid', 0, 0, 0), (14, 'lid', -6, 0, 0), (19, 'lid', -6, 0, 0), (21, 'lid', 4, 0, 0), (28, 'lid', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (14, 'jaw', 25, 0, 0), (21, 'jaw', 35, 0, 0), (28, 'jaw', 0, 0, 0)],
        2: [(0, 'core', 0, 0, 0), (4, 'core', -12, 0, 0), (6, 'core', 10, 0, 0), (12, 'core', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (4, 'jaw', 15, 0, 0), (6, 'jaw', 45, 0, 0), (12, 'jaw', 0, 0, 0),
            (0, 'tent_fr', 0, 0, 0), (4, 'tent_fr', -20, -15, 0), (6, 'tent_fr', 5, 0, 0), (12, 'tent_fr', 0, 0, 0),
            (0, 'tent_fl', 0, 0, 0), (4, 'tent_fl', -20, 15, 0), (6, 'tent_fl', 5, 0, 0), (12, 'tent_fl', 0, 0, 0)],
        3: [(0, 'core', 0, 0, 0), (3, 'core', 6, 0, 0), (9, 'core', 0, 0, 0),
            (0, 'tent_fr', 0, 0, 0), (3, 'tent_fr', -35, 0, 0), (6, 'tent_fr', 20, 0, 0), (10, 'tent_fr', 0, 0, 0),
            (0, 'tent_fl', 0, 0, 0), (3, 'tent_fl', -35, 0, 0), (6, 'tent_fl', 20, 0, 0), (10, 'tent_fl', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (3, 'jaw', 30, 0, 0), (10, 'jaw', 0, 0, 0)],
        4: [(0, 'core', 0, 0, 0), (8, 'core', -6, 0, 4), (14, 'core', -6, 0, -4), (20, 'core', -8, 0, 5), (26, 'core', -8, 0, -5),
            (32, 'core', -10, 0, 6), (38, 'core', -10, 0, -6), (40, 'core', 10, 0, 0), (44, 'core', 4, 0, 0), (50, 'core', 0, 0, 0),
            (0, 'lid', 0, 0, 0), (8, 'lid', -8, 0, 6), (20, 'lid', 8, 0, -6), (32, 'lid', -10, 0, 8), (38, 'lid', 10, 0, -8), (40, 'lid', 0, 0, 0), (50, 'lid', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (8, 'jaw', 30, 0, 0), (38, 'jaw', 40, 0, 0), (41, 'jaw', 50, 0, 0), (50, 'jaw', 0, 0, 0),
            (0, 'tent_fr', 0, 0, 0), (10, 'tent_fr', -60, 0, 0), (38, 'tent_fr', -65, 0, 0), (41, 'tent_fr', 20, 0, 0), (50, 'tent_fr', 0, 0, 0),
            (0, 'tent_fl', 0, 0, 0), (10, 'tent_fl', -60, 0, 0), (38, 'tent_fl', -65, 0, 0), (41, 'tent_fl', 20, 0, 0), (50, 'tent_fl', 0, 0, 0),
            (0, 'tent_br', 0, 0, 0), (10, 'tent_br', -60, 0, 0), (38, 'tent_br', -65, 0, 0), (41, 'tent_br', 20, 0, 0), (50, 'tent_br', 0, 0, 0),
            (0, 'tent_bl', 0, 0, 0), (10, 'tent_bl', -60, 0, 0), (38, 'tent_bl', -65, 0, 0), (41, 'tent_bl', 20, 0, 0), (50, 'tent_bl', 0, 0, 0),
            (0, 'tent2_fr', 0, 0, 0), (10, 'tent2_fr', -30, 0, 0), (41, 'tent2_fr', 10, 0, 0), (50, 'tent2_fr', 0, 0, 0),
            (0, 'tent2_fl', 0, 0, 0), (10, 'tent2_fl', -30, 0, 0), (41, 'tent2_fl', 10, 0, 0), (50, 'tent2_fl', 0, 0, 0)],
    })
