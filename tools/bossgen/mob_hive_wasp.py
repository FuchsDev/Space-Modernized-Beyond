"""Hive Wasp (Sporos): pilzbefallene Riesenwespe mit dunkelrotem Chitin, Facettenaugen, vier Fluegeln, Giftstachel
und Pilzhueten, Myzel und Cordyceps-Stiel, die aus Ruecken und Hinterleib wachsen."""
import math
from model import B, C
from paint import mat
from mobs import MOBS

mat('hw_chitin', 'plate', (104, 30, 24), (176, 82, 52))
mat('hw_chitin_d', 'plate', (58, 22, 18), (120, 58, 38))
mat('hw_brown', 'scale', (78, 46, 28), (140, 92, 52))
mat('hw_band', 'scale', (182, 112, 36), (226, 170, 80))
mat('hw_eye', 'eye', (40, 12, 0), (255, 170, 60))
mat('hw_mandible', 'bone', (70, 40, 24))
mat('hw_wing', 'ice', (196, 186, 156), (130, 112, 84))
mat('hw_vein', 'frame', (70, 40, 28))
mat('hw_sting', 'bone', (36, 24, 20))
mat('hw_venom', 'glow', (110, 190, 20), (230, 255, 140))
mat('hw_cap', 'cap', (214, 142, 54), (252, 232, 186))
mat('hw_cap_d', 'cap', (164, 96, 40), (234, 204, 150))
mat('hw_gill', 'gill', (232, 196, 140), (180, 130, 80), glow_col=(255, 180, 60))
mat('hw_stalk', 'fiber', (234, 226, 204))
mat('hw_myc', 'fiber', (210, 200, 178))
mat('hw_spore', 'glow', (220, 110, 20), (255, 226, 130))


def _mc(cubes):
    return [C(-c.x - c.w, c.y, c.z, c.w, c.h, c.d, c.mat, c.infl) for c in cubes]


_ROLE_MIRROR = {'wing_r': 'wing_l'}


def _mirror(b, a='_r', z='_l'):
    rx, ry, rz = (math.degrees(v) for v in b.rot)
    return B(b.name.replace(a, z), (-b.pivot[0], b.pivot[1], b.pivot[2]), (rx, -ry, -rz), _mc(b.cubes),
             [_mirror(ch, a, z) for ch in b.children], role=_ROLE_MIRROR.get(b.role, b.role), param=b.param)


def _ch(x, y, z, w, h, d, m, k=1):
    """Gefaster Block aus drei Wuerfeln."""
    return [C(x, y + k, z + k, w, h - 2 * k, d - 2 * k, m), C(x + k, y, z + k, w - 2 * k, h, d - 2 * k, m),
            C(x + k, y + k, z, w - 2 * k, h - 2 * k, d, m)]


def _shroom(name, pivot, rot, h, r, glow=False):
    """Kleiner Pilz: Stiel, Hut mit Lamellen darunter."""
    return B(name, pivot, rot, [
        C(-0.5, -h, -0.5, 1, h, 1, 'hw_stalk'),
        C(-r + 0.5, -h - 1, -r + 0.5, 2 * r - 1, 1, 2 * r - 1, 'hw_gill'),
        C(-r, -h - 3, -r, 2 * r, 2, 2 * r, 'hw_cap'),
        C(-0.5, -h - 3.6, -0.5, 1, 1, 1, 'hw_spore' if glow else 'hw_cap_d')])


# ── Fluegel (vorn gross, hinten klein) ───────────────────────────────────────────
hw_wing_fr = B('wingf_r', (-2, -3, -1), (0, 22, 22), [
    C(-12, -0.5, -1, 12, 1, 4, 'hw_wing'), C(-12, -0.6, -1.4, 12, 1, 1, 'hw_vein'),
    C(-6, -0.55, 0, 1, 1, 3, 'hw_vein')], role='wing_r')
hw_wing_br = B('wingb_r', (-2, -3, 1.5), (0, 40, 12), [
    C(-9, -0.5, 0, 9, 1, 3, 'hw_wing'), C(-9, -0.6, -0.3, 9, 1, 1, 'hw_vein')], role='wing_r')

