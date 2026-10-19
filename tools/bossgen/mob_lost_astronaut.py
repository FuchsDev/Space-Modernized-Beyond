"""Lost Astronaut (Mun): untoter Astronaut im zerschlissenen weiss-orangen Raumanzug, gesprungenes Helmvisier mit
schwachem Leuchten dahinter, Lebenserhaltungs-Rucksack mit Schlaeuchen, schlurfende Haltung."""
from model import B, C
from paint import mat
from mobs import MOBS

mat('la_suit', 'cloth', (226, 226, 220), (170, 168, 160))
mat('la_suit_d', 'cloth', (172, 170, 162), (120, 118, 112))
mat('la_orange', 'plate', (226, 128, 32), (255, 196, 110))
mat('la_ring', 'plate', (150, 156, 166), (210, 214, 220))
mat('la_helm', 'plate', (232, 234, 236), (255, 255, 255))
mat('la_frame', 'frame', (58, 60, 68))
mat('la_visor', 'obsidian', (56, 46, 30), (214, 168, 70))
mat('la_glass', 'ice', (120, 110, 80), (60, 50, 34))
mat('la_crack', 'plain', (236, 240, 236))
mat('la_ghost', 'glow', (40, 150, 120), (150, 255, 210))
mat('la_eye', 'eye', (10, 90, 70), (190, 255, 220))
mat('la_flesh', 'flesh', (110, 120, 96), (64, 70, 56))
mat('la_torn', 'plain', (66, 62, 58))
mat('la_hose', 'pipe', (110, 112, 118), (60, 62, 68))
mat('la_pack', 'plate', (200, 202, 206), (240, 240, 244))
mat('la_led_r', 'glow', (170, 20, 20), (255, 110, 90))
mat('la_led_g', 'glow', (20, 150, 60), (150, 255, 170))
mat('la_boot', 'plate', (96, 98, 106), (150, 152, 160))


def X(s, x, w): return x if s > 0 else -x - w    # spiegeln (s=-1 rechts = Modell -x)


# ── Helm: Kugelhelm mit Visier, Riss, schwacher Schein dahinter ──
la_head = B('head', (0, -12, -0.5), (10, 4, -14), [
    C(-4.5, -9, -4.5, 9, 9, 9, 'la_helm'), C(-5, -8, -3.5, 10, 7, 7, 'la_helm'), C(-3.5, -10, -3.5, 7, 11, 7, 'la_helm'),
    C(-3, -7, -4.2, 6, 5, 1, 'la_ghost'), C(0, -5, -4.6, 2, 2, 1, 'la_eye'), C(-3, -5.5, -4.6, 2, 1, 1, 'la_ghost'),
    C(-3.5, -7.5, -5, 3, 6, 1, 'la_visor'), C(-0.5, -7.5, -5, 1, 2, 1, 'la_visor'), C(1.5, -7.5, -5, 2, 2, 1, 'la_visor'),
    C(2.5, -5.5, -5, 1, 4, 1, 'la_visor'), C(-0.5, -2.5, -5, 3, 1, 1, 'la_visor'), C(0.5, -7.5, -5.3, 1, 1, 1, 'la_crack'),
    C(-1.5, -4.5, -5.3, 1, 1, 1, 'la_crack'), C(2.5, -6.5, -5.3, 1, 1, 1, 'la_crack'),
    C(-4, -8.5, -5.5, 8, 1, 1, 'la_ring'), C(-4, -1.5, -5.5, 8, 1, 1, 'la_ring'),
    C(-5, -8.5, -5.5, 1, 8, 2, 'la_ring'), C(4, -8.5, -5.5, 1, 8, 2, 'la_ring'),
    C(-5.5, -6, -1.5, 1, 3, 3, 'la_ring'), C(4.5, -6, -1.5, 1, 3, 3, 'la_ring'),
    C(4.5, -9, -2, 1, 2, 2, 'la_frame'), C(-4, -13, 1, 1, 4, 1, 'la_frame'), C(-4.5, -13.5, 0.5, 2, 1, 2, 'la_led_r')],
    role='head')

# ── Arme: dicke Anzugaermel, Schulterringe, zerrissener Handschuh (links blanke Hand) ──
def la_arm(s, rot, fore_rot):
    t = 'r' if s < 0 else 'l'
    if s < 0:
        hand = [C(-2, 5, -2, 4, 3, 4, 'la_boot'), C(-1.5, 8, -2, 1, 2, 1, 'la_boot'), C(0.5, 8, -2, 1, 2, 1, 'la_boot')]
    else:
        hand = [C(-1.5, 5, -1.5, 3, 3, 3, 'la_flesh'), C(-1.5, 8, -1.5, 1, 3, 1, 'la_flesh'), C(0.5, 8, -1.5, 1, 3, 1, 'la_flesh'),
                C(-1.5, 8, 0.5, 1, 2, 1, 'la_flesh'), C(-2.5, 4, -2.5, 5, 1, 5, 'la_torn')]
    fore = B('fore_' + t, (X(s, 1.5, 0), 5.5, 0), fore_rot, [
        C(-2, 0, -2, 4, 5, 4, 'la_suit'), C(-2.5, 0, -2.5, 5, 1, 5, 'la_ring'), C(-2.5, 3.5, -2.5, 5, 1, 5, 'la_ring')] + hand)
    return B('arm_' + t, (s * 5.5, -10, 0), rot, [
        C(X(s, -0.5, 4), -1, -2, 4, 7, 4, 'la_suit'), C(X(s, -1, 5), -2, -2.5, 5, 3, 5, 'la_orange'),
        C(X(s, 2.5, 1), 1, -1, 1, 2, 2, 'la_torn')],
        children=[fore], role='arm_' + t)


