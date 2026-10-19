"""Toxic Sludge (Dres): Klumpen aus gruenem Giftschleim mit leuchtendem Kern, eingeschlossenen Giftfaessern und Schrott,
Blasen auf der Oberflaeche und kriechenden Pseudopodien."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('ts_slime', 'ice', (112, 196, 56), (46, 118, 26))
mat('ts_slime_d', 'flesh', (60, 132, 34), (34, 88, 20))
mat('ts_body', 'flesh', (104, 190, 50), (62, 140, 32))
mat('ts_core', 'crystal', (120, 230, 40), (236, 255, 150))
mat('ts_glow', 'glow', (110, 220, 30), (230, 255, 150))
mat('ts_bubble', 'glow', (170, 250, 100), (246, 255, 214))
mat('ts_drum', 'rust', (196, 160, 44), (120, 84, 30))
mat('ts_drum_d', 'plate', (70, 66, 52), (150, 140, 110))
mat('ts_hazard', 'hazard', (230, 190, 30))
mat('ts_scrap', 'rust', (120, 112, 104), (140, 76, 40))
mat('ts_pipe', 'pipe', (128, 120, 112), (70, 64, 60))


def ch(x, y, z, w, h, d, m, k=1):
    """Gefaster Block aus drei Wuerfeln (weichere Schleimform)."""
    return [C(x, y + k, z + k, w, h - 2 * k, d - 2 * k, m), C(x + k, y, z + k, w - 2 * k, h, d - 2 * k, m),
            C(x + k, y + k, z, w - 2 * k, h - 2 * k, d, m)]


def drum(name, pivot, rot, h=8):
    """Liegendes/schraeges Giftfass: achtkantiger Zylinder mit Spannringen und Warnband."""
    return B(name, pivot, rot, [
        C(-3, -h / 2, -2, 6, h, 4, 'ts_drum'), C(-2, -h / 2, -3, 4, h, 6, 'ts_drum'),
        C(-3, -h / 2 + 1.5, -2, 6, 1, 4, 'ts_drum_d', 0.25), C(-2, -h / 2 + 1.5, -3, 4, 1, 6, 'ts_drum_d', 0.25),
        C(-3, h / 2 - 2.5, -2, 6, 1, 4, 'ts_hazard', 0.25), C(-2, -h / 2 - 0.5, -2, 4, 1, 4, 'ts_drum_d'),
        C(-1, -h / 2 - 1, -1, 1, 1, 1, 'ts_drum_d')])


# ── Pseudopodien: zweigliedrige Schleimarme am Boden, Leuchttropfen an der Spitze ──
def pod(i, pivot, yaw, size, lift=0):
    s = size
    L = s + 1
    tip = B('pod_%d_tip' % i, (0, 0.5, -L + 0.5), (-8 - lift, 0, 0), [
        C(-s / 2 + 0.5, -s / 2 + 0.5, -L + 1, s - 1, s - 1, L - 1, 'ts_slime'), C(-0.5, -0.5, -L, 1, 1, 1, 'ts_glow')],
        role='tentacle', param=i * 1.1 + 0.6)
    return B('pod_%d' % i, pivot, (lift, yaw, 0), [
        C(-s / 2, -s / 2, -L, s, s, L, 'ts_slime_d'), C(-s / 2 + 0.5, -s / 2 - 0.5, -L + 1, 1, 1, 1, 'ts_bubble')],
        children=[tip], role='tentacle', param=i * 1.1)


ts_core = B('core', (0, -7, -6.8), (36, 45, 0), [C(-2.5, -2.5, -2.5, 5, 5, 5, 'ts_core'), C(-1.5, -1.5, -1.5, 3, 3, 3, 'ts_glow', 0.8)])
ts_reach = B('reach', (2, -13, -1), (-12, 0, 18), [
    C(-2, -5, -2, 4, 5, 4, 'ts_slime'), C(-1, -5.5, -2.5, 2, 1, 1, 'ts_bubble')], role='tentacle', param=2.2, children=[
    B('reach_tip', (0, -4.5, 0), (-28, 0, -10), [C(-1.5, -3, -1.5, 3, 3, 3, 'ts_slime'), C(-0.5, -4, -0.5, 1, 1, 1, 'ts_glow')],
      role='tentacle', param=3.1)])

ts_scrap = [
    B('pipe_stub', (-8, -8, 2), (30, 20, 40), [C(-1, -1, -5, 2, 2, 9, 'ts_pipe'), C(-1.5, -1.5, -6, 3, 3, 1, 'ts_drum_d')]),
    B('plate_shard', (3, -11, 4.5), (-25, 15, 20), [C(-3, -4, -0.5, 6, 5, 1, 'ts_scrap'), C(-2, -5, -0.5, 3, 1, 1, 'ts_scrap')]),
    B('gear', (-5.5, -4.5, -8.2), (0, 0, 20), [C(-2, -2, -0.5, 4, 4, 1, 'ts_drum_d'), C(-2.5, -1, -0.5, 5, 2, 1, 'ts_drum_d'),
                                            C(-1, -2.5, -0.5, 2, 5, 1, 'ts_drum_d')])]

ts_bubbles = [C(x, y, z, w, w, w, 'ts_bubble') for x, y, z, w in [
    (-6.5, -10, -5, 2), (4, -13.5, -2, 1), (-2, -15, 1, 2), (8.5, -7.5, 3, 1), (-9, -6.5, -4, 1), (1, -11.5, -6.5, 1), (-4, -13, -3, 1),
    (10, -4, -6, 1), (5, -9.5, -7.6, 1)]]

ts_blob = B('blob', (0, 24, 0), (0, 0, 0),
    ch(-11, -4, -9, 22, 4, 18, 'ts_slime_d', 1) + ch(-10, -10, -8.5, 20, 8, 17, 'ts_body', 3) + ch(-7, -14, -6, 14, 6, 12, 'ts_body', 2)
    + [C(-4, -15, -3.5, 8, 2, 7, 'ts_body'), C(2.5, -12, -7.5, 6, 5, 5, 'ts_body'), C(-10, -9, 0, 4, 6, 7, 'ts_body'), C(5, -11.5, 2, 5, 4, 5, 'ts_slime_d'), C(-11.5, -2, -6, 1, 2, 12, 'ts_slime_d'), C(10.5, -2, -6, 1, 2, 12, 'ts_slime_d')]
    + ts_bubbles,
    children=[ts_core, ts_reach, drum('drum_a', (6.5, -6.5, -4.5), (20, -20, 72)), drum('drum_b', (-4.5, -10.5, 3.5), (0, 35, 84), 7),
              pod(0, (-7, -2.5, -8), 25, 4), pod(1, (7, -2.5, -8), -30, 4), pod(2, (-10, -2.5, 3), 95, 3),
              pod(3, (10, -2.5, 4), -100, 3), pod(4, (2, -2.5, 9), 190, 4)] + ts_scrap, role='body')

MOBS['toxic_sludge'] = dict(java='toxicSludge', tex=256, scale=1.0, roots=[ts_blob],
    doc='Toxic Sludge: Klumpen aus gruenem Giftschleim mit leuchtendem Kern, eingeschlossenen Giftfaessern und Schrott, Blasen und kriechenden Pseudopodien.',
    poses={
        1: [(0, 'blob', 0, 0, 0), (4, 'blob', -10, 0, 0), (7, 'blob', 12, 0, 0), (10, 'blob', 0, 0, 0),
            (0, 'reach', 0, 0, 0), (4, 'reach', 25, 0, -15), (7, 'reach', -95, 0, -10), (10, 'reach', 0, 0, 0),
            (0, 'reach_tip', 0, 0, 0), (4, 'reach_tip', 20, 0, 0), (7, 'reach_tip', -40, 0, 0), (10, 'reach_tip', 0, 0, 0),
            (0, 'pod_0', 0, 0, 0), (4, 'pod_0', 45, 0, 0), (7, 'pod_0', -10, 0, 0), (10, 'pod_0', 0, 0, 0),
            (0, 'pod_1', 0, 0, 0), (4, 'pod_1', 45, 0, 0), (7, 'pod_1', -10, 0, 0), (10, 'pod_1', 0, 0, 0),
            (0, 'pod_0_tip', 0, 0, 0), (4, 'pod_0_tip', 30, 0, 0), (7, 'pod_0_tip', -15, 0, 0), (10, 'pod_0_tip', 0, 0, 0),
            (0, 'pod_1_tip', 0, 0, 0), (4, 'pod_1_tip', 30, 0, 0), (7, 'pod_1_tip', -15, 0, 0), (10, 'pod_1_tip', 0, 0, 0)],
    })
