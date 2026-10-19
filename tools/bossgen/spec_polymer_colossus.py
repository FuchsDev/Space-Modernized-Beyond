"""Polymer Colossus (Tekto): Chemie-Riese aus PVC-Platten und gruenen Rohren, Gasmaske, Fluessigkeitstanks, Rohrzange und Schlauchspritze."""
import math
from model import B, C
from paint import mat
from specs import BOSSES

mat('pc_pvc', 'pvc', (232, 234, 224))
mat('pc_pvc_d', 'pvc', (178, 184, 176))
mat('pc_frame', 'frame', (54, 60, 58))
mat('pc_pipe', 'pipe', (74, 156, 62), (40, 92, 34))
mat('pc_pipe_d', 'pipe', (128, 134, 138), (84, 88, 92))
mat('pc_hazard', 'hazard', (230, 190, 30))
mat('pc_fluid', 'glow', (70, 180, 20), (226, 255, 130))
mat('pc_eye', 'eye', (24, 56, 0), (214, 255, 96))
mat('pc_steel', 'plate', (112, 118, 126), (176, 182, 190))
mat('pc_rust', 'rust', (118, 110, 102), (150, 92, 44))
mat('pc_rubber', 'plain', (42, 46, 44))
mat('pc_bubble', 'glow', (200, 255, 150), (250, 255, 230))
mat('pc_red', 'plate', (168, 40, 30), (220, 120, 90))


def _mc(cubes):
    return [C(-c.x - c.w, c.y, c.z, c.w, c.h, c.d, c.mat, c.infl) for c in cubes]


_ROLE_MIRROR = {'wing_r': 'wing_l', 'arm_r': 'arm_l', 'leg_r': 'leg_l'}


def _mirror(b, a='_r', z='_l'):
    rx, ry, rz = (math.degrees(v) for v in b.rot)
    return B(b.name.replace(a, z), (-b.pivot[0], b.pivot[1], b.pivot[2]), (rx, -ry, -rz), _mc(b.cubes),
             [_mirror(ch, a, z) for ch in b.children], role=_ROLE_MIRROR.get(b.role, b.role), param=b.param)


def _ch(x, y, z, w, h, d, m, k=1):
    """Gefaster Block aus drei Wuerfeln."""
    return [C(x, y + k, z + k, w, h - 2 * k, d - 2 * k, m), C(x + k, y, z + k, w - 2 * k, h, d - 2 * k, m),
            C(x + k, y + k, z, w - 2 * k, h - 2 * k, d, m)]


def _cyl_y(x, y, z, r, h, m):
    """Achtkantiger Zylinder entlang y (x/z = Mitte, Durchmesser 2r)."""
    k = max(1, round(r * 0.6))
    return [C(x - r, y, z - k, 2 * r, h, 2 * k, m), C(x - k, y, z - r, 2 * k, h, 2 * r, m)]


def _cyl_z(x, y, z, r, d, m):
    """Achtkantiger Zylinder entlang z (x/y = Mitte)."""
    k = max(1, round(r * 0.6))
    return [C(x - r, y - k, z, 2 * r, 2 * k, d, m), C(x - k, y - r, z, 2 * k, 2 * r, d, m)]


# ── Beine ───────────────────────────────────────────────────────────────────────
pc_foot_r = B('foot_r', (0, 6, 0), (0, 0, 0), [
    C(-4, 0, -6, 8, 3, 10, 'pc_pvc_d'), C(-4.5, 0.5, -7.5, 9, 2.5, 3, 'pc_hazard'), C(-3, -1, -4, 6, 1, 6, 'pc_frame'),
    C(-3.5, 1, 4, 7, 2, 2, 'pc_frame')])
pc_shin_r = B('shin_r', (0, 7, 0), (0, 0, 0), [
    C(-3, 0, -3, 6, 6, 6, 'pc_frame'), C(-3.5, -0.5, -4.2, 7, 6, 2, 'pc_pvc'), C(-3.8, 0, -2.5, 1, 5, 5, 'pc_pvc_d'),
    C(-3, -2, -4.8, 6, 3, 2, 'pc_pvc_d'), C(-1, -2, 2.5, 2, 8, 2, 'pc_pipe')], children=[pc_foot_r])
