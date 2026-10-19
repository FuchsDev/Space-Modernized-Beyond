"""Cinder Imp (Moho): kleiner schwebender Feuerkobold aus Lavakruste mit gluehenden Rissen, Fledermausfluegeln,
Hoernern, Glutaugen, Feuerball in der Klaue und Schwanz mit Glutspitze."""
import math
from model import B, C
from paint import mat
from mobs import MOBS

mat('ci_crust', 'crust', (62, 36, 30), glow_col=(255, 116, 22))
mat('ci_crust_d', 'crust', (42, 27, 26), glow_col=(255, 82, 12))
mat('ci_obs', 'obsidian', (32, 22, 36), (140, 92, 176))
mat('ci_horn', 'bone', (112, 88, 72))
mat('ci_claw', 'bone', (58, 44, 42))
mat('ci_mem', 'flesh', (104, 30, 22), (190, 62, 26))
mat('ci_skin', 'scale', (86, 40, 32), (160, 76, 44))
mat('ci_glow', 'glow', (255, 86, 8), (255, 236, 130))
mat('ci_fire', 'glow', (255, 140, 20), (255, 250, 200))
mat('ci_eye', 'eye', (170, 50, 0), (255, 252, 160))


def _mc(cubes):
    return [C(-c.x - c.w, c.y, c.z, c.w, c.h, c.d, c.mat, c.infl) for c in cubes]


_RM = {'wing_l': 'wing_r', 'arm_l': 'arm_r', 'leg_l': 'leg_r'}


def _mirror(b):
    """Spiegelt einen linken Knochenbaum (+x) auf die rechte Seite (-x); Namen *_l -> *_r."""
    rx, ry, rz = (math.degrees(v) for v in b.rot)
    return B(b.name[:-2] + '_r' if b.name.endswith('_l') else b.name + '_r', (-b.pivot[0], b.pivot[1], b.pivot[2]),
             (rx, -ry, -rz), _mc(b.cubes), [_mirror(ch) for ch in b.children], role=_RM.get(b.role, b.role), param=b.param)


# ── Kopf ──
def horn_l():
    tip = B('horntip_l', (0, -2, 0), (-40, 0, -15), [C(-0.5, -2, -0.5, 1, 2, 1, 'ci_horn'), C(-0.5, -3, -0.5, 1, 1, 1, 'ci_glow')])
    return B('horn_l', (1.8, -4.6, -1), (-35, 0, 38), [C(-1, -2.5, -1, 2, 3, 2, 'ci_horn'), C(-1.2, -0.6, -1.2, 2, 1, 2, 'ci_obs', 0.2)],
             children=[tip])


def ear_l():
    return B('ear_l', (2.5, -3, -0.5), (0, -20, -12), [C(0, -1, -0.5, 3, 2, 1, 'ci_skin'), C(2, -2, -0.5, 1, 1, 1, 'ci_skin')])


ci_jaw = B('jaw', (0, -1.2, -2), (0, 0, 0), [
    C(-2, 0, -2.6, 4, 1, 3, 'ci_skin'), C(-1.5, -0.6, -2.4, 3, 1, 2, 'ci_glow'),
    C(-2, -0.9, -2.8, 1, 1, 1, 'ci_horn'), C(1, -0.9, -2.8, 1, 1, 1, 'ci_horn')], role='jaw')

ci_head = B('head', (0, -4.5, -0.5), (0, 0, 0), [
    C(-2.5, -5, -3, 5, 4, 5, 'ci_crust'),                                            # Schaedel
    C(-3, -4.2, -3.4, 6, 1, 2, 'ci_obs'),                                            # Brauenwulst
    C(-2, -3, -3.3, 1, 1, 1, 'ci_eye'), C(1, -3, -3.3, 1, 1, 1, 'ci_eye'),           # Augen
    C(-1.5, -2.5, -4.6, 3, 2, 2, 'ci_skin'),                                       # Schnauze
    C(-1, -2.2, -4.9, 1, 1, 1, 'ci_glow'), C(0, -2.2, -4.9, 1, 1, 1, 'ci_glow'),     # Nuestern
    C(-1.5, -1.6, -3.6, 1, 1, 1, 'ci_horn'), C(0.5, -1.6, -3.6, 1, 1, 1, 'ci_horn'), # Hauer
    C(-0.5, -5.8, -2, 1, 1, 4, 'ci_obs'), C(-2, -1.2, -1.5, 4, 1, 3, 'ci_skin')],  # Scheitelgrat, Kinn
    children=[ci_jaw, horn_l(), _mirror(horn_l()), ear_l(), _mirror(ear_l())], role='head')

