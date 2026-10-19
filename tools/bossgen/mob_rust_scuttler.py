"""Rust Scuttler (Duna): kleiner, schneller Spinnenroboter - flacher rostiger Panzerkoerper, sechs Gelenkbeine,
Sensorkopf mit grossem Linsenauge, zwei Kneifzangen, Energiezelle mit Gluehschlitz und Antenne."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('rs_rust', 'rust', (160, 82, 44), (90, 42, 24))
mat('rs_rust_d', 'rust', (110, 60, 38), (64, 34, 22))
mat('rs_plate', 'plate', (176, 104, 60), (226, 172, 118))
mat('rs_steel', 'plate', (96, 88, 82), (156, 146, 134))
mat('rs_frame', 'frame', (58, 52, 50))
mat('rs_pipe', 'pipe', (128, 114, 102), (74, 64, 56))
mat('rs_hazard', 'hazard', (230, 190, 30))
mat('rs_eye', 'eye', (100, 40, 0), (255, 210, 90))
mat('rs_glow', 'glow', (220, 110, 10), (255, 230, 150))


def X(s, x, w): return x if s > 0 else -x - w    # spiegeln (s=-1 rechts = Modell -x)


# ── Beine: Huefte -> Oberschenkel schraeg hoch -> Knie -> Unterschenkel mit Spitze ──
def rs_leg(tag, s, z, yaw, role):
    shin = B('shin_' + tag, (X(s, 7, 0), 0, 0), (0, 0, s * 20), [
        C(-1, 0, -1, 2, 8, 2, 'rs_rust'), C(-1.5, 1, -1.5, 3, 3, 3, 'rs_plate'), C(-0.5, 8, -0.5, 1, 3, 1, 'rs_steel'),
        C(-1.5, -1.5, -1.5, 3, 3, 3, 'rs_steel')])
    femur = B('femur_' + tag, (0, 0, 0), (0, 0, -s * 38), [
        C(X(s, 0, 7), -1, -1, 7, 2, 2, 'rs_rust'), C(X(s, 1, 5), -2, -1.5, 5, 1, 3, 'rs_plate'), C(X(s, 2, 4), 1, -0.5, 4, 1, 1, 'rs_pipe')],
        children=[shin])
    return B('leg_' + tag, (s * 4.5, 0.5, z), (0, s * yaw, 0), [C(X(s, -1, 2), -1.5, -1.5, 2, 3, 3, 'rs_frame')],
             children=[femur], role=role)


# ── Kneifzangen ──
def rs_claw(s):
    t = 'r' if s < 0 else 'l'
    tip = B('pincer_' + t, (0, 0, -4), (0, s * 30, 0), [
        C(-1, -1, -3, 2, 2, 3, 'rs_steel'), C(X(s, -1, 1), -1, -4, 1, 2, 1, 'rs_frame')])
    return B('claw_' + t, (s * 3, 1.5, -5.5), (15, -s * 20, 0), [
        C(-1, -1, -4, 2, 2, 4, 'rs_rust_d'), C(-1.5, -1.5, -1, 3, 3, 2, 'rs_frame'), C(X(s, 1, 1), -0.5, -4.5, 1, 1, 2, 'rs_frame')],
        children=[tip])


rs_head = B('head', (0, -0.5, -5.5), (0, 0, 0), [
    C(-3, -2, -4, 6, 4, 4, 'rs_plate'), C(-3.5, -2.5, -3, 7, 1, 3, 'rs_rust_d'), C(-2, -1.5, -4.5, 4, 3, 1, 'rs_frame'),
    C(-1.5, -1, -5, 3, 2, 1, 'rs_eye'), C(-3, 0.5, -4.5, 1, 1, 1, 'rs_glow'), C(2, 0.5, -4.5, 1, 1, 1, 'rs_glow'),
    C(-2.5, 2, -3.5, 5, 1, 3, 'rs_frame')],
    role='head')

rs_antenna = B('antenna', (-2.5, -3, 3.5), (-25, 0, -15), [C(-0.5, -5, -0.5, 1, 5, 1, 'rs_frame'), C(-1, -6, -1, 2, 1, 2, 'rs_glow')])

rs_body = B('body', (0, 17, 0), (0, 0, 0), [
    C(-4.5, -2, -6, 9, 4, 12, 'rs_rust'), C(-5.5, -1, -5, 11, 2, 10, 'rs_rust_d'), C(-4, -3, -5, 8, 1, 10, 'rs_plate'),
    C(-3, -4, -3, 6, 1, 6, 'rs_plate'), C(-3.5, -3.5, -5.5, 7, 1, 2, 'rs_hazard'), C(-1, -4.5, -1.5, 2, 1, 3, 'rs_frame'),
    C(-3, -2, 5.5, 6, 4, 3, 'rs_frame'), C(-2, -1, 8, 4, 1, 1, 'rs_glow'), C(-2, 0.5, 8, 4, 1, 1, 'rs_glow'),
    C(2.5, -6, 3.5, 1, 4, 1, 'rs_pipe'), C(2, -6.5, 3, 2, 1, 2, 'rs_frame'), C(-3, 2, -4, 6, 1, 8, 'rs_frame')],
    children=[rs_head, rs_antenna, rs_claw(-1), rs_claw(1),
              rs_leg('r1', -1, -3.5, 32, 'leg_a'), rs_leg('r2', -1, 0, 0, 'leg_b'), rs_leg('r3', -1, 3.5, -32, 'leg_a'),
              rs_leg('l1', 1, -3.5, 32, 'leg_b'), rs_leg('l2', 1, 0, 0, 'leg_a'), rs_leg('l3', 1, 3.5, -32, 'leg_b')],
    role='body')


def _legs(t, rx, kz):
    """Pose-Schluessel fuer alle Beine: rx um die Beinachse (Fuss nach hinten), kz Knie-Spreizung (seitenrichtig)."""
    out = []
    for tag in ('r1', 'r2', 'r3', 'l1', 'l2', 'l3'):
        s = -1 if tag[0] == 'r' else 1
        out += [(t, 'leg_' + tag, rx, 0, 0), (t, 'shin_' + tag, 0, 0, s * kz)]
    return out


MOBS['rust_scuttler'] = dict(java='rustScuttler', tex=128, scale=0.8, roots=[rs_body],
    doc='Rust Scuttler: kleiner rostiger Spinnenroboter mit sechs Beinen, Sensorauge und Kneifzangen.',
    poses={
        1: [(0, 'claw_r', 0, 0, 0), (4, 'claw_r', -20, -30, 0), (6, 'claw_r', 10, 20, 0), (10, 'claw_r', 0, 0, 0),
            (0, 'claw_l', 0, 0, 0), (4, 'claw_l', -20, 30, 0), (6, 'claw_l', 10, -20, 0), (10, 'claw_l', 0, 0, 0),
            (0, 'pincer_r', 0, 0, 0), (4, 'pincer_r', 0, -35, 0), (6, 'pincer_r', 0, 10, 0), (10, 'pincer_r', 0, 0, 0),
            (0, 'pincer_l', 0, 0, 0), (4, 'pincer_l', 0, 35, 0), (6, 'pincer_l', 0, -10, 0), (10, 'pincer_l', 0, 0, 0),
            (0, 'body', 0, 0, 0), (4, 'body', -8, 0, 0), (6, 'body', 10, 0, 0), (10, 'body', 0, 0, 0)],
        3: _legs(0, 0, 0) + _legs(3, -10, 25) + _legs(6, 40, -15) + _legs(10, 20, -25) + _legs(14, 0, 0)
           + [(0, 'body', 0, 0, 0), (3, 'body', 10, 0, 0), (6, 'body', -22, 0, 0), (10, 'body', -8, 0, 0), (14, 'body', 0, 0, 0),
              (0, 'claw_r', 0, 0, 0), (6, 'claw_r', -30, -20, 0), (14, 'claw_r', 0, 0, 0),
              (0, 'claw_l', 0, 0, 0), (6, 'claw_l', -30, 20, 0), (14, 'claw_l', 0, 0, 0)],
    })
