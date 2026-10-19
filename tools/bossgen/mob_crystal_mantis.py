"""Crystal Mantis (Eve): aufgerichtete Gottesanbeterin aus violettem Chitin mit tuerkisen Schrabidium-Kristallen -
Fangarme mit Kristallklingen, schmaler Dreieckskopf mit Facettenaugen und Fuehlern, vier duenne Laufbeine,
Kristallfluegel auf dem Hinterleib."""
import math
from model import B, C
from paint import mat
from mobs import MOBS

mat('mc_chitin', 'scale', (104, 58, 150), (176, 126, 222))
mat('mc_chitin_d', 'scale', (62, 34, 96), (120, 76, 170))
mat('mc_plate', 'plate', (120, 70, 168), (200, 160, 240))
mat('mc_joint', 'obsidian', (34, 22, 46), (150, 110, 210))
mat('mc_crystal', 'crystal', (24, 170, 168), (150, 255, 236))
mat('mc_crystal_d', 'crystal', (18, 110, 120), (90, 230, 220))
mat('mc_wing', 'ice', (70, 150, 160), (36, 90, 120))
mat('mc_glow', 'glow', (0, 190, 180), (200, 255, 245))
mat('mc_eye', 'eye', (0, 70, 70), (150, 255, 230))


def _mc(cubes):
    return [C(-c.x - c.w, c.y, c.z, c.w, c.h, c.d, c.mat, c.infl) for c in cubes]


_RM = {'arm_l': 'arm_r', 'leg_l': 'leg_r'}


def _mirror(b, role=None):
    """Spiegelt einen linken Knochenbaum (+x) auf die rechte Seite (-x); Namen *_l -> *_r."""
    rx, ry, rz = (math.degrees(v) for v in b.rot)
    return B(b.name[:-2] + '_r', (-b.pivot[0], b.pivot[1], b.pivot[2]), (rx, -ry, -rz), _mc(b.cubes), [_mirror(ch) for ch in b.children],
             role=role or _RM.get(b.role, b.role), param=b.param)


def shard(name, pos, rot, h, m='mc_crystal'):
    """Kristallspitze: breiter Fuss, schmale leuchtende Spitze."""
    return B(name, pos, rot, [C(-1, -h, -1, 2, h, 2, m), C(-0.5, -h - 1.5, -0.5, 1, 2, 1, 'mc_glow')])

# ── Kopf ──
def antenna_l():
    tip = B('antip_l', (0, -5, 0), (-35, 0, 0), [C(-0.5, -5, -0.5, 1, 5, 1, 'mc_chitin_d'), C(-0.5, -6, -0.5, 1, 1, 1, 'mc_glow')])
    return B('antenna_l', (1, -3.2, -1.5), (-30, 0, 18), [C(-0.5, -5, -0.5, 1, 5, 1, 'mc_chitin_d')], children=[tip])


mc_head = B('head', (0, -14.5, -0.5), (10, 0, 0), [
    C(-3, -3.5, -2, 6, 3, 3.5, 'mc_chitin'),                                          # Stirn zwischen den Augen
    C(-5.5, -4.5, -2.2, 3, 3, 3, 'mc_eye'), C(2.5, -4.5, -2.2, 3, 3, 3, 'mc_eye'),              # Facettenaugen
    C(-2, -0.5, -2, 4, 2, 2.5, 'mc_chitin'),                                        # Gesicht
    C(-1, 1.5, -1.8, 2, 2, 2, 'mc_chitin_d'),                                        # Kinnspitze
    C(-1.5, 3, -2.2, 1, 1, 1, 'mc_joint'), C(0.5, 3, -2.2, 1, 1, 1, 'mc_joint'),    # Mandibeln
    C(-1.5, -3.7, -2.3, 1, 1, 1, 'mc_glow'), C(0.5, -3.7, -2.3, 1, 1, 1, 'mc_glow'), # Punktaugen
    C(-2.5, -4.5, -1, 5, 1, 2, 'mc_crystal')],                                      # Kristallkrone
    children=[antenna_l(), _mirror(antenna_l())], role='head')