pc_leg_r = B('leg_r', (-5, 6, 0), (0, 0, 0), [
    C(-3.5, -1, -3.5, 7, 8, 7, 'pc_pvc'), C(-4, 3, -4, 8, 2, 8, 'pc_pipe'), C(-3, 0, -4.2, 6, 3, 1, 'pc_pvc_d'),
    C(-4.2, -1, -2, 1, 6, 4, 'pc_frame')], children=[pc_shin_r], role='leg_r')
pc_tasset_r = B('tasset_r', (-8, -1, 0), (0, 0, 10), [C(-1.5, 0, -4.5, 2, 7, 9, 'pc_pvc_d'), C(-2, 5, -5, 2, 2, 10, 'pc_hazard')])

# ── Kopf: Gasmaske mit Linsenaugen und Filter ───────────────────────────────────
pc_filter = B('filter', (0, -3.5, -6), (28, 0, 0), [
    C(-2.5, -2, -2, 5, 4, 3, 'pc_rubber')] + _cyl_z(0, 0, -7, 3, 5, 'pc_pipe') + [
    C(-2, -2, -7.6, 4, 4, 1, 'pc_frame'), C(-3.2, -1, -4, 1, 2, 2, 'pc_hazard'), C(2.2, -1, -4, 1, 2, 2, 'pc_hazard')])
pc_mhose_r = B('mhose_r', (-5, -3, -3), (35, 0, 20), [C(-1, 0, -1, 2, 7, 2, 'pc_pipe'), C(-1.5, 0, -1.5, 3, 1, 3, 'pc_pipe_d')])
pc_head = B('head', (0, -21, -1.5), (-9, 0, 0),
    _ch(-5.5, -12, -5, 11, 11, 10, 'pc_pvc') + [
    C(-3, -1, -3, 6, 3, 6, 'pc_frame'),                                 # Hals
    C(-1.5, -13, -5.5, 3, 2, 11, 'pc_hazard'),                          # Helmkamm
    C(-5, -9.5, -6, 10, 9, 2, 'pc_rubber'),                             # Maskenplatte
    C(-5.2, -9, -6.6, 4, 4, 1, 'pc_steel'), C(1.2, -9, -6.6, 4, 4, 1, 'pc_steel'),   # Linsenringe
    C(-4.7, -8.5, -7.2, 3, 3, 1, 'pc_eye'), C(1.7, -8.5, -7.2, 3, 3, 1, 'pc_eye'),   # Linsen
    C(-6.5, -5, -4, 1, 3, 3, 'pc_pipe'), C(5.5, -5, -4, 1, 3, 3, 'pc_pipe'),         # Seitenventile
    C(-6, -10, -2, 1, 5, 5, 'pc_pvc_d'), C(5, -10, -2, 1, 5, 5, 'pc_pvc_d'),         # Ohrschalen
    C(2.5, -16, 2, 2, 5, 2, 'pc_pipe_d'), C(2, -17, 1.5, 3, 1, 3, 'pc_frame'),        # Abluftstutzen
], children=[pc_filter, pc_mhose_r, _mirror(pc_mhose_r)], role='head')

