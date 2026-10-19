"""Void Shade (Ike): schlanker Schatten aus Leere, Kapuzenkopf mit violetten Augen, Kristallsplitter auf den Schultern,
lange Klauenarme, leuchtende Risse und ein zerfaserter Schleier statt Beinen."""
import math
from model import B, C
from paint import mat
from mobs import MOBS

mat('vs_void', 'obsidian', (28, 22, 40), (120, 76, 186))
mat('vs_rune', 'runes', (30, 24, 44), glow_col=(190, 110, 255))
mat('vs_cloth', 'cloth', (36, 28, 50), (84, 46, 130))
mat('vs_rag', 'fiber', (42, 32, 60))
mat('vs_black', 'plain', (8, 6, 12))
mat('vs_crack', 'glow', (146, 56, 228), (244, 206, 255))
mat('vs_eye', 'glow', (206, 120, 255), (236, 196, 255))
mat('vs_claw', 'bone', (150, 132, 170))
mat('vs_shard', 'crystal', (54, 30, 88), (206, 146, 255))


def M(s, x, w): return x if s > 0 else -x - w    # x spiegeln (s=+1 links = Modell +x)


# ── Klauenarm: duenner Oberarm, langer Unterarm mit Rissband, drei gebogene Klauen ──
def vs_arm(s):
    t = 'r' if s < 0 else 'l'
    claws = []
    for i, (dx, ln) in enumerate(((-1, 6), (0, 7), (1, 6))):
        tip = B('tip_%s%d' % (t, i), (0, ln - 0.5, 0), (-30, 0, 0), [C(-0.5, 0, -0.5, 1, 3, 1, 'vs_shard')])
        claws.append(B('claw_%s%d' % (t, i), (s * 0.2 + dx * 1.1, 1.5, -0.5 + abs(dx) * 0.6), (-14, dx * 10, s * -4 + dx * 6),
                       [C(-0.5, 0, -0.5, 1, ln, 1, 'vs_claw')], children=[tip]))
    thumb = B('thumb_' + t, (s * -1.4, 1, -1), (-40, 0, s * 30), [C(-0.5, 0, -0.5, 1, 4, 1, 'vs_claw')])
    hand = B('hand_' + t, (0, 8.5, 0), (-10, 0, 0), [C(-1.5, 0, -1.5, 3, 2, 3, 'vs_void'), C(-1, -0.5, -1.8, 2, 1, 1, 'vs_crack')],
             children=claws + [thumb])
    fore = B('fore_' + t, (0, 7.5, 0), (-22, 0, 0), [
        C(-1, 0, -1, 2, 9, 2, 'vs_void'), C(-1.5, 2, -1.5, 3, 3, 3, 'vs_rune'), C(M(s, 0.8, 1), 5, -0.5, 1, 4, 1, 'vs_crack'),
        C(M(s, 0.5, 1), -1, 0, 1, 3, 2, 'vs_shard')], children=[hand])
    return B('arm_' + t, (s * 4.5, -10, 0), (6, 0, s * -9), [
        C(-1, -1, -1, 2, 9, 2, 'vs_void'), C(M(s, -1, 4), -2, -2, 4, 3, 4, 'vs_cloth'), C(M(s, 0.7, 1), 2, -0.5, 1, 4, 1, 'vs_crack')],
        children=[fore], role='arm_' + t)


# ── Schultersplitter ──
vs_spikes = [B('spike_%s%d' % (t, i), (s * (3.6 + i * 1.6), -11.5 + i, 0.5 + i), (-15 + i * 25, 0, s * (25 + i * 15)),
               [C(-1, -5 + i * 2, -1, 2, 5 - i * 2, 2, 'vs_shard')]) for t, s in (('r', -1), ('l', 1)) for i in range(2)]

# ── Kopf: spitze Kapuze, schwarzes Gesicht, Augenschlitze, Splitterkrone ──
vs_crown = [B('crown_%d' % i, (x, -6, z), (rx, 0, rz), [C(-0.5, -h, -0.5, 1, h, 1, 'vs_shard')])
            for i, (x, z, h, rx, rz) in enumerate([(0, 0.5, 3, -24, 0), (-2, 1, 2, -18, -26), (2, 1, 2, -18, 26)])]
vs_head = B('head', (0, -11, -0.5), (6, 0, 0), [
    C(-3, -6, -3, 6, 6, 6, 'vs_cloth'), C(-3.5, -5.5, -3.6, 7, 6, 2, 'vs_cloth'), C(-2, -5, -3.9, 4, 5, 1, 'vs_black'),
    C(-1.9, -3.5, -4.2, 2, 1, 1, 'vs_eye'), C(0.1, -3.5, -4.2, 2, 1, 1, 'vs_eye'), C(-1.2, -2.5, -4.1, 1, 1, 1, 'vs_crack'),
    C(0.6, -1.8, -4.1, 1, 1, 1, 'vs_crack'), C(-2, -6.5, 2.5, 4, 4, 3, 'vs_cloth'), C(-1, -6, 5, 2, 3, 3, 'vs_rag'),
    C(-0.5, -5.5, 7.5, 1, 2, 2, 'vs_cloth')], children=vs_crown, role='head')

# ── Rumpf: schmaler Brustkorb mit Runenrissen, Taille ──
vs_cape = [B('cape_%d' % i, (x, -11, 2.6), (14 + abs(x) * 3, 0, x * -3), [C(-1, 0, 0, 2, ln, 1, 'vs_cloth')], children=[
    B('cape_%db' % i, (0, ln - 0.5, 0), (8, 0, 0), [C(-1, 0, 0, 2, ln2, 1, 'vs_rag')], role='tentacle', param=i * 1.3 + 2)], role='tentacle', param=i * 1.3 + 1)
    for i, (x, ln, ln2) in enumerate([(-3, 9, 7), (-1, 10, 9), (1, 10, 8), (3, 9, 6)])]

