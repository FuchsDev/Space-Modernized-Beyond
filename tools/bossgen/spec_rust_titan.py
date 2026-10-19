"""Rust Titan (Duna): verrosteter sechsbeiniger Spinnenpanzer mit Geschuetzturm, Sensorbuendel und Auspuffrohren."""
from model import B, C
from paint import mat
from specs import BOSSES

mat('rt_rust', 'rust', (158, 80, 44), (88, 42, 26))
mat('rt_plate', 'plate', (150, 132, 98), (200, 112, 62))
mat('rt_plate_r', 'plate', (172, 96, 56), (226, 170, 116))
mat('rt_steel', 'plate', (92, 84, 78), (150, 140, 128))
mat('rt_frame', 'frame', (60, 54, 52))
mat('rt_pipe', 'pipe', (124, 112, 102), (72, 62, 56))
mat('rt_hazard', 'hazard', (230, 190, 30))
mat('rt_cable', 'fiber', (44, 40, 38))
mat('rt_sand', 'rock', (196, 140, 92))
mat('rt_eye', 'eye', (90, 36, 0), (255, 206, 90))
mat('rt_glow', 'glow', (210, 96, 10), (255, 226, 140))


def M(s, x, w): return x if s > 0 else -x - w    # x spiegeln (s=+1 links = Modell +x)


def rt_leg(tag, s, z, splay, role):
    foot = B('foot_' + tag, (0, 32, 0), (0, 0, s * 13), [
        C(M(s, -3.5, 7), 0, -4, 7, 3, 8, 'rt_steel'), C(M(s, -2, 4), 1, -6.5, 4, 2, 3, 'rt_frame')])
    shin = B('shin_' + tag, (M(s, 16, 0), 0, 0), (0, 0, s * 27), [
        C(M(s, -2.5, 5), 0, -2.5, 5, 32, 5, 'rt_rust'), C(M(s, -3.5, 7), 1, -3.5, 7, 10, 7, 'rt_plate'),
        C(M(s, -3, 6), 25, -3, 6, 2, 6, 'rt_hazard'), C(M(s, -2, 4), -6, -1.5, 4, 6, 3, 'rt_frame')],
        children=[foot])
    thigh = B('thigh_' + tag, (M(s, 3, 0), 0, 0), (0, 0, s * -40), [
        C(M(s, 0, 17), -2.5, -2.5, 17, 5, 5, 'rt_rust'), C(M(s, 1, 14), -4, -3.5, 14, 2, 7, 'rt_plate'),
        C(M(s, 14.5, 5), -3.5, -3.5, 5, 7, 7, 'rt_frame')], children=[shin])
    return B('leg_' + tag, (s * 12, 0, z), (0, s * splay, 0), [
        C(M(s, -2, 6), -4, -4, 6, 8, 8, 'rt_frame')],
        children=[thigh], role=role)


# ── Geschuetzturm (Kopf) mit Langrohr ──
rt_cannon = B('cannon', (0, -6, -10), (0, 0, 0), [
    C(-4.5, -3.5, -4, 9, 7, 4, 'rt_steel'), C(-1.5, -1.5, -33, 3, 3, 30, 'rt_pipe'), C(-2.5, -2.5, -20, 5, 5, 6, 'rt_frame'),
    C(-3, -2.5, -38, 6, 5, 5, 'rt_frame'), C(-1, -1, -38.5, 2, 2, 1, 'rt_glow'), C(-2, -2, -9, 4, 4, 3, 'rt_rust'),
    C(3, 0, -12, 1, 1, 9, 'rt_frame')])
rt_antenna = B('antenna', (-7, -12, 8), (-18, 0, -8), [C(-0.5, -16, -0.5, 1, 16, 1, 'rt_frame'), C(-1, -17, -1, 2, 2, 2, 'rt_glow')])
rt_scope = B('periscope', (4.5, -12, -3), (0, -15, 0), [
    C(-2, -4, -2, 4, 4, 5, 'rt_steel'), C(-1.5, -3.5, -2.5, 3, 2, 1, 'rt_eye'), C(-1, -1, -1, 2, 2, 2, 'rt_frame')])
