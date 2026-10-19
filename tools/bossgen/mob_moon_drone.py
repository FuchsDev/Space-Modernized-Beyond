"""Moon Drone (Mun): Sicherheitsdrohne der Mondstation - runder Panzerrumpf, zwei Rotor-Mantelringe, rotes Kameraauge,
Antennen, Warnstreifen und Laserlauf unter dem Rumpf."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('md_plate', 'plate', (206, 210, 218), (250, 250, 252))
mat('md_plate_d', 'plate', (110, 116, 128), (176, 182, 192))
mat('md_frame', 'frame', (56, 58, 66))
mat('md_hazard', 'hazard', (230, 190, 30))
mat('md_pipe', 'pipe', (180, 184, 192), (110, 112, 120))
mat('md_red', 'glow', (190, 20, 20), (255, 130, 110))
mat('md_thrust', 'glow', (40, 120, 230), (210, 240, 255))
mat('md_eye', 'eye', (90, 0, 0), (255, 70, 50))


def X(s, x, w): return x if s > 0 else -x - w    # spiegeln (s=-1 rechts = Modell -x)


def md_duct(s):
    t = 'r' if s < 0 else 'l'
    rotor = B('rotor_' + t, (X(s, 6, 0), 0, 0), (0, s * 30, 0), [
        C(-3, -0.5, -0.5, 6, 1, 1, 'md_frame'), C(-0.5, -0.5, -3, 1, 1, 6, 'md_frame'), C(-1, -1.5, -1, 2, 2, 2, 'md_plate_d')])
    return B('duct_' + t, (s * 5.5, -1, 0), (8, 0, s * -12), [
        C(X(s, 0, 2), -1, -1, 2, 2, 2, 'md_frame'),                                   # Ausleger
        C(X(s, 2, 8), -1.5, -4, 8, 3, 1, 'md_plate'), C(X(s, 2, 8), -1.5, 3, 8, 3, 1, 'md_plate'),   # Mantelring
        C(X(s, 2, 1), -1.5, -3, 1, 3, 6, 'md_plate'), C(X(s, 9, 1), -1.5, -3, 1, 3, 6, 'md_plate'),
        C(X(s, 1.5, 9), 0.5, -4.5, 9, 1, 1, 'md_hazard'), C(X(s, 1.5, 9), 0.5, 3.5, 9, 1, 1, 'md_hazard'),
        C(X(s, 9.5, 1), -1, -1, 1, 2, 2, 'md_red')],
        children=[rotor])


md_gun = B('gun', (0, 5, -1.5), (0, 0, 0), [
    C(-3, -0.5, -3, 6, 3, 6, 'md_plate_d'), C(-2, 2, -2, 4, 1, 4, 'md_frame'), C(-1, 0.5, -11, 2, 2, 9, 'md_frame'),
    C(-1.5, 0, -6, 3, 3, 1, 'md_plate'), C(-1.5, 0, -8, 3, 3, 1, 'md_plate'), C(-1.5, 0, -11.5, 3, 3, 1, 'md_plate_d'),
    C(-0.5, 1, -12, 1, 1, 1, 'md_red'), C(-0.5, 2.5, -10, 1, 1, 7, 'md_red'), C(-3.5, 0, -1, 1, 2, 2, 'md_frame'), C(2.5, 0, -1, 1, 2, 2, 'md_frame')])

md_ant_a = B('antenna_a', (-2.5, -6, 2), (-12, 0, -14), [C(-0.5, -6, -0.5, 1, 6, 1, 'md_frame'), C(-1, -7, -1, 2, 1, 2, 'md_red')])
md_ant_b = B('antenna_b', (2.5, -6, 3), (-20, 0, 18), [C(-0.5, -4, -0.5, 1, 4, 1, 'md_frame'), C(-0.5, -5, -0.5, 1, 1, 1, 'md_plate')])

md_hull = B('hull', (0, 15, 0), (0, 0, 0), [
    C(-5, -5, -5, 10, 10, 10, 'md_plate'), C(-6, -4, -4, 12, 8, 8, 'md_plate'), C(-4, -4, -6, 8, 8, 12, 'md_plate'),
    C(-4, -6, -4, 8, 12, 8, 'md_plate_d'),                                        # Kappe oben / Boden
    C(-6.5, 0, -4.5, 13, 2, 9, 'md_hazard'), C(-4.5, 0, -6.5, 9, 2, 13, 'md_hazard'),  # Warngurt
    C(-3.5, -3.5, -7, 7, 7, 1, 'md_frame'), C(-2, -2, -7.5, 4, 4, 1, 'md_plate_d'), C(-1.5, -1.5, -8, 3, 3, 1, 'md_eye'),
    C(-4, -4.5, -8, 8, 1, 2, 'md_plate_d'),                                       # Blende ueber dem Auge
    C(-3, -7, -3, 6, 1, 6, 'md_frame'), C(-1, -7.5, -1, 2, 1, 2, 'md_red'),          # Luke + Warnlicht
    C(-2.5, -2.5, 6, 5, 5, 2, 'md_frame'), C(-1.5, -1.5, 7.5, 3, 3, 1, 'md_thrust'),   # Heckduese
    C(-6.5, -3.5, -2.5, 1, 3, 5, 'md_plate_d'), C(5.5, -3.5, -2.5, 1, 3, 5, 'md_plate_d'),
    C(-7, -3, 2.5, 1, 2, 1, 'md_red'), C(6, -3, 2.5, 1, 2, 1, 'md_red'),
    C(-4.5, 2.5, -6.5, 2, 1, 1, 'md_red'), C(2.5, 2.5, -6.5, 2, 1, 1, 'md_red')],
    children=[md_duct(-1), md_duct(1), md_gun, md_ant_a, md_ant_b], role='bob')

MOBS['moon_drone'] = dict(java='moonDrone', tex=128, scale=0.7, roots=[md_hull],
    doc='Moon Drone: Sicherheitsdrohne der Mondstation mit rotem Kameraauge, Rotor-Mantelringen und Laserlauf.',
    poses={
        2: [(0, 'hull', 0, 0, 0), (4, 'hull', 10, 0, 0), (7, 'hull', -8, 0, 0), (11, 'hull', -3, 0, 0), (14, 'hull', 0, 0, 0),
            (0, 'gun', 0, 0, 0), (4, 'gun', 12, 0, 0), (7, 'gun', -14, 0, 0), (11, 'gun', -4, 0, 0), (14, 'gun', 0, 0, 0),
            (0, 'duct_r', 0, 0, 0), (4, 'duct_r', -20, 0, 0), (9, 'duct_r', 10, 0, 0), (14, 'duct_r', 0, 0, 0),
            (0, 'duct_l', 0, 0, 0), (4, 'duct_l', -20, 0, 0), (9, 'duct_l', 10, 0, 0), (14, 'duct_l', 0, 0, 0),
            (0, 'antenna_a', 0, 0, 0), (7, 'antenna_a', 25, 0, 0), (14, 'antenna_a', 0, 0, 0),
            (0, 'antenna_b', 0, 0, 0), (7, 'antenna_b', 25, 0, 0), (14, 'antenna_b', 0, 0, 0)],
    })