vs_torso = B('torso', (0, 0, 0), (8, 0, 0), [
    C(-4, -11, -2.5, 8, 6, 5, 'vs_void'), C(-3, -6, -2, 6, 3, 4, 'vs_void'), C(-2.5, -4, -2, 5, 4, 4, 'vs_void'),
    C(-2.5, -10.5, -2.8, 1, 2, 1, 'vs_crack'), C(-1.5, -9, -2.8, 1, 2, 1, 'vs_crack'), C(-0.5, -7.5, -2.8, 1, 2, 1, 'vs_crack'),
    C(0.5, -6.5, -2.8, 2, 1, 1, 'vs_crack'), C(1.5, -10, -2.8, 1, 2, 1, 'vs_crack'), C(-2, -5.5, -2.3, 1, 3, 1, 'vs_crack'),
    C(-3.5, -10, -2.7, 7, 1, 1, 'vs_rune'), C(-4.5, -11.5, -2, 9, 2, 4, 'vs_cloth'), C(-2, -10, 2.2, 4, 7, 1, 'vs_rune')],
    children=[vs_head, vs_arm(-1), vs_arm(1)] + vs_spikes + vs_cape, role='body')

# ── Schleier: Fetzenbahnen rings um die Huefte (zweigliedrig, wellend) ──
vs_veil = []
for i in range(9):
    a = math.radians(i * 40 + 20)
    x, z = 3.0 * math.sin(a), -2.4 * math.cos(a)
    w = 3 if i % 2 == 0 else 2
    la, lb = 7, 5 + (i * 5) % 4
    out = 12
    tip = B('veil_%db' % i, (0, la - 0.5, 0), (-4 * math.cos(a), 0, -4 * math.sin(a)), [
        C(-w / 2, 0, -0.5, w, lb, 1, 'vs_rag'), C(-0.5, lb - 1, -0.5, 1, 2, 1, 'vs_cloth')], role='tentacle', param=i * 0.9 + 0.5)
    vs_veil.append(B('veil_%d' % i, (x, 0, z), (-out * math.cos(a), 0, -out * math.sin(a)), [
        C(-w / 2, 0, -0.5, w, la, 1, 'vs_cloth')], children=[tip], role='tentacle', param=i * 0.9))

vs_core = B('core', (0, 0, 0), (0, 0, 0), [
    C(-3, -1, -2.5, 6, 6, 5, 'vs_cloth'), C(-2, 4, -1.5, 4, 5, 3, 'vs_black'), C(-1, 8, -1, 2, 4, 2, 'vs_rag'),
    C(-3.5, -1.5, -3, 7, 2, 6, 'vs_void'), C(-1, -1, -3.3, 2, 1, 1, 'vs_crack')], children=vs_veil)

vs_root = B('shade', (0, 8.5, 0), (0, 0, 0), [], children=[vs_torso, vs_core], role='bob')

MOBS['void_shade'] = dict(java='voidShade', tex=128, scale=1.0, roots=[vs_root],
    doc='Void Shade: schlanker Schatten aus Leere mit Kapuze, violett leuchtenden Augen und Rissen, langen Klauenarmen und zerfasertem Schleier.',
    poses={
        1: [(0, 'arm_r', 0, 0, 0), (3, 'arm_r', -150, 0, -25), (6, 'arm_r', -20, 0, 35), (8, 'arm_r', -10, 0, 20), (10, 'arm_r', 0, 0, 0),
            (0, 'fore_r', 0, 0, 0), (3, 'fore_r', -30, 0, 0), (6, 'fore_r', 10, 0, 0), (10, 'fore_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (3, 'arm_l', -40, 0, -10), (6, 'arm_l', -70, 0, -20), (10, 'arm_l', 0, 0, 0),
            (0, 'torso', 0, 0, 0), (3, 'torso', -10, 25, 0), (6, 'torso', 18, -20, 0), (10, 'torso', 0, 0, 0),
            (0, 'head', 0, 0, 0), (3, 'head', 5, -15, 0), (6, 'head', -5, 15, 0), (10, 'head', 0, 0, 0)],
        3: [(0, 'torso', 0, 0, 0), (4, 'torso', 42, 0, 0), (7, 'torso', 42, 0, 0), (10, 'torso', 0, 0, 0),
            (0, 'head', 0, 0, 0), (4, 'head', -25, 0, 0), (7, 'head', -25, 0, 0), (10, 'head', 0, 0, 0),
            (0, 'arm_r', 0, 0, 0), (4, 'arm_r', -70, 0, 50), (7, 'arm_r', -70, 0, 50), (10, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (4, 'arm_l', -70, 0, -50), (7, 'arm_l', -70, 0, -50), (10, 'arm_l', 0, 0, 0),
            (0, 'fore_r', 0, 0, 0), (4, 'fore_r', -60, 0, 0), (7, 'fore_r', -60, 0, 0), (10, 'fore_r', 0, 0, 0),
            (0, 'fore_l', 0, 0, 0), (4, 'fore_l', -60, 0, 0), (7, 'fore_l', -60, 0, 0), (10, 'fore_l', 0, 0, 0),
            (0, 'core', 0, 0, 0), (4, 'core', 20, 0, 0), (7, 'core', 20, 0, 0), (10, 'core', 0, 0, 0)],
    })
