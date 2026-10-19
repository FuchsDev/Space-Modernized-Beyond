"""Abyss Diver (Laythe): untoter Tiefseetaucher im Messing-Taucheranzug mit Bullaugenhelm, Bleischuhen,
abgerissenem Luftschlauch und Druckluft-Harpune im linken Arm."""
import math
from model import B, C
from paint import mat
from mobs import MOBS

mat('ad_helm', 'plate', (186, 136, 58), (246, 206, 118))
mat('ad_brass', 'plate', (164, 116, 46), (232, 188, 98))
mat('ad_brass_d', 'plate', (112, 76, 32), (196, 146, 68))
mat('ad_verdi', 'rust', (150, 112, 50), (70, 150, 126))
mat('ad_suit', 'cloth', (104, 98, 76), (66, 62, 48))
mat('ad_suit_d', 'cloth', (78, 74, 58), (52, 48, 38))
mat('ad_leather', 'plain', (82, 56, 36))
mat('ad_lead', 'plate', (70, 76, 84), (128, 136, 146))
mat('ad_lead_d', 'plate', (46, 50, 56), (92, 98, 108))
mat('ad_hose', 'pipe', (64, 60, 52), (112, 98, 72))
mat('ad_glass', 'glow', (8, 46, 48), (70, 196, 170))
mat('ad_eye', 'eye', (0, 50, 36), (170, 255, 220))
mat('ad_flesh', 'flesh', (112, 128, 104), (64, 82, 66))
mat('ad_bone', 'bone', (214, 208, 184))
mat('ad_steel', 'plate', (150, 158, 166), (214, 220, 226))
mat('ad_wood', 'fiber', (106, 70, 40))
mat('ad_rubber', 'plain', (36, 34, 34))
mat('ad_weed', 'fiber', (46, 96, 62))


def _mc(cubes):
    return [C(-c.x - c.w, c.y, c.z, c.w, c.h, c.d, c.mat, c.infl) for c in cubes]


_ROLE_MIRROR = {'arm_r': 'arm_l', 'leg_r': 'leg_l'}


def _mirror(b, a='_r', z='_l'):
    rx, ry, rz = (math.degrees(v) for v in b.rot)
    return B(b.name.replace(a, z), (-b.pivot[0], b.pivot[1], b.pivot[2]), (rx, -ry, -rz), _mc(b.cubes),
             [_mirror(ch, a, z) for ch in b.children], role=_ROLE_MIRROR.get(b.role, b.role), param=b.param)


def _ch(x, y, z, w, h, d, m, k=1):
    """Gefaster Block aus drei Wuerfeln."""
    return [C(x, y + k, z + k, w, h - 2 * k, d - 2 * k, m), C(x + k, y, z + k, w - 2 * k, h, d - 2 * k, m),
            C(x + k, y + k, z, w - 2 * k, h - 2 * k, d, m)]


# ── Beine mit Bleischuhen ───────────────────────────────────────────────────────
ad_leg_r = B('leg_r', (-2.2, 13, 0), (0, 0, 0), [
    C(-2, 0, -2, 4, 7, 4, 'ad_suit'),                                   # Hosenbein
    C(-2.3, 3, -2.4, 4, 3, 1, 'ad_leather'),                            # Knieflicken
    C(-2.6, 6, -3, 5, 4, 6, 'ad_lead'),                                 # Bleischuh-Schaft
    C(-2.8, 6, -3.2, 5, 1, 6, 'ad_leather', 0.1),                       # Riemen oben
    C(-3, 9, -4.5, 6, 2, 8, 'ad_lead'),                                 # Schuh
    C(-2.5, 9.2, -5, 5, 1.6, 1, 'ad_brass'),                            # Messing-Zehenkappe
    C(-3.2, 10.6, -4.8, 6, 1, 8, 'ad_lead_d', 0.15)])                   # dicke Sohle
ad_leg_l = _mirror(ad_leg_r)

# ── Kopf: Messinghelm mit Bullaugen ──────────────────────────────────────────────
ad_hose3 = B('hose3', (0, 5, 0), (-12, 0, 0), [
    C(-1, 0, -1, 2, 5, 2, 'ad_hose'), C(-1.4, 4, -1.4, 3, 1, 3, 'ad_brass_d'),
    C(-1, 5, -1, 1, 2, 1, 'ad_rubber'), C(0, 5, 0, 1, 1, 1, 'ad_rubber')], role='chain', param=2)