# ── Beine (haengen im Flug nach hinten) ─────────────────────────────────────────
def _leg(name, z, rx, rz):
    tarsus = B(name + '_tarsus', (0, 4, 0), (-25, 0, 0), [
        C(-0.5, 0, -1, 1, 2, 1, 'hw_chitin_d')])
    tibia = B(name + '_tibia', (0, 3, 0), (-45, 0, 0), [
        C(-0.5, 0, -0.5, 1, 4, 1, 'hw_brown'), C(-0.7, 1, -0.7, 1, 1, 1, 'hw_myc', 0.2)], children=[tarsus])
    return B(name, (-2.5, 2.5, z), (rx, 0, rz), [
        C(-0.5, -0.5, -0.5, 1, 4, 1, 'hw_chitin')], children=[tibia])


hw_legs = []
for i, (z, rx) in enumerate(((-2, 10), (0.5, 25), (3, 40))):
    lg = _leg('leg%d_r' % i, z, rx, 30)
    hw_legs += [lg, _mirror(lg)]

# ── Kopf ────────────────────────────────────────────────────────────────────────
hw_ant_r = B('antenna_r', (-1.2, -3, -7.5), (40, 25, 0), [
    C(-0.5, -5, -0.5, 1, 5, 1, 'hw_chitin_d'), C(-0.5, -6, -2.5, 1, 1, 3, 'hw_chitin_d')], role='tentacle', param=0)
hw_mand_r = B('mandible_r', (-1.5, 1.5, -8), (0, 15, 0), [
    C(-1, 0, -2, 1, 2, 2, 'hw_mandible'), C(-0.5, 1, -3, 1, 1, 1, 'hw_mandible')])
hw_head = B('head', (0, -1, -3), (4, 0, 0),
    _ch(-3.5, -3.5, -8, 7, 7, 5, 'hw_chitin') + [
    C(-4.4, -3, -7.5, 2, 5, 4, 'hw_eye'), C(2.4, -3, -7.5, 2, 5, 4, 'hw_eye'),        # Facettenaugen
    C(-1, -4.1, -6.5, 2, 1, 1, 'hw_spore'), C(-1.5, -3.8, -5.5, 3, 1, 1, 'hw_eye'),  # Punktaugen
    C(-2, -1.5, -8.6, 4, 3, 1, 'hw_band'), C(-1, -2.5, -8.4, 2, 1, 1, 'hw_band'),    # Gesichtsschild
    C(-2, 2.5, -7.5, 4, 1, 3, 'hw_chitin_d'),
    C(1.5, -3.9, -4.5, 2, 1, 2, 'hw_myc'), C(1.8, -4.7, -4.1, 1, 1, 1, 'hw_spore')],   # Myzelfleck am Scheitel
    children=[hw_ant_r, _mirror(hw_ant_r), hw_mand_r, _mirror(hw_mand_r)], role='head')

for _c in hw_head.cubes: _c.z += 3                                   # Kopf sitzt direkt am Thorax
for _b in hw_head.children: _b.pivot = (_b.pivot[0], _b.pivot[1], _b.pivot[2] + 3)

# ── Hinterleib mit Stachel ───────────────────────────────────────────────────────
hw_stinger = B('stinger', (0, 0.5, 12.5), (15, 0, 0), [
    C(-1, -1, -0.5, 2, 2, 2, 'hw_chitin_d'), C(-0.5, -0.5, 1.5, 1, 1, 3, 'hw_sting'),
    C(-0.5, -0.3, 4.4, 1, 1, 1, 'hw_venom')])