rt_tfront = B('turret_front', (0, -11, -9), (-30, 0, 0), [C(-9, 0, -2, 18, 7, 2, 'rt_plate_r')])
rt_turret = B('turret', (0, -10, 0), (0, 0, 0), [
    C(-7, -3, -7, 14, 3, 14, 'rt_frame'), C(-10, -11, -9, 20, 8, 18, 'rt_plate'), C(-9, -12, -7, 18, 1, 15, 'rt_rust'),
    C(-9, -10, 9, 18, 7, 7, 'rt_rust'), C(-8, -11, 14, 16, 2, 3, 'rt_frame'), C(-7, -14, 1, 6, 2, 6, 'rt_plate_r'),
    C(-12, -9, -6, 2, 3, 8, 'rt_pipe'), C(10, -9, -6, 2, 3, 8, 'rt_pipe'), C(-10.5, -5, -9.5, 7, 2, 2, 'rt_hazard'), C(3.5, -5, -9.5, 7, 2, 2, 'rt_hazard'),
    C(5, -8, 15.5, 3, 2, 1, 'rt_glow')],
    children=[rt_cannon, rt_antenna, rt_scope, rt_tfront], role='head')

# ── Rumpf: Panzerwanne, Bugplatte mit Sensoraugen, Motorblock mit Auspuff, Kabel ──
rt_glacis = B('glacis', (0, -6, -17), (-38, 0, 0), [
    C(-11, 0, -2, 22, 11, 2, 'rt_plate'), C(-6.5, 2.5, -3, 4, 3, 1, 'rt_eye'), C(2.5, 2.5, -3, 4, 3, 1, 'rt_eye'),
    C(-2, 1.5, -2.5, 4, 2, 1, 'rt_eye'), C(-9, 6.5, -2.6, 2, 2, 1, 'rt_glow'), C(7, 6.5, -2.6, 2, 2, 1, 'rt_glow'),
    C(-1.5, 6, -2.6, 3, 2, 1, 'rt_eye'), C(-8, 1, -3, 4, 1, 1, 'rt_frame'), C(4, 1, -3, 4, 1, 1, 'rt_frame')])
rt_mandible = [B('mandible_' + t, (s * 6, 4, -19), (20, s * -18, 0), [
    C(-1.5, -1.5, -7, 3, 3, 8, 'rt_steel'), C(-1, -1, -9, 2, 3, 2, 'rt_frame')]) for t, s in (('r', -1), ('l', 1))]
rt_exhaust = [B('exhaust_' + t, (s * 6, -6, 17), (-22, 0, s * 12), [
    C(-1.5, -15, -1.5, 3, 15, 3, 'rt_pipe'), C(-2, -17, -2, 4, 3, 4, 'rt_frame'), C(-1, -17.5, -1, 2, 1, 2, 'rt_glow')]) for t, s in (('r', -1), ('l', 1))]
rt_cables = [B('cable_%d' % i, (x, -10, z), (ax, ay, 0), [C(-0.5, -0.5, 0, 1, 1, l, 'rt_cable')])
             for i, (x, z, ax, ay, l) in enumerate([(-9, -6, -8, 30, 14), (9.5, 4, 12, -20, 13), (-10, 6, 15, 10, 10)])]
rt_hull = B('hull', (0, 2, 2), (0, 0, 0), [
    C(-12, -6, -18, 24, 10, 35, 'rt_rust'), C(-10, -10, -14, 20, 4, 28, 'rt_plate_r'), C(-9, 4, -13, 18, 3, 25, 'rt_frame'),
    C(-13.5, -7, -14, 2, 7, 27, 'rt_steel'), C(11.5, -7, -14, 2, 7, 27, 'rt_steel'), C(-14, -8, -10, 2, 1, 18, 'rt_hazard'),
    C(12, -8, -10, 2, 1, 18, 'rt_hazard'), C(-9, -9, 17, 18, 11, 6, 'rt_frame'), C(-7, -7, 22.5, 14, 6, 1, 'rt_steel'),
    C(-5, -6, 23, 3, 2, 1, 'rt_glow'), C(2, -6, 23, 3, 2, 1, 'rt_glow'), C(-10, 1, -21, 20, 3, 4, 'rt_hazard'),
    C(-9, -11, 7, 18, 1, 7, 'rt_sand'), C(-7, -11, -13, 14, 1, 5, 'rt_frame')],
    children=[rt_glacis, rt_turret] + rt_mandible + rt_exhaust + rt_cables, role='body')

rt_legs = [rt_leg('fl', 1, -11, 38, 'leg_a'), rt_leg('fr', -1, -11, 38, 'leg_b'),
           rt_leg('ml', 1, 1, 4, 'leg_b'), rt_leg('mr', -1, 1, 4, 'leg_a'),
           rt_leg('bl', 1, 13, -36, 'leg_a'), rt_leg('br', -1, 13, -36, 'leg_b')]

