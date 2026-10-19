"""Scrap Gunner (Duna): aus Schrott geflickter zweibeiniger Roboter-Schuetze mit Ruecklaufknien, Gatling-Arm,
Greifzange, Rauchrohren auf dem Ruecken und orange gluehendem Zyklopenauge."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('sg_rust', 'rust', (152, 80, 42), (92, 46, 24))
mat('sg_plate', 'plate', (140, 122, 98), (200, 122, 70))
mat('sg_patch', 'plate', (104, 122, 104), (184, 104, 62))
mat('sg_steel', 'plate', (92, 88, 86), (152, 146, 138))
mat('sg_frame', 'frame', (58, 52, 50))
mat('sg_pipe', 'pipe', (124, 110, 98), (72, 62, 56))
mat('sg_hazard', 'hazard', (230, 190, 30))
mat('sg_cable', 'fiber', (42, 38, 36))
mat('sg_soot', 'plain', (34, 30, 30))
mat('sg_glow', 'glow', (214, 92, 10), (255, 222, 130))
mat('sg_eye', 'eye', (110, 36, 0), (255, 206, 90))


def M(s, x, w): return x if s > 0 else -x - w    # x spiegeln (s=+1 links = Modell +x)


# ── Ruecklaufbeine: Oberschenkel nach hinten, Schienbein nach vorn, Spreizfuss ──
def sg_leg(s):
    t = 'r' if s < 0 else 'l'
    foot = B('foot_' + t, (0, 6.5, 0), (24, 0, 0), [
        C(-2, 0, -4, 4, 2, 6, 'sg_steel'), C(-2.5, 0.5, -5.5, 2, 1.5, 2, 'sg_frame'), C(0.5, 0.5, -5.5, 2, 1.5, 2, 'sg_frame'),
        C(-1, 0.5, 1.5, 2, 1.5, 2, 'sg_frame'), C(-1.5, -1, -1.5, 3, 1, 3, 'sg_rust')])
    shin = B('shin_' + t, (0, 5.5, 0.5), (-50, 0, 0), [
        C(-1.5, 0, -1.5, 3, 7, 3, 'sg_frame'), C(-2, 1, -2.5, 4, 5, 1, 'sg_plate' if s < 0 else 'sg_patch'),
        C(-1, 0, 1.5, 2, 6, 1, 'sg_pipe'), C(-2, -1, -2, 4, 2, 4, 'sg_steel')], children=[foot])
    thigh = B('leg_' + t, (s * 3.2, 11, 0.5), (26, 0, s * -3), [
        C(-2, -1, -2, 4, 8, 4, 'sg_rust'), C(M(s, 1.6, 1), 0, -1.5, 1, 5, 3, 'sg_plate'), C(-2.5, 1, -2.6, 5, 2, 1, 'sg_hazard'),
        C(-1, 1, 2, 2, 5, 1, 'sg_pipe')], children=[shin], role='leg_' + t)
    return thigh


# ── Kopf: verbeulter Kasten mit Zyklopenauge, Kuehlrippen und Antenne ──
sg_head = B('head', (0, -11.5, -2), (-8, 0, 0), [
    C(-3, -5, -3.5, 6, 5, 6, 'sg_steel'), C(-3.5, -6, -4, 7, 2, 5, 'sg_rust'), C(-2, -3.6, -4.4, 4, 4, 1, 'sg_frame'),
    C(-1.5, -3.1, -4.9, 3, 3, 1, 'sg_eye'), C(-2.5, -0.5, -4.2, 5, 1, 1, 'sg_frame'), C(-4, -4, -1.5, 1, 3, 3, 'sg_patch'),
    C(3, -4.5, -2, 1, 2, 2, 'sg_frame'), C(1.5, -10, 1, 1, 4, 1, 'sg_frame'), C(1, -10.5, 0.5, 2, 1, 2, 'sg_glow'),
    C(-2.5, -6.5, 0.5, 2, 1, 2, 'sg_frame')], role='head')

# ── Gatling-Arm (rechts, Modell -x) ──
sg_barrels = B('barrels', (0, 2, -4), (0, 0, 12), [
    C(-1.5, -1.5, -10, 1, 1, 10, 'sg_steel'), C(0.5, -1.5, -10, 1, 1, 10, 'sg_steel'), C(-1.5, 0.5, -10, 1, 1, 10, 'sg_steel'),
    C(0.5, 0.5, -10, 1, 1, 10, 'sg_steel'), C(-0.5, -0.5, -9, 1, 1, 9, 'sg_frame'), C(-2, -2, -3, 4, 4, 1, 'sg_frame'),
    C(-2, -2, -8, 4, 4, 1, 'sg_rust'), C(-1.5, -1.5, -10.6, 1, 1, 1, 'sg_glow'), C(0.5, 0.5, -10.6, 1, 1, 1, 'sg_glow')])
sg_gun = B('gun', (-1.5, 5.5, 0), (40, 0, 0), [
    C(-2.5, -1, -4, 5, 5, 7, 'sg_steel'), C(-3, -1.5, -2, 6, 1, 4, 'sg_hazard'), C(-4.8, 0, -2.5, 2, 4, 4, 'sg_rust'),
    C(-4.5, 4, -1.5, 2, 2, 2, 'sg_cable'), C(-2, 4, 3, 4, 2, 2, 'sg_frame'), C(1.5, -2.5, -1, 1, 2, 3, 'sg_frame')],
    children=[sg_barrels])
sg_arm_r = B('arm_r', (-6, -9, 0), (-50, 0, 6), [
    C(-4.5, -2.5, -3, 5, 4, 6, 'sg_plate'), C(-5, -3, -2, 1, 5, 4, 'sg_hazard'), C(-3, 1, -1.5, 3, 5, 3, 'sg_frame'),
    C(-2.5, 2, 1.5, 2, 3, 1, 'sg_pipe')], children=[sg_gun], role='arm_r')

# ── Greifarm (links): duenner Rohrarm mit Zange ──
sg_claws = [B('pincer_%s' % t, (0, 6.5, 0), (0, 0, s * 22), [
    C(M(s, 0, 1), 0, -1, 1, 3, 2, 'sg_steel'), C(M(s, -0.6, 1), 2.5, -1, 1, 2, 2, 'sg_frame')]) for t, s in (('a', -1), ('b', 1))]
sg_fore_l = B('fore_l', (1.5, 5, 0), (-35, 0, 0), [
    C(-1, 0, -1, 2, 5, 2, 'sg_pipe'), C(-1.5, 4.5, -1.5, 3, 2, 3, 'sg_frame'), C(-1.5, 1, -1.6, 3, 2, 1, 'sg_patch')],
    children=sg_claws)
sg_arm_l = B('arm_l', (5.5, -9, 0), (8, 0, -10), [
    C(-0.5, -2, -2.5, 4, 3, 5, 'sg_patch'), C(0.5, 0, -1, 2, 6, 2, 'sg_pipe'), C(0, 0.5, -1.5, 3, 1, 3, 'sg_frame')],
    children=[sg_fore_l], role='arm_l')

# ── Rauchrohre auf dem Ruecken ──
sg_stack = B('stack', (2, -9.5, 5), (-14, 0, 10), [
    C(-1.5, -9, -1.5, 3, 9, 3, 'sg_pipe'), C(-2, -10, -2, 4, 2, 4, 'sg_frame'), C(-1, -10.5, -1, 2, 1, 2, 'sg_soot'),
    C(-2, -4, -2, 4, 1, 4, 'sg_rust')])
sg_stack2 = B('stack2', (-2.5, -8, 5), (-20, 0, -14), [
    C(-1, -6, -1, 2, 6, 2, 'sg_pipe'), C(-1.5, -7, -1.5, 3, 2, 3, 'sg_frame'), C(-0.5, -7.5, -0.5, 1, 1, 1, 'sg_soot')])

# ── Rumpf: Rostkessel mit Nietenplatten, Feuerungsgitter, Rucksack, Kabeln ──
sg_torso = B('torso', (0, 0, 0), (10, 0, 0), [
    C(-5, -12, -3.5, 10, 12, 7, 'sg_rust'), C(-4.5, -11.5, -4.5, 9, 6, 1, 'sg_plate'), C(-3, -5, -4.4, 6, 4, 1, 'sg_frame'),
    C(-2.5, -4.5, -4.8, 5, 3, 1, 'sg_glow'), C(-1.5, -4.5, -5.3, 1, 3, 1, 'sg_frame'), C(0.5, -4.5, -5.3, 1, 3, 1, 'sg_frame'),
    C(-3.5, 0, -2.5, 7, 3, 5, 'sg_frame'), C(-4, -11, 3.5, 8, 9, 3, 'sg_steel'), C(-4.5, -4, 3, 9, 1, 4, 'sg_hazard'),
    C(-5.5, -10, -2, 1, 5, 4, 'sg_patch'), C(4.5, -7, -1, 1, 5, 4, 'sg_plate'), C(-5, -12.5, -3, 10, 1, 6, 'sg_steel'),
    C(-3, -2, 6.5, 2, 2, 1, 'sg_glow'), C(1, -2, 6.5, 2, 2, 1, 'sg_glow')],
    children=[sg_head, sg_arm_r, sg_arm_l, sg_stack, sg_stack2], role='body')
sg_cables = [B('cable_%d' % i, p, r, [C(-0.5, 0, -0.5, 1, l, 1, 'sg_cable')]) for i, (p, r, l) in
             enumerate([((-4.6, -11, 4), (30, 0, 14), 9), ((4, -10, 5.5), (40, 0, -10), 8)])]
sg_torso.add(*sg_cables)

sg_hips = B('hips', (0, 11, 0.5), (0, 0, 0), [
    C(-4.5, -2, -2.5, 9, 3, 5, 'sg_frame'), C(-3.5, -1, -3.2, 7, 2, 1, 'sg_hazard'), C(-5.5, -1.5, -1.5, 2, 2, 3, 'sg_steel'),
    C(3.5, -1.5, -1.5, 2, 2, 3, 'sg_steel')], children=[sg_torso])

MOBS['scrap_gunner'] = dict(java='scrapGunner', tex=128, scale=1.0, roots=[sg_hips, sg_leg(-1), sg_leg(1)],
    doc='Scrap Gunner: aus rostigen Platten zusammengeflickter Roboter-Schuetze mit Ruecklaufbeinen, Gatling-Arm, Greifzange, Rauchrohren und orange gluehendem Auge.',
    poses={
        2: [(0, 'arm_r', 0, 0, 0), (2, 'arm_r', -14, 10, 0), (4, 'arm_r', -32, 10, 0), (5, 'arm_r', -14, 10, 0), (7, 'arm_r', -30, 10, 0),
            (8, 'arm_r', -14, 10, 0), (10, 'arm_r', -28, 10, 0), (12, 'arm_r', -14, 10, 0), (14, 'arm_r', 0, 0, 0),
            (0, 'gun', 0, 0, 0), (2, 'gun', -6, 0, 0), (4, 'gun', -14, 0, 0), (5, 'gun', -6, 0, 0), (7, 'gun', -14, 0, 0), (8, 'gun', -6, 0, 0),
            (10, 'gun', -14, 0, 0), (12, 'gun', -6, 0, 0), (14, 'gun', 0, 0, 0),
            (0, 'barrels', 0, 0, 0), (14, 'barrels', 0, 0, 720),
            (0, 'torso', 0, 0, 0), (2, 'torso', -4, 16, 0), (4, 'torso', -12, 16, 0), (5, 'torso', -6, 16, 0), (7, 'torso', -12, 16, 0),
            (8, 'torso', -6, 16, 0), (10, 'torso', -11, 16, 0), (12, 'torso', -4, 16, 0), (14, 'torso', 0, 0, 0),
            (0, 'head', 0, 0, 0), (2, 'head', 6, -10, 0), (12, 'head', 6, -10, 0), (14, 'head', 0, 0, 0),
            (0, 'stack', 0, 0, 0), (4, 'stack', -10, 0, 6), (7, 'stack', 4, 0, -4), (10, 'stack', -8, 0, 6), (14, 'stack', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (2, 'arm_l', -30, 0, -20), (12, 'arm_l', -30, 0, -20), (14, 'arm_l', 0, 0, 0)],
    })
