"""Magma Hound (Moho): vierbeiniger Lavahund aus Basalt mit Lavakruste, gluehendem Maul und Glutaugen,
Rueckenkamm aus Obsidianzacken und Schwanz mit Glutspitze."""
import math
from model import B, C
from paint import mat
from mobs import MOBS

mat('mh_basalt', 'rock', (78, 72, 76))
mat('mh_basalt_d', 'rock', (52, 48, 54))
mat('mh_crust', 'crust', (60, 36, 30), glow_col=(255, 112, 20))
mat('mh_crust_d', 'crust', (42, 28, 26), glow_col=(255, 80, 10))
mat('mh_obs', 'obsidian', (28, 20, 34), (138, 92, 180))
mat('mh_tooth', 'bone', (236, 218, 184))
mat('mh_claw', 'bone', (66, 52, 50))
mat('mh_glow', 'glow', (255, 84, 8), (255, 236, 130))
mat('mh_maw', 'glow', (230, 50, 0), (255, 196, 70))
mat('mh_eye', 'eye', (170, 50, 0), (255, 252, 160))


def _ch(x, y, z, w, h, d, m, k=1):
    """Gefaster Block aus drei Wuerfeln (runde Silhouette statt Kiste)."""
    return [C(x, y + k, z + k, w, h - 2 * k, d - 2 * k, m), C(x + k, y, z + k, w - 2 * k, h, d - 2 * k, m),
            C(x + k, y + k, z, w - 2 * k, h - 2 * k, d, m)]


# ── Kopf ──
mh_jaw = B('jaw', (0, 0.5, -4.5), (8, 0, 0), [
    C(-2.5, 0, -6.5, 5, 2, 7, 'mh_basalt_d'), C(-2, -0.6, -6, 4, 1, 6, 'mh_maw'),
    C(-2.5, -1, -6.5, 1, 1, 1, 'mh_tooth'), C(1.5, -1, -6.5, 1, 1, 1, 'mh_tooth'),
    C(-2.5, -1, -3.5, 1, 1, 1, 'mh_tooth'), C(1.5, -1, -3.5, 1, 1, 1, 'mh_tooth'),
    C(-1.5, 2, -4, 3, 1, 4, 'mh_crust_d')], role='jaw')


def ear(side):
    n = 'r' if side < 0 else 'l'
    return B('ear_' + n, (side * 2.5, -3.5, -1.5), (-45, 0, side * 20), [
        C(-1, -2, -1, 2, 2, 2, 'mh_obs'), C(-0.5, -3.5, -0.5, 1, 2, 1, 'mh_obs')])


mh_head = B('head', (0, -3, -9), (10, 0, 0), [
    C(-3.5, -4, -5, 7, 5, 6, 'mh_basalt'), C(-3, -4.4, -4, 6, 1, 5, 'mh_crust'),                                            # Schaedel
    C(-4, -3, -3, 1, 3, 3, 'mh_basalt'), C(3, -3, -3, 1, 3, 3, 'mh_basalt'),         # Wangen
    C(-2.5, -2.5, -11, 5, 3, 6, 'mh_basalt'),                                         # Schnauze
    C(-1.5, -3.4, -10.5, 3, 1, 6, 'mh_obs'),                                          # Nasengrat
    C(-1, -2.7, -11.4, 2, 1, 1, 'mh_obs'),                                            # Nase
    C(-2, 0.4, -10.5, 4, 1, 6, 'mh_maw'),                                             # Gaumenglut
    C(-2.5, 0.4, -11, 1, 2, 1, 'mh_tooth'), C(1.5, 0.4, -11, 1, 2, 1, 'mh_tooth'),   # Fangzaehne
    C(-2.5, 0.4, -8, 1, 1, 1, 'mh_tooth'), C(1.5, 0.4, -8, 1, 1, 1, 'mh_tooth'),
    C(-3.6, -3, -5.3, 2, 1, 1, 'mh_eye'), C(1.6, -3, -5.3, 2, 1, 1, 'mh_eye'),       # Glutaugen
    C(-3.5, -4.6, -5.5, 7, 1, 2, 'mh_obs')],                                          # Brauenwulst
    children=[mh_jaw, ear(-1), ear(1)], role='head')