# ── Fangarme ──
def raptor_l():
    tibia = B('tibia_l', (0, 8.5, -0.5), (150, 0, 0), [
        C(-1, 0, -1, 2, 7, 2, 'mc_chitin'), C(-0.5, 0.5, -2.5, 1, 6, 2, 'mc_crystal'),   # Schiene mit Kristallklinge
        C(-0.5, 6.5, -2, 1, 2, 1, 'mc_crystal_d'), C(-0.5, 8, -2, 1, 1, 1, 'mc_glow'), C(-0.5, 1, 1, 1, 5, 1, 'mc_joint')])
    femur = B('femur_l', (0, 5.5, 0), (-140, 0, 0), [
        C(-1, 0, -1.5, 2, 9, 3, 'mc_chitin'), C(-1.2, 0.5, -1.7, 1, 7, 2, 'mc_plate', 0.15),
        C(-0.5, 1, 1.5, 1, 7, 1, 'mc_crystal'), C(-0.5, 2.5, 2.5, 1, 4, 1, 'mc_crystal'), C(-0.5, 7, 1.5, 1, 2, 2, 'mc_glow'),
        C(-1.5, -0.5, -2, 3, 1.5, 4, 'mc_joint')], children=[tibia])
    return B('arm_l', (2, -10, -1.5), (-30, -10, -15), [
        C(-1, -0.5, -1, 2, 6, 2, 'mc_chitin_d'), C(-1.4, 0, -1.4, 1, 4, 1, 'mc_plate', 0.2), C(-1.2, -1, -1.2, 2, 2, 2, 'mc_joint', 0.2)],
        children=[femur], role='arm_l')

# ── Vorderbrust (aufgerichtet) ──
mc_thorax = B('thorax', (0, -1.5, -2), (22, 0, 0), [
    C(-2, -14, -2, 4, 15, 4, 'mc_chitin'),                                           # Halsschild
    C(-2.5, -13.5, -2.3, 5, 3, 4.6, 'mc_plate'), C(-2.5, -8, -2.3, 5, 3, 4.6, 'mc_plate'), C(-2.5, -3, -2.3, 5, 2, 4.6, 'mc_plate'),
    C(-1.5, -14, -2.5, 3, 14, 1, 'mc_chitin_d'),                                      # Bauchseite
    C(-3, -11, -2, 1, 3, 3, 'mc_joint'), C(2, -11, -2, 1, 3, 3, 'mc_joint')],      # Armgelenke
    children=[mc_head, raptor_l(), _mirror(raptor_l()),
              shard('tspike_1', (0, -12, 2), (45, 0, 0), 2), shard('tspike_2', (0, -6.5, 2), (50, 0, 0), 3, 'mc_crystal_d')])

# ── Laufbeine ──
def walk_l(name, z, ry, role):
    foot = B('tars_%s_l' % name, (13, 0, 0), (0, 0, -35), [C(0, -0.5, -0.5, 3, 1, 1, 'mc_joint')])
    tib = B('tib_%s_l' % name, (6, 0, 0), (0, 0, 108), [
        C(0, -0.5, -0.5, 13, 1, 1, 'mc_chitin_d', 0.15), C(5, -0.6, -0.6, 2, 1, 1, 'mc_crystal', 0.1)], children=[foot])
    return B('leg_%s_l' % name, (2.5, 1, z), (0, ry, -38), [
        C(-0.5, -1, -1, 7, 2, 2, 'mc_chitin'), C(5, -1.2, -1.2, 2, 2, 2, 'mc_joint', 0.15)], children=[tib], role=role)

# ── Hinterleib mit Kristallfluegeln ──
mc_abdomen = B('abdomen', (0, 0, 3.5), (-12, 0, 0), [
    C(-3.5, -2.5, 0, 7, 6, 5, 'mc_chitin'), C(-3, -2, 5, 6, 5, 4, 'mc_chitin_d'), C(-2, -1.5, 9, 4, 4, 3, 'mc_chitin'),
    C(-1, -0.5, 12, 2, 2, 2, 'mc_chitin_d'),
    C(-3, 3.5, 0.5, 6, 1, 4, 'mc_plate'), C(-2.5, 3, 5.5, 5, 1, 3, 'mc_plate'),                  # Bauchplatten
    C(-3.8, -3.2, -2, 4, 1, 12, 'mc_wing'), C(-0.2, -3.4, -2, 4, 1, 12, 'mc_wing'),             # gefaltete Fluegel
    C(-0.5, -3.8, -1, 1, 1, 10, 'mc_crystal')],
    children=[shard('aspike_1', (-2, -3.5, 3), (-30, 0, -20), 3), shard('aspike_2', (2, -3.5, 6), (-25, 0, 25), 4),
              shard('aspike_3', (0, -3, 9), (-45, 0, 0), 3, 'mc_crystal_d')])

