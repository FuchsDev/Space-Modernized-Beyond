"""Schrab Spitter (Eve): gedrungene Kristallkroete mit violetter Warzenhaut, tuerkis leuchtendem Kehlsack, breitem Maul,
Glubschaugen und Schrabidium-Kristallauswuechsen auf dem Ruecken; spuckt Saeurekugeln im Bogen."""
import math
from model import B, C
from paint import mat
from mobs import MOBS

mat('ss_skin', 'flesh', (96, 64, 124), (70, 136, 140))
mat('ss_skin_d', 'scale', (64, 42, 88), (110, 78, 150))
mat('ss_belly', 'scale', (150, 128, 168), (196, 178, 214))
mat('ss_wart', 'plain', (44, 30, 60))
mat('ss_crystal', 'crystal', (24, 170, 168), (150, 255, 236))
mat('ss_crystal_d', 'crystal', (18, 104, 116), (90, 230, 220))
mat('ss_sac', 'glow', (0, 170, 160), (170, 255, 240))
mat('ss_mouth', 'glow', (10, 120, 110), (90, 240, 200))
mat('ss_tongue', 'flesh', (150, 60, 110), (200, 100, 150))
mat('ss_eye', 'eye', (110, 70, 0), (255, 226, 90))
mat('ss_lid', 'scale', (70, 48, 92), (110, 80, 140))
mat('ss_claw', 'bone', (60, 44, 70))
mat('ss_glow', 'glow', (0, 190, 180), (200, 255, 245))


def _ch(x, y, z, w, h, d, m, k=1):
    """Gefaster Block aus drei Wuerfeln (runde Silhouette statt Kiste)."""
    return [C(x, y + k, z + k, w, h - 2 * k, d - 2 * k, m), C(x + k, y, z + k, w - 2 * k, h, d - 2 * k, m),
            C(x + k, y + k, z, w - 2 * k, h - 2 * k, d, m)]


def _mc(cubes):
    return [C(-c.x - c.w, c.y, c.z, c.w, c.h, c.d, c.mat, c.infl) for c in cubes]


_RM = {'leg_l': 'leg_r'}


def _mirror(b, role=None):
    rx, ry, rz = (math.degrees(v) for v in b.rot)
    return B(b.name[:-2] + '_r', (-b.pivot[0], b.pivot[1], b.pivot[2]), (rx, -ry, -rz), _mc(b.cubes),
             [_mirror(ch) for ch in b.children], role=role or _RM.get(b.role, b.role), param=b.param)


def shard(name, pos, rot, h, w=2, m='ss_crystal'):
    """Kristallsaeule mit Sockel und leuchtender Spitze."""
    return B(name, pos, rot, [C(-w / 2, -h, -w / 2, w, h, w, m), C(-0.5, -h - 1.5, -0.5, 1, 2, 1, 'ss_glow'),
                              C(-w / 2 - 0.5, -1, -w / 2 - 0.5, w + 1, 1.5, w + 1, 'ss_skin_d')])

# ── Kopf mit Maul und Kehlsack ──
ss_jaw = B('jaw', (0, 0, 1), (0, 0, 0), [
    C(-6.5, 0, -9.5, 13, 2, 9.5, 'ss_skin'), C(-5.5, -0.6, -9, 11, 1, 8, 'ss_mouth'),
    C(-2, -1, -7.5, 4, 1, 5, 'ss_tongue'),
    C(-5, 2, -8.5, 10, 4, 7, 'ss_sac'), C(-4, 5.5, -7.5, 8, 1, 5, 'ss_sac'),         # Kehlsack
    C(-6, 1.5, -2, 12, 2, 2, 'ss_skin_d')], role='jaw')


def eye_l():
    return B('eye_l', (3.8, -4, -5), (0, 0, 0), [
        C(-2, -2.5, -2, 4, 3, 4, 'ss_lid'), C(-1.5, -2.5, -2.4, 3, 2, 2, 'ss_eye'), C(-2.2, -3.2, -1.5, 4, 1, 3, 'ss_skin_d')])


ss_head = B('head', (0, -1, -6), (10, 0, 0), [
    C(-6.5, -4, -10, 13, 4, 10, 'ss_skin'),                                         # Oberkopf
    C(-5.5, -4.8, -9, 11, 1, 8, 'ss_skin_d'),                                        # Schaedelplatte
    C(-7, -1, -9.5, 14, 1, 9, 'ss_skin_d'),                                          # Oberlippe
    C(-2, -3.5, -10.5, 1, 1, 1, 'ss_wart'), C(1, -3.5, -10.5, 1, 1, 1, 'ss_wart'),  # Nasenloecher
    C(-6.5, -0.2, -10.2, 13, 1, 1, 'ss_glow'),                                       # Lippenglut
    C(-7.2, -3, -3, 1, 2, 2, 'ss_wart'), C(6.2, -3, -3, 1, 2, 2, 'ss_wart')],       # Ohrdruesen
    children=[ss_jaw, eye_l(), _mirror(eye_l())], role='head')

