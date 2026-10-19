"""Refinery Drone (Tekto): Quadrokopter der Raffinerie mit gelb-schwarzen Warnstreifen, Brennstofftank auf dem Ruecken,
Sensorkopf und Flammenwerfer-Duese mit Glut unter der Nase."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('rd_plate', 'plate', (92, 96, 104), (156, 162, 172))
mat('rd_yellow', 'plate', (214, 168, 30), (250, 222, 120))
mat('rd_hazard', 'hazard', (230, 190, 30))
mat('rd_frame', 'frame', (52, 54, 60))
mat('rd_steel', 'pipe', (168, 172, 178), (110, 114, 120))
mat('rd_dark', 'plate', (40, 40, 44), (84, 86, 92))
mat('rd_tank', 'pipe', (186, 64, 34), (120, 36, 20))
mat('rd_soot', 'rust', (70, 66, 62), (34, 30, 28))
mat('rd_blade', 'plate', (60, 62, 68), (120, 124, 132))
mat('rd_ember', 'glow', (210, 60, 10), (255, 214, 90))
mat('rd_flame', 'glow', (40, 120, 255), (200, 240, 255))
mat('rd_eye', 'eye', (90, 20, 0), (255, 150, 40))
mat('rd_led_r', 'glow', (200, 20, 20), (255, 140, 120))
mat('rd_led_g', 'glow', (20, 170, 40), (160, 255, 160))
mat('rd_gauge', 'glow', (180, 120, 10), (255, 236, 150))
mat('rd_hose', 'pipe', (44, 44, 46), (80, 80, 84))


def _cyl_z(x, y, z, r, d, m):
    """Achtkantiger Zylinder entlang z (x/y = Mitte)."""
    k = max(1, round(r * 0.6))
    return [C(x - r, y - k, z, 2 * r, 2 * k, d, m), C(x - k, y - r, z, 2 * k, 2 * r, d, m)]


def _cyl_x(x, y, z, r, w, m):
    """Achtkantiger Zylinder entlang x (y/z = Mitte)."""
    k = max(1, round(r * 0.6))
    return [C(x, y - k, z - r, w, 2 * k, 2 * r, m), C(x, y - r, z - k, w, 2 * r, 2 * k, m)]


# ── Rotorausleger (X-Anordnung) ──────────────────────────────────────────────────
def _arm(name, ry, led, phase):
    rotor = B(name + '_rotor', (0, -4.5, -11), (0, 30 + phase * 37, 4), [
        C(-1, -1, -1, 2, 1, 2, 'rd_dark'),                                  # Nabe
        C(-6, -0.5, -0.5, 5, 1, 1, 'rd_blade'), C(1, -0.5, -0.5, 5, 1, 1, 'rd_blade'),
        C(-6.5, -0.5, -0.8, 1, 1, 2, 'rd_hazard'), C(5.5, -0.5, -0.8, 1, 1, 2, 'rd_hazard')])   # Blattspitzen
    return B(name, (0, -2, 0), (0, ry, 0), [
        C(-1, -1, -10, 2, 2, 6, 'rd_frame'),                                # Ausleger
        C(-0.5, 1, -9, 1, 1, 4, 'rd_steel'),                                # Strebe
        C(-1.5, -3, -12.5, 3, 4, 3, 'rd_yellow'),                           # Motorgehaeuse
        C(-2, -1.5, -13, 4, 1, 4, 'rd_hazard'),                             # Warnring
        C(-1, -4, -12, 2, 1, 2, 'rd_dark'),                                 # Motorkopf
        C(-0.5, 0.5, -13.4, 1, 1, 1, led)],                                 # Positionslicht
        children=[rotor])


rd_arms = [_arm('arm_fr', 45, 'rd_led_g', 0), _arm('arm_fl', -45, 'rd_led_r', 1),
           _arm('arm_br', 135, 'rd_led_g', 2), _arm('arm_bl', -135, 'rd_led_r', 3)]

# ── Sensorkopf ──────────────────────────────────────────────────────────────────
rd_head = B('head', (0, -4, -3.5), (0, 0, 0), [
    C(-2.5, -3, -3, 5, 3, 4, 'rd_dark'), C(-3, -3.5, -3.4, 6, 1, 5, 'rd_yellow'),
    C(-2, -2.4, -3.5, 4, 2, 1, 'rd_eye'),                                   # Kameraauge
    C(2, -6, 0, 1, 3, 1, 'rd_frame'), C(1.5, -7, -0.5, 2, 1, 2, 'rd_led_r')],   # Antenne
    role='head')

# ── Flammenduese ─────────────────────────────────────────────────────────────────
rd_nozzle = B('nozzle', (0, 2.5, -4), (12, 0, 0), [
    C(-2.5, -1.5, -2, 5, 4, 4, 'rd_plate'),                                 # Lafette
    *_cyl_z(0, 0.5, -9, 1.5, 7, 'rd_steel'),                                # Rohr
    C(-2, -1, -5, 4, 3, 1, 'rd_frame'), C(-2, -1, -7, 4, 3, 1, 'rd_frame'), # Kuehlrippen
    C(-2, -1.5, -11, 4, 4, 2, 'rd_soot'),                                   # verrusste Muendung
    C(-1, -0.5, -11.4, 2, 2, 1, 'rd_ember'),                                # Glut
    C(-0.5, 2.5, -10.5, 1, 1, 1, 'rd_flame'),                               # Zuendflamme
    C(-0.5, 2, -6, 1, 1, 5, 'rd_hose')])                                    # Zuendleitung

# ── Rumpf ───────────────────────────────────────────────────────────────────────
rd_body = B('drone', (0, 17, 0), (0, 0, 0), [
    C(-5, -2, -5, 10, 4, 10, 'rd_plate'), C(-4, -3, -5, 8, 6, 10, 'rd_plate'), C(-4, -2, -6, 8, 4, 12, 'rd_plate'),
    C(-4, -4, -5, 8, 1, 9, 'rd_yellow'),                                    # Deckplatte
    C(-5.3, 0, -5.3, 11, 1, 11, 'rd_hazard'),                               # Warnband
    C(-3, -2, -6.5, 6, 2, 1, 'rd_hazard'),                                  # Warnblech vorn
    C(-3, 0.5, -6.6, 6, 1, 1, 'rd_frame'),                                  # Frontgrill
    C(-5.4, -2, -3, 1, 2, 6, 'rd_yellow'), C(4.4, -2, -3, 1, 2, 6, 'rd_yellow'),   # Seitenklappen
    C(-2, 3, -3, 4, 1, 6, 'rd_frame'),                                      # Unterboden
    # Tank auf dem Ruecken
    *_cyl_x(-4.5, -7, 2, 3, 9, 'rd_tank'),
    C(-5, -8, 1, 1, 2, 2, 'rd_frame'), C(4, -8, 1, 1, 2, 2, 'rd_frame'),   # Endkappen
    C(-2.5, -10, -1, 1, 6, 6, 'rd_frame', 0.2), C(1.5, -10, -1, 1, 6, 6, 'rd_frame', 0.2),   # Spannbaender
    C(-1.5, -10.4, 0.5, 3, 1, 3, 'rd_hazard'),
    C(-0.5, -11.4, 1.5, 1, 1, 1, 'rd_frame'), C(-1, -12, 1, 2, 1, 2, 'rd_tank'),   # Ventil
    C(4.6, -7.5, 1, 1, 2, 2, 'rd_gauge'),                                  # Manometer
    # Brennstoffleitung zur Duese
    C(-5.5, -4, 3, 1, 6, 1, 'rd_hose'), C(-5.5, 1, -4, 1, 1, 8, 'rd_hose'), C(-5, 2, -5, 3, 1, 1, 'rd_hose'),
    # Abgasstutzen hinten
    C(-2.5, -1.5, 6, 2, 2, 2, 'rd_soot'), C(0.5, -1.5, 6, 2, 2, 2, 'rd_soot'),
    C(-2, -1, 7.6, 1, 1, 1, 'rd_ember'), C(1, -1, 7.6, 1, 1, 1, 'rd_ember'),
    # Landekufen
    C(-4, 3, -3, 1, 3, 1, 'rd_frame'), C(3, 3, -3, 1, 3, 1, 'rd_frame'), C(-4, 3, 3, 1, 3, 1, 'rd_frame'), C(3, 3, 3, 1, 3, 1, 'rd_frame'),
    C(-4.5, 6, -5, 2, 1, 10, 'rd_dark'), C(2.5, 6, -5, 2, 1, 10, 'rd_dark')],
    children=[rd_head, rd_nozzle] + rd_arms, role='bob')

MOBS['refinery_drone'] = dict(java='refineryDrone', tex=128, scale=0.8, roots=[rd_body],
    doc='Refinery Drone: Quadrokopter der Raffinerie mit gelb-schwarzen Warnstreifen, rotem Brennstofftank, '
        'Sensorkopf und Flammenwerfer-Duese mit Glut unter der Nase.',
    poses={
        # Flammenstoss (14): Nase hoch zum Ausholen, bei 7 nach vorn kippen und Duese senken, halten bis 11
        2: [(0, 'drone', 0, 0, 0), (4, 'drone', -12, 0, 0), (7, 'drone', 18, 0, 0), (11, 'drone', 18, 0, 0), (14, 'drone', 0, 0, 0),
            (0, 'nozzle', 0, 0, 0), (4, 'nozzle', -8, 0, 0), (7, 'nozzle', 14, 0, 0), (11, 'nozzle', 14, 0, 0), (14, 'nozzle', 0, 0, 0),
            (0, 'head', 0, 0, 0), (4, 'head', -10, 0, 0), (7, 'head', 6, 0, 0), (14, 'head', 0, 0, 0)],
    })