# ── Rechter Arm: riesige Rohrzange ──────────────────────────────────────────────
pc_wrench = B('wrench', (0, 9, 0), (0, 0, 0), [
    C(-3.5, -1, -3.5, 7, 3, 7, 'pc_frame'),                             # Handgelenk
    *_cyl_y(0, 1, 0, 2, 4, 'pc_steel'),                                 # Schaft
    *_ch(-4.5, 4, -4, 9, 11, 9, 'pc_red'),                              # Gehaeuse (rot lackiert)
    C(-5, 6, -4.5, 10, 2, 10, 'pc_hazard'),
    C(-4, 4, -14, 8, 4, 11, 'pc_steel'),                                # Hakenbacke (fest, oben)
    C(-4, 8, -14, 8, 2, 3, 'pc_steel'), C(-3.5, 9.5, -14, 7, 1, 3, 'pc_frame'),   # Hakenspitze mit Zaehnen
    C(-3.5, 12, -12, 7, 3, 9, 'pc_steel'), C(-3, 11, -12, 6, 1, 3, 'pc_frame'),   # Unterbacke (verstellbar)
    *_cyl_z(0, 10, -6, 2, 3, 'pc_pipe_d'),                              # Stellmutter
    C(-4.8, 9, -3, 1, 5, 6, 'pc_rust'), C(3.8, 9, -3, 1, 5, 6, 'pc_rust'),
    C(-2, 15, -2, 4, 2, 4, 'pc_steel')])
pc_fore_r = B('fore_r', (-0.5, 9, 0), (-12, 0, 0),
    _ch(-4.5, 0, -4.5, 9, 10, 9, 'pc_pvc') + [C(-5, 6, -5, 10, 2, 10, 'pc_pipe'), C(-5.2, 1, -2, 1, 4, 4, 'pc_hazard')],
    children=[pc_wrench])
pc_arm_r = B('arm_r', (-14, -16, 0), (0, 0, 6),
    _ch(-7, -6, -6.5, 12, 7, 13, 'pc_pvc', 2) + [
    C(-7.5, 0, -7, 13, 2, 14, 'pc_hazard'),                             # Schulterpanzer-Saum
    C(-6, -8, -2, 2, 3, 4, 'pc_pipe_d'),
    C(-3.5, 1, -3.5, 7, 9, 7, 'pc_frame'), C(-4, 3, -4, 8, 4, 8, 'pc_pvc_d')], children=[pc_fore_r], role='arm_r')

# ── Linker Arm: Schlauchspritze ─────────────────────────────────────────────────
pc_nozzle = B('nozzle', (0.5, 9, 0), (0, 0, 0), [
    C(-3, -1, -3, 6, 5, 6, 'pc_rubber'),                                # Handschuh
    *_cyl_z(0, 2, -13, 2, 11, 'pc_pipe'),                               # Spritzrohr
    C(-2.5, -0.5, -9, 5, 5, 2, 'pc_pipe_d'), C(-2.5, -0.5, -12, 5, 5, 1, 'pc_pipe_d'),
    C(-3, -1, -16, 6, 6, 3, 'pc_steel'),                                # Duese
    C(-2, 0, -16.6, 4, 4, 1, 'pc_fluid'),
    C(-1, 4, -6, 2, 4, 2, 'pc_frame'), C(-1, 7, -6, 2, 1, 4, 'pc_frame')])   # Abzug/Griff
pc_fore_l = B('fore_l', (0.5, 9, 0), (-12, 0, 0),
    _ch(-4, 0, -4, 8, 10, 8, 'pc_pvc') + [
    C(-4.5, 1, -4.5, 9, 2, 9, 'pc_pipe'), C(-4.5, 5, -4.5, 9, 2, 9, 'pc_pipe'),   # Schlauchwindungen
    C(3.5, -2, 1, 2, 12, 2, 'pc_pipe')], children=[pc_nozzle])
pc_arm_l = B('arm_l', (14, -16, 0), (0, 0, -6),
    _mc(_ch(-7, -6, -6.5, 12, 7, 13, 'pc_pvc', 2)) + [
    C(-5.5, 0, -7, 13, 2, 14, 'pc_hazard'),
    C(4, -8, -2, 2, 3, 4, 'pc_pipe_d'),
    C(-3.5, 1, -3.5, 7, 9, 7, 'pc_frame'), C(-4, 3, -4, 8, 4, 8, 'pc_pvc_d')], children=[pc_fore_l], role='arm_l')

