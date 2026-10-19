"""Cryo Behemoth (Minmus): gorillaartiger Golem aus Minz-Eis und Pressschnee mit Kristallknoecheln und Kristallruecken."""
from model import B, C
from paint import mat
from specs import BOSSES

mat('cb_ice', 'ice', (206, 255, 236), (128, 228, 198))
mat('cb_body', 'ice', (78, 156, 158), (30, 82, 96))
mat('cb_deep', 'obsidian', (30, 70, 80), (130, 220, 210))
mat('cb_snow', 'snow', (245, 250, 255))
mat('cb_rock', 'rock', (70, 92, 104))
mat('cb_crys', 'crystal', (150, 246, 232), (236, 255, 252))
mat('cb_core', 'glow', (20, 196, 140), (230, 255, 244))
mat('cb_eye', 'eye', (8, 70, 60), (210, 255, 240))


def spike(name, pivot, rot, h=12, w=4, m='cb_crys'):
    """Spitz zulaufender Eiskristall, waechst vom Pivot nach oben (-y)."""
    if w >= 4: tiers = [(w, round(h * 0.45)), (w - 2, round(h * 0.35)), (max(1, w - 4), max(1, h - round(h * 0.45) - round(h * 0.35)))]
    else: tiers = [(w, round(h * 0.6)), (max(1, w - 2), h - round(h * 0.6))]
    cubes, y = [], 1
    for ww, hh in tiers:
        cubes.append(C(-ww / 2, y - hh, -ww / 2, ww, hh, ww, m)); y -= hh
    return B(name, pivot, rot, cubes)


# ── Kopf: klein, tief zwischen den Schultern, Stirnwulst, leuchtende Augen, Kiefer ──
cb_jaw = B('jaw', (0, -2, -2), (6, 0, 0), [
    C(-4.5, 0, -7, 9, 3, 7, 'cb_body'), C(-3.5, -0.5, -6.5, 7, 1, 5, 'cb_core'), C(-5, 2, -5, 10, 2, 5, 'cb_snow')], role='jaw')
cb_head = B('head', (0, -22, -9), (-24, 0, 0), [
    C(-5, -8, -7, 10, 7, 8, 'cb_body'), C(-6, -9, -8.5, 12, 3, 5, 'cb_ice'), C(-5, -6.5, -7.5, 10, 3, 1, 'cb_deep'), C(-4.5, -6, -8, 3, 2, 1, 'cb_eye'),
    C(1.5, -6, -8, 3, 2, 1, 'cb_eye'), C(-3.5, -4, -9, 7, 3, 3, 'cb_ice'), C(-4.5, -10, -5, 9, 2, 7, 'cb_snow')],
    children=[cb_jaw, spike('crown_a', (-3, -10, -2), (-35, 0, -25), 6, 2), spike('crown_b', (2.5, -10, -1), (-40, 0, 22), 7, 3)], role='head')

# ── Arme: Schulterfels mit Kristallen, Oberarm, massiger Unterarm, Faust mit Kristallknoecheln ──
def cb_arm(s):
    t = 'r' if s < 0 else 'l'
    def X(x, w): return x if s > 0 else -x - w   # Spiegeln (Modell -x = rechts)
    fist = B('fist_' + t, (0, 12, -1), (8, 0, 0), [
        C(X(-6, 12), 0, -6, 12, 9, 11, 'cb_body'), C(X(-6.5, 13), 4, -7, 13, 3, 4, 'cb_deep')]
        + [C(X(-5.5 + i * 4, 3), 3.5, -9, 3, 4, 3, 'cb_crys') for i in range(3)])
    fore = B('fore_' + t, (X(0.5, 0), 13, 0), (-14, 0, s * 14), [
        C(X(-5, 11), 0, -5.5, 11, 13, 11, 'cb_ice'), C(X(-5.5, 12), -1, -6, 12, 3, 12, 'cb_snow'), C(X(5, 3), 3, -4, 3, 8, 8, 'cb_deep')],
        children=[fist, spike('elbow_' + t, (X(2, 0), 1, 5), (-115, 0, s * 25), 8, 3)])
    pauld = B('pauldron_' + t, (0, -1, 0), (0, 0, s * 10), [
        C(X(-6, 13), -6, -7, 13, 10, 14, 'cb_ice'), C(X(-5.5, 12), -7.5, -6, 12, 2, 12, 'cb_snow')],
        children=[spike('sh_%s1' % t, (X(-2, 0), -7, -1), (-15, 0, s * 30), 14, 5), spike('sh_%s2' % t, (X(2.5, 0), -6, 3), (-50, 0, s * 60), 10, 4)])
    return B('arm_' + t, (s * 15, -21, 0), (-30, 0, s * -26), [
        C(X(-4, 8), 2, -4, 8, 12, 8, 'cb_body'), C(X(-4.5, 9), 8, -4.5, 9, 2, 9, 'cb_deep')],
        children=[pauld, fore], role='arm_' + t)