# ── Rucksack: Lebenserhaltung mit Tanks, Lueftung, Statuslicht, abgerissenem Schlauch ──
la_hose = B('hose_loose', (2.5, -1.5, 6.5), (20, 0, 10), [
    C(-0.5, 0, -0.5, 1, 5, 1, 'la_hose'), C(-1, 5, -1, 2, 1, 2, 'la_frame')], role='tentacle', param=1.0)
la_pack = B('pack', (0, -6, 2.5), (0, 0, 0), [
    C(-4, -6, 0, 8, 11, 3, 'la_pack'), C(-4.5, -6.5, 3, 4, 10, 3, 'la_orange'), C(0.5, -6.5, 3, 4, 10, 3, 'la_orange'),
    C(-4, -7.5, 3.5, 3, 1, 2, 'la_frame'), C(1, -7.5, 3.5, 3, 1, 2, 'la_frame'),
    C(-3, 4, 0.5, 6, 2, 3, 'la_frame'), C(-0.5, -4, 3, 1, 7, 2.5, 'la_frame'), C(-0.5, -2, 5.5, 1, 1, 1, 'la_led_r'),
    C(-5, -3, 0.5, 1, 1, 1, 'la_hose'), C(4, -3, 0.5, 1, 1, 1, 'la_hose')],
    children=[la_hose])
# Schlaeuche Rucksack -> Brustpanel
la_tube_r = B('tube_r', (-4.5, -9, 1), (-25, 0, 0), [C(-0.5, -0.5, -4, 1, 1, 4, 'la_hose'), C(-0.5, -0.5, -5, 1, 4, 1, 'la_hose')])
la_tube_l = B('tube_l', (4.5, -9, 1), (-25, 0, 0), [C(-0.5, -0.5, -4, 1, 1, 4, 'la_hose'), C(-0.5, -0.5, -5, 1, 4, 1, 'la_hose')])

la_rags = [B('rag_%d' % i, (x, -1, z), (rx, 0, rz), [C(-1, 0, -0.5, w, h, 1, m)], role='tentacle', param=i * 1.7)
           for i, (x, z, rx, rz, w, h, m) in enumerate([(-3, -3, 8, 6, 2, 5, 'la_suit_d'), (2.5, -3, 4, -10, 2, 4, 'la_orange'),
                                                       (4.5, 1, 0, -14, 1, 4, 'la_suit_d'), (-1, 3, -10, 0, 3, 4, 'la_suit_d')])]

la_body = B('body', (0, 12, 0), (16, 0, 3), [
    C(-4.5, -12, -2.5, 9, 12, 5, 'la_suit'), C(-5, -5, -3, 10, 2, 6, 'la_orange'), C(-5, -2, -3, 10, 2, 6, 'la_frame'),
    C(-4, -13.5, -3, 8, 2, 6, 'la_ring'), C(-3, -11, -3.5, 6, 4, 1, 'la_frame'), C(-2.5, -10.5, -4, 1, 1, 1, 'la_led_g'),
    C(-0.5, -10.5, -4, 1, 1, 1, 'la_led_r'), C(1.5, -10.5, -4, 1, 1, 1, 'la_orange'), C(-2.5, -8.5, -4, 5, 1, 1, 'la_ring'),
    C(1.5, -7, -3, 2, 3, 1, 'la_flesh'), C(-4, -12, -3, 2, 2, 1, 'la_torn'), C(-1.5, -1.5, -3.5, 3, 1, 1, 'la_ring')],
    children=[la_head, la_arm(-1, (-72, 8, 4), (-18, 0, 0)), la_arm(1, (-38, -6, -8), (-30, 0, 0)), la_pack, la_tube_r, la_tube_l] + la_rags,
    role='body')


def la_leg(s, rot):
    t = 'r' if s < 0 else 'l'
    return B('leg_' + t, (s * 2.4, 12, 0.5), rot, [
        C(-2.5, -0.5, -2.5, 5, 8, 5, 'la_suit'), C(-2, 5.5, -3, 4, 2, 1, 'la_orange'), C(-2.5, 3, -2.6, 5, 1, 5, 'la_suit_d'),
        C(-3, 8, -3.5, 6, 4, 6, 'la_boot'), C(-3, 11, -4.5, 6, 1, 1, 'la_frame'), C(-3.5, 7.5, -3, 7, 1, 5, 'la_ring')]
        + ([C(-2.6, 1, -2.7, 2, 3, 1, 'la_torn'), C(-2.2, 1.5, -2.8, 1, 2, 1, 'la_flesh')] if s > 0 else [C(0.5, 0.5, -2.6, 2, 2, 1, 'la_torn')]),
        role='leg_' + t)


MOBS['lost_astronaut'] = dict(java='lostAstronaut', tex=128, scale=0.92, roots=[la_body, la_leg(-1, (0, 4, 2)), la_leg(1, (-8, -10, -3))],
    doc='Lost Astronaut: untoter Astronaut im zerschlissenen Raumanzug mit gesprungenem Visier und Lebenserhaltungs-Rucksack.',
    poses={
        1: [(0, 'arm_r', 0, 0, 0), (4, 'arm_r', -60, 0, 10), (7, 'arm_r', 40, 0, -5), (10, 'arm_r', 0, 0, 0),
            (0, 'arm_l', 0, 0, 0), (4, 'arm_l', -90, 0, -10), (7, 'arm_l', 20, 0, 5), (10, 'arm_l', 0, 0, 0),
            (0, 'body', 0, 0, 0), (4, 'body', -10, 0, 0), (7, 'body', 14, 0, 0), (10, 'body', 0, 0, 0),
            (0, 'head', 0, 0, 0), (4, 'head', -15, 0, 10), (7, 'head', 10, 0, 0), (10, 'head', 0, 0, 0)],
    })