hw_abdomen = B('abdomen', (0, 0.5, 5), (-28, 0, 0),
    _ch(-3, -3, 0, 6, 6, 3, 'hw_chitin') +
    _ch(-4, -3.5, 3, 8, 7, 4, 'hw_brown') +
    _ch(-3.5, -3, 7, 7, 6, 3, 'hw_chitin') + [
    C(-3.5, -3, 2.5, 7, 6, 1, 'hw_band', 0.25), C(-4, -3.5, 6.5, 8, 7, 1, 'hw_band', 0.15),   # Ringe
    C(-2.5, -2, 10, 5, 4, 2, 'hw_band'), C(-1.5, -1, 11.5, 3, 3, 1, 'hw_chitin_d'),
    C(-4.3, -1, 4, 1, 3, 2, 'hw_myc'), C(-4.4, 0, 4.6, 1, 1, 1, 'hw_spore'),          # Myzelflecken
    C(2.8, -2, 8, 1, 2, 2, 'hw_myc'), C(-1, 3.2, 5, 3, 1, 2, 'hw_myc'),
    C(-4.6, -2.5, 5, 2, 1, 3, 'hw_cap_d'), C(-5, -2, 5.5, 1, 2, 2, 'hw_gill'),       # Konsolenpilz seitlich
    C(3.4, 0, 3.5, 2, 1, 3, 'hw_cap_d')],
    children=[hw_stinger, _shroom('shroom_a', (1, -3.5, 4.5), (-10, 0, 20), 2, 1.5, True),
              _shroom('shroom_b', (-1.5, -3.5, 8), (15, 0, -15), 1, 1)])

# ── Thorax ──────────────────────────────────────────────────────────────────────
hw_cordyceps = B('cordyceps', (0.5, -3, 1), (-15, 0, 10), [
    C(-0.5, -6, -0.5, 1, 6, 1, 'hw_stalk'), C(-1, -9, -1, 2, 3, 2, 'hw_spore'), C(-0.5, -10, -0.5, 1, 1, 1, 'hw_cap')],
    role='tentacle', param=3)
hw_body = B('body', (0, 14, 0), (0, 0, 0),
    _ch(-3, -3, -3, 6, 6, 8, 'hw_chitin') + [
    C(-2.5, -3.6, -2, 5, 1, 5, 'hw_chitin_d'),                          # Rueckenschild
    C(-3.4, -1, -1.5, 1, 2, 4, 'hw_chitin_d'), C(2.4, -1, -1.5, 1, 2, 4, 'hw_chitin_d'),   # Flanken
    C(-1.5, 2.5, -2, 3, 1, 5, 'hw_brown'),                              # Brust
    C(-1, -1, 4.5, 2, 2, 2, 'hw_chitin_d'),                             # Wespentaille
    C(-2, -4.2, 0, 2, 1, 2, 'hw_myc'), C(0.5, -4, 2.5, 2, 1, 2, 'hw_myc'),     # Myzel
    C(-2.5, -4.6, 1, 1, 1, 1, 'hw_spore'), C(1.5, -4.5, 3, 1, 1, 1, 'hw_spore')],
    children=[hw_head, hw_abdomen, hw_wing_fr, hw_wing_br, _mirror(hw_wing_fr), _mirror(hw_wing_br), hw_cordyceps,
              _shroom('shroom_c', (-1.5, -3.5, -1), (-15, 0, -25), 2, 1.5),
              _shroom('shroom_d', (2, -3.5, 1.5), (10, 0, 30), 1, 1, True)] + hw_legs, role='bob')

MOBS['hive_wasp'] = dict(java='hiveWasp', tex=128, scale=0.75, roots=[hw_body],
    doc='Hive Wasp: pilzbefallene Riesenwespe mit dunkelrotem Chitin, Facettenaugen, vier Fluegeln, Giftstachel '
        'und Pilzhueten, Myzel und leuchtendem Cordyceps-Stiel auf Ruecken und Hinterleib.',
    poses={
        # Stich (10): Hinterleib unter dem Koerper nach vorn schwingen, bei 6 zustechen
        1: [(0, 'abdomen', 0, 0, 0), (4, 'abdomen', -60, 0, 0), (6, 'abdomen', -85, 0, 0), (10, 'abdomen', 0, 0, 0),
            (0, 'stinger', 0, 0, 0), (4, 'stinger', -25, 0, 0), (6, 'stinger', -40, 0, 0), (10, 'stinger', 0, 0, 0),
            (0, 'body', 0, 0, 0), (4, 'body', -15, 0, 0), (6, 'body', 5, 0, 0), (10, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (4, 'head', 15, 0, 0), (10, 'head', 0, 0, 0)],
    })
