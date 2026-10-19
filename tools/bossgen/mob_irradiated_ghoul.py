"""Irradiated Ghoul (Dres): verstrahlter Kraftwerksarbeiter im zerfetzten gelben Schutzanzug, gruen leuchtende
Geschwuere und Augen, halb abgerissene Gasmaske, Atemluftflasche mit Schlauch, Strahlenplakette."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('ig_suit', 'pvc', (186, 164, 64))
mat('ig_grime', 'rust', (140, 122, 54), (82, 74, 40))
mat('ig_suit_d', 'cloth', (150, 130, 56), (92, 82, 40))
mat('ig_flesh', 'flesh', (118, 132, 92), (74, 92, 52))
mat('ig_sore', 'glow', (84, 196, 34), (224, 255, 140))
mat('ig_eye', 'glow', (150, 255, 60), (150, 255, 60))
mat('ig_rubber', 'plain', (40, 44, 40))
mat('ig_mask', 'plate', (72, 78, 74), (136, 146, 136))
mat('ig_lens', 'glow', (36, 70, 30), (120, 180, 90))
mat('ig_filter', 'pipe', (98, 104, 98), (54, 58, 56))
mat('ig_tank', 'pipe', (168, 64, 40), (110, 40, 26))
mat('ig_hazard', 'hazard', (230, 190, 30))
mat('ig_bone', 'bone', (204, 198, 168))
mat('ig_hose', 'fiber', (52, 56, 50))


def M(s, x, w): return x if s > 0 else -x - w    # x spiegeln (s=+1 links = Modell +x)


# ── Beine: Anzughosen mit Riss am Knie, Gummistiefel ──
def ig_leg(s):
    t = 'r' if s < 0 else 'l'
    shin = B('shin_' + t, (0, 6.5, 0), (16, 0, 0), [
        C(-1.5, 0, -1.5, 3, 6, 3, 'ig_suit'), C(-2, 5, -2.5, 4, 2, 4, 'ig_rubber'), C(-2, 4.5, -2, 4, 1, 3.5, 'ig_suit_d'),
        C(M(s, 1.2, 1), 1, -1, 1, 2, 2, 'ig_flesh')])
    return B('leg_' + t, (s * 2, 10.5, 0.5), (-14, 0, s * -3), [
        C(-1.5, -0.5, -1.5, 3, 7, 3, 'ig_suit'), C(-1.4, 4.5, -1.8, 2, 2, 1, 'ig_flesh'), C(-1, 5, -2, 1, 1, 1, 'ig_sore')],
        children=[shin], role='leg_' + t)


# ── Kopf: Kapuze, entbloesstes Gesicht mit Leuchtaugen und Zaehnen, halb abgerissene Gasmaske ──
ig_mask = B('mask', (3.1, -1.2, -3.0), (62, 34, 22), [
    C(-4, -1.5, -1, 4, 4, 1, 'ig_mask'), C(-3.5, -1, -1.4, 1, 1, 1, 'ig_lens'), C(-1.5, -1, -1.4, 1, 1, 1, 'ig_lens'),
    C(-3, 2, -2.5, 2, 2, 2, 'ig_filter'), C(-2.6, 3.5, -2.1, 1, 1, 1, 'ig_rubber'), C(0, -1, -1, 1, 1, 5, 'ig_rubber')])
ig_head = B('head', (0, -10, -1.5), (-36, 0, 0), [
    C(-3, -6, -3, 6, 6, 6, 'ig_suit'), C(-3.5, -6.5, -2, 7, 3, 5, 'ig_suit_d'), C(-2.5, -5, -3.3, 5, 5, 1, 'ig_flesh'),
    C(-2, -3.8, -3.6, 1, 1, 1, 'ig_eye'), C(1, -3.8, -3.6, 1, 1, 1, 'ig_eye'), C(-1.5, -1.5, -3.6, 3, 1, 1, 'ig_bone'),
    C(-2, -0.8, -3.4, 4, 1, 1, 'ig_flesh'), C(-3.3, -4, -1, 1, 1, 1, 'ig_sore'), C(-0.5, -6.6, 0, 2, 1, 2, 'ig_sore'),
    C(-2, -2, 3, 4, 2, 1, 'ig_rubber')], children=[ig_mask], role='head')


# ── Arme: rechts zerfetzter Aermel mit Klauenhand, links Gummihandschuh ──
def ig_hand(s, glove):
    t = 'r' if s < 0 else 'l'
    m = 'ig_rubber' if glove else 'ig_flesh'
    fingers = [B('finger_%s%d' % (t, i), (-1 + i, 1.5, -1), (-20 - i * 5, 0, 0),
                 [C(-0.5, 0, -0.5, 1, 3, 1, m), C(-0.5, 2.5, -0.9, 1, 1, 1, 'ig_bone' if not glove else 'ig_rubber')]) for i in range(3)]
    return B('hand_' + t, (0, 6, 0), (-10, 0, 0), [C(-1.5, 0, -1.5, 3, 2, 3, m)], children=fingers)


ig_fore_r = B('fore_r', (0, 5.5, 0), (-28, 0, 0), [
    C(-1, 0, -1, 2, 6, 2, 'ig_flesh'), C(-1.5, 0, -1.5, 3, 2, 3, 'ig_suit_d'), C(-1.3, 2.5, -1.3, 1, 2, 1, 'ig_sore'),
    C(0.3, 4, -0.5, 1, 1, 1, 'ig_sore')], children=[ig_hand(-1, False),
        B('rag_r', (-1.2, 1.5, 0.5), (10, 0, 15), [C(-0.5, 0, -0.5, 1, 4, 1, 'ig_suit_d')], role='tentacle', param=0.7)])
ig_arm_r = B('arm_r', (-4.6, -9, 0), (-18, 0, 8), [
    C(-1.5, -1, -1.5, 3, 6, 3, 'ig_suit'), C(-2, -1.5, -2, 3, 3, 4, 'ig_suit_d'), C(-1.8, 3, -1.8, 1, 2, 2, 'ig_flesh')],
    children=[ig_fore_r], role='arm_r')
ig_fore_l = B('fore_l', (0, 5.5, 0), (-18, 0, 0), [
    C(-1.5, 0, -1.5, 3, 4, 3, 'ig_suit'), C(-1.5, 3.5, -1.5, 3, 3, 3, 'ig_rubber'), C(-1.8, 3, -1.8, 3.5, 1, 3.5, 'ig_hazard')],
    children=[ig_hand(1, True)])
ig_arm_l = B('arm_l', (4.6, -9, 0), (-8, 0, -6), [
    C(-1.5, -1, -1.5, 3, 6, 3, 'ig_suit'), C(-1, -1.5, -2, 3, 3, 4, 'ig_suit_d'), C(1.2, 1, -0.5, 1, 1, 1, 'ig_sore')],
    children=[ig_fore_l], role='arm_l')

# ── Rumpf: gebeugt, Riss mit Geschwueren, Strahlenplakette, Atemluftflasche, Schlauch ──
ig_hose = B('hose', (2, -9, 3.5), (-60, 0, 20), [C(-0.5, 0, -0.5, 1, 7, 1, 'ig_hose')])
ig_flap = B('flap', (-1, 0, 2.6), (12, 0, 0), [C(-2, 0, -0.5, 3, 4, 1, 'ig_suit_d')], role='tentacle', param=1.5)
ig_torso = B('torso', (0, -1, 0), (32, 0, 0), [
    C(-4, -10, -2.5, 8, 10, 5, 'ig_suit'), C(-3, -8, -2.8, 3, 4, 1, 'ig_flesh'), C(-3.2, -7.5, -3.0, 3, 1, 1, 'ig_bone'), C(-3.2, -5.8, -3.0, 3, 1, 1, 'ig_bone'), C(-2, -6.8, -3.2, 1, 1, 1, 'ig_sore'),
    C(-1.5, -5.5, -3.1, 1, 1, 1, 'ig_sore'), C(1.5, -9, -2.8, 2, 2, 1, 'ig_hazard'), C(-4.5, -1.5, -3, 9, 1, 6, 'ig_rubber'),
    C(-2.5, -9, 2.5, 5, 7, 2, 'ig_tank'), C(-1.5, -10, 3, 3, 1, 1, 'ig_filter'), C(2.8, -6, 1, 1, 2, 2, 'ig_flesh'),
    C(3.3, -5.5, 1.5, 1, 1, 1, 'ig_sore'), C(-4.4, -8, -1, 1, 2, 2, 'ig_sore'), C(-2, -3, 2.6, 3, 2, 1, 'ig_flesh'),
    C(-3, -10.5, -2, 6, 1, 4, 'ig_suit_d'), C(-4.2, -4, -2.6, 4, 3, 1, 'ig_grime'), C(1, -4.5, -2.7, 3, 4, 1, 'ig_grime')],
    children=[ig_head, ig_arm_r, ig_arm_l, ig_hose, ig_flap], role='body')

ig_hips = B('hips', (0, 10.5, 0.5), (0, 0, 0), [
    C(-3.5, -2, -2, 7, 3, 4, 'ig_suit_d'), C(-4, -1.5, -2.5, 8, 1, 5, 'ig_rubber'), C(2.5, -1, -2.8, 2, 2, 2, 'ig_suit_d'),
    C(-4.5, -1, -1, 1, 3, 2, 'ig_hazard')], children=[ig_torso])

MOBS['irradiated_ghoul'] = dict(java='irradiatedGhoul', tex=128, scale=1.0, roots=[ig_hips, ig_leg(-1), ig_leg(1)],
    doc='Irradiated Ghoul: verstrahlter Kraftwerksarbeiter im zerfetzten Schutzanzug mit gruen leuchtenden Geschwueren und Augen, halb abgerissener Gasmaske und Atemluftflasche.',
    poses={
        1: [(0, 'arm_r', 0, 0, 0), (3, 'arm_r', -120, 0, -18), (6, 'arm_r', -45, 0, 18), (10, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (3, 'arm_l', -110, 0, 18), (6, 'arm_l', -40, 0, -18), (10, 'arm_l', 0, 0, 0),
            (0, 'fore_r', 0, 0, 0), (3, 'fore_r', -20, 0, 0), (6, 'fore_r', 15, 0, 0), (10, 'fore_r', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (3, 'torso', -12, 0, 0), (6, 'torso', 14, 0, 0), (10, 'torso', 0, 0, 0),
            (0, 'head', 0, 0, 0), (3, 'head', -10, 0, 0), (6, 'head', 12, 0, 0), (10, 'head', 0, 0, 0)],
    })
