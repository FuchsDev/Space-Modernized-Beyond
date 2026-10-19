"""Spezifikation der Stufen-Bosse: Knochen, Wuerfel, Materialien, Rollen, Angriffsposen."""
from model import B, C
from paint import mat

BOSSES = {}

# ════════════════════════════════════════════════════════════════════════════
# 1 Lunar Sentinel: Sicherheits-Mech der Mondstation (Ruecklaufknie, Raketenwerfer, Laserarm)
# ════════════════════════════════════════════════════════════════════════════
mat('ls_plate', 'plate', (204, 208, 214), (250, 250, 250))
mat('ls_plate_d', 'plate', (104, 110, 122), (170, 176, 186))
mat('ls_frame', 'frame', (60, 62, 70))
mat('ls_hazard', 'hazard', (230, 190, 30))
mat('ls_piston', 'pipe', (190, 194, 200), (120, 122, 128))
mat('ls_red', 'glow', (190, 20, 20), (255, 120, 100))
mat('ls_orange', 'glow', (230, 90, 10), (255, 220, 140))
mat('ls_eye', 'eye', (90, 0, 0), (255, 80, 60))

def ls_leg(side):
    s = side  # -1 rechts (Modell -x), +1 links
    foot = B('foot_' + ('r' if s < 0 else 'l'), (0, 9, 0), (-30, 0, 0), [
        C(-3, 0, -6, 6, 2, 9, 'ls_plate_d'), C(-3.5, 0.5, -8, 2, 1, 3, 'ls_frame'), C(1.5, 0.5, -8, 2, 1, 3, 'ls_frame'),
        C(-1, 0.5, -9, 2, 1, 4, 'ls_frame'), C(-1, -1, 3, 2, 3, 2, 'ls_frame')])
    shin = B('shin_' + ('r' if s < 0 else 'l'), (0, 9, 0), (50, 0, 0), [
        C(-2, 0, -2, 4, 10, 4, 'ls_frame'), C(-2.5, 1, -3.5, 5, 6, 2, 'ls_plate'), C(-1, 0, 2, 2, 8, 2, 'ls_piston')], children=[foot])
    thigh = B('leg_' + ('r' if s < 0 else 'l'), (s * 6, 5.5, 0), (-20, 0, s * -4), [
        C(-3, -1, -3, 6, 10, 6, 'ls_plate'), C(-3.5, 0, -3.5, 7, 4, 1, 'ls_plate_d'), C(-1, 2, 3, 2, 7, 2, 'ls_piston'),
        C(-3.5, 6, -1, 7, 2, 2, 'ls_hazard')], children=[shin], role='leg_' + ('r' if s < 0 else 'l'))
    return thigh

ls_head = B('head', (0, -15, -1), (0, 0, 0), [
    C(-4.5, -7, -5, 9, 7, 9, 'ls_plate'), C(-5, -8, -5.5, 10, 2, 8, 'ls_plate_d'), C(-3.5, -4.5, -6, 7, 2, 1, 'ls_eye'),
    C(-2.5, -1, -6.5, 5, 2, 2, 'ls_frame'), C(-5.5, -5, -2, 1, 4, 4, 'ls_plate_d'), C(4.5, -5, -2, 1, 4, 4, 'ls_plate_d'),
    C(2.5, -14, 2, 1, 7, 1, 'ls_frame'), C(2, -15, 1.5, 2, 1, 2, 'ls_red')], role='head')

ls_pod = B('pod', (-8, -16, 2), (0, 0, -8), [
    C(-5, -6, -5, 9, 7, 10, 'ls_plate'), C(-5.5, -1, -5.5, 10, 1, 11, 'ls_hazard'),
    C(-4, -5, -6, 3, 2, 1, 'ls_orange'), C(0, -5, -6, 3, 2, 1, 'ls_orange'), C(-4, -2.5, -6, 3, 1, 1, 'ls_frame'), C(0, -2.5, -6, 3, 1, 1, 'ls_frame')])

ls_claw = [B('claw%d' % i, (-1.5 + i * 1.5, 9, -2), (-25 + i * 5, 0, 0), [C(-0.5, 0, -1, 1, 5, 2, 'ls_frame'), C(-0.5, 4, -2, 1, 2, 1, 'ls_plate_d')]) for i in range(3)]
ls_fore_r = B('fore_r', (-1.5, 8, 0), (-15, 0, 0), [
    C(-3.5, 0, -3.5, 7, 9, 7, 'ls_plate'), C(-4, 2, -4, 8, 2, 8, 'ls_hazard'), C(-2.5, 8, -2.5, 5, 2, 5, 'ls_frame')], children=ls_claw)
ls_arm_r = B('arm_r', (-11, -11, 0), (0, 0, 8), [
    C(-4, -2, -3, 5, 9, 6, 'ls_frame'), C(-6, -5, -4.5, 8, 6, 9, 'ls_plate'), C(-6.5, -2, -5, 9, 1, 10, 'ls_plate_d'),
    C(-2, 4, 2.5, 2, 6, 2, 'ls_piston')], children=[ls_fore_r], role='arm_r')

