"""Deep Angler (Laythe): Tiefsee-Anglerfisch mit rundem Schaedel, riesigem Unterbiss-Maul voller Nadelzaehne,
Leuchtangel ueber dem Kopf, Brustflossen, Biolumineszenzpunkten und Schwanzflosse."""
import math
from model import B, C
from paint import mat
from mobs import MOBS

mat('da_skin', 'scale', (32, 46, 72), (70, 102, 140))
mat('da_skin_d', 'scale', (18, 26, 44), (42, 62, 94))
mat('da_belly', 'scale', (70, 82, 104), (126, 140, 162))
mat('da_mouth', 'flesh', (84, 16, 38), (150, 44, 76))
mat('da_tooth', 'bone', (232, 228, 206))
mat('da_fin', 'fiber', (36, 74, 110))
mat('da_ray', 'bone', (84, 110, 140))
mat('da_stalk', 'flesh', (28, 44, 72), (70, 160, 200))
mat('da_lure', 'glow', (40, 200, 255), (240, 255, 255))
mat('da_glow', 'glow', (0, 130, 170), (150, 255, 250))
mat('da_eye', 'eye', (4, 34, 44), (150, 255, 236))


def _ch(x, y, z, w, h, d, m, k=1):
    """Gefaster Block aus drei Wuerfeln (runde Silhouette statt Kiste)."""
    return [C(x, y + k, z + k, w, h - 2 * k, d - 2 * k, m), C(x + k, y, z + k, w - 2 * k, h, d - 2 * k, m),
            C(x + k, y + k, z, w - 2 * k, h - 2 * k, d, m)]


def _mc(cubes):
    return [C(-c.x - c.w, c.y, c.z, c.w, c.h, c.d, c.mat, c.infl) for c in cubes]


def _mirror(b):
    rx, ry, rz = (math.degrees(v) for v in b.rot)
    return B(b.name[:-2] + '_r', (-b.pivot[0], b.pivot[1], b.pivot[2]), (rx, -ry, -rz), _mc(b.cubes),
             [_mirror(ch) for ch in b.children], role={'wing_l': 'wing_r'}.get(b.role, b.role), param=b.param)

# ── Leuchtangel ──
da_bulb = B('lure_bulb', (0, -4, 0), (-60, 0, 0), [
    C(-1.5, -3, -1.5, 3, 3, 3, 'da_lure', 0.3), C(-1, -3.8, -1, 2, 1, 2, 'da_stalk'),
    C(-0.5, 0, -0.5, 1, 2, 1, 'da_glow')])
da_lure_tip = B('lure_tip', (0, -5, 0), (55, 0, 0), [C(-0.5, -4, -0.5, 1, 4, 1, 'da_stalk', 0.1)],
                children=[da_bulb], role='tentacle', param=1.5)
da_lure = B('lure', (0, -6, -4), (-20, 0, 0), [
    C(-1, -5, -1, 2, 5, 2, 'da_stalk'), C(-1.5, -1, -1.5, 3, 1, 3, 'da_skin_d'), C(-0.5, -3.5, 1, 1, 2, 1, 'da_ray')],
    children=[da_lure_tip], role='tentacle', param=0.5)

# ── Unterkiefer (Unterbiss) ──
_LOWER = [(-5.5, 3), (-3.5, 4), (-1.5, 3), (0.5, 3), (2.5, 4), (4.5, 3)]
da_jaw = B('jaw', (0, 1.5, 0), (6, 0, 0),
    _ch(-6.5, 0, -11, 13, 5, 11, 'da_skin') + [
    C(-5.5, -0.5, -10, 11, 1, 9, 'da_mouth'),                                       # Mundboden
    C(-5.5, 4, -10, 11, 2, 9, 'da_belly'),                                          # Kehle
    C(-1, 5.5, -11, 2, 1, 2, 'da_glow')]                                            # Kinnlicht
    + [C(x, -h, -11.2, 1, h, 1, 'da_tooth') for x, h in _LOWER]
    + [C(-6.2, -2, -8, 1, 2, 1, 'da_tooth'), C(5.2, -2, -8, 1, 2, 1, 'da_tooth'),
       C(-6.2, -2, -5, 1, 2, 1, 'da_tooth'), C(5.2, -2, -5, 1, 2, 1, 'da_tooth')],
    role='jaw')

# ── Brustflossen ──
def fin_l():
    return B('fin_l', (5.5, 1, -1), (0, 30, 20), [
        C(0, -0.5, 0, 6, 1, 5, 'da_fin'), C(0, -0.8, 0, 6, 1, 1, 'da_ray', 0.1), C(0, -0.8, 2.5, 5, 1, 1, 'da_ray'),
        C(5, -0.5, 1, 2, 1, 4, 'da_fin'), C(-0.5, -1, -0.5, 2, 2, 2, 'da_skin_d')], role='wing_l')

# ── Schwanz ──
da_tailfin = B('tail_fin', (0, 0, 4), (0, 0, 0), [
    C(-0.5, -6, 0, 1, 12, 3, 'da_fin'), C(-0.5, -7, 2, 1, 3, 3, 'da_fin'), C(-0.5, 4, 2, 1, 3, 3, 'da_fin'),
    C(-1, -5.5, -0.5, 2, 1, 4, 'da_ray'), C(-1, 4.5, -0.5, 2, 1, 4, 'da_ray'), C(-1, -0.5, 0, 2, 1, 4, 'da_ray'),
    C(-1, -2, -0.5, 2, 4, 1, 'da_skin_d')], role='chain', param=3)