# ── Beine ──
def front_l(role):
    foot = B('ffoot_l', (0, 5.2, 0), (-10, 0, 14), [
        C(-2, 0, -3, 4, 1, 4, 'ss_skin_d'), C(-2.5, 0.2, -4, 1, 1, 2, 'ss_claw'), C(-0.5, 0.2, -4.5, 1, 1, 2, 'ss_claw'),
        C(1.5, 0.2, -4, 1, 1, 2, 'ss_claw')])
    return B('fleg_l', (6, 2.5, -4.5), (24, 0, -14), [
        C(-1.5, -1, -1.5, 3, 6, 3, 'ss_skin'), C(-1.8, -1.5, -1.8, 3, 3, 3, 'ss_skin_d', 0.2)], children=[foot], role=role)


def hind_l(role):
    foot = B('hfoot_l', (0, 3.5, 0), (35, 0, 0), [
        C(-2, 0, -6, 4, 1, 7, 'ss_skin_d'), C(-2.5, 0.2, -7, 1, 1, 2, 'ss_claw'), C(-0.5, 0.2, -7.5, 1, 1, 2, 'ss_claw'),
        C(1.5, 0.2, -7, 1, 1, 2, 'ss_claw')])
    shin = B('hshin_l', (0, 2, -4), (-35, 0, 0), [C(-1.5, 0, -1.5, 3, 4, 3, 'ss_skin')], children=[foot])
    return B('hleg_l', (7, 1.5, 3), (14, -12, 0), 
        _ch(-2.5, -3, -5, 5, 6, 9, 'ss_skin') + [C(-1, -3.4, -3, 2, 1, 2, 'ss_wart'), C(0, -3.4, 1, 1, 1, 1, 'ss_wart'),
        C(2.1, -1.5, -2.5, 1, 1, 2, 'ss_wart'), C(2.1, 0.5, 1, 1, 1, 1, 'ss_wart')],
        children=[shin], role=role)

# ── Rumpf ──
ss_body = B('body', (0, 16.2, 1.5), (-14, 0, 0),
    _ch(-7.5, -4.5, -7, 15, 9, 15, 'ss_skin') + [
    C(-6.5, 3.5, -6, 13, 2, 12, 'ss_belly'),                                          # Bauch
    C(-5.5, -5.1, -5, 11, 1, 11, 'ss_skin_d'),                                        # Rueckenplatte
    C(-4.5, -5.3, 2, 2, 1, 2, 'ss_wart'), C(3, -5.3, -2, 2, 1, 2, 'ss_wart'), C(-8.1, -2, 3, 1, 2, 2, 'ss_wart'),
    C(7.1, -1, -3, 1, 2, 2, 'ss_wart'), C(-8.1, 0, -4, 1, 1, 1, 'ss_wart'), C(7.1, -3, 4, 1, 1, 1, 'ss_wart')],
    children=[ss_head, front_l('leg_a'), _mirror(front_l('leg_a'), 'leg_b'), hind_l('leg_b'), _mirror(hind_l('leg_b'), 'leg_a'),
              shard('crys_1', (-2, -4.5, -1), (-12, 0, -16), 8, 3),
              shard('crys_2', (2.5, -4.5, 0.5), (12, 0, 24), 6, 2, 'ss_crystal_d'),
              shard('crys_3', (-0.5, -4.5, 4), (38, 0, -6), 6, 3),
              shard('crys_4', (4.5, -4.5, 4.5), (30, 0, 40), 4, 2, 'ss_crystal_d'),
              shard('crys_5', (-5, -4, 1.5), (5, 0, -48), 4),
              shard('crys_6', (0.5, -4.5, -4.5), (-30, 0, 8), 3, 2, 'ss_crystal_d'),
              shard('crys_7', (-3.5, -3.5, 6.5), (55, 0, -30), 3)],
    role='body')

MOBS['schrab_spitter'] = dict(java='schrabSpitter', tex=128, scale=1.15, roots=[ss_body], pscale=9,
    doc='Schrab Spitter: gedrungene Kristallkroete mit violetter Warzenhaut, tuerkis leuchtendem Kehlsack, breitem Maul, '
        'Glubschaugen und Schrabidium-Kristallauswuechsen auf dem Ruecken.',
    poses={
        2: [(0, 'head', 0, 0, 0), (6, 'head', -28, 0, 0), (8, 'head', 18, 0, 0), (11, 'head', 10, 0, 0), (14, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (6, 'jaw', 8, 0, 0), (8, 'jaw', 50, 0, 0), (11, 'jaw', 40, 0, 0), (14, 'jaw', 0, 0, 0),
            (0, 'body', 0, 0, 0), (6, 'body', -10, 0, 0), (8, 'body', 8, 0, 0), (14, 'body', 0, 0, 0),
            (0, 'fleg_r', 0, 0, 0), (6, 'fleg_r', 10, 0, 0), (8, 'fleg_r', -8, 0, 0), (14, 'fleg_r', 0, 0, 0),
            (0, 'fleg_l', 0, 0, 0), (6, 'fleg_l', 10, 0, 0), (8, 'fleg_l', -8, 0, 0), (14, 'fleg_l', 0, 0, 0)],
    })