LIFT = lambda tag, s, t, a: (t, 'thigh_' + tag, 0, 0, s * a)
BOSSES['rust_titan'] = dict(java='rustTitan', tex=256, scale=1.4, hitbox=(3.5, 3.8), roots=[rt_hull] + rt_legs,
    doc='Rust Titan: verrosteter sechsbeiniger Spinnenpanzer mit Panzerwanne, drehbarem Geschuetzturm mit Langrohr, bernsteinfarbenem Sensorbuendel, Auspuffrohren und Kabeln.',
    # Angriffe: cannon(20), sandstorm(60), charge(24), quake(26)
    poses={
        0: [(0, 'turret', 0, 28, 0), (0, 'cannon', 24, 0, 0), (0, 'hull', 5, 0, 0), (0, 'antenna', 40, 0, 0), (0, 'periscope', 20, 0, 0)],
        1: [(0, 'cannon', 0, 0, 0), (8, 'cannon', -14, 0, 0), (15, 'cannon', -14, 0, 0), (16, 'cannon', -24, 0, 0), (20, 'cannon', 0, 0, 0),
            (0, 'hull', 0, 0, 0), (8, 'hull', 4, 0, 0), (16, 'hull', -6, 0, 0), (20, 'hull', 0, 0, 0),
            (0, 'turret', 0, 0, 0), (8, 'turret', 0, 0, 0), (16, 'turret', -6, 0, 0), (20, 'turret', 0, 0, 0)],
        2: [(0, 'turret', 0, 0, 0), (15, 'turret', 0, 90, 0), (30, 'turret', 0, 180, 0), (45, 'turret', 0, 270, 0), (60, 'turret', 0, 360, 0),
            (0, 'hull', 0, 0, 0), (8, 'hull', 0, 0, 0), (52, 'hull', 0, 0, 0), (60, 'hull', 0, 0, 0),
            (0, 'cannon', 0, 0, 0), (8, 'cannon', -20, 0, 0), (52, 'cannon', -20, 0, 0), (60, 'cannon', 0, 0, 0),
            (0, 'exhaust_r', 0, 0, 0), (6, 'exhaust_r', -15, 0, -10), (54, 'exhaust_r', -15, 0, -10), (60, 'exhaust_r', 0, 0, 0),
            (0, 'exhaust_l', 0, 0, 0), (6, 'exhaust_l', -15, 0, 10), (54, 'exhaust_l', -15, 0, 10), (60, 'exhaust_l', 0, 0, 0)]
           + [k for t, s in (('fl', 1), ('fr', -1), ('ml', 1), ('mr', -1), ('bl', 1), ('br', -1))
              for k in (LIFT(t, s, 0, 0), LIFT(t, s, 8, 12), LIFT(t, s, 52, 12), LIFT(t, s, 60, 0))],
        3: [(0, 'hull', 0, 0, 0), (8, 'hull', -8, 0, 0), (12, 'hull', 9, 0, 0), (22, 'hull', 9, 0, 0), (24, 'hull', 0, 0, 0),
            (0, 'cannon', 0, 0, 0), (8, 'cannon', -10, 0, 0), (12, 'cannon', 8, 0, 0), (22, 'cannon', 8, 0, 0), (24, 'cannon', 0, 0, 0),
            (0, 'mandible_r', 0, 0, 0), (8, 'mandible_r', 0, -25, 0), (12, 'mandible_r', 0, 10, 0), (22, 'mandible_r', 0, 10, 0), (24, 'mandible_r', 0, 0, 0),
            (0, 'mandible_l', 0, 0, 0), (8, 'mandible_l', 0, 25, 0), (12, 'mandible_l', 0, -10, 0), (22, 'mandible_l', 0, -10, 0), (24, 'mandible_l', 0, 0, 0),
            LIFT('fl', 1, 0, 0), LIFT('fl', 1, 8, -25), LIFT('fl', 1, 12, 0), LIFT('fl', 1, 24, 0),
            LIFT('fr', -1, 0, 0), LIFT('fr', -1, 8, -25), LIFT('fr', -1, 12, 0), LIFT('fr', -1, 24, 0)],
        4: [(0, 'hull', 0, 0, 0), (6, 'hull', -7, 0, 0), (12, 'hull', 5, 0, 0), (15, 'hull', -4, 0, 0), (18, 'hull', 5, 0, 0),
            (21, 'hull', -4, 0, 0), (24, 'hull', 5, 0, 0), (26, 'hull', 0, 0, 0)]
           + [k for t, s in (('fl', 1), ('fr', -1), ('ml', 1), ('mr', -1)) for k in (
               LIFT(t, s, 0, 0), LIFT(t, s, 6, -30), LIFT(t, s, 12, 4), LIFT(t, s, 15, -20), LIFT(t, s, 18, 4),
               LIFT(t, s, 21, -20), LIFT(t, s, 24, 4), LIFT(t, s, 26, 0))],
    })