# ── Arme: rechte Klaue haelt einen Feuerball ──
def arm(side):
    n = 'r' if side < 0 else 'l'
    def cx(x, w): return -x - w if side < 0 else x
    hand = [C(cx(-0.5, 1), 2.6, -1.6, 1, 2, 1, 'ci_claw'), C(cx(0.5, 1), 2.6, -1.6, 1, 2, 1, 'ci_claw'),
            C(cx(-0.5, 1), 2.6, 0.4, 1, 1, 1, 'ci_claw')]
    if side < 0:
        hand += [C(-1.5, 3.6, -2.5, 3, 3, 3, 'ci_fire'), C(-1, 3.1, -2, 2, 4, 2, 'ci_glow', 0.15)]
    fore = B('fore_' + n, (cx(0, 0) if side > 0 else 0, 3.2, 0), (-35, 0, 0), [
        C(cx(-1, 2), 0, -1, 2, 3, 2, 'ci_skin'), C(cx(-1.2, 1), 0.5, -0.6, 1, 2, 1, 'ci_obs', 0.15)] + hand)
    return B('arm_' + n, (side * 3.6, -3.2, -0.3), (10, 0, side * -12), [
        C(cx(-1, 2), -0.5, -1, 2, 4, 2, 'ci_skin'), C(cx(-1.4, 3), -1.2, -1.4, 3, 2, 3, 'ci_obs')],
        children=[fore], role='arm_' + n)

# ── Beine (haengen beim Schweben) ──
def leg(side):
    n = 'r' if side < 0 else 'l'
    def cx(x, w): return -x - w if side < 0 else x
    foot = B('foot_' + n, (0, 2.6, 0), (-25, 0, 0), [
        C(cx(-1, 2), 0, -2, 2, 1, 3, 'ci_skin'), C(cx(-1, 1), 0.3, -2.8, 1, 1, 1, 'ci_claw'), C(cx(0, 1), 0.3, -2.8, 1, 1, 1, 'ci_claw')])
    shin = B('shin_' + n, (0, 2.6, 0.2), (85, 0, 0), [C(cx(-0.8, 2), 0, -0.8, 2, 3, 2, 'ci_skin')], children=[foot])
    return B('leg_' + n, (side * 1.6, 3.6, 0), (-50, 0, side * -8), [
        C(cx(-1, 2), -0.5, -1.2, 2, 3.5, 3, 'ci_skin'), C(cx(-1.3, 1), 0, -1, 1, 2, 2, 'ci_obs')],
        children=[shin], role='leg_' + n)

# ── Fluegel (Fledermaus): Oberarmknochen, Fingerknochen, Membran ──
def wing_l():
    tip = B('wtip_l', (5.5, 0, 0), (0, 0, -28), [
        C(0, -0.5, -0.5, 6, 1, 1, 'ci_obs'), C(5.5, -1.5, -0.5, 1, 1, 1, 'ci_claw'),
        C(0, 0.5, 0, 5, 3, 1, 'ci_mem'), C(0, 3.5, 0, 3, 2, 1, 'ci_mem'), C(0, 5.5, 0, 1, 1, 1, 'ci_mem'),
        C(2.5, 0.5, -0.4, 1, 4, 1, 'ci_obs', -0.1)])
    return B('wing_l', (1.5, -2.6, 2.2), (0, -38, -30), [
        C(0, -0.8, -0.8, 6, 2, 2, 'ci_obs'), C(0, 1, 0, 6, 4, 1, 'ci_mem'), C(1, 5, 0, 4, 2, 1, 'ci_mem'),
        C(3.5, 1, -0.4, 1, 6, 1, 'ci_obs', -0.1), C(-0.5, -1, -1, 2, 2, 2, 'ci_skin')],
        children=[tip], role='wing_l')