ad_hose2 = B('hose2', (0, 5, 0), (-18, 0, 0), [C(-1, 0, -1, 2, 5, 2, 'ad_hose'), C(-1.3, 1, -1.3, 3, 1, 3, 'ad_hose', -0.1),
                                              C(0.8, 2, 0, 1, 3, 1, 'ad_weed')],
               children=[ad_hose3], role='chain', param=1)
ad_hose1 = B('hose1', (0, -6, 6), (40, 0, 0), [C(-1, 0, -1, 2, 5, 2, 'ad_hose'), C(-1.4, 2, -1.4, 3, 1, 3, 'ad_hose', -0.1)],
               children=[ad_hose2], role='chain', param=0)

ad_head = B('head', (0, -11, 0), (0, 0, 0),
    _ch(-5, -11, -5, 10, 10, 10, 'ad_helm', 2) + [
    C(-4, -10, -4, 8, 8, 8, 'ad_helm'),                                 # Kugel fuellen
    C(-4.5, -2, -4.5, 9, 2, 9, 'ad_brass_d'),                           # Halsring
    # Frontbullauge mit Gitter
    C(-3, -9, -6, 6, 1, 1, 'ad_brass_d'), C(-3, -4, -6, 6, 1, 1, 'ad_brass_d'),
    C(-3, -8, -6, 1, 4, 1, 'ad_brass_d'), C(2, -8, -6, 1, 4, 1, 'ad_brass_d'),
    C(-2, -8, -5.5, 4, 4, 1, 'ad_glass'),
    C(-0.5, -8, -5.75, 1, 4, 1, 'ad_brass'),                            # Gitterstab
    C(-1.8, -6.6, -5.65, 1, 1, 1, 'ad_eye'), C(0.8, -6.6, -5.65, 1, 1, 1, 'ad_eye'),
    # Seitenbullaugen
    C(-5.7, -8, -2, 1, 4, 4, 'ad_brass_d'), C(-6, -7, -1, 1, 2, 2, 'ad_glass'),
    C(4.7, -8, -2, 1, 4, 4, 'ad_brass_d'), C(5, -7, -1, 1, 2, 2, 'ad_glass'),
    # Oberes Bullauge / Ventil
    C(-1.5, -12, -2, 3, 1, 3, 'ad_brass_d'), C(-1, -12.3, -1.5, 2, 1, 2, 'ad_glass'),
    C(3, -4, -4.2, 2, 2, 2, 'ad_brass_d'), C(3.5, -3.6, -4.8, 1, 1, 1, 'ad_steel'),     # Ausatemventil
    C(-1.5, -7.5, 4.6, 3, 3, 2, 'ad_brass_d'),                          # Schlauchstutzen hinten
    C(-4, -10.6, 1, 2, 1, 2, 'ad_verdi'), C(1.5, -10.4, -4, 2, 1, 2, 'ad_verdi'),         # Gruenspan
    C(-5.3, -3, 2, 1, 1, 2, 'ad_weed'),
], children=[ad_hose1], role='head')

# ── Arme ────────────────────────────────────────────────────────────────────────
ad_arm_r = B('arm_r', (-6, -9, 0), (-18, 0, 6), [
    C(-2.5, -2, -2.5, 5, 4, 5, 'ad_suit'),                              # Schulterwulst
    C(-2, 1, -2, 4, 7, 4, 'ad_suit_d'),                                 # Aermel
    C(-2.3, 3.5, -2.3, 4, 2, 4, 'ad_leather', 0.25),                    # Ellbogenflicken
    C(-2.2, 7, -2.2, 4, 1, 4, 'ad_rubber', 0.3),                        # Manschette
    C(-1.5, 8, -1.5, 3, 2, 3, 'ad_flesh'),                              # verweste Hand
    C(-1.5, 10, -1.5, 1, 2, 1, 'ad_bone'), C(0.5, 10, -1.5, 1, 2, 1, 'ad_bone'), C(-0.5, 10, 0.5, 1, 1, 1, 'ad_bone')],
    role='arm_r')

ad_gun = B('harpoon', (0, 9, -0.5), (75, 0, 0), [
    C(-1.5, -1, -2, 3, 3, 7, 'ad_wood'),                                # Schaft
    C(-1, 2, -1, 2, 3, 2, 'ad_wood'),                                   # Pistolengriff
    C(-1, -1.5, -12, 2, 2, 10, 'ad_brass'),                             # Rohr
    C(-1.5, -2, -9, 3, 3, 1, 'ad_brass_d'), C(-1.5, -2, -12.5, 3, 3, 1, 'ad_brass_d'),   # Rohrbaender
    C(-1, 0.5, -9, 2, 2, 6, 'ad_lead'),                                 # Druckluftkessel
    C(1, -1.5, -6, 1, 2, 2, 'ad_glass'),                                # Manometer
    C(-0.5, -2.8, -15, 1, 1, 15, 'ad_steel'),                           # Harpune
    C(-1, -3.3, -17, 2, 2, 2, 'ad_steel'),                              # Spitze
    C(-1.5, -2.8, -15.5, 3, 1, 1, 'ad_steel'),                          # Widerhaken
    C(-0.5, -3.5, -2, 1, 1, 1, 'ad_brass_d')])                          # Kimme