# ── Ruecken: zwei Tanks mit blubbernder Fluessigkeit ────────────────────────────
def _tank(x):
    return (_cyl_y(x, -7, 3.5, 3, 14, 'pc_fluid') + [
        C(x - 3.5, -9, 0, 7, 2, 7, 'pc_pvc'), C(x - 3.5, 7, 0, 7, 2, 7, 'pc_pvc'),      # Deckel/Boden
        C(x - 3.6, -1, 0, 7, 1, 7, 'pc_frame'),                                           # Mittelband
        C(x - 1, -11, 2.5, 2, 2, 2, 'pc_pipe_d'), C(x - 2, -12, 2, 4, 1, 3, 'pc_frame'),  # Ventilrad
        C(x - 0.5, -8, 6.8, 1, 15, 1, 'pc_frame'),                                        # Kaefigstrebe
        C(x - 2, 3, 6.4, 1, 1, 1, 'pc_bubble'), C(x + 1, -3, 6.4, 1, 1, 1, 'pc_bubble'), C(x - 2, -5, 6.4, 1, 1, 1, 'pc_bubble')])


pc_tanks = B('tanks', (0, -12, 6), (-4, 0, 0), [C(-9, -8, -1, 18, 16, 2, 'pc_frame')] + _tank(-4.5) + _tank(4.5) + [
    C(-1, -10, 1, 2, 2, 7, 'pc_pipe'), C(-0.5, 0, 6.5, 1, 1, 1, 'pc_fluid')])
pc_hose_r = B('hose_r', (-4.5, -23, 8.5), (12, 0, 0), [C(-1, -1, -7, 2, 2, 8, 'pc_pipe'), C(-1.5, -1.5, -5, 3, 3, 1, 'pc_pipe_d'), C(-1.5, -1.5, 0, 3, 3, 1, 'pc_pipe_d')])

# ── Rumpf ───────────────────────────────────────────────────────────────────────
pc_torso = B('torso', (0, -1, 0), (7, 0, 0),
    _ch(-11, -20, -6.5, 22, 14, 13, 'pc_pvc', 2) + [
    C(-6, -8, -4, 12, 8, 8, 'pc_frame'),                                 # Taille (Mechanik)
    C(-6.5, -6, -4.5, 13, 2, 9, 'pc_pipe'), C(-6.5, -3, -4.5, 13, 2, 9, 'pc_pipe'),
    C(-8, -18, -7.5, 16, 9, 1, 'pc_pvc_d'),                              # Brustplatte
    C(-3, -16.5, -8.2, 6, 6, 1, 'pc_frame'), C(-2, -15.5, -8.8, 4, 4, 1, 'pc_fluid'),   # Messfenster
    C(-11.5, -8, -7, 23, 2, 14, 'pc_hazard'),                            # Warnband
    C(-6.5, -23, -4.5, 13, 3, 9, 'pc_frame'),                            # Kragen
    C(-7.5, -12, -8, 2, 3, 1, 'pc_frame'), C(5.5, -12, -8, 2, 3, 1, 'pc_frame'),        # Luftschlitze
    C(-11.8, -17, -3, 1, 7, 6, 'pc_pvc_d'), C(10.8, -17, -3, 1, 7, 6, 'pc_pvc_d'),
], children=[pc_head, pc_arm_r, pc_arm_l, pc_tanks, pc_hose_r, _mirror(pc_hose_r)], role='body')

pc_hips = B('hips', (0, 2, 0), (0, 0, 0),
    _ch(-8, -2, -5, 16, 7, 10, 'pc_frame') + [
    C(-8.5, -1, -5.5, 17, 2, 11, 'pc_hazard'), C(-4, 1, -6.5, 8, 5, 2, 'pc_pvc'), C(-2, 2, -7, 4, 2, 1, 'pc_fluid')],
    children=[pc_torso, pc_leg_r, _mirror(pc_leg_r), pc_tasset_r, _mirror(pc_tasset_r)])