# ── Schwanz mit Glutspitze ──
ci_tip = B('tail_tip', (0, 0, 3), (40, 0, 0), [
    C(-1.5, -1, 1.5, 3, 2, 2, 'ci_obs'), C(-1, -0.5, 3.5, 2, 1, 2, 'ci_glow'),
    C(-0.5, -1.5, 2, 1, 3, 2, 'ci_fire')], role='chain', param=3)
ci_tail2 = B('tail_2', (0, 0, 3.5), (30, 0, 0), [C(-0.5, -0.5, 0, 1, 1, 3.5, 'ci_skin')], children=[ci_tip], role='chain', param=2)
ci_tail = B('tail_1', (0, 2.5, 1.5), (-45, 0, 0), [C(-1, -1, 0, 2, 2, 4, 'ci_skin'), C(-0.5, -1.6, 0.5, 1, 1, 2, 'ci_obs')],
            children=[ci_tail2], role='chain', param=1)

ci_body = B('body', (0, 14, 0), (12, 0, 0), [
    C(-3, -5, -2, 6, 5, 4, 'ci_crust'),                                              # Brust
    C(-2.5, 0, -1.5, 5, 3, 3, 'ci_skin'),                                          # Becken
    C(-2, -4, -2.4, 4, 2, 1, 'ci_obs'),                                               # Brustplatte
    C(-1, -1.8, -2.4, 2, 2, 1, 'ci_glow'),                                            # Glutkern
    C(-2, 0.6, -1.8, 4, 1, 1, 'ci_obs'),                                              # Guertelkruste
    C(-0.5, -5, 2, 1, 5, 1, 'ci_obs'), C(-2.5, -4.5, 1.6, 5, 3, 1, 'ci_crust_d')],
    children=[ci_head, arm(-1), arm(1), leg(-1), leg(1), wing_l(), _mirror(wing_l()), ci_tail], role='bob')

MOBS['cinder_imp'] = dict(java='cinderImp', tex=64, scale=1.0, roots=[ci_body], pscale=14,
    doc='Cinder Imp: schwebender Feuerkobold aus Lavakruste mit gluehenden Rissen, Hoernern, Glutaugen, Fledermausfluegeln, '
        'Feuerball in der rechten Klaue und Schwanz mit Glutspitze.',
    poses={
        2: [(0, 'arm_r', 0, 0, 0), (6, 'arm_r', -165, 15, 10), (9, 'arm_r', -45, 0, 0), (14, 'arm_r', 0, 0, 0),
            (0, 'fore_r', 0, 0, 0), (6, 'fore_r', -40, 0, 0), (9, 'fore_r', 10, 0, 0), (14, 'fore_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (6, 'arm_l', -40, 0, -20), (9, 'arm_l', 20, 0, 0), (14, 'arm_l', 0, 0, 0),
            (0, 'body', 0, 0, 0), (6, 'body', -12, 18, 0), (9, 'body', 18, -12, 0), (14, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (6, 'head', -10, -10, 0), (9, 'head', 5, 8, 0), (14, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (6, 'jaw', 15, 0, 0), (9, 'jaw', 35, 0, 0), (14, 'jaw', 0, 0, 0),
            (0, 'wing_r', 0, 0, 0), (6, 'wing_r', 0, 10, -25), (9, 'wing_r', 0, 0, 20), (14, 'wing_r', 0, 0, 0),
            (0, 'wing_l', 0, 0, 0), (6, 'wing_l', 0, -10, 25), (9, 'wing_l', 0, 0, -20), (14, 'wing_l', 0, 0, 0),
            (0, 'tail_1', 0, 0, 0), (6, 'tail_1', -15, 0, 0), (9, 'tail_1', 15, 0, 0), (14, 'tail_1', 0, 0, 0)],
    })