# ── Rumpf (vorgebeugt): Bauch, Brustkorb mit Kernspalt, Buckel, Kristallruecken ──
cb_core = B('core', (0, -17, -10.5), (0, 0, 45), [C(-4, -4, -1.5, 8, 8, 2, 'cb_core'), C(-5, -5, -0.5, 10, 10, 1, 'cb_crys')])
cb_cracks = [B('crack_%d' % i, (x, -17, -11.8), (0, 0, r), [C(-0.5, -l, -0.5, 1, l, 1, 'cb_core')]) for i, (x, r, l) in enumerate([(-3.5, -62, 6), (3.5, 40, 7), (2.5, 128, 5)])]
cb_pec_r = B('pec_r', (-3.5, -24, -9), (0, 14, 4), [C(-9, 0, -2.5, 9, 12, 3, 'cb_ice')])
cb_pec_l = B('pec_l', (3.5, -24, -9), (0, -14, -4), [C(0, 0, -2.5, 9, 12, 3, 'cb_ice')])
cb_spikes = [spike('sp_1', (0, -28, 1), (-25, 0, 0), 18, 6), spike('sp_2', (-6.5, -27, 3), (-40, 0, -26), 13, 5),
             spike('sp_3', (6.5, -27, 4), (-34, 0, 30), 15, 5), spike('sp_4', (1, -23, 9), (-62, 0, 8), 12, 5),
             spike('sp_5', (-4, -15, 11), (-84, 0, -16), 9, 4), spike('sp_6', (4, -10, 10), (-95, 0, 18), 7, 3)]
cb_torso = B('torso', (0, -1, 0), (26, 0, 0), [
    C(-9, -10, -7, 18, 11, 14, 'cb_body'), C(-10, -4, -8, 11, 3, 16, 'cb_snow'), C(2, -5, -8, 8, 3, 9, 'cb_snow'), C(-12, -24, -9, 24, 15, 18, 'cb_body'),
    C(-11, -28, -5, 22, 5, 14, 'cb_ice'), C(-10, -22, 8, 20, 13, 3, 'cb_ice'), C(-2, -24, 10, 4, 16, 2, 'cb_rock'), C(-3, -22, -9.5, 6, 12, 1, 'cb_core'),
    C(-13, -27, -7, 26, 3, 9, 'cb_snow'), C(-7, -12, -8.5, 14, 3, 2, 'cb_ice'), C(-12.5, -19, -3, 25, 7, 9, 'cb_rock')],
    children=[cb_core] + cb_cracks + [cb_pec_r, cb_pec_l, cb_head, cb_arm(-1), cb_arm(1)] + cb_spikes, role='body')
cb_hips = B('hips', (0, 8, 5), (0, 0, 0), [C(-9, -3, -6, 18, 7, 12, 'cb_deep'), C(-9.5, 1, -6.5, 19, 2, 13, 'cb_snow')], children=[cb_torso])