# ── Beine ──
def front_leg(side, role):
    n = 'f' + ('r' if side < 0 else 'l')
    paw = B('paw_' + n, (0, 3.5, 0), (-10, 0, 0), [
        C(-1.5, 0, -2.5, 3, 1.5, 4, 'mh_basalt_d'), C(-1.5, 0.6, -3.3, 1, 1, 1, 'mh_claw'), C(0.5, 0.6, -3.3, 1, 1, 1, 'mh_claw'),
        C(-0.5, 0.6, -3.5, 1, 1, 1, 'mh_claw')])
    low = B('shin_' + n, (0, 5, 0), (-6, 0, 0), [
        C(-1, 0, -1, 2, 4, 2, 'mh_basalt'), C(-1.5, -0.5, -1.6, 3, 2, 1, 'mh_crust_d')], children=[paw])
    return B('leg_' + n, (side * 3.2, 2, -6), (8, 0, 0), [
        C(-1.5, -2, -2, 3, 7, 4, 'mh_basalt'), C(-2 if side < 0 else 1, -2.5, -1.5, 1, 4, 3, 'mh_crust')],
        children=[low], role=role)


def back_leg(side, role):
    n = 'b' + ('r' if side < 0 else 'l')
    paw = B('paw_' + n, (0, 4.5, 0), (-30, 0, 0), [
        C(-1.5, 0, -2.5, 3, 1.5, 4, 'mh_basalt_d'), C(-1.5, 0.6, -3.3, 1, 1, 1, 'mh_claw'), C(0.5, 0.6, -3.3, 1, 1, 1, 'mh_claw')])
    hock = B('hock_' + n, (0, 4.5, 1), (40, 0, 0), [
        C(-1, 0, -1, 2, 5, 2, 'mh_basalt'), C(-0.5, 0, 1, 1, 2, 1, 'mh_obs')], children=[paw])
    return B('leg_' + n, (side * 3.2, 1, 6), (-20, 0, 0), [
        C(-2, -2, -2.5, 4, 7, 5, 'mh_basalt'), C(-2.5 if side < 0 else 1.5, -1.5, -2, 1, 4, 4, 'mh_crust')],
        children=[hock], role=role)

# ── Rueckenkamm ──
def spike(i, pos, h, rx):
    return B('crest_%d' % i, pos, (rx, 0, 0), [
        C(-1, -h, -1, 2, h, 2, 'mh_obs'), C(-0.5, -h - 1.5, -0.5, 1, 2, 1, 'mh_obs'), C(-1.5, -1, -1.5, 3, 1, 3, 'mh_crust_d')])


CREST = [((0, -5, -7.5), 3, -35), ((0, -5, -4), 4, -40), ((0, -4.5, -0.5), 3, -45), ((0, -4.5, 3), 3, -50), ((0, -4.5, 6.5), 2, -55)]

# ── Schwanz ──
mh_tail_tip = B('tail_tip', (0, 0, 4), (-15, 0, 0), [
    C(-1, -1, 0, 2, 2, 3, 'mh_crust_d'), C(-0.5, -0.5, 3, 1, 1, 2, 'mh_glow'), C(-0.5, -2, 0.5, 1, 1, 2, 'mh_obs')],
    role='chain', param=2)
mh_tail = B('tail', (0, -2, 8.5), (20, 0, 0), [C(-1.5, -1.5, 0, 3, 3, 4, 'mh_crust'), C(-0.5, -2.5, 0.5, 1, 1, 3, 'mh_obs')],
            children=[mh_tail_tip], role='chain', param=1)

