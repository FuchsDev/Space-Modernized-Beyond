"""Glacier Brute (Minmus): gebeugter Eisriese aus Pressschnee und Minzeis - massige Arme mit Eisfaeusten,
Kristallgrat auf dem Ruecken, kleiner Kopf tief zwischen den Schultern, Eiszaehne."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('gb_pack', 'rock', (196, 212, 226), (150, 170, 190))
mat('gb_pack_d', 'rock', (138, 156, 176))
mat('gb_snow', 'snow', (245, 250, 255))
mat('gb_ice', 'ice', (178, 252, 226), (78, 186, 160))
mat('gb_ice_d', 'ice', (92, 176, 168), (36, 96, 108))
mat('gb_crys', 'crystal', (140, 244, 222), (232, 255, 250))
mat('gb_core', 'glow', (24, 190, 140), (220, 255, 240))
mat('gb_eye', 'eye', (8, 80, 64), (210, 255, 236))
mat('gb_stone', 'rock', (84, 96, 110))


def X(s, x, w): return x if s > 0 else -x - w    # spiegeln (s=-1 rechts = Modell -x)


def spike(name, pivot, rot, h, w, m='gb_crys'):
    """Spitz zulaufender Eiskristall, waechst vom Pivot nach oben (-y)."""
    tiers = []
    for ww, hh in [(w, round(h * 0.5)), (max(1, w - 2), round(h * 0.3)), (max(1, w - 3), h - round(h * 0.5) - round(h * 0.3))]:
        if tiers and tiers[-1][0] == ww: tiers[-1] = (ww, tiers[-1][1] + hh)
        else: tiers.append((ww, hh))
    cubes, y = [], 1
    for ww, hh in tiers:
        if hh >= 1: cubes.append(C(-ww / 2, y - hh, -ww / 2, ww, hh, ww, m)); y -= hh
    return B(name, pivot, rot, cubes)


# ── Kopf: klein, Stirnwulst aus Eis, leuchtende Augen, Unterkiefer mit Eiszaehnen ──
gb_jaw = B('jaw', (0, 0, -1), (8, 0, 0), [
    C(-3.5, 0, -5, 7, 2, 5, 'gb_pack_d'), C(-3, -1, -5.5, 1, 2, 1, 'gb_ice'), C(2, -1, -5.5, 1, 2, 1, 'gb_ice')], role='jaw')
gb_head = B('head', (0, -20, -7), (-20, 0, 0), [
    C(-3.5, -6, -5, 7, 6, 6, 'gb_pack'), C(-4, -6.5, -6, 8, 2, 4, 'gb_ice'), C(-3, -4, -5.5, 6, 2, 1, 'gb_stone'),
    C(-2.5, -3.75, -5.75, 2, 1, 1, 'gb_eye'), C(0.5, -3.75, -5.75, 2, 1, 1, 'gb_eye'), C(-1, -2.5, -6.5, 2, 2, 2, 'gb_ice_d'),
    C(-3.5, -7.5, -3, 7, 2, 5, 'gb_snow')],
    children=[gb_jaw, spike('horn_r', (-3, -6.5, -2), (-20, 0, -40), 5, 2), spike('horn_l', (3, -6.5, -2), (-20, 0, 40), 5, 2)],
    role='head')


# ── Arme: Schulterbrocken mit Kristallen, Oberarm, massiger Eis-Unterarm, Faust mit Kristallknoecheln ──
def gb_arm(s):
    t = 'r' if s < 0 else 'l'
    fist = B('fist_' + t, (X(s, 0.5, 0), 9, -0.5), (10, 0, 0), [
        C(-4.5, 0, -4.5, 9, 7, 8, 'gb_pack'), C(-5, 1, -5, 10, 3, 4, 'gb_ice_d'), C(-3.5, 6, -3.5, 7, 1, 6, 'gb_pack_d')]
        + [C(-4 + i * 3, 2, -6.5, 2, 3, 2, 'gb_crys') for i in range(3)])
    fore = B('fore_' + t, (X(s, 0, 0), 9.5, 0), (-10, 0, s * 8), [
        C(-4, 0, -4, 8, 10, 8, 'gb_ice'), C(-4.5, -1, -4.5, 9, 3, 9, 'gb_snow'), C(X(s, 3, 2), 2, -3, 2, 6, 6, 'gb_ice_d')],
        children=[fist, spike('elbow_' + t, (0, 2, 4), (-100, 0, 0), 6, 3)])
    shoulder = B('shoulder_' + t, (0, -1, 0), (0, 0, s * 12), [
        C(-5, -5, -5, 10, 8, 10, 'gb_pack'), C(-4.5, -6, -4, 9, 2, 8, 'gb_snow'), C(X(s, 4, 2), -3, -3, 2, 5, 6, 'gb_ice_d')],
        children=[spike('sh_%s1' % t, (X(s, 1, 0), -5.5, -1), (-10, 0, s * 25), 9, 4), spike('sh_%s2' % t, (X(s, 3.5, 0), -4, 2.5), (-40, 0, s * 55), 6, 3)])
    return B('arm_' + t, (s * 10, -17, -1), (-34, 0, s * -14), [
        C(-3.5, 1, -3.5, 7, 10, 7, 'gb_pack_d'), C(-4, 5, -4, 8, 2, 8, 'gb_ice_d')],
        children=[shoulder, fore], role='arm_' + t)


# ── Rumpf (stark vorgebeugt): Bauch, Brust mit Kernspalt, Buckel mit Kristallgrat ──
gb_ridge = [spike('ridge_%d' % i, (x, y, z), (rx, 0, rz), h, w) for i, (x, y, z, rx, rz, h, w) in enumerate([
    (0, -21, 3, -20, 0, 10, 5), (-4.5, -19, 4, -30, -28, 7, 4), (4.5, -18, 4.5, -34, 30, 8, 4),
    (0, -14, 6, -55, 0, 8, 4), (-3, -9, 6, -70, -18, 6, 3), (3, -6, 5.5, -80, 18, 5, 3)])]
gb_body = B('body', (0, -2, 0), (28, 0, 0), [
    C(-7.5, -9, -6, 15, 10, 11, 'gb_pack'), C(-8, -5, -6.5, 16, 3, 12, 'gb_snow'), C(-10, -21, -7, 20, 13, 13, 'gb_pack'),
    C(-9, -23, -4, 18, 3, 10, 'gb_ice'), C(-8, -19, -8, 16, 8, 2, 'gb_ice'), C(-1.5, -18, -8.5, 3, 7, 1, 'gb_core'),
    C(-4, -16, -8.75, 2, 2, 1, 'gb_core'), C(-10.5, -14, -5, 21, 4, 10, 'gb_stone'), C(-8, -20, 5.5, 16, 12, 2, 'gb_ice_d'),
    C(-6, -8, -6.5, 12, 2, 1, 'gb_ice_d')],
    children=[gb_head, gb_arm(-1), gb_arm(1)] + gb_ridge, role='body')
gb_hips = B('hips', (0, 10, 3), (0, 0, 0), [C(-7, -3, -4, 14, 6, 9, 'gb_pack_d'), C(-7.5, 1, -4.5, 15, 2, 10, 'gb_snow')], children=[gb_body])


# ── Beine: kurz und staemmig, Eisplatten an den Knien ──
def gb_leg(s):
    t = 'r' if s < 0 else 'l'
    return B('leg_' + t, (s * 5, 10, 3.5), (0, 0, 0), [
        C(-3.5, -1, -3.5, 7, 7, 7, 'gb_pack'), C(-4, 5, -4, 8, 4, 8, 'gb_ice'), C(-3, 5.5, -4.75, 6, 3, 1, 'gb_crys'),
        C(-3.5, 9, -3.5, 7, 3, 7, 'gb_pack'), C(-4.5, 11, -5.5, 9, 3, 9, 'gb_pack_d'), C(-4, 12, -6.5, 8, 2, 1, 'gb_ice_d')],
        role='leg_' + t)


MOBS['glacier_brute'] = dict(java='glacierBrute', tex=256, scale=1.05, roots=[gb_hips, gb_leg(-1), gb_leg(1)],
    doc='Glacier Brute: gebeugter Eisriese aus Pressschnee und Minzeis mit Eisfaeusten, Kristallgrat und kleinem Kopf.',
    poses={
        1: [(0, 'arm_r', 0, 0, 0), (4, 'arm_r', -95, 0, -15), (7, 'arm_r', 15, 0, 5), (10, 'arm_r', 0, 0, 0),
            (0, 'body', 0, 0, 0), (4, 'body', -10, -18, 0), (7, 'body', 10, 14, 0), (10, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (4, 'head', -10, 0, 0), (7, 'head', 8, 0, 0), (10, 'head', 0, 0, 0)],
        3: [(0, 'arm_r', 0, 0, 0), (12, 'arm_r', -150, 0, 18), (15, 'arm_r', 12, 0, 0), (18, 'arm_r', 12, 0, 0), (22, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (12, 'arm_l', -150, 0, -18), (15, 'arm_l', 12, 0, 0), (18, 'arm_l', 12, 0, 0), (22, 'arm_l', 0, 0, 0),
            (0, 'body', 0, 0, 0), (12, 'body', -30, 0, 0), (15, 'body', 18, 0, 0), (18, 'body', 18, 0, 0), (22, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (12, 'head', -15, 0, 0), (15, 'head', 15, 0, 0), (22, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (12, 'jaw', 30, 0, 0), (15, 'jaw', 10, 0, 0), (22, 'jaw', 0, 0, 0)],
    })