# ── Beine: kurz und staemmig ──
def cb_leg(s):
    t = 'r' if s < 0 else 'l'
    foot = B('foot_' + t, (0, 6, 0), (0, 0, 0), [C(-5.5, 0, -7, 11, 3, 12, 'cb_deep'), C(-5, 0.5, -9, 10, 2, 2, 'cb_crys')])
    shin = B('shin_' + t, (0, 7, 0), (0, 0, 0), [C(-5, 0, -5, 10, 6, 10, 'cb_body'), C(-5.5, -1, -5.5, 11, 2, 11, 'cb_snow')], children=[foot])
    return B('leg_' + t, (s * 8, 8, 5), (0, 0, 0), [C(-4.5, -2, -4.5, 9, 9, 9, 'cb_ice'), C(s * 4 - 1, 0, -3, 2, 6, 6, 'cb_deep')],
             children=[shin], role='leg_' + t)


BOSSES['cryo_behemoth'] = dict(java='cryoBehemoth', tex=256, scale=1.5, hitbox=(3.2, 5.0), roots=[cb_hips, cb_leg(-1), cb_leg(1)],
    doc='Cryo Behemoth: gorillaartiger Golem aus Minz-Eis und Pressschnee mit Kristallknoecheln, Kristallstacheln auf Ruecken und Schultern und leuchtendem Kern im Brustspalt.',
    # Angriffe: ice_spikes(24), frost_breath(30), boulder(14), slam(18)
    poses={
        0: [(0, 'torso', 18, 0, 0), (0, 'head', 32, 0, 0), (0, 'arm_r', 22, 0, 6), (0, 'arm_l', 22, 0, -6), (0, 'jaw', -6, 0, 0)],
        1: [(0, 'arm_r', 0, 0, 0), (10, 'arm_r', -165, 0, -12), (20, 'arm_r', -25, 0, 0), (24, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (10, 'arm_l', -165, 0, 12), (20, 'arm_l', -25, 0, 0), (24, 'arm_l', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (10, 'torso', -22, 0, 0), (20, 'torso', 22, 0, 0), (24, 'torso', 0, 0, 0),
            (0, 'head', 0, 0, 0), (10, 'head', -20, 0, 0), (20, 'head', 10, 0, 0), (24, 'head', 0, 0, 0)],
        2: [(0, 'head', 0, 0, 0), (6, 'head', -28, 0, 0), (10, 'head', 12, 0, 0), (26, 'head', 12, 0, 0), (30, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (6, 'jaw', 10, 0, 0), (10, 'jaw', 38, 0, 0), (26, 'jaw', 38, 0, 0), (30, 'jaw', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (6, 'torso', -12, 0, 0), (10, 'torso', 6, 0, 0), (26, 'torso', 6, 0, 0), (30, 'torso', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (8, 'arm_r', 15, 0, 25), (26, 'arm_r', 15, 0, 25), (30, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (8, 'arm_l', 15, 0, -25), (26, 'arm_l', 15, 0, -25), (30, 'arm_l', 0, 0, 0)],
        3: [(0, 'arm_r', 0, 0, 0), (6, 'arm_r', -175, 15, 0), (10, 'arm_r', -55, 0, 0), (14, 'arm_r', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (6, 'torso', -14, 22, 0), (10, 'torso', 16, -18, 0), (14, 'torso', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (6, 'arm_l', -30, 0, -15), (10, 'arm_l', 20, 0, 0), (14, 'arm_l', 0, 0, 0)],
        4: [(0, 'arm_r', 0, 0, 0), (7, 'arm_r', -170, 0, -28), (12, 'arm_r', -15, 0, 0), (18, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (7, 'arm_l', -170, 0, 28), (12, 'arm_l', -15, 0, 0), (18, 'arm_l', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (7, 'torso', -24, 0, 0), (12, 'torso', 28, 0, 0), (18, 'torso', 0, 0, 0),
            (0, 'head', 0, 0, 0), (7, 'head', -15, 0, 0), (12, 'head', 15, 0, 0), (18, 'head', 0, 0, 0),
            (0, 'jaw', 0, 0, 0), (7, 'jaw', 30, 0, 0), (12, 'jaw', 10, 0, 0), (18, 'jaw', 0, 0, 0)],
    })