mh_body = B('body', (0, 12, 0), (0, 0, 0),
    _ch(-5, -4, -10.5, 10, 9, 10.5, 'mh_basalt') +                                   # Brustkorb (tief)
    _ch(-4, -3.5, -0.5, 8, 6, 9.5, 'mh_basalt_d') + [                                 # Lende (eingezogen)
    C(-4, -5, -9, 8, 2, 8, 'mh_crust'), C(-3, -4.5, 0.5, 6, 1.5, 8, 'mh_crust'),     # Krustensattel
    C(-3.5, 4.4, -9, 7, 1, 7, 'mh_glow'),                                             # gluehender Bauch
    C(-2.5, 2, 1, 5, 1, 6, 'mh_crust_d')]
    + [C(x, -2.5, z, 1, 5, 1, 'mh_crust_d') for x in (-5.4, 4.4) for z in (-8, -5.5, -3)]   # Rippenkruste
    + [C(-3, -3.5, -12, 6, 6, 2, 'mh_basalt_d')],                                      # Hals
    children=[mh_head, front_leg(-1, 'leg_a'), front_leg(1, 'leg_b'), back_leg(-1, 'leg_b'), back_leg(1, 'leg_a'), mh_tail]
             + [spike(i + 1, *c) for i, c in enumerate(CREST)],
    role='body')

LEGS_A = ['leg_fr', 'leg_bl']
LEGS_B = ['leg_fl', 'leg_br']

MOBS['magma_hound'] = dict(java='magmaHound', tex=128, scale=1.0, roots=[mh_body], pscale=10,
    doc='Magma Hound: Lavahund aus Basalt und Lavakruste mit gluehendem Maul, Glutaugen, Rueckenkamm aus Obsidianzacken '
        'und Schwanz mit Glutspitze.',
    poses={
        1: [(0, 'head', 0, 0, 0), (3, 'head', -25, 0, 0), (6, 'head', 18, 0, 0), (10, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (3, 'jaw', 40, 0, 0), (6, 'jaw', -5, 0, 0), (10, 'jaw', 0, 0, 0),
            (0, 'body', 0, 0, 0), (3, 'body', -6, 0, 0), (6, 'body', 8, 0, 0), (10, 'body', 0, 0, 0),
            (0, 'leg_fr', 0, 0, 0), (3, 'leg_fr', -10, 0, 0), (6, 'leg_fr', -25, 0, 0), (10, 'leg_fr', 0, 0, 0),
            (0, 'leg_fl', 0, 0, 0), (3, 'leg_fl', -10, 0, 0), (6, 'leg_fl', -25, 0, 0), (10, 'leg_fl', 0, 0, 0)],
        3: [(0, 'body', 0, 0, 0), (4, 'body', 10, 0, 0), (8, 'body', -22, 0, 0), (11, 'body', 10, 0, 0), (14, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (4, 'head', 10, 0, 0), (8, 'head', 15, 0, 0), (14, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (4, 'jaw', 0, 0, 0), (8, 'jaw', 35, 0, 0), (14, 'jaw', 0, 0, 0),
            (0, 'leg_fr', 0, 0, 0), (4, 'leg_fr', 25, 0, 0), (8, 'leg_fr', -70, 0, 0), (11, 'leg_fr', -20, 0, 0), (14, 'leg_fr', 0, 0, 0),
            (0, 'leg_fl', 0, 0, 0), (4, 'leg_fl', 25, 0, 0), (8, 'leg_fl', -70, 0, 0), (11, 'leg_fl', -20, 0, 0), (14, 'leg_fl', 0, 0, 0),
            (0, 'leg_br', 0, 0, 0), (4, 'leg_br', -25, 0, 0), (8, 'leg_br', 55, 0, 0), (11, 'leg_br', 10, 0, 0), (14, 'leg_br', 0, 0, 0),
            (0, 'leg_bl', 0, 0, 0), (4, 'leg_bl', -25, 0, 0), (8, 'leg_bl', 55, 0, 0), (11, 'leg_bl', 10, 0, 0), (14, 'leg_bl', 0, 0, 0),
            (0, 'tail', 0, 0, 0), (4, 'tail', 15, 0, 0), (8, 'tail', -30, 0, 0), (14, 'tail', 0, 0, 0)],
    })