ls_cannon = B('cannon', (1.5, 8, 0), (-20, 0, 0), [
    C(-3, 0, -5, 7, 8, 9, 'ls_plate'), C(-1, 2, -17, 3, 3, 12, 'ls_frame'), C(-1.5, 1.5, -11, 4, 4, 2, 'ls_plate_d'),
    C(-1.5, 1.5, -15, 4, 4, 1, 'ls_plate_d'), C(-0.5, 2.5, -18, 2, 2, 1, 'ls_red'), C(4, 1, -3, 1, 6, 6, 'ls_frame'),
    C(-3.5, 5, -2, 8, 1, 6, 'ls_hazard')])
ls_arm_l = B('arm_l', (11, -11, 0), (0, 0, -8), [
    C(-1, -2, -3, 5, 9, 6, 'ls_frame'), C(-2, -5, -4.5, 8, 6, 9, 'ls_plate'), C(-2.5, -2, -5, 9, 1, 10, 'ls_plate_d')],
    children=[ls_cannon], role='arm_l')

ls_torso = B('torso', (0, -1, 0), (6, 0, 0), [
    C(-8, -15, -5, 16, 15, 10, 'ls_plate'), C(-6, -13, -6, 12, 7, 1, 'ls_plate_d'), C(-2, -11, -6.5, 4, 3, 1, 'ls_red'),
    C(-8.5, -4, -5.5, 17, 2, 11, 'ls_hazard'), C(-6, -14, 5, 12, 11, 4, 'ls_frame'), C(-5, -12, 9, 3, 2, 1, 'ls_orange'),
    C(2, -12, 9, 3, 2, 1, 'ls_orange'), C(-6, -21, 6, 2, 7, 2, 'ls_piston'), C(4, -21, 6, 2, 7, 2, 'ls_piston'),
    C(-9, -15, -4, 1, 8, 8, 'ls_plate_d'), C(8, -15, -4, 1, 8, 8, 'ls_plate_d')],
    children=[ls_head, ls_pod, ls_arm_r, ls_arm_l], role='body')

ls_hips = B('hips', (0, 5.5, 0), (0, 0, 0), [C(-6, -2, -4, 12, 5, 8, 'ls_frame'), C(-4, 1, -4.5, 8, 2, 9, 'ls_plate_d')], children=[ls_torso])

BOSSES['lunar_sentinel'] = dict(java='lunarSentinel', tex=256, scale=1.35, roots=[ls_hips, ls_leg(-1), ls_leg(1)],
    doc='Lunar Sentinel: Sicherheits-Mech mit Ruecklaufknien, Raketenwerfer auf der Schulter und Laserkanone am linken Arm.',
    # Angriffe: laser, rockets, stomp, drones (Reihenfolge wie in Bosses.LunarSentinel)
    poses={
        0: [(0, 'torso', 25, 0, 0), (0, 'head', 30, 0, 0), (0, 'arm_r', 20, 0, 10), (0, 'arm_l', 20, 0, -10)],
        1: [(0, 'arm_l', 0, 0, 0), (8, 'arm_l', -35, -10, 0), (26, 'arm_l', -35, -10, 0), (32, 'arm_l', 0, 0, 0),
            (0, 'cannon', 0, 0, 0), (8, 'cannon', -15, 0, 0), (26, 'cannon', -15, 0, 0), (32, 'cannon', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (8, 'torso', -5, -15, 0), (26, 'torso', -5, -15, 0), (32, 'torso', 0, 0, 0)],
        2: [(0, 'pod', 0, 0, 0), (6, 'pod', -35, 0, 0), (30, 'pod', -35, 0, 0), (34, 'pod', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (6, 'torso', 8, 0, 0), (32, 'torso', 8, 0, 0), (36, 'torso', 0, 0, 0)],
        3: [(0, 'arm_r', 0, 0, 0), (8, 'arm_r', -150, 0, 0), (13, 'arm_r', -20, 0, 0), (20, 'arm_r', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (8, 'torso', -15, 0, 0), (13, 'torso', 20, 0, 0), (20, 'torso', 0, 0, 0)],
        4: [(0, 'head', 0, 0, 0), (5, 'head', -30, 0, 0), (15, 'head', 0, 0, 0), (0, 'arm_l', 0, 0, 0), (5, 'arm_l', -40, 0, -30), (15, 'arm_l', 0, 0, 0)],
    })


# ── weitere Bosse aus eigenen Modulen (spec_<name>.py), Reihenfolge nach Stufe ──
import importlib, os as _os
_here = _os.path.dirname(_os.path.abspath(__file__))
for _n in ['cryo_behemoth', 'rust_titan', 'monolith_keeper', 'core_abomination', 'magma_wyrm', 'schrab_hydra', 'abyssal_leviathan',
           'polymer_colossus', 'mycelial_queen']:
    if _os.path.exists(_os.path.join(_here, 'spec_%s.py' % _n)):
        importlib.import_module('spec_%s' % _n)