BOSSES['polymer_colossus'] = dict(java='polymerColossus', tex=256, scale=1.3, pscale=4, hitbox=(2.8, 4.4), roots=[pc_hips],
    doc='Polymer Colossus: Chemie-Riese aus weissen PVC-Platten und gruenen Rohren mit Gasmaske, Linsenaugen und Filter, '
        'leuchtenden Chlortanks auf dem Ruecken, Rohrzange am rechten und Schlauchspritze am linken Arm.',
    # Angriffe: chlorine, pipe_slam, plastic_barrage, toxic_beam (Reihenfolge wie in Bosses.PolymerColossus)
    poses={
        # ruhend: vornuebergesunken, Arme haengen, Zange am Boden
        0: [(0, 'torso', 18, 0, 0), (0, 'head', 15, 0, 0), (0, 'arm_r', -12, 0, 6), (0, 'arm_l', -14, 0, -6), (0, 'fore_r', 10, 0, 0), (0, 'nozzle', 30, 0, 0)],
        # chlorine (sofort, ~20): Hohlkreuz, Kopf hoch, Spritze in die Luft - Tanks entlueften
        1: [(0, 'torso', 0, 0, 0), (6, 'torso', -12, 0, 0), (14, 'torso', -12, 0, 0), (20, 'torso', 0, 0, 0),
            (0, 'head', 0, 0, 0), (6, 'head', -22, 0, 0), (14, 'head', -22, 0, 0), (20, 'head', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (6, 'arm_l', -140, 0, -20), (14, 'arm_l', -140, 0, -20), (20, 'arm_l', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (6, 'arm_r', 0, 0, 25), (14, 'arm_r', 0, 0, 25), (20, 'arm_r', 0, 0, 0)],
        # pipe_slam (18): Zange ueber den Kopf, bei 13-14 Schlag auf den Boden
        2: [(0, 'arm_r', 0, 0, 0), (8, 'arm_r', -170, 0, 10), (13, 'arm_r', -40, 0, 0), (15, 'arm_r', -40, 0, 0), (18, 'arm_r', 0, 0, 0),
            (0, 'fore_r', 0, 0, 0), (8, 'fore_r', -35, 0, 0), (13, 'fore_r', 0, 0, 0), (18, 'fore_r', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (8, 'torso', -14, 10, 0), (13, 'torso', 24, 0, 0), (15, 'torso', 24, 0, 0), (18, 'torso', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (8, 'arm_l', -20, 0, -30), (13, 'arm_l', 10, 0, -15), (18, 'arm_l', 0, 0, 0)],
        # plastic_barrage (26, Schuss alle 5 Ticks): Spritze nach vorn, Rueckstoss je Schuss
        3: [(0, 'arm_l', 0, 0, 0), (4, 'arm_l', -85, -10, 0), (7, 'arm_l', -74, -10, 0), (10, 'arm_l', -85, -10, 0), (12, 'arm_l', -74, -10, 0),
            (15, 'arm_l', -85, -10, 0), (17, 'arm_l', -74, -10, 0), (20, 'arm_l', -85, -10, 0), (22, 'arm_l', -74, -10, 0), (24, 'arm_l', -80, -10, 0), (26, 'arm_l', 0, 0, 0),
            (0, 'fore_l', 0, 0, 0), (4, 'fore_l', 10, 0, 0), (24, 'fore_l', 10, 0, 0), (26, 'fore_l', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (4, 'torso', 0, -18, 0), (24, 'torso', 0, -18, 0), (26, 'torso', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (4, 'arm_r', 15, 0, 12), (24, 'arm_r', 15, 0, 12), (26, 'arm_r', 0, 0, 0)],
        # toxic_beam (18, Strahl bei 16 aus dem Visier): aufrichten, Arme breit, Kopf nach vorn stossen
        4: [(0, 'head', 0, 0, 0), (8, 'head', -18, 0, 0), (14, 'head', -18, 0, 0), (16, 'head', 10, 0, 0), (18, 'head', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (8, 'torso', -12, 0, 0), (14, 'torso', -12, 0, 0), (16, 'torso', 10, 0, 0), (18, 'torso', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (8, 'arm_r', -25, 0, 40), (16, 'arm_r', -25, 0, 40), (18, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (8, 'arm_l', -25, 0, -40), (16, 'arm_l', -25, 0, -40), (18, 'arm_l', 0, 0, 0)],
    })