ad_arm_l = B('arm_l', (6, -9, 0), (-75, 0, -4), [
    C(-2.5, -2, -2.5, 5, 4, 5, 'ad_suit'),
    C(-2, 1, -2, 4, 7, 4, 'ad_suit_d'),
    C(-2.3, 3.5, -2.3, 4, 2, 4, 'ad_leather', 0.25),
    C(-2.2, 7, -2.2, 4, 1, 4, 'ad_rubber', 0.3),
    C(-1.5, 8, -1.5, 3, 2, 3, 'ad_flesh'),
    C(2.2, 0, -1, 1, 6, 2, 'ad_leather')],                              # Armriemen
    children=[ad_gun], role='arm_l')

# ── Rumpf ───────────────────────────────────────────────────────────────────────
ad_body = B('body', (0, 13, 0), (0, 0, 0), [
    C(-4.5, -11, -3, 9, 11, 6, 'ad_suit'),                              # Anzug
    C(-5.5, -12, -3.5, 11, 3, 7, 'ad_brass'),                           # Schulterkorsett
    C(-4.5, -10, -4, 9, 2, 8, 'ad_verdi'),                              # Korsettsaum
    C(-5.2, -12.6, -2.5, 1, 1, 1, 'ad_brass_d'), C(4.2, -12.6, -2.5, 1, 1, 1, 'ad_brass_d'),   # Fluegelmuttern
    C(-5.2, -12.6, 1.5, 1, 1, 1, 'ad_brass_d'), C(4.2, -12.6, 1.5, 1, 1, 1, 'ad_brass_d'),
    C(-3, -8, -4, 6, 4, 1, 'ad_lead'), C(-2, -7.5, -4.4, 4, 1, 1, 'ad_brass'),    # Brustgewicht
    C(-3, -8, 3, 6, 5, 1, 'ad_lead'),                                   # Rueckengewicht
    C(-5, -3, -3.5, 10, 2, 7, 'ad_leather'),                            # Guertel
    C(-1, -3, -3.9, 2, 2, 1, 'ad_brass'),                               # Schnalle
    C(3.6, -2, -2, 2, 4, 1, 'ad_leather'), C(4, 1.5, -1.9, 1, 2, 1, 'ad_steel'),  # Messerscheide
    C(-4.8, -1, -3.2, 9, 2, 6, 'ad_suit_d', 0.1),                       # Hosenbund
    C(-4.7, -6, -2, 1, 2, 1, 'ad_weed'), C(1, -1, -3.4, 1, 2, 1, 'ad_weed')],
    children=[ad_head, ad_arm_r, ad_arm_l], role='body')

MOBS['abyss_diver'] = dict(java='abyssDiver', tex=128, scale=1.0, roots=[ad_body, ad_leg_r, ad_leg_l],
    doc='Abyss Diver: untoter Tiefseetaucher im Messing-Taucheranzug mit Bullaugenhelm (schwaches Leuchten innen), '
        'Bleischuhen, abgerissenem Luftschlauch und Druckluft-Harpune im linken Arm.',
    poses={
        # Harpunenschuss (14): zielen, bei 8 Rueckstoss nach oben, Koerper ruckt zurueck
        2: [(0, 'arm_l', 0, 0, 0), (5, 'arm_l', -12, -6, 0), (8, 'arm_l', -12, -6, 0), (9, 'arm_l', -38, -6, 0), (14, 'arm_l', 0, 0, 0),
            (0, 'harpoon', 0, 0, 0), (5, 'harpoon', 0, 0, 0), (9, 'harpoon', -10, 0, 0), (14, 'harpoon', 0, 0, 0),
            (0, 'body', 0, 0, 0), (5, 'body', 0, -18, 0), (8, 'body', 0, -18, 0), (9, 'body', -8, -18, 0), (14, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (5, 'head', 6, 12, 0), (8, 'head', 6, 12, 0), (14, 'head', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (5, 'arm_r', -50, 0, -20), (8, 'arm_r', -50, 0, -20), (14, 'arm_r', 0, 0, 0)],
    })