mc_body = B('body', (0, 12, 1), (0, 0, 0), [
    C(-2.5, -2, -3, 5, 4, 7, 'mc_chitin'), C(-3, -2.5, -2, 6, 1, 5, 'mc_plate'), C(-2, 1.5, -2.5, 4, 1, 6, 'mc_chitin_d')],
    children=[mc_thorax, mc_abdomen,
              walk_l('m', -1.5, 35, 'leg_b'), _mirror(walk_l('m', -1.5, 35, 'leg_b'), 'leg_a'),
              walk_l('h', 2.5, -40, 'leg_a'), _mirror(walk_l('h', 2.5, -40, 'leg_a'), 'leg_b')],
    role='body')

MOBS['crystal_mantis'] = dict(java='crystalMantis', tex=128, scale=1.0, roots=[mc_body], pscale=8,
    doc='Crystal Mantis: aufgerichtete Gottesanbeterin aus violettem Chitin mit Fangarmen und tuerkisen Schrabidium-'
        'Kristallklingen, schmalem Kopf mit Facettenaugen und Fuehlern, vier Laufbeinen und Kristallfluegeln.',
    poses={
        1: [(0, 'thorax', 0, 0, 0), (3, 'thorax', -14, 0, 0), (5, 'thorax', 22, 0, 0), (10, 'thorax', 0, 0, 0),
            (0, 'head', 0, 0, 0), (3, 'head', 10, 0, 0), (5, 'head', -15, 0, 0), (10, 'head', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (3, 'arm_r', -45, 0, 0), (5, 'arm_r', 10, 0, 0), (10, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (3, 'arm_l', -45, 0, 0), (5, 'arm_l', 10, 0, 0), (10, 'arm_l', 0, 0, 0),
            (0, 'femur_r', 0, 0, 0), (3, 'femur_r', -25, 0, 0), (5, 'femur_r', 70, 0, 0), (10, 'femur_r', 0, 0, 0),
            (0, 'femur_l', 0, 0, 0), (3, 'femur_l', -25, 0, 0), (5, 'femur_l', 70, 0, 0), (10, 'femur_l', 0, 0, 0),
            (0, 'tibia_r', 0, 0, 0), (3, 'tibia_r', -70, 0, 0), (5, 'tibia_r', 5, 0, 0), (10, 'tibia_r', 0, 0, 0),
            (0, 'tibia_l', 0, 0, 0), (3, 'tibia_l', -70, 0, 0), (5, 'tibia_l', 5, 0, 0), (10, 'tibia_l', 0, 0, 0)],
        3: [(0, 'body', 0, 0, 0), (4, 'body', 8, 0, 0), (8, 'body', -15, 0, 0), (14, 'body', 0, 0, 0),
            (0, 'thorax', 0, 0, 0), (4, 'thorax', 12, 0, 0), (8, 'thorax', -10, 0, 0), (14, 'thorax', 0, 0, 0),
            (0, 'abdomen', 0, 0, 0), (4, 'abdomen', 6, 0, 0), (8, 'abdomen', 12, 0, 0), (14, 'abdomen', 0, 0, 0),
            (0, 'leg_m_l', 0, 0, 0), (4, 'leg_m_l', 0, 0, -18), (8, 'leg_m_l', -25, 0, 25), (14, 'leg_m_l', 0, 0, 0),
            (0, 'leg_m_r', 0, 0, 0), (4, 'leg_m_r', 0, 0, 18), (8, 'leg_m_r', -25, 0, -25), (14, 'leg_m_r', 0, 0, 0),
            (0, 'leg_h_l', 0, 0, 0), (4, 'leg_h_l', 0, 0, -18), (8, 'leg_h_l', 25, 0, 30), (14, 'leg_h_l', 0, 0, 0),
            (0, 'leg_h_r', 0, 0, 0), (4, 'leg_h_r', 0, 0, 18), (8, 'leg_h_r', 25, 0, -30), (14, 'leg_h_r', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (4, 'arm_r', 15, 0, 0), (8, 'arm_r', -40, 0, 0), (14, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (4, 'arm_l', 15, 0, 0), (8, 'arm_l', -40, 0, 0), (14, 'arm_l', 0, 0, 0)],
    })
