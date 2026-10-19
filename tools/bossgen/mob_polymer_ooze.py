"""Polymer Ooze (Tekto): zaeher Polymerschleim in Milchweiss/Limettengruen mit leuchtendem Kern, eingeschlossenen
Rohrstuecken und Plastikteilen, Tropfen und tastenden Pseudopodien."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('po_gel', 'ice', (196, 238, 150), (118, 196, 52))
mat('po_gel_d', 'ice', (134, 204, 64), (78, 150, 30))
mat('po_white', 'ice', (246, 250, 236), (204, 232, 176))
mat('po_drip', 'glow', (150, 220, 70), (226, 255, 170))
mat('po_core', 'glow', (150, 236, 20), (255, 255, 214))
mat('po_halo', 'glow', (60, 140, 10), (190, 250, 70))
mat('po_pipe', 'pipe', (152, 158, 162), (96, 100, 104))
mat('po_pipe_g', 'pipe', (74, 156, 62), (40, 92, 34))
mat('po_pvc', 'pvc', (232, 234, 224))
mat('po_red', 'pvc', (196, 52, 40))
mat('po_blue', 'pvc', (52, 104, 188))
mat('po_hazard', 'hazard', (230, 190, 30))
mat('po_rust', 'rust', (118, 110, 102), (150, 92, 44))


def _ch(x, y, z, w, h, d, m, k=1):
    """Gefaster Block aus drei Wuerfeln (runde Silhouette)."""
    return [C(x, y + k, z + k, w, h - 2 * k, d - 2 * k, m), C(x + k, y, z + k, w - 2 * k, h, d - 2 * k, m),
            C(x + k, y + k, z, w - 2 * k, h - 2 * k, d, m)]


def _drip(x, y, z, n, m='po_gel'):
    """Laufnase an einer Seitenflaeche: duenner Faden mit Tropfen unten."""
    return [C(x, y, z, 1, n, 1, m), C(x - 0.5, y + n, z - 0.5, 2, 2, 2, 'po_drip')]


# ── Pseudopodien (tasten nach vorn/seitlich, Spitze mit Tropfen) ──────────────────
def _pod(name, pivot, rot, length, phase):
    tip = B(name + '_tip', (0, 0, -length), (-18, 0, 0), [
        C(-1, -1, -length, 2, 2, length + 1, 'po_gel'), C(-1.5, -1.5, -length - 2, 3, 3, 2, 'po_white'),
        C(-0.5, 1, -length - 1.5, 1, 2, 1, 'po_drip'), C(-0.5, -1.4, -length + 1, 1, 1, 2, 'po_drip')],
        role='tentacle', param=phase + 0.5)
    return B(name, pivot, rot, [
        C(-2, -2, -length, 4, 4, length + 2, 'po_gel'), C(-1.5, -2.6, -length + 1, 3, 1, length - 1, 'po_white'),
        C(-2.5, -2.5, -1, 5, 5, 3, 'po_gel'), C(-1.5, 1.6, -length + 2, 3, 1, 2, 'po_drip')],
        children=[tip], role='tentacle', param=phase)


po_pod_r = _pod('pod_r', (-7, -5, -6), (22, 35, 0), 6, 0.0)
po_pod_l = _pod('pod_l', (7, -6, -5), (24, -40, 0), 5, 2.0)
po_pod_b = _pod('pod_b', (7, -4, 6), (16, -140, 0), 5, 4.0)

# ── eingeschlossener Schrott (schraeg herausragend) ──────────────────────────────
po_junk_pipe = B('junk_pipe', (-4, -12, -1), (10, 0, 30), [
    C(-1, -7, -1, 2, 8, 2, 'po_pipe'), C(-1.5, -8, -1.5, 3, 1, 3, 'po_rust'), C(-1.5, -3, -1.5, 3, 1, 3, 'po_pipe')])
po_junk_elbow = B('junk_elbow', (8, -7, 2), (0, 0, -10), [
    C(0, -1, -1, 5, 2, 2, 'po_pipe_g'), C(4, -4, -1, 2, 5, 2, 'po_pipe_g'), C(3.5, -5, -1.5, 3, 1, 3, 'po_pvc')])
po_junk_shard = B('junk_shard', (4, -14, 2), (-20, 25, -25), [
    C(-2, -5, -0.5, 4, 6, 1, 'po_red'), C(-1, -6, -0.5, 2, 1, 1, 'po_red')])
po_junk_sign = B('junk_sign', (-6, -6, 6), (15, -20, 20), [
    C(-3, -3, 0, 5, 4, 1, 'po_hazard'), C(-0.5, 1, 0, 1, 2, 1, 'po_pipe')])

# ── Koerper ─────────────────────────────────────────────────────────────────────
po_body = B('body', (0, 24, 0), (0, 0, 0), [
    C(-11, -2, -7, 21, 2, 15, 'po_gel_d'), C(-8, -2, -10, 17, 2, 20, 'po_gel_d'),            # Pfuetze am Boden
    C(-13, -1, 2, 4, 1, 5, 'po_gel_d'), C(9, -1, -9, 4, 1, 4, 'po_gel_d')] +
    _ch(-8, -11, -7, 16, 10, 14, 'po_gel', 2) +                        # Hauptmasse
    _ch(-10.5, -8, -9, 8, 7, 9, 'po_gel', 2) +                         # Lappen vorn rechts
    _ch(2, -9, 0, 9, 8, 9, 'po_gel', 2) +                              # Lappen hinten links
    _ch(2, -13, -8, 7, 6, 7, 'po_gel', 2) +                          # haengender Wulst vorn links
    _ch(-7, -15, -4, 11, 6, 10, 'po_white', 2) +                       # milchige Kuppe
    _ch(-4, -18, -1, 6, 4, 6, 'po_white') + [                          # Zipfel
    C(-2.5, -19, 0.5, 2, 1, 2, 'po_white'),
    # leuchtender Kern, von Gelstraengen gehalten
    C(-2.5, -9, -8.2, 5, 5, 4, 'po_core'), C(-1.5, -10, -7.6, 3, 1, 3, 'po_core'),
    C(-4.5, -7, -7.4, 2, 3, 1, 'po_halo'), C(2.5, -10, -7.4, 2, 2, 1, 'po_halo'), C(-1, -4, -7.4, 3, 1, 1, 'po_halo'),
    C(-3, -6.5, -8.5, 4, 1, 1, 'po_gel'), C(1, -9.5, -8.4, 2, 1, 1, 'po_white'),
    C(-8.4, -9, -3, 1, 3, 3, 'po_halo'), C(10.4, -6, 3, 1, 2, 3, 'po_halo'), C(-2, -14.5, 5.4, 3, 2, 1, 'po_halo'),
    # Blasen
    C(4, -6, -7.4, 1, 1, 1, 'po_drip'), C(-7, -5, -9.4, 1, 1, 1, 'po_drip'), C(6, -3, -7.4, 1, 1, 1, 'po_drip'),
    C(-5, -14, -4.4, 1, 1, 1, 'po_drip'), C(10.4, -4, 5, 1, 1, 1, 'po_drip'), C(-8.4, -5, 2, 1, 1, 1, 'po_drip'),
    # eingeschlossene Plastikteile, die durch die Oberflaeche stossen
    C(5, -4, -7.6, 3, 2, 1, 'po_blue'), C(-8.6, -10, 2, 2, 2, 4, 'po_pvc'),
    C(-12, -3, -5, 3, 2, 2, 'po_pipe'), C(-13, -3.5, -5.5, 1, 3, 3, 'po_rust'),
    # Laufnasen und Tropfen
    *_drip(-7, -5, -9.4, 2), *_drip(4, -7, -8.3, 2, 'po_white'), *_drip(10.4, -8, 6, 3), *_drip(-8.4, -9, 4, 4),
    *_drip(3, -11, 7.4, 4, 'po_white'), *_drip(-5, -14, 6.4, 3, 'po_white'),
    C(-13.5, -2, 3.5, 1, 1, 1, 'po_drip')],
    children=[po_pod_r, po_pod_l, po_pod_b, po_junk_pipe, po_junk_elbow, po_junk_shard, po_junk_sign], role='body')

MOBS['polymer_ooze'] = dict(java='polymerOoze', tex=128, scale=1.0, roots=[po_body],
    doc='Polymer Ooze: zaeher milchweiss-limettengruener Polymerschleim mit leuchtendem Kern, eingeschlossenen '
        'Rohrstuecken und Plastikteilen, Laufnasen und tastenden Pseudopodien; teilt sich bei Treffern.',
    poses={
        # Schlag (10): zurueckziehen und aufbaeumen, bei 6-7 nach vorn klatschen, Pseudopodien schlagen zu
        1: [(0, 'body', 0, 0, 0), (4, 'body', -10, 0, 0), (7, 'body', 14, 0, 0), (10, 'body', 0, 0, 0),
            (0, 'pod_r', 0, 0, 0), (4, 'pod_r', -45, 15, 0), (7, 'pod_r', 30, -10, 0), (10, 'pod_r', 0, 0, 0),
            (0, 'pod_l', 0, 0, 0), (4, 'pod_l', -45, -15, 0), (7, 'pod_l', 30, 10, 0), (10, 'pod_l', 0, 0, 0),
            (0, 'pod_r_tip', 0, 0, 0), (4, 'pod_r_tip', -20, 0, 0), (7, 'pod_r_tip', 25, 0, 0), (10, 'pod_r_tip', 0, 0, 0),
            (0, 'pod_l_tip', 0, 0, 0), (4, 'pod_l_tip', -20, 0, 0), (7, 'pod_l_tip', 25, 0, 0), (10, 'pod_l_tip', 0, 0, 0)],
    })