da_tail2 = B('tail_2', (0, 0, 5), (0, 0, 0), [
    C(-2.5, -3, 0, 5, 6, 4, 'da_skin'), C(-2, 2.5, 0.5, 4, 1, 3, 'da_belly'), C(-0.5, -5, 0, 1, 2, 4, 'da_fin'),
    C(-2.7, -1, 1, 1, 1, 1, 'da_glow'), C(1.7, -1, 1, 1, 1, 1, 'da_glow')], children=[da_tailfin], role='chain', param=2)
da_tail1 = B('tail_1', (0, -0.5, 3.5), (0, 0, 0), [
    C(-4, -4.5, 0, 8, 9, 5, 'da_skin'), C(-3.5, 3.5, 0.5, 7, 1, 4, 'da_belly'), C(-0.5, -6.5, 0.5, 1, 2, 4, 'da_fin'),
    C(-4.3, -1, 1, 1, 1, 1, 'da_glow'), C(3.3, -1, 1, 1, 1, 1, 'da_glow'), C(-4.3, 1, 3, 1, 1, 1, 'da_glow'), C(3.3, 1, 3, 1, 1, 1, 'da_glow')],
    children=[da_tail2], role='chain', param=1)

# ── Rueckenstacheln ──
def spine(i, z, h, rx):
    return B('spine_%d' % i, (0, -6, z), (rx, 0, 0), [C(-0.5, -h, -0.5, 1, h, 1, 'da_ray'), C(-0.5, -h, 0.5, 1, h - 1, 2, 'da_fin')])

# ── Rumpf (Kopf und Koerper in einem) ──
_UPPER = [(-5.5, 3), (-3.5, 2), (-1.5, 3), (0.5, 3), (2.5, 2), (4.5, 3)]
da_body = B('body', (0, 16.5, 0), (0, 0, 0),
    _ch(-6, -6, -9, 12, 8, 13, 'da_skin') + [
    C(-5, -6.6, -7, 10, 1, 10, 'da_skin_d'),                                        # Schaedeldach
    C(-6.5, -4, -5, 13, 3, 6, 'da_skin_d'),                                         # Kiemenwuelste
    C(-6.6, -2.5, -3.5, 1, 2, 3, 'da_glow'), C(5.6, -2.5, -3.5, 1, 2, 3, 'da_glow'), # Kiemenlicht
    C(-6.5, -4.5, -7.8, 2, 2, 2, 'da_eye'), C(4.5, -4.5, -7.8, 2, 2, 2, 'da_eye'),   # Augen
    C(-5.5, 1.5, -8, 11, 1, 10, 'da_mouth'),                                         # Gaumen
    C(-4, -2, -9.5, 8, 2, 1, 'da_skin_d'),                                           # Oberlippe
    C(-6.6, 0, 0, 1, 1, 1, 'da_glow'), C(5.6, 0, 0, 1, 1, 1, 'da_glow'), C(-6.6, -3, 2, 1, 1, 1, 'da_glow'), C(5.6, -3, 2, 1, 1, 1, 'da_glow')]
    + [C(x, 2, -9, 1, h, 1, 'da_tooth') for x, h in _UPPER],
    children=[da_jaw, da_lure, fin_l(), _mirror(fin_l()), da_tail1, spine(1, -1, 3, -30), spine(2, 1.5, 3, -40)],
    role='bob')

MOBS['deep_angler'] = dict(java='deepAngler', tex=128, scale=1.0, roots=[da_body], pscale=10,
    doc='Deep Angler: Tiefsee-Anglerfisch mit Unterbiss-Maul voller Nadelzaehne, Leuchtangel ueber dem Kopf, Brustflossen, '
        'Biolumineszenzpunkten und Schwanzflosse.',
    poses={
        1: [(0, 'jaw', 0, 0, 0), (3, 'jaw', 50, 0, 0), (6, 'jaw', -4, 0, 0), (10, 'jaw', 0, 0, 0),
            (0, 'body', 0, 0, 0), (3, 'body', -12, 0, 0), (6, 'body', 10, 0, 0), (10, 'body', 0, 0, 0),
            (0, 'lure', 0, 0, 0), (3, 'lure', -20, 0, 0), (6, 'lure', 20, 0, 0), (10, 'lure', 0, 0, 0),
            (0, 'tail_1', 0, 0, 0), (3, 'tail_1', 0, 20, 0), (6, 'tail_1', 0, -25, 0), (10, 'tail_1', 0, 0, 0),
            (0, 'fin_r', 0, 0, 0), (3, 'fin_r', 0, 0, 30), (6, 'fin_r', 0, 0, -20), (10, 'fin_r', 0, 0, 0),
            (0, 'fin_l', 0, 0, 0), (3, 'fin_l', 0, 0, -30), (6, 'fin_l', 0, 0, 20), (10, 'fin_l', 0, 0, 0)],
    })
